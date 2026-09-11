package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.SessionSummary
import com.example.ui.theme.AccentMint
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.StreakFire
import com.example.ui.theme.StreakGold
import com.example.ui.theme.WrongRed

@Composable
fun RoundSummaryScreen(
  summary: SessionSummary?,
  onPlayAgain: () -> Unit,
  onViewLeaderboard: () -> Unit,
  onHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isReviewExpanded by remember { mutableStateOf(false) }

  if (summary == null) {
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text("No session summary available.")
    }
    return
  }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Victory Header Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("summary_header_card"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                Color.Transparent
              )
            )
          )
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Trophy Icon
          Surface(
            shape = CircleShape,
            color = StreakGold.copy(alpha = 0.2f),
            modifier = Modifier.size(68.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = StreakGold,
                modifier = Modifier.size(38.dp)
              )
            }
          }

          Text(
            text = "Round Complete!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = summary.quizTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Final Score Display
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 6.dp)
          ) {
            Text(
              text = "FINAL SCORE",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${summary.totalScore}",
              style = MaterialTheme.typography.displayLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.testTag("summary_final_score")
            )
          }

          // Personal Best Badge
          if (summary.isNewPersonalBest) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = AccentMint.copy(alpha = 0.18f),
              border = BorderStroke(1.dp, AccentMint)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.WorkspacePremium,
                  contentDescription = null,
                  tint = AccentMint,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "New Personal Best! 🌟",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  color = AccentMint
                )
              }
            }
          }
        }
      }
    }

    // 4 Key Metric Cards (2x2 Grid)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricTile(
          icon = Icons.Default.Check,
          iconColor = CorrectGreen,
          title = "Accuracy",
          value = "${summary.accuracyPct}%",
          subValue = "${summary.correctCount}/${summary.totalQuestions} Correct",
          modifier = Modifier.weight(1f)
        )
        MetricTile(
          icon = Icons.Default.LocalFireDepartment,
          iconColor = StreakFire,
          title = "Best Streak",
          value = "${summary.highestStreak}x",
          subValue = if (summary.highestStreak >= 3) "Combo Multiplier!" else "Nice effort!",
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val avgSec = String.format("%.1f", summary.averageTimeMs / 1000f)
        MetricTile(
          icon = Icons.Default.Speed,
          iconColor = MaterialTheme.colorScheme.primary,
          title = "Avg Speed",
          value = "${avgSec}s",
          subValue = "Per Question",
          modifier = Modifier.weight(1f)
        )
        MetricTile(
          icon = Icons.Default.ElectricBolt,
          iconColor = StreakGold,
          title = "Rewards",
          value = "+${summary.xpGained} XP",
          subValue = "+${summary.coinsGained} Coins",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Expandable Question Review
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isReviewExpanded = !isReviewExpanded },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Question Review",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "${summary.questionResults.size} Qs",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Icon(
            imageVector = if (isReviewExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Expand Review"
          )
        }

        AnimatedVisibility(visible = isReviewExpanded) {
          Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            summary.questionResults.forEach { result ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                  1.dp,
                  if (result.isCorrect) CorrectGreen.copy(alpha = 0.4f) else WrongRed.copy(alpha = 0.4f)
                )
              ) {
                Column(
                  modifier = Modifier.padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "Q${result.questionNumber}: ${if (result.isCorrect) "Correct ✓" else if (result.isTimeout) "Timeout ⏱️" else "Wrong ✗"}",
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold,
                      color = if (result.isCorrect) CorrectGreen else WrongRed
                    )
                    Text(
                      text = "+${result.pointsAwarded} pts",
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }

                  Text(
                    text = result.questionText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Text(
                    text = "Correct answer: ${result.correctText}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CorrectGreen,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Action CTAs
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = onPlayAgain,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("play_again_button")
      ) {
        Icon(imageVector = Icons.Default.Replay, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Play Again",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onViewLeaderboard,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("summary_leaderboard_button")
        ) {
          Icon(imageVector = Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Leaderboard", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
          onClick = {
            val shareIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(
                Intent.EXTRA_TEXT,
                "⚡ I just scored ${summary.totalScore} pts with ${summary.accuracyPct}% accuracy on '${summary.quizTitle}' in Quiz Arena! Can you beat my high score?"
              )
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Score"))
          },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("share_score_button")
        ) {
          Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Share Score", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      Button(
        onClick = onHome,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("return_home_button")
      ) {
        Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Back to Quiz Hub", fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@Composable
private fun MetricTile(
  icon: ImageVector,
  iconColor: Color,
  title: String,
  value: String,
  subValue: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.SemiBold
        )
      }

      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = subValue,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
