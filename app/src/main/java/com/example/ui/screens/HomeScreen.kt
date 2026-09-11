package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuizEntity
import com.example.data.local.UserEntity
import com.example.ui.components.DifficultyBadge
import com.example.ui.theme.AccentMint
import com.example.ui.theme.StreakFire
import com.example.ui.theme.StreakGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  user: UserEntity?,
  quizzes: List<QuizEntity>,
  isSoundEnabled: Boolean,
  onToggleSound: () -> Unit,
  onStartQuiz: (QuizEntity) -> Unit,
  onOpenLeaderboard: () -> Unit,
  onOpenProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val level = if (user != null) (user.totalXp / 200) + 1 else 1
  val xpInCurrentLevel = if (user != null) user.totalXp % 200 else 0
  val levelProgress = (xpInCurrentLevel / 200f).coerceIn(0f, 1f)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App title with Target Quiz emblem
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = "🎯", fontSize = 20.sp)
            }
          }
          Column {
            Text(
              text = "Quiz Arena",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Speed Trivia & Combos",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Action Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onToggleSound,
            modifier = Modifier.testTag("sound_toggle_button")
          ) {
            Icon(
              imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
              contentDescription = "Toggle Audio",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = onOpenLeaderboard,
            modifier = Modifier.testTag("home_leaderboard_button")
          ) {
            Icon(
              imageVector = Icons.Default.Leaderboard,
              contentDescription = "Leaderboard",
              tint = MaterialTheme.colorScheme.primary
            )
          }

          IconButton(
            onClick = onOpenProfile,
            modifier = Modifier.testTag("home_profile_button")
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Profile",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    // Player Status Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenProfile() }
          .testTag("player_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(42.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = (user?.username ?: "P").take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }

              Column {
                Text(
                  text = user?.username ?: "QuizPro",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Level $level Challenger",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            // Coins & Best Streak Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = StreakGold.copy(alpha = 0.15f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text(text = "🪙", fontSize = 12.sp)
                  Text(
                    text = "${user?.coins ?: 100}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = StreakGold
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = StreakFire.copy(alpha = 0.15f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = StreakFire,
                    modifier = Modifier.size(14.dp)
                  )
                  Text(
                    text = "${user?.highestStreak ?: 0}x",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = StreakFire
                  )
                }
              }
            }
          }

          // XP Progress Bar
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "${user?.totalXp ?: 0} XP total",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "$xpInCurrentLevel / 200 XP to Level ${level + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            LinearProgressIndicator(
              progress = { levelProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
          }
        }
      }
    }

    // Daily Speed Blitz Hero Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_banner_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(
                  MaterialTheme.colorScheme.primary,
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.82f)
                )
              )
            )
            .padding(20.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = AccentMint
              ) {
                Text(
                  text = "DAILY ARENA",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.Black,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }

              Text(
                text = "⚡ SPEED BONUS ON",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
              )
            }

            Text(
              text = "15-Second Blitz: Race the Clock!",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Text(
              text = "Earn up to +50 speed points per question and trigger a 2x combo with 5 consecutive answers.",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.85f),
              lineHeight = 20.sp
            )

            Button(
              onClick = {
                val featuredQuiz = quizzes.firstOrNull()
                if (featuredQuiz != null) onStartQuiz(featuredQuiz)
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
              ),
              modifier = Modifier
                .padding(top = 4.dp)
                .testTag("quick_play_button")
            ) {
              Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Start Speed Round",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Quiz Categories Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Quiz Arenas",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "${quizzes.size} Available",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Quizzes List
    items(quizzes) { quiz ->
      QuizItemCard(
        quiz = quiz,
        onStart = { onStartQuiz(quiz) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun QuizItemCard(
  quiz: QuizEntity,
  onStart: () -> Unit
) {
  val icon: ImageVector = when (quiz.iconName) {
    "science" -> Icons.Default.Science
    "history" -> Icons.Default.History
    "public" -> Icons.Default.Public
    "movie" -> Icons.Default.Movie
    else -> Icons.Default.Psychology
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onStart() }
      .testTag("quiz_card_${quiz.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Category Icon
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        modifier = Modifier.size(50.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
          )
        }
      }

      // Quiz Details
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          DifficultyBadge(difficulty = quiz.difficulty)

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${quiz.timeLimitSec}s/Q",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Text(
          text = quiz.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = quiz.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2
        )
      }

      // Play button
      Button(
        onClick = onStart,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .size(44.dp)
          .testTag("play_quiz_button_${quiz.id}"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Play ${quiz.title}",
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}
