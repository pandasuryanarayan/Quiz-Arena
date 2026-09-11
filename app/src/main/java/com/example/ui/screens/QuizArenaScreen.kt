package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuizEntity
import com.example.ui.components.CountdownTimerBar
import com.example.ui.components.ScoreBadge
import com.example.ui.components.StreakBadge
import com.example.ui.model.ActiveQuestionState
import com.example.ui.model.AnswerOutcome
import com.example.ui.model.OptionUiModel
import com.example.ui.theme.AccentMint
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.CorrectGreenBg
import com.example.ui.theme.StreakFire
import com.example.ui.theme.TimerWarning
import com.example.ui.theme.WrongRed
import com.example.ui.theme.WrongRedBg

@Composable
fun QuizArenaScreen(
  quiz: QuizEntity,
  state: ActiveQuestionState?,
  totalScore: Int,
  currentStreak: Int,
  onOptionSelected: (String) -> Unit,
  onNextQuestion: () -> Unit,
  onExitGame: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showExitConfirmDialog by remember { mutableStateOf(false) }

  if (state == null) {
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "Loading question...",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    return
  }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Bar: Score, Arena Title, Streak
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      ScoreBadge(score = totalScore)

      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "🎯",
            fontSize = 14.sp
          )
          Text(
            text = "QUIZ ARENA",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      StreakBadge(
        streak = currentStreak,
        multiplier = state.streakMultiplier
      )
    }

    // Category and Question count banner
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
      ) {
        Text(
          text = "QUESTION ${state.questionIndex + 1} OF ${state.totalQuestions}",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }

      Text(
        text = quiz.category.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp
      )
    }

    // 15s Countdown Timer Bar
    CountdownTimerBar(
      timeRemainingMs = state.timeRemainingMs,
      timeLimitSec = state.timeLimitSec
    )

    // Main Question Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("question_card"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = state.question.text,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          lineHeight = 26.sp
        )
      }
    }

    // Question Options List (A, B, C, D)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      state.question.options.forEach { option ->
        OptionButton(
          option = option,
          isSelected = state.selectedOptionId == option.id,
          isAnswerLocked = state.isAnswerLocked,
          outcome = state.outcome,
          onSelect = { onOptionSelected(option.id) }
        )
      }
    }

    // Immediate Feedback Banner
    AnimatedVisibility(
      visible = state.isAnswerLocked,
      enter = fadeIn() + slideInVertically()
    ) {
      FeedbackSection(
        state = state,
        onNext = onNextQuestion
      )
    }

    // Bottom Navigation row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = { showExitConfirmDialog = true },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("exit_game_button")
      ) {
        Icon(
          imageVector = Icons.Default.ExitToApp,
          contentDescription = "Exit Game",
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Exit Game")
      }

      if (state.isAnswerLocked) {
        Button(
          onClick = onNextQuestion,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier.testTag("next_question_button")
        ) {
          Text(text = "Next Question")
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next Question",
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }

  if (showExitConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showExitConfirmDialog = false },
      title = { Text(text = "Quit Quiz Round?") },
      text = { Text(text = "Are you sure you want to abandon the current quiz? Progress will not be counted on the leaderboard.") },
      confirmButton = {
        Button(
          onClick = {
            showExitConfirmDialog = false
            onExitGame()
          },
          colors = ButtonDefaults.buttonColors(containerColor = WrongRed)
        ) {
          Text("Quit Round")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExitConfirmDialog = false }) {
          Text("Keep Playing")
        }
      }
    )
  }
}

