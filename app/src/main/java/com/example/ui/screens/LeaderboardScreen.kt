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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.QuizEntity
import com.example.ui.theme.AccentMint
import com.example.ui.theme.StreakFire
import com.example.ui.theme.StreakGold

@Composable
fun LeaderboardScreen(
  entries: List<LeaderboardEntryEntity>,
  quizzes: List<QuizEntity>,
  selectedPeriod: String,
  selectedQuizId: String?,
  onPeriodChange: (String) -> Unit,
  onQuizFilterChange: (String?) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val periods = listOf("DAILY", "WEEKLY", "ALL_TIME")
  val selectedTabIndex = periods.indexOf(selectedPeriod).coerceAtLeast(0)

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("leaderboard_back_button")
      ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
      }
      Text(
        text = "Global Leaderboards",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
      )
    }

    // Period Tabs (Daily / Weekly / All-Time)
    TabRow(
      selectedTabIndex = selectedTabIndex,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
          color = MaterialTheme.colorScheme.primary
        )
      }
    ) {
      periods.forEachIndexed { index, period ->
        val label = when (period) {
          "DAILY" -> "Daily"
          "WEEKLY" -> "Weekly"
          else -> "All-Time"
        }
        Tab(
          selected = selectedTabIndex == index,
          onClick = { onPeriodChange(period) },
          text = {
            Text(
              text = label,
              fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
            )
          },
          modifier = Modifier.testTag("tab_${period.lowercase()}")
        )
      }
    }

    // Category / Quiz horizontal filter chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        FilterChip(
          selected = selectedQuizId == null,
          onClick = { onQuizFilterChange(null) },
          label = { Text("All Categories") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.primary
          )
        )
      }
      items(quizzes) { quiz ->
        FilterChip(
          selected = selectedQuizId == quiz.id,
          onClick = { onQuizFilterChange(quiz.id) },
          label = { Text(quiz.title.replace(" Arena", "").replace(" Challenge", "")) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.primary
          )
        )
      }
    }

    if (entries.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "No rankings yet for this period.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Play a round to claim the top spot!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Top 3 Podium
        if (entries.size >= 3) {
          item {
            PodiumSection(
              first = entries[0],
              second = entries[1],
              third = entries[2]
            )
            Spacer(modifier = Modifier.height(8.dp))
          }
        }

        // List remaining ranks
        val startIndex = if (entries.size >= 3) 3 else 0
        itemsIndexed(entries.subList(startIndex, entries.size)) { subIndex, entry ->
          val rank = startIndex + subIndex + 1
          LeaderboardRow(rank = rank, entry = entry)
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
private fun PodiumSection(
  first: LeaderboardEntryEntity,
  second: LeaderboardEntryEntity,
  third: LeaderboardEntryEntity
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "TOP PODIUM",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
      ) {
        // 2nd Place
        PodiumPillar(
          rank = 2,
          entry = second,
          badgeColor = Color(0xFFC0C0C0), // Silver
          pillarHeight = 90.dp
        )

        // 1st Place (Tallest)
        PodiumPillar(
          rank = 1,
          entry = first,
          badgeColor = StreakGold, // Gold
          pillarHeight = 120.dp
        )

        // 3rd Place
        PodiumPillar(
          rank = 3,
          entry = third,
          badgeColor = Color(0xFFCD7F32), // Bronze
          pillarHeight = 75.dp
        )
      }
    }
  }
}

@Composable
private fun PodiumPillar(
  rank: Int,
  entry: LeaderboardEntryEntity,
  badgeColor: Color,
  pillarHeight: androidx.compose.ui.unit.Dp
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.width(90.dp)
  ) {
    // Rank Trophy Icon
    Surface(
      shape = CircleShape,
      color = badgeColor.copy(alpha = 0.2f),
      border = BorderStroke(1.5.dp, badgeColor),
      modifier = Modifier.size(36.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = "#$rank",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = badgeColor
        )
      }
    }

    Text(
      text = entry.username,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Bold,
      color = if (entry.isLocalPlayer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
      maxLines = 1
    )

    Text(
      text = "${entry.highScore} pts",
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.ExtraBold,
      color = MaterialTheme.colorScheme.primary
    )

    // Pillar Block
    Surface(
      shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
      color = badgeColor.copy(alpha = 0.15f),
      border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f)),
      modifier = Modifier
        .fillMaxWidth()
        .height(pillarHeight)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
      ) {
        Text(
          text = "${entry.accuracyPct}%",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
private fun LeaderboardRow(
  rank: Int,
  entry: LeaderboardEntryEntity
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (entry.isLocalPlayer) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
    border = if (entry.isLocalPlayer) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("leaderboard_row_$rank")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Rank circle
      Text(
        text = "#$rank",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(32.dp)
      )

      // Avatar initial
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(36.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = entry.username.take(1).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      // Username + Local indicator
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = entry.username,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (entry.isLocalPlayer) FontWeight.Bold else FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (entry.isLocalPlayer) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primary
            ) {
              Text(
                text = "YOU",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Acc: ${entry.accuracyPct}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${entry.totalTimeMs / 1000}s total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // High Score
      Text(
        text = "${entry.highScore}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}
