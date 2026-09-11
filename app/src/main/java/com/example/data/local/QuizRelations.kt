package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class QuestionWithOptions(
  @Embedded val question: QuestionEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "questionId"
  )
  val options: List<QuestionOptionEntity>
)

data class QuizWithQuestions(
  @Embedded val quiz: QuizEntity,
  @Relation(
    entity = QuestionEntity::class,
    parentColumn = "id",
    entityColumn = "quizId"
  )
  val questions: List<QuestionWithOptions>
)
