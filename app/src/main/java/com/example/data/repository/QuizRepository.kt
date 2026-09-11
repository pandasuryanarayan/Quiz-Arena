package com.example.data.repository

import com.example.data.local.GameSessionEntity
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.PlayerAnswerEntity
import com.example.data.local.QuestionWithOptions
import com.example.data.local.QuizDao
import com.example.data.local.QuizEntity
import com.example.data.local.UserEntity
import com.example.data.seed.SeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class QuizRepository(private val dao: QuizDao) {

  val user: Flow<UserEntity?> = dao.getUser()
  val allQuizzes: Flow<List<QuizEntity>> = dao.getAllQuizzes()
  val recentSessions: Flow<List<GameSessionEntity>> = dao.getRecentSessions()

  suspend fun ensureDataSeeded() = withContext(Dispatchers.IO) {
    val count = dao.getQuizCount()
    if (count == 0) {
      dao.insertUser(SeedData.defaultUser)
      dao.insertQuizzes(SeedData.quizzes)
      val (questions, options) = SeedData.getQuestionsAndOptions()
      dao.insertQuestions(questions)
      dao.insertOptions(options)
      dao.insertLeaderboardEntries(SeedData.getInitialLeaderboardEntries())
    }
  }

  suspend fun getQuizById(quizId: String): QuizEntity? = withContext(Dispatchers.IO) {
    dao.getQuizById(quizId)
  }

  suspend fun getQuestionsWithOptions(quizId: String): List<QuestionWithOptions> =
    withContext(Dispatchers.IO) {
      dao.getQuestionsWithOptions(quizId)
    }

  fun getLeaderboard(quizId: String, period: String): Flow<List<LeaderboardEntryEntity>> {
    return dao.getLeaderboard(quizId, period)
  }

  fun getGlobalLeaderboard(period: String): Flow<List<LeaderboardEntryEntity>> {
    return dao.getGlobalLeaderboard(period)
  }

  fun getHighScoreForQuiz(quizId: String): Flow<Int?> {
    return dao.getHighScoreForQuiz(quizId)
  }

  suspend fun saveGameSession(
    sessionId: String,
    quizId: String,
    quizTitle: String,
    totalScore: Int,
    correctCount: Int,
    totalQuestions: Int,
    highestStreak: Int,
    startedAt: Long,
    completedAt: Long,
    averageResponseTimeMs: Long,
    answers: List<PlayerAnswerEntity>
  ) = withContext(Dispatchers.IO) {
    val session = GameSessionEntity(
      id = sessionId,
      quizId = quizId,
      quizTitle = quizTitle,
      totalScore = totalScore,
      correctCount = correctCount,
      totalQuestions = totalQuestions,
      highestStreak = highestStreak,
      status = "COMPLETED",
      startedAt = startedAt,
      completedAt = completedAt,
      averageResponseTimeMs = averageResponseTimeMs
    )
    dao.insertSession(session)
    dao.insertPlayerAnswers(answers)

    // Update user stats
    val currentUser = dao.getUserSync() ?: SeedData.defaultUser
    val accuracy = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
    val xpEarned = (totalScore / 10).coerceAtLeast(20)
    val coinsEarned = (correctCount * 10) + (highestStreak * 5)

    val updatedUser = currentUser.copy(
      totalXp = currentUser.totalXp + xpEarned,
      coins = currentUser.coins + coinsEarned,
      highestStreak = maxOf(currentUser.highestStreak, highestStreak),
      gamesPlayed = currentUser.gamesPlayed + 1,
      totalCorrectAnswers = currentUser.totalCorrectAnswers + correctCount
    )
    dao.insertUser(updatedUser)

    // Insert or update Player's Leaderboard entry for Daily, Weekly, All-Time
    val periods = listOf("DAILY", "WEEKLY", "ALL_TIME")
    for (period in periods) {
      val entry = LeaderboardEntryEntity(
        id = "lb_${quizId}_${period}_player",
        quizId = quizId,
        username = currentUser.username,
        highScore = totalScore,
        accuracyPct = accuracy,
        period = period,
        totalTimeMs = (completedAt - startedAt).coerceAtLeast(1000L),
        isLocalPlayer = true,
        avatarSeed = "player",
        updatedAt = completedAt
      )
      dao.insertLeaderboardEntry(entry)
    }
  }

  suspend fun updateUsername(newUsername: String) = withContext(Dispatchers.IO) {
    val currentUser = dao.getUserSync() ?: SeedData.defaultUser
    dao.insertUser(currentUser.copy(username = newUsername))
  }
}