@Composable
private fun OptionButton(
  option: OptionUiModel,
  isSelected: Boolean,
  isAnswerLocked: Boolean,
  outcome: AnswerOutcome,
  onSelect: () -> Unit
) {
  // Determine color states based on outcome and selection
  val isCorrectOption = option.isCorrect
  val isThisSelectedAndWrong = isSelected && !isCorrectOption
  val isThisSelectedAndCorrect = isSelected && isCorrectOption
  val shouldHighlightCorrectAnswer = isAnswerLocked && isCorrectOption

  val containerColor by animateColorAsState(
    targetValue = when {
      isThisSelectedAndCorrect -> CorrectGreen.copy(alpha = 0.16f)
      isThisSelectedAndWrong -> WrongRed.copy(alpha = 0.16f)
      shouldHighlightCorrectAnswer -> CorrectGreen.copy(alpha = 0.12f)
      else -> MaterialTheme.colorScheme.surface
    },
    animationSpec = spring(),
    label = "option_bg"
  )

  val borderColor by animateColorAsState(
    targetValue = when {
      isThisSelectedAndCorrect -> CorrectGreen
      isThisSelectedAndWrong -> WrongRed
      shouldHighlightCorrectAnswer -> CorrectGreen
      else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    },
    animationSpec = spring(),
    label = "option_border"
  )

  val letterBgColor = when {
    isThisSelectedAndCorrect || shouldHighlightCorrectAnswer -> CorrectGreen
    isThisSelectedAndWrong -> WrongRed
    else -> MaterialTheme.colorScheme.primaryContainer
  }

  val letterTextColor = when {
    isThisSelectedAndCorrect || shouldHighlightCorrectAnswer || isThisSelectedAndWrong -> Color.White
    else -> MaterialTheme.colorScheme.primary
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(enabled = !isAnswerLocked) { onSelect() }
      .testTag("option_${option.letter}"),
    shape = RoundedCornerShape(16.dp),
    color = containerColor,
    border = BorderStroke(if (isSelected || shouldHighlightCorrectAnswer) 2.dp else 1.dp, borderColor),
    shadowElevation = if (isSelected) 3.dp else 0.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Letter Badge [ A ]
      Surface(
        shape = CircleShape,
        color = letterBgColor,
        modifier = Modifier.size(36.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          if (shouldHighlightCorrectAnswer && !isThisSelectedAndWrong) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Correct",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          } else if (isThisSelectedAndWrong) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Wrong",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          } else {
            Text(
              text = option.letter,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = letterTextColor
            )
          }
        }
      }

      // Option text
      Text(
        text = option.text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = if (isSelected || shouldHighlightCorrectAnswer) FontWeight.SemiBold else FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(1f)
      )

      // Outcome badge indicator
      if (isThisSelectedAndCorrect) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CorrectGreen
        ) {
          Text(
            text = "SELECTED ✓",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      } else if (isThisSelectedAndWrong) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = WrongRed
        ) {
          Text(
            text = "WRONG ✗",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      } else if (shouldHighlightCorrectAnswer && !isSelected) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CorrectGreen
        ) {
          Text(
            text = "CORRECT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun FeedbackSection(
  state: ActiveQuestionState,
  onNext: () -> Unit
) {
  val (cardColor, borderColor, title, pointsLabel) = when (state.outcome) {
    AnswerOutcome.CORRECT -> {
      val bonusText = if (state.speedBonus > 0) " + ${state.speedBonus} Speed Bonus" else ""
      val multText = if (state.streakMultiplier > 1.0f) " (x${state.streakMultiplier} Streak!)" else ""
      Quadruple(
        CorrectGreen.copy(alpha = 0.1f),
        CorrectGreen,
        "Correct Answer! 🎉",
        "+${state.pointsEarnedThisQuestion} Pts (+100 Base$bonusText)$multText"
      )
    }
    AnswerOutcome.WRONG -> {
      Quadruple(
        WrongRed.copy(alpha = 0.1f),
        WrongRed,
        "Incorrect 😕",
        "Streak reset to 0 • 0 Pts"
      )
    }
    AnswerOutcome.TIMEOUT -> {
      Quadruple(
        TimerWarning.copy(alpha = 0.12f),
        TimerWarning,
        "Time's Up! ⏱️",
        "0 Pts awarded • Keep your speed up!"
      )
    }
    else -> Quadruple(Color.Transparent, Color.Transparent, "", "")
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("feedback_section"),
    shape = RoundedCornerShape(16.dp),
    color = cardColor,
    border = BorderStroke(1.5.dp, borderColor)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = borderColor
        )

        Text(
          text = pointsLabel,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      // Explanation
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = state.question.explanation,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 20.sp
        )
      }
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
