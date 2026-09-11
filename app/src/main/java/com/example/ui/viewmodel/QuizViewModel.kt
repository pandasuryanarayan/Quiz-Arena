package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.PlayerAnswerEntity
import com.example.data.local.QuizEntity
import com.example.data.local.UserEntity
import com.example.data.repository.QuizRepository
import com.example.ui.model.ActiveQuestionState
import com.example.ui.model.AnswerOutcome
import com.example.ui.model.NavigationScreen
import com.example.ui.model.OptionUiModel
import com.example.ui.model.QuestionResultUi
import com.example.ui.model.QuestionUiModel
import com.example.ui.model.SessionSummary
import com.example.util.AudioHapticManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class QuizViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: QuizRepository = QuizRepository(
    AppDatabase.getDatabase(application).quizDao()
  )
  val audioHaptic = AudioHapticManager(application)

  val user: StateFlow<UserEntity?> = repository.user.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null
  )

  val quizzes: StateFlow<List<QuizEntity>> = repository.allQuizzes.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  private val _currentScreen = MutableStateFlow(NavigationScreen.HOME)
  val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

  // Active game session state
  private val _activeQuiz = MutableStateFlow<QuizEntity?>(null)
  val activeQuiz: StateFlow<QuizEntity?> = _activeQuiz.asStateFlow()

  private val _activeQuestionState = MutableStateFlow<ActiveQuestionState?>(null)
  val activeQuestionState: StateFlow<ActiveQuestionState?> = _activeQuestionState.asStateFlow()

  private val _totalScore = MutableStateFlow(0)
  val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

  private val _currentStreak = MutableStateFlow(0)
  val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

  private val _sessionSummary = MutableStateFlow<SessionSummary?>(null)
  val sessionSummary: StateFlow<SessionSummary?> = _sessionSummary.asStateFlow()

  // Leaderboard filters
  private val _leaderboardPeriod = MutableStateFlow("DAILY")
  val leaderboardPeriod: StateFlow<String> = _leaderboardPeriod.asStateFlow()

  private val _leaderboardQuizFilter = MutableStateFlow<String?>(null)
  val leaderboardQuizFilter: StateFlow<String?> = _leaderboardQuizFilter.asStateFlow()

  val leaderboardEntries: StateFlow<List<LeaderboardEntryEntity>> = _leaderboardPeriod.flatMapLatest { period ->
    _leaderboardQuizFilter.flatMapLatest { quizId ->
      if (quizId == null) {
        repository.getGlobalLeaderboard(period)
      } else {
        repository.getLeaderboard(quizId, period)
      }
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Internal game execution tracking
  private var currentQuestions: List<QuestionUiModel> = emptyList()
  private var currentQuestionIndex = 0
  private var sessionStartTimeMs = 0L
  private var questionStartTimeMs = 0L
  private var highestStreakThisSession = 0
  private var correctAnswersThisSession = 0
  private var currentSessionId = ""
  private val recordedAnswers = mutableListOf<PlayerAnswerEntity>()
  private val recordedResults = mutableListOf<QuestionResultUi>()
  private var timerJob: Job? = null
  private var autoAdvanceJob: Job? = null

  init {
    viewModelScope.launch {
      repository.ensureDataSeeded()
    }
  }

  fun navigateTo(screen: NavigationScreen) {
    if (screen != NavigationScreen.ARENA) {
      stopTimer()
      autoAdvanceJob?.cancel()
    }
    _currentScreen.value = screen
  }

  fun setLeaderboardPeriod(period: String) {
    _leaderboardPeriod.value = period
  }

  fun setLeaderboardQuizFilter(quizId: String?) {
    _leaderboardQuizFilter.value = quizId
  }

  fun startQuiz(quiz: QuizEntity) {
    viewModelScope.launch {
      val rawQuestions = repository.getQuestionsWithOptions(quiz.id)
      if (rawQuestions.isEmpty()) return@launch

      currentQuestions = rawQuestions.map { qWithOpts ->
        QuestionUiModel(
          id = qWithOpts.question.id,
          text = qWithOpts.question.questionText,
          explanation = qWithOpts.question.explanation,
          position = qWithOpts.question.position,
          options = qWithOpts.options.map { opt ->
            OptionUiModel(
              id = opt.id,
              letter = opt.optionLetter,
              text = opt.optionText,
              isCorrect = opt.isCorrect
            )
          }
        )
      }.shuffled().take(quiz.questionCount)

      _activeQuiz.value = quiz
      _totalScore.value = 0
      _currentStreak.value = 0
      highestStreakThisSession = 0
      correctAnswersThisSession = 0
      currentQuestionIndex = 0
      currentSessionId = "ses_" + UUID.randomUUID().toString().take(8)
      recordedAnswers.clear()
      recordedResults.clear()
      sessionStartTimeMs = System.currentTimeMillis()

      _currentScreen.value = NavigationScreen.ARENA
      loadQuestion(currentQuestionIndex)
    }
  }

  private fun loadQuestion(index: Int) {
    stopTimer()
    autoAdvanceJob?.cancel()

    if (index >= currentQuestions.size) {
      finishGameSession()
      return
    }

    val q = currentQuestions[index]
    val timeLimit = _activeQuiz.value?.timeLimitSec ?: 15
    val streakMult = calculateStreakMultiplier(_currentStreak.value)

    _activeQuestionState.value = ActiveQuestionState(
      question = q,
      questionIndex = index,
      totalQuestions = currentQuestions.size,
      timeLimitSec = timeLimit,
      timeRemainingMs = timeLimit * 1000L,
      selectedOptionId = null,
      outcome = AnswerOutcome.NONE,
      pointsEarnedThisQuestion = 0,
      speedBonus = 0,
      streakMultiplier = streakMult,
      isAnswerLocked = false
    )

    questionStartTimeMs = System.currentTimeMillis()
    startTimer(timeLimit * 1000L)
  }

  private fun startTimer(durationMs: Long) {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      var remaining = durationMs
      val interval = 50L
      var lastWarningSec = -1

      while (remaining > 0) {
        delay(interval)
        val currentState = _activeQuestionState.value
        if (currentState == null || currentState.isAnswerLocked) {
          break
        }

        remaining -= interval
        val safeRemaining = remaining.coerceAtLeast(0L)
        _activeQuestionState.value = currentState.copy(timeRemainingMs = safeRemaining)

        val secondsLeft = (safeRemaining / 1000L).toInt()
        if (secondsLeft in 1..3 && secondsLeft != lastWarningSec) {
          lastWarningSec = secondsLeft
          audioHaptic.playTimerWarning()
        }
      }

      val finalState = _activeQuestionState.value
      if (finalState != null && !finalState.isAnswerLocked && remaining <= 0) {
        handleTimeout()
      }
    }
  }

  private fun stopTimer() {
    timerJob?.cancel()
    timerJob = null
  }

  fun submitAnswer(optionId: String) {
    val state = _activeQuestionState.value ?: return
    if (state.isAnswerLocked) return

    stopTimer()
    val responseTimeMs = (System.currentTimeMillis() - questionStartTimeMs).coerceAtLeast(100L)
    val selectedOption = state.question.options.firstOrNull { it.id == optionId }
    val correctOption = state.question.options.firstOrNull { it.isCorrect }
    val isCorrect = selectedOption?.isCorrect == true

    var pointsAwarded = 0
    var speedBonus = 0
    val streakMultiplier = calculateStreakMultiplier(_currentStreak.value)

    if (isCorrect) {
      // Speed bonus: up to 50 pts proportional to time remaining
      val maxTime = state.timeLimitSec * 1000f
      speedBonus = ((state.timeRemainingMs / maxTime) * 50f).toInt().coerceIn(0, 50)
      val basePoints = 100
      pointsAwarded = ((basePoints + speedBonus) * streakMultiplier).toInt()

      val newStreak = _currentStreak.value + 1
      _currentStreak.value = newStreak
      if (newStreak > highestStreakThisSession) {
        highestStreakThisSession = newStreak
      }
      correctAnswersThisSession++
      _totalScore.value += pointsAwarded

      if (newStreak == 3 || newStreak == 5 || newStreak == 7) {
        audioHaptic.playStreakCombo(newStreak)
      } else {
        audioHaptic.playCorrect()
      }
    } else {
      _currentStreak.value = 0
      audioHaptic.playWrong()
    }

    _activeQuestionState.value = state.copy(
      selectedOptionId = optionId,
      outcome = if (isCorrect) AnswerOutcome.CORRECT else AnswerOutcome.WRONG,
      pointsEarnedThisQuestion = pointsAwarded,
      speedBonus = speedBonus,
      streakMultiplier = streakMultiplier,
      isAnswerLocked = true
    )

    // Record answer
    val answerEntity = PlayerAnswerEntity(
      sessionId = currentSessionId,
      questionId = state.question.id,
      selectedOptionId = optionId,
      isCorrect = isCorrect,
      responseTimeMs = responseTimeMs,
      pointsAwarded = pointsAwarded,
      speedBonus = speedBonus,
      streakMultiplier = streakMultiplier
    )
    recordedAnswers.add(answerEntity)

    recordedResults.add(
      QuestionResultUi(
        questionNumber = state.questionIndex + 1,
        questionText = state.question.text,
        selectedLetter = selectedOption?.letter,
        selectedText = selectedOption?.text,
        correctLetter = correctOption?.letter ?: "A",
        correctText = correctOption?.text ?: "",
        isCorrect = isCorrect,
        isTimeout = false,
        responseTimeMs = responseTimeMs,
        pointsAwarded = pointsAwarded,
        speedBonus = speedBonus,
        streakMultiplier = streakMultiplier,
        explanation = state.question.explanation
      )
    )

    // Automatically advance after delay
    scheduleAutoAdvance(if (isCorrect) 1800L else 2400L)
  }

  private fun handleTimeout() {
    val state = _activeQuestionState.value ?: return
    if (state.isAnswerLocked) return

    stopTimer()
    val responseTimeMs = state.timeLimitSec * 1000L
    val correctOption = state.question.options.firstOrNull { it.isCorrect }
    _currentStreak.value = 0

    audioHaptic.playTimeout()

    _activeQuestionState.value = state.copy(
      selectedOptionId = null,
      outcome = AnswerOutcome.TIMEOUT,
      pointsEarnedThisQuestion = 0,
      speedBonus = 0,
      streakMultiplier = 1.0f,
      isAnswerLocked = true,
      timeRemainingMs = 0L
    )

    val answerEntity = PlayerAnswerEntity(
      sessionId = currentSessionId,
      questionId = state.question.id,
      selectedOptionId = null,
      isCorrect = false,
      responseTimeMs = responseTimeMs,
      pointsAwarded = 0,
      speedBonus = 0,
      streakMultiplier = 1.0f
    )
    recordedAnswers.add(answerEntity)

    recordedResults.add(
      QuestionResultUi(
        questionNumber = state.questionIndex + 1,
        questionText = state.question.text,
        selectedLetter = null,
        selectedText = "Timed Out",
        correctLetter = correctOption?.letter ?: "A",
        correctText = correctOption?.text ?: "",
        isCorrect = false,
        isTimeout = true,
        responseTimeMs = responseTimeMs,
        pointsAwarded = 0,
        speedBonus = 0,
        streakMultiplier = 1.0f,
        explanation = state.question.explanation
      )
    )

    scheduleAutoAdvance(2500L)
  }

  private fun scheduleAutoAdvance(delayMs: Long) {
    autoAdvanceJob?.cancel()
    autoAdvanceJob = viewModelScope.launch {
      delay(delayMs)
      nextQuestion()
    }
  }

  fun nextQuestion() {
    autoAdvanceJob?.cancel()
    currentQuestionIndex++
    loadQuestion(currentQuestionIndex)
  }

  private fun finishGameSession() {
    stopTimer()
    autoAdvanceJob?.cancel()

    val quiz = _activeQuiz.value ?: return
    val completedAt = System.currentTimeMillis()
    val totalTimeMs = (completedAt - sessionStartTimeMs).coerceAtLeast(1000L)
    val totalQuestions = currentQuestions.size
    val accuracy = if (totalQuestions > 0) (correctAnswersThisSession * 100) / totalQuestions else 0
    val avgTime = if (totalQuestions > 0) totalTimeMs / totalQuestions else 0L

    val finalScore = _totalScore.value
    val xp = (finalScore / 10).coerceAtLeast(25)
    val coins = (correctAnswersThisSession * 10) + (highestStreakThisSession * 5)

    viewModelScope.launch {
      repository.saveGameSession(
        sessionId = currentSessionId,
        quizId = quiz.id,
        quizTitle = quiz.title,
        totalScore = finalScore,
        correctCount = correctAnswersThisSession,
        totalQuestions = totalQuestions,
        highestStreak = highestStreakThisSession,
        startedAt = sessionStartTimeMs,
        completedAt = completedAt,
        averageResponseTimeMs = avgTime,
        answers = recordedAnswers
      )
    }

    _sessionSummary.value = SessionSummary(
      sessionId = currentSessionId,
      quizId = quiz.id,
      quizTitle = quiz.title,
      totalScore = finalScore,
      correctCount = correctAnswersThisSession,
      totalQuestions = totalQuestions,
      accuracyPct = accuracy,
      highestStreak = highestStreakThisSession,
      totalTimeMs = totalTimeMs,
      averageTimeMs = avgTime,
      isNewPersonalBest = finalScore > 1000,
      xpGained = xp,
      coinsGained = coins,
      questionResults = recordedResults.toList()
    )

    audioHaptic.playVictory()
    _currentScreen.value = NavigationScreen.SUMMARY
  }

  fun updatePlayerUsername(newName: String) {
    if (newName.isNotBlank()) {
      viewModelScope.launch {
        repository.updateUsername(newName.trim())
      }
    }
  }

  private fun calculateStreakMultiplier(streak: Int): Float {
    return when {
      streak >= 5 -> 2.0f
      streak >= 3 -> 1.5f
      else -> 1.0f
    }
  }

  override fun onCleared() {
    super.onCleared()
    stopTimer()
    autoAdvanceJob?.cancel()
    audioHaptic.release()
  }
}
