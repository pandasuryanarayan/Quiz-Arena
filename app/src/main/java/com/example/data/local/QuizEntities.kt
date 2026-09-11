package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val id: String = "user_default",
  val username: String = "Player 1",
  val totalXp: Int = 240,
  val coins: Int = 150,
  val highestStreak: Int = 0,
  val gamesPlayed: Int = 0,
  val totalCorrectAnswers: Int = 0
)

@Entity(tableName = "quizzes")
data class QuizEntity(
  @PrimaryKey val id: String,
  val title: String,
  val category: String,
  val difficulty: String = "medium", // easy, medium, hard
  val timeLimitSec: Int = 15,
  val description: String = "",
  val iconName: String = "science",
  val questionCount: Int = 8
)

@Entity(
  tableName = "questions",
  foreignKeys = [
    ForeignKey(
      entity = QuizEntity::class,
      parentColumns = ["id"],
      childColumns = ["quizId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("quizId")]
)
data class QuestionEntity(
  @PrimaryKey val id: String,
  val quizId: String,
  val questionText: String,
  val explanation: String,
  val pointsBase: Int = 100,
  val position: Int = 1
)

@Entity(
  tableName = "question_options",
  foreignKeys = [
    ForeignKey(
      entity = QuestionEntity::class,
      parentColumns = ["id"],
      childColumns = ["questionId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("questionId")]
)
data class QuestionOptionEntity(
  @PrimaryKey val id: String,
  val questionId: String,
  val optionLetter: String, // "A", "B", "C", "D"
  val optionText: String,
  val isCorrect: Boolean
)

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
  @PrimaryKey val id: String,
  val quizId: String,
  val quizTitle: String,
  val totalScore: Int,
  val correctCount: Int,
  val totalQuestions: Int,
  val highestStreak: Int,
  val status: String = "COMPLETED", // IN_PROGRESS, COMPLETED
  val startedAt: Long,
  val completedAt: Long?,
  val averageResponseTimeMs: Long
)

@Entity(
  tableName = "player_answers",
  foreignKeys = [
    ForeignKey(
      entity = GameSessionEntity::class,
      parentColumns = ["id"],
      childColumns = ["sessionId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("sessionId"), Index("questionId")]
)
data class PlayerAnswerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: String,
  val questionId: String,
  val selectedOptionId: String?,
  val isCorrect: Boolean,
  val responseTimeMs: Long,
  val pointsAwarded: Int,
  val speedBonus: Int,
  val streakMultiplier: Float
)

@Entity(
  tableName = "leaderboards",
  indices = [Index(value = ["quizId", "period", "highScore"])]
)
data class LeaderboardEntryEntity(
  @PrimaryKey val id: String,
  val quizId: String,
  val username: String,
  val highScore: Int,
  val accuracyPct: Int,
  val period: String, // "DAILY", "WEEKLY", "ALL_TIME"
  val totalTimeMs: Long,
  val isLocalPlayer: Boolean = false,
  val avatarSeed: String = "1",
  val updatedAt: Long = System.currentTimeMillis()
)
