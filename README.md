# Quiz Arena 🎯

**Quiz Arena** is an arcade-style, fast-paced trivia quiz game built with modern Android, Kotlin, Jetpack Compose, and Room Database. Test your knowledge across multiple categories, beat the countdown timer, rack up streak multipliers, and climb to the top of the global leaderboards.

---

## ✨ Features

- **⏱️ 15-Second Countdown Blitz**:
  - Live animated countdown bar that shifts dynamically from mint green to warning amber and critical pulsing red.
  - Audible countdown tick warnings in the final 3 seconds.

- **⚡ Dynamic Speed Bonus & Streak Combos**:
  - Earn base points (100 pts) plus up to **+50 Speed Bonus** points for answering fast.
  - Chain consecutive correct answers to unlock **Combo Multipliers** (3x streak = **1.5x score**, 5x streak = **2.0x score**).
  - Reset streak on incorrect answer or timeout.

- **🎯 Rich Question Bank & Diverse Categories**:
  - Pre-seeded with 40 questions across 5 arenas:
    - 🧪 **Science & Tech** (Physics, astronomy, biology, computing)
    - 🏛️ **History Buff** (Ancient civilizations, world milestones)
    - 🌍 **Globe Trotter** (World geography, landmarks, capitals)
    - 🎬 **Pop Culture & Cinema** (Movies, music, Oscars, gaming)
    - ⚡ **Ultimate Brain Sprint** (Mixed high-speed trivia)

- **🏆 Global & Category Leaderboards**:
  - Filter scores by **Daily**, **Weekly**, and **All-Time** timeframes.
  - Filter by specific quiz categories or view overall rankings.
  - Interactive **Top 3 Podium** displaying Gold, Silver, and Bronze rankings.
  - Local player rank highlighting with comparison stats (accuracy, total response time).

- **📊 Comprehensive Round Summary & Review**:
  - Detailed performance breakdown: Final score, accuracy %, highest streak combo, average response speed, and XP/Coins gained.
  - Personal Best detection and celebrations.
  - Expandable question-by-question review showing your choice, correct answer, points awarded, and educational explanations.
  - Quick social sharing intent to challenge friends.

- **👤 Player Profile & Career Trophy Shelf**:
  - Player progression system with XP levels and collectible coins.
  - Editable player username synced directly with leaderboard entries.
  - Career achievements shelf (Speed Demon, On Fire!, Arena Veteran, Quiz Master).
  - Toggles for sound effects and haptic vibration feedback.

- **🔊 Audio & Haptic Feedback**:
  - Responsive sound feedback via Android's `ToneGenerator` for correct/wrong answers, timeouts, streak milestones, and victories.
  - Tactile haptic feedback for taps and state transitions.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) with SQLite
- **Concurrency**: Kotlin Coroutines & Reactive `StateFlow`
- **Audio & Haptics**: Native Android `ToneGenerator` & `Vibrator` / `VibrationEffect`
- **Build System**: Gradle Kotlin DSL (`.gradle.kts`) with Version Catalog (`libs.versions.toml`)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17 or higher
- Android SDK Platform 35 (compileSdk 35, minSdk 24)

### Building and Running
1. Clone the repository or open the project folder in Android Studio.
2. Ensure Gradle sync completes successfully.
3. Select an emulator or physical device running Android 7.0+ (API 24+).
4. Click **Run > Run 'app'** or build via terminal:
   ```bash
   ./gradlew assembleDebug
   ```

### Running Tests
To run unit and Robolectric tests:
```bash
./gradlew testDebugUnitTest
```

---

## 📂 Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt               # Main entry point & screen navigation controller
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt        # Room database definition & versioning
│   │   ├── QuizDao.kt            # Room DAOs for questions, sessions, and leaderboards
│   │   └── QuizEntities.kt       # Room Entities (User, Quiz, Question, Leaderboard, etc.)
│   └── repository/
│       └── QuizRepository.kt     # Repository coordinating DB access & pre-seeded trivia
├── ui/
│   ├── components/
│   │   └── CommonComponents.kt   # CountdownTimerBar, StreakBadge, ScoreBadge, DifficultyBadge
│   ├── model/
│   │   └── QuizModels.kt         # UI models, states, and screen navigation enums
│   ├── screens/
│   │   ├── HomeScreen.kt         # Arena selection, player card & daily blitz hero banner
│   │   ├── QuizArenaScreen.kt    # Active question view, options, countdown & feedback
│   │   ├── RoundSummaryScreen.kt # Victory card, accuracy metrics, question review & share
│   │   ├── LeaderboardScreen.kt  # Podium, tabs (Daily/Weekly/All-Time) & rankings list
│   │   └── ProfileScreen.kt      # Player stats, trophies, settings & username edit
│   ├── theme/
│   │   ├── Color.kt              # Material 3 color definitions & palette
│   │   └── Theme.kt              # Theme setup and dynamic color handling
│   └── viewmodel/
│       └── QuizViewModel.kt      # Game engine, timer coroutines, and state management
└── util/
    └── AudioHapticManager.kt     # ToneGenerator synthesizers and haptic feedback
```

---