package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.theme.AccentMint
import com.example.ui.theme.StreakFire
import com.example.ui.theme.StreakGold

@Composable
fun ProfileScreen(
  user: UserEntity?,
  isSoundEnabled: Boolean,
  isHapticEnabled: Boolean,
  onToggleSound: () -> Unit,
  onToggleHaptic: () -> Unit,
  onUpdateUsername: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showEditNameDialog by remember { mutableStateOf(false) }
  var editedName by remember(user?.username) { mutableStateOf(user?.username ?: "") }

  val scrollState = rememberScrollState()
  val level = if (user != null) (user.totalXp / 200) + 1 else 1

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("profile_back_button")
      ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
      }
      Text(
        text = "Player Profile & Achievements",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
      )
    }

    // Avatar & Name Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.size(72.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = (user?.username ?: "P").take(1).uppercase(),
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = user?.username ?: "QuizPro",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
          )
          IconButton(
            onClick = { showEditNameDialog = true },
            modifier = Modifier
              .size(32.dp)
              .testTag("edit_username_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Username",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        ) {
          Text(
            text = "LEVEL $level ARENA CHALLENGER",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Stats Grid
    Text(
      text = "Career Statistics",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ProfileStatCard(
          title = "Total Score Points",
          value = "${user?.totalXp?.times(10) ?: 0}",
          icon = Icons.Default.ElectricBolt,
          iconColor = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
          title = "Highest Streak",
          value = "${user?.highestStreak ?: 0}x",
          icon = Icons.Default.LocalFireDepartment,
          iconColor = StreakFire,
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ProfileStatCard(
          title = "Total Coins",
          value = "${user?.coins ?: 100}",
          icon = Icons.Default.EmojiEvents,
          iconColor = StreakGold,
          modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
          title = "Rounds Played",
          value = "${user?.gamesPlayed ?: 0}",
          icon = Icons.Default.Speed,
          iconColor = AccentMint,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Achievements Showcase
    Text(
      text = "Earned Trophies & Badges",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      AchievementItem(
        title = "Speed Demon",
        description = "Answered a question in under 4 seconds.",
        isUnlocked = true,
        icon = Icons.Default.Speed,
        badgeColor = AccentMint
      )
      AchievementItem(
        title = "On Fire! (3x Streak)",
        description = "Achieved a 3-question streak with 1.5x score combo.",
        isUnlocked = (user?.highestStreak ?: 0) >= 3,
        icon = Icons.Default.LocalFireDepartment,
        badgeColor = StreakFire
      )
      AchievementItem(
        title = "Arena Veteran",
        description = "Completed multiple rounds across categories.",
        isUnlocked = (user?.gamesPlayed ?: 0) >= 2,
        icon = Icons.Default.MilitaryTech,
        badgeColor = MaterialTheme.colorScheme.primary
      )
      AchievementItem(
        title = "Quiz Master (5x Streak)",
        description = "Achieved a 5-question streak with 2.0x score multiplier.",
        isUnlocked = (user?.highestStreak ?: 0) >= 5,
        icon = Icons.Default.EmojiEvents,
        badgeColor = StreakGold
      )
    }

    // Settings
    Text(
      text = "Game Settings",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
            Column {
              Text(text = "Sound Effects", fontWeight = FontWeight.SemiBold)
              Text(
                text = "Audio chimes for correct/wrong answers & timer ticks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Switch(
            checked = isSoundEnabled,
            onCheckedChange = { onToggleSound() }
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(imageVector = Icons.Default.Vibration, contentDescription = null)
            Column {
              Text(text = "Haptic Vibration", fontWeight = FontWeight.SemiBold)
              Text(
                text = "Tactile pulse feedback on button taps and results",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Switch(
            checked = isHapticEnabled,
            onCheckedChange = { onToggleHaptic() }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }

  if (showEditNameDialog) {
    AlertDialog(
      onDismissRequest = { showEditNameDialog = false },
      title = { Text(text = "Change Player Name") },
      text = {
        OutlinedTextField(
          value = editedName,
          onValueChange = { editedName = it.take(20) },
          label = { Text("Player Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("username_input_field")
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateUsername(editedName)
            showEditNameDialog = false
          },
          modifier = Modifier.testTag("save_username_button")
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditNameDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun ProfileStatCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
      Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
      Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
private fun AchievementItem(
  title: String,
  description: String,
  isUnlocked: Boolean,
  icon: ImageVector,
  badgeColor: Color
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    border = if (isUnlocked) BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)) else null,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Surface(
        shape = CircleShape,
        color = if (isUnlocked) badgeColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(44.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isUnlocked) badgeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (isUnlocked) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = AccentMint.copy(alpha = 0.2f)
            ) {
              Text(
                text = "UNLOCKED",
                style = MaterialTheme.typography.labelSmall,
                color = AccentMint,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }

        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
