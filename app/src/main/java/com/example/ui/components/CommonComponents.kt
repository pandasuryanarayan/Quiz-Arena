package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentMint
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.StreakFire
import com.example.ui.theme.StreakGold
import com.example.ui.theme.TimerCritical
import com.example.ui.theme.TimerWarning
import com.example.ui.theme.WrongRed

@Composable
fun CountdownTimerBar(
  timeRemainingMs: Long,
  timeLimitSec: Int,
  modifier: Modifier = Modifier
) {
  val totalMs = (timeLimitSec * 1000L).coerceAtLeast(1000L)
  val progress = (timeRemainingMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
  val secondsLeft = (timeRemainingMs / 1000L) + if (timeRemainingMs % 1000L > 0) 1 else 0

  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 60),
    label = "timer_progress"
  )

  val barColor by animateColorAsState(
    targetValue = when {
      progress > 0.45f -> AccentMint
      progress > 0.22f -> TimerWarning
      else -> TimerCritical
    },
    animationSpec = spring(),
    label = "timer_color"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (progress <= 0.22f && progress > 0f) 1.08f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Timer,
          contentDescription = null,
          tint = barColor,
          modifier = Modifier
            .size(16.dp)
            .scale(pulseScale)
        )
        Text(
          text = "COUNTDOWN",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      Surface(
        color = barColor.copy(alpha = 0.16f),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = "${secondsLeft}s",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = barColor,
          modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .scale(pulseScale)
            .testTag("countdown_seconds_text")
        )
      }
    }

    // Progress track & fill
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .testTag("timer_countdown_bar")
    ) {
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(animatedProgress)
          .clip(RoundedCornerShape(5.dp))
          .background(
            Brush.horizontalGradient(
              listOf(
                barColor,
                barColor.copy(alpha = 0.85f)
              )
            )
          )
      )
    }
  }
}

@Composable
fun StreakBadge(
  streak: Int,
  multiplier: Float,
  modifier: Modifier = Modifier
) {
  val isCombActive = streak >= 3
  val infiniteTransition = rememberInfiniteTransition(label = "streak_flame")
  val flameScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isCombActive) 1.15f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(500),
      repeatMode = RepeatMode.Reverse
    ),
    label = "flame_scale"
  )

  Surface(
    modifier = modifier.testTag("streak_badge"),
    shape = RoundedCornerShape(16.dp),
    color = if (isCombActive) StreakFire.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
    border = if (isCombActive) androidx.compose.foundation.BorderStroke(1.5.dp, StreakFire) else null
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.LocalFireDepartment,
        contentDescription = "Streak Fire",
        tint = if (isCombActive) StreakFire else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
          .size(18.dp)
          .scale(flameScale)
      )
      Text(
        text = if (streak > 0) "${streak}x" else "0x",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = if (isCombActive) StreakFire else MaterialTheme.colorScheme.onSurface
      )
      if (multiplier > 1.0f) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = StreakGold
        ) {
          Text(
            text = "${multiplier}x",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
    }
  }
}

@Composable
fun ScoreBadge(
  score: Int,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.testTag("score_badge"),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.primaryContainer
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.ElectricBolt,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = "$score",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        text = "pts",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
      )
    }
  }
}

@Composable
fun DifficultyBadge(
  difficulty: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, label) = when (difficulty.lowercase()) {
    "easy" -> Triple(CorrectGreen.copy(alpha = 0.15f), CorrectGreen, "EASY")
    "hard" -> Triple(WrongRed.copy(alpha = 0.15f), WrongRed, "HARD")
    else -> Triple(TimerWarning.copy(alpha = 0.15f), TimerWarning, "MEDIUM")
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = bgColor
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      color = textColor,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
  }
}
