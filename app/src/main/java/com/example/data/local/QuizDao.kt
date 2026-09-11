package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

  // User
  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  fun getUser(id: String = "user_default"): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun getUserSync(id: String = "user_default"): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)

  // Quizzes
  @Query("SELECT * FROM quizzes")
  fun getAllQuizzes(): Flow<List<QuizEntity>>

  @Query("SELECT * FROM quizzes WHERE id = :id LIMIT 1")
  suspend fun getQuizById(id: String): QuizEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuizzes(quizzes: List<QuizEntity>)

  // Questions with Options
  @Transaction
  @Query("SELECT * FROM questions WHERE quizId = :quizId ORDER BY position ASC")
  suspend fun getQuestionsWithOptions(quizId: String): List<QuestionWithOptions>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuestions(questions: List<QuestionEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOptions(options: List<QuestionOptionEntity>)

  // Game Sessions
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: GameSessionEntity)

  @Query("SELECT * FROM game_sessions ORDER BY startedAt DESC LIMIT 20")
  fun getRecentSessions(): Flow<List<GameSessionEntity>>

  @Query("SELECT MAX(totalScore) FROM game_sessions WHERE quizId = :quizId")
  fun getHighScoreForQuiz(quizId: String): Flow<Int?>

  // Player Answers
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPlayerAnswer(answer: PlayerAnswerEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPlayerAnswers(answers: List<PlayerAnswerEntity>)

  @Query("SELECT * FROM player_answers WHERE sessionId = :sessionId")
  suspend fun getAnswersForSession(sessionId: String): List<PlayerAnswerEntity>

  // Leaderboard
  @Query("SELECT * FROM leaderboards WHERE quizId = :quizId AND period = :period ORDER BY highScore DESC, totalTimeMs ASC LIMIT 50")
  fun getLeaderboard(quizId: String, period: String): Flow<List<LeaderboardEntryEntity>>

  @Query("SELECT * FROM leaderboards WHERE period = :period ORDER BY highScore DESC, totalTimeMs ASC LIMIT 50")
  fun getGlobalLeaderboard(period: String): Flow<List<LeaderboardEntryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLeaderboardEntry(entry: LeaderboardEntryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)

  @Query("SELECT COUNT(*) FROM quizzes")
  suspend fun getQuizCount(): Int
}
