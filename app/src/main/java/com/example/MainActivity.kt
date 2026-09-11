package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.model.NavigationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizArenaScreen
import com.example.ui.screens.RoundSummaryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        QuizApp()
      }
    }
  }
}

@Composable
fun QuizApp(
  quizViewModel: QuizViewModel = viewModel()
) {
  val currentScreen by quizViewModel.currentScreen.collectAsStateWithLifecycle()
  val user by quizViewModel.user.collectAsStateWithLifecycle()
  val quizzes by quizViewModel.quizzes.collectAsStateWithLifecycle()
  val activeQuiz by quizViewModel.activeQuiz.collectAsStateWithLifecycle()
  val activeQuestionState by quizViewModel.activeQuestionState.collectAsStateWithLifecycle()
  val totalScore by quizViewModel.totalScore.collectAsStateWithLifecycle()
  val currentStreak by quizViewModel.currentStreak.collectAsStateWithLifecycle()
  val sessionSummary by quizViewModel.sessionSummary.collectAsStateWithLifecycle()
  val leaderboardEntries by quizViewModel.leaderboardEntries.collectAsStateWithLifecycle()
  val leaderboardPeriod by quizViewModel.leaderboardPeriod.collectAsStateWithLifecycle()
  val leaderboardQuizFilter by quizViewModel.leaderboardQuizFilter.collectAsStateWithLifecycle()

  var soundState by remember { mutableStateOf(quizViewModel.audioHaptic.soundEnabled) }
  var hapticState by remember { mutableStateOf(quizViewModel.audioHaptic.hapticEnabled) }

  // Handle system back navigation
  BackHandler(enabled = currentScreen != NavigationScreen.HOME) {
    when (currentScreen) {
      NavigationScreen.ARENA -> {
        quizViewModel.navigateTo(NavigationScreen.HOME)
      }
      NavigationScreen.SUMMARY -> {
        quizViewModel.navigateTo(NavigationScreen.HOME)
      }
      NavigationScreen.LEADERBOARD -> {
        quizViewModel.navigateTo(NavigationScreen.HOME)
      }
      NavigationScreen.PROFILE -> {
        quizViewModel.navigateTo(NavigationScreen.HOME)
      }
      NavigationScreen.HOME -> {}
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .safeDrawingPadding()
  ) { innerPadding ->
    AnimatedContent(
      targetState = currentScreen,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "screen_transition",
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) { screen ->
      when (screen) {
        NavigationScreen.HOME -> {
          HomeScreen(
            user = user,
            quizzes = quizzes,
            isSoundEnabled = soundState,
            onToggleSound = {
              val newSound = !soundState
              soundState = newSound
              quizViewModel.audioHaptic.soundEnabled = newSound
            },
            onStartQuiz = { quiz ->
              quizViewModel.startQuiz(quiz)
            },
            onOpenLeaderboard = {
              quizViewModel.navigateTo(NavigationScreen.LEADERBOARD)
            },
            onOpenProfile = {
              quizViewModel.navigateTo(NavigationScreen.PROFILE)
            }
          )
        }

        NavigationScreen.ARENA -> {
          activeQuiz?.let { quiz ->
            QuizArenaScreen(
              quiz = quiz,
              state = activeQuestionState,
              totalScore = totalScore,
              currentStreak = currentStreak,
              onOptionSelected = { optionId ->
                quizViewModel.submitAnswer(optionId)
              },
              onNextQuestion = {
                quizViewModel.nextQuestion()
              },
              onExitGame = {
                quizViewModel.navigateTo(NavigationScreen.HOME)
              }
            )
          }
        }

        NavigationScreen.SUMMARY -> {
          RoundSummaryScreen(
            summary = sessionSummary,
            onPlayAgain = {
              activeQuiz?.let { quizViewModel.startQuiz(it) }
                ?: quizViewModel.navigateTo(NavigationScreen.HOME)
            },
            onViewLeaderboard = {
              quizViewModel.navigateTo(NavigationScreen.LEADERBOARD)
            },
            onHome = {
              quizViewModel.navigateTo(NavigationScreen.HOME)
            }
          )
        }

        NavigationScreen.LEADERBOARD -> {
          LeaderboardScreen(
            entries = leaderboardEntries,
            quizzes = quizzes,
            selectedPeriod = leaderboardPeriod,
            selectedQuizId = leaderboardQuizFilter,
            onPeriodChange = { period ->
              quizViewModel.setLeaderboardPeriod(period)
            },
            onQuizFilterChange = { quizId ->
              quizViewModel.setLeaderboardQuizFilter(quizId)
            },
            onBack = {
              quizViewModel.navigateTo(NavigationScreen.HOME)
            }
          )
        }

        NavigationScreen.PROFILE -> {
          ProfileScreen(
            user = user,
            isSoundEnabled = soundState,
            isHapticEnabled = hapticState,
            onToggleSound = {
              val newSound = !soundState
              soundState = newSound
              quizViewModel.audioHaptic.soundEnabled = newSound
            },
            onToggleHaptic = {
              val newHaptic = !hapticState
              hapticState = newHaptic
              quizViewModel.audioHaptic.hapticEnabled = newHaptic
            },
            onUpdateUsername = { newName ->
              quizViewModel.updatePlayerUsername(newName)
            },
            onBack = {
              quizViewModel.navigateTo(NavigationScreen.HOME)
            }
          )
        }
      }
    }
  }
}
