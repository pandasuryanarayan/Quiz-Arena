package com.example.ui.model

enum class AnswerOutcome {
  NONE,
  CORRECT,
  WRONG,
  TIMEOUT
}

enum class NavigationScreen {
  HOME,
  ARENA,
  SUMMARY,
  LEADERBOARD,
  PROFILE
}

data class OptionUiModel(
  val id: String,
  val letter: String,
  val text: String,
  val isCorrect: Boolean
)

data class QuestionUiModel(
  val id: String,
  val text: String,
  val explanation: String,
  val position: Int,
  val options: List<OptionUiModel>
)

data class QuestionResultUi(
  val questionNumber: Int,
  val questionText: String,
  val selectedLetter: String?,
  val selectedText: String?,
  val correctLetter: String,
  val correctText: String,
  val isCorrect: Boolean,
  val isTimeout: Boolean,
  val responseTimeMs: Long,
  val pointsAwarded: Int,
  val speedBonus: Int,
  val streakMultiplier: Float,
  val explanation: String
)

data class SessionSummary(
  val sessionId: String,
  val quizId: String,
  val quizTitle: String,
  val totalScore: Int,
  val correctCount: Int,
  val totalQuestions: Int,
  val accuracyPct: Int,
  val highestStreak: Int,
  val totalTimeMs: Long,
  val averageTimeMs: Long,
  val isNewPersonalBest: Boolean,
  val xpGained: Int,
  val coinsGained: Int,
  val questionResults: List<QuestionResultUi>
)

data class ActiveQuestionState(
  val question: QuestionUiModel,
  val questionIndex: Int,
  val totalQuestions: Int,
  val timeLimitSec: Int = 15,
  val timeRemainingMs: Long = 15000L,
  val selectedOptionId: String? = null,
  val outcome: AnswerOutcome = AnswerOutcome.NONE,
  val pointsEarnedThisQuestion: Int = 0,
  val speedBonus: Int = 0,
  val streakMultiplier: Float = 1.0f,
  val isAnswerLocked: Boolean = false
)
