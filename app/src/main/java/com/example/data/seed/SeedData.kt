package com.example.data.seed

import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.QuestionOptionEntity
import com.example.data.local.QuizEntity
import com.example.data.local.UserEntity

object SeedData {

  val defaultUser = UserEntity(
    id = "user_default",
    username = "QuizPro",
    totalXp = 380,
    coins = 120,
    highestStreak = 4,
    gamesPlayed = 3,
    totalCorrectAnswers = 18
  )

  val quizzes = listOf(
    QuizEntity(
      id = "quiz_science",
      title = "Science & Tech Arena",
      category = "Science & Tech",
      difficulty = "medium",
      timeLimitSec = 15,
      description = "Fast-paced questions on astrophysics, cellular biology, computing, and quantum mechanics.",
      iconName = "science",
      questionCount = 8
    ),
    QuizEntity(
      id = "quiz_history",
      title = "History Buff Blitz",
      category = "History & Culture",
      difficulty = "medium",
      timeLimitSec = 15,
      description = "Race through ancient empires, pivotal battles, revolutions, and legendary figures.",
      iconName = "history",
      questionCount = 8
    ),
    QuizEntity(
      id = "quiz_geography",
      title = "Globe Trotter Challenge",
      category = "World Geography",
      difficulty = "easy",
      timeLimitSec = 15,
      description = "Test your navigational instinct with world capitals, extreme terrain, and natural wonders.",
      iconName = "public",
      questionCount = 8
    ),
    QuizEntity(
      id = "quiz_popculture",
      title = "Pop Culture & Cinema",
      category = "Entertainment",
      difficulty = "easy",
      timeLimitSec = 15,
      description = "Speed round trivia covering box office blockbusters, legendary music, and streaming hits.",
      iconName = "movie",
      questionCount = 8
    ),
    QuizEntity(
      id = "quiz_general",
      title = "Ultimate Brain Sprint",
      category = "General Knowledge",
      difficulty = "hard",
      timeLimitSec = 15,
      description = "High-stakes multi-discipline trivia designed to push your reaction speed to the limit.",
      iconName = "psychology",
      questionCount = 8
    )
  )

  fun getQuestionsAndOptions(): Pair<List<QuestionEntity>, List<QuestionOptionEntity>> {
    val questions = mutableListOf<QuestionEntity>()
    val options = mutableListOf<QuestionOptionEntity>()

    data class QData(
      val qId: String,
      val quizId: String,
      val text: String,
      val explanation: String,
      val pos: Int,
      val optA: String,
      val optB: String,
      val optC: String,
      val optD: String,
      val correctLetter: String
    )

    val rawList = listOf(
      // Science & Tech
      QData(
        "q_sci_1", "quiz_science",
        "What cellular organelle is commonly referred to as the 'powerhouse of the cell'?",
        "Mitochondria generate most of the chemical energy (ATP) needed by eukaryotic cells.",
        1, "Ribosome", "Mitochondria", "Endoplasmic Reticulum", "Golgi Apparatus", "B"
      ),
      QData(
        "q_sci_2", "quiz_science",
        "Which particle has no electric charge and resides in the atomic nucleus?",
        "Neutrons are subatomic particles with zero net electric charge, discovered by James Chadwick.",
        2, "Proton", "Positron", "Neutron", "Electron", "C"
      ),
      QData(
        "q_sci_3", "quiz_science",
        "What is the fastest possible speed in the universe according to special relativity?",
        "Light travels at approximately 299,792,458 meters per second in a vacuum.",
        3, "Speed of sound in helium", "Speed of light in vacuum", "Tachyon propagation", "Gravitational escape velocity", "B"
      ),
      QData(
        "q_sci_4", "quiz_science",
        "In computer science, what is the time complexity of binary search on a sorted array of N elements?",
        "Binary search halves the search range at each step, yielding O(log N) logarithmic time complexity.",
        4, "O(1)", "O(N)", "O(log N)", "O(N log N)", "C"
      ),
      QData(
        "q_sci_5", "quiz_science",
        "Which chemical element has the atomic number 1 and is the most abundant in the cosmos?",
        "Hydrogen makes up roughly 75% of the elemental mass of the universe.",
        5, "Hydrogen", "Helium", "Carbon", "Nitrogen", "A"
      ),
      QData(
        "q_sci_6", "quiz_science",
        "What phenomenon causes light waves to bend when entering a medium of different optical density?",
        "Refraction is the change in wave direction caused by a change in its transmission speed.",
        6, "Diffraction", "Refraction", "Polarization", "Dispersion", "B"
      ),
      QData(
        "q_sci_7", "quiz_science",
        "Which space telescope launched in December 2021 observes the universe primarily in infrared?",
        "The James Webb Space Telescope (JWST) operates at Lagrange point L2 with high infrared resolution.",
        7, "Hubble", "Kepler", "James Webb Space Telescope", "Chandra", "C"
      ),
      QData(
        "q_sci_8", "quiz_science",
        "What fundamental programming concept describes a function calling itself to solve smaller subproblems?",
        "Recursion involves a base condition and one or more recursive calls to resolve complex inductive tasks.",
        8, "Polymorphism", "Recursion", "Encapsulation", "Memoization", "B"
      ),

      // History
      QData(
        "q_hist_1", "quiz_history",
        "Which ancient civilization constructed the architectural wonders of Machu Picchu?",
        "Machu Picchu was built in the 15th century by the Inca Empire under Emperor Pachacuti.",
        1, "Aztec", "Maya", "Inca", "Olmec", "C"
      ),
      QData(
        "q_hist_2", "quiz_history",
        "In what year was the Magna Carta signed by King John of England at Runnymede?",
        "Signed in 1215, the Magna Carta first placed limits on royal authority under English law.",
        2, "1066", "1215", "1492", "1776", "B"
      ),
      QData(
        "q_hist_3", "quiz_history",
        "Who was the first emperor of a unified imperial China, renowned for his Terracotta Army?",
        "Qin Shi Huang founded the Qin dynasty after conquering all warring states in 221 BCE.",
        3, "Kublai Khan", "Sun Tzu", "Qin Shi Huang", "Han Wudi", "C"
      ),
      QData(
        "q_hist_4", "quiz_history",
        "Which Renaissance polymath painted the enigmatic masterpiece 'Mona Lisa'?",
        "Leonardo da Vinci began painting the portrait in Florence around 1503.",
        4, "Michelangelo", "Raphael", "Donatello", "Leonardo da Vinci", "D"
      ),
      QData(
        "q_hist_5", "quiz_history",
        "The famous battle of Waterloo in 1815 marked the final defeat of which military commander?",
        "Napoleon Bonaparte was decisively defeated by Anglo-allied and Prussian coalition armies.",
        5, "Napoleon Bonaparte", "Duke of Wellington", "Otto von Bismarck", "Alexander the Great", "A"
      ),
      QData(
        "q_hist_6", "quiz_history",
        "Which ancient library in Egypt was regarded as one of the greatest capitals of universal knowledge?",
        "The Great Library of Alexandria in Hellenistic Egypt flourished under the Ptolemaic dynasty.",
        6, "Library of Pergamum", "Library of Alexandria", "House of Wisdom", "Villa of the Papyri", "B"
      ),
      QData(
        "q_hist_7", "quiz_history",
        "What historic wall was demolished starting in November 1989, symbolizing the end of the Cold War?",
        "The fall of the Berlin Wall paved the way for German reunification and European integration.",
        7, "Hadrian's Wall", "The Berlin Wall", "Great Wall of Gorgan", "Aurelian Walls", "B"
      ),
      QData(
        "q_hist_8", "quiz_history",
        "Which ship famously transported the Pilgrims across the Atlantic to Plymouth Colony in 1620?",
        "The Mayflower carried 102 passengers across the stormy Atlantic ocean in late 1620.",
        8, "Santa Maria", "HMS Beagle", "Mayflower", "Golden Hind", "C"
      ),

      // Geography
      QData(
        "q_geo_1", "quiz_geography",
        "Which continent contains the largest non-polar desert in the world, the Sahara?",
        "The Sahara spans across North Africa, covering over 9.2 million square kilometers.",
        1, "Asia", "South America", "Africa", "Australia", "C"
      ),
      QData(
        "q_geo_2", "quiz_geography",
        "What is the official capital city of Australia?",
        "Canberra was selected as the purpose-built compromise capital between Sydney and Melbourne in 1908.",
        2, "Sydney", "Melbourne", "Brisbane", "Canberra", "D"
      ),
      QData(
        "q_geo_3", "quiz_geography",
        "Which oceanic trench represents the deepest surveyed point on Earth's crust?",
        "The Mariana Trench in the western Pacific reaches a depth of nearly 11,000 meters at Challenger Deep.",
        3, "Puerto Rico Trench", "Java Trench", "Mariana Trench", "Tonga Trench", "C"
      ),
      QData(
        "q_geo_4", "quiz_geography",
        "Through how many time zones does mainland Russia span from west to east?",
        "Russia stretches across 11 contiguous time zones, from Kaliningrad to Kamchatka.",
        4, "5", "8", "11", "14", "C"
      ),
      QData(
        "q_geo_5", "quiz_geography",
        "Which river is the longest on Earth, flowing northward through eleven African nations?",
        "The Nile River extends approximately 6,650 kilometers (4,132 miles) into the Mediterranean Sea.",
        5, "Amazon River", "Nile River", "Yangtze River", "Mississippi River", "B"
      ),
      QData(
        "q_geo_6", "quiz_geography",
        "Mount Everest, Earth's highest peak above sea level, sits on the border of Nepal and which region?",
        "Everest straddles the international frontier between Nepal and the Tibet Autonomous Region of China.",
        6, "India", "Bhutan", "China (Tibet)", "Pakistan", "C"
      ),
      QData(
        "q_geo_7", "quiz_geography",
        "What is the smallest independent sovereign nation in the world by both area and population?",
        "Vatican City covers an area of roughly 49 hectares (121 acres) within Rome, Italy.",
        7, "Monaco", "Nauru", "San Marino", "Vatican City", "D"
      ),
      QData(
        "q_geo_8", "quiz_geography",
        "Which country boasts the longest coastline in the world, bordering three separate oceans?",
        "Canada's coastline stretches over 243,042 kilometers across the Atlantic, Pacific, and Arctic oceans.",
        8, "Indonesia", "Canada", "Norway", "Russia", "B"
      ),

      // Pop Culture
      QData(
        "q_pop_1", "quiz_popculture",
        "Which film won the Best Picture Oscar at the 92nd Academy Awards, becoming the first non-English winner?",
        "Bong Joon-ho's 'Parasite' made historic Oscars history in 2020 by winning both Best Picture and International Feature.",
        1, "Roma", "Parasite", "Amélie", "Life is Beautiful", "B"
      ),
      QData(
        "q_pop_2", "quiz_popculture",
        "What is the best-selling music album of all time worldwide, with over 70 million estimated sales?",
        "Michael Jackson's 1982 album 'Thriller' produced seven top-ten singles and remains the top seller.",
        2, "The Dark Side of the Moon", "Back in Black", "Thriller", "Abbey Road", "C"
      ),
      QData(
        "q_pop_3", "quiz_popculture",
        "In the Marvel Cinematic Universe, what is the fictional metal vibranium-rich nation ruled by King T'Challa?",
        "Wakanda is an advanced African nation hidden from the world behind holographic cloaks.",
        3, "Latveria", "Sokovia", "Wakanda", "Genosha", "C"
      ),
      QData(
        "q_pop_4", "quiz_popculture",
        "Which legendary video game franchise features the Master Chief as its primary armored protagonist?",
        "Halo debuted in 2001 with 'Combat Evolved', centering Master Chief Petty Officer John-117.",
        4, "Gears of War", "Doom", "Halo", "Metroid", "C"
      ),
      QData(
        "q_pop_5", "quiz_popculture",
        "Which British author created the magical wizarding universe of Harry Potter?",
        "The seven-book fantasy series debuted in 1997 with 'The Philosopher's Stone'.",
        5, "J.R.R. Tolkien", "C.S. Lewis", "J.K. Rowling", "Philip Pullman", "C"
      ),
      QData(
        "q_pop_6", "quiz_popculture",
        "What is the name of the alternate upside-down dimension in the Netflix series 'Stranger Things'?",
        "The Upside Down is a dark, decaying parallel dimension inhabited by predatory creatures like the Demogorgon.",
        6, "The Nether", "The Upside Down", "The Phantom Zone", "The Twilight Realm", "B"
      ),
      QData(
        "q_pop_7", "quiz_popculture",
        "Who holds the record for the most Grammy Awards won by any individual artist in music history?",
        "Beyoncé has earned an unprecedented 32 Grammy Awards across her illustrious career.",
        7, "Taylor Swift", "Beyoncé", "Stevie Wonder", "Paul McCartney", "B"
      ),
      QData(
        "q_pop_8", "quiz_popculture",
        "Which classic 1980 arcade game features a yellow circle navigating a maze eating pellets while evading ghosts?",
        "Toru Iwatani designed Pac-Man, released by Namco in Japan in May 1980.",
        8, "Space Invaders", "Galaga", "Pac-Man", "Dig Dug", "C"
      ),

      // General Brain Sprint
      QData(
        "q_gen_1", "quiz_general",
        "How many chess pieces does each player start with at the beginning of a standard game?",
        "Each side starts with 16 pieces: 1 King, 1 Queen, 2 Rooks, 2 Knights, 2 Bishops, and 8 Pawns.",
        1, "14", "16", "18", "20", "B"
      ),
      QData(
        "q_gen_2", "quiz_general",
        "Which precious metal is known by the chemical symbol 'Au' derived from the Latin 'aurum'?",
        "Gold is represented by Au on the periodic table of elements.",
        2, "Silver", "Gold", "Platinum", "Copper", "B"
      ),
      QData(
        "q_gen_3", "quiz_general",
        "What is the Roman numeral representation for the number 500?",
        "In Roman numerals, D equals 500, C equals 100, and M equals 1,000.",
        3, "L", "C", "D", "M", "C"
      ),
      QData(
        "q_gen_4", "quiz_general",
        "Which bird is capable of flying backwards and possesses the highest wingbeat frequency?",
        "Hummingbirds can hover in midair and fly backwards due to unique ball-and-socket shoulder joints.",
        4, "Swallow", "Hummingbird", "Falcon", "Swift", "B"
      ),
      QData(
        "q_gen_5", "quiz_general",
        "What is the mathematical constant defined as the ratio of a circle's circumference to its diameter?",
        "Pi (π) is an irrational number approximately equal to 3.14159265.",
        5, "Euler's number (e)", "Golden Ratio (phi)", "Pi (π)", "Planck's Constant", "C"
      ),
      QData(
        "q_gen_6", "quiz_general",
        "How many keys are on a standard full-size modern acoustic piano keyboard?",
        "A standard modern piano keyboard comprises 52 white keys and 36 black keys, totaling 88 keys.",
        6, "76", "84", "88", "92", "C"
      ),
      QData(
        "q_gen_7", "quiz_general",
        "Which organ in the human body consumes roughly 20% of the body's resting oxygen and glucose?",
        "Despite accounting for only about 2% of total body weight, the human brain demands 20% of metabolic energy.",
        7, "Heart", "Liver", "Brain", "Kidneys", "C"
      ),
      QData(
        "q_gen_8", "quiz_general",
        "What is the primary gas composing approximately 78% of Earth's atmosphere?",
        "Nitrogen (N2) comprises ~78%, Oxygen ~21%, and Argon ~0.93% of Earth's atmosphere.",
        8, "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", "B"
      )
    )

    for (item in rawList) {
      questions.add(
        QuestionEntity(
          id = item.qId,
          quizId = item.quizId,
          questionText = item.text,
          explanation = item.explanation,
          pointsBase = 100,
          position = item.pos
        )
      )

      options.add(
        QuestionOptionEntity(
          id = "${item.qId}_A",
          questionId = item.qId,
          optionLetter = "A",
          optionText = item.optA,
          isCorrect = item.correctLetter == "A"
        )
      )
      options.add(
        QuestionOptionEntity(
          id = "${item.qId}_B",
          questionId = item.qId,
          optionLetter = "B",
          optionText = item.optB,
          isCorrect = item.correctLetter == "B"
        )
      )
      options.add(
        QuestionOptionEntity(
          id = "${item.qId}_C",
          questionId = item.qId,
          optionLetter = "C",
          optionText = item.optC,
          isCorrect = item.correctLetter == "C"
        )
      )
      options.add(
        QuestionOptionEntity(
          id = "${item.qId}_D",
          questionId = item.qId,
          optionLetter = "D",
          optionText = item.optD,
          isCorrect = item.correctLetter == "D"
        )
      )
    }

    return Pair(questions, options)
  }

  fun getInitialLeaderboardEntries(): List<LeaderboardEntryEntity> {
    val entries = mutableListOf<LeaderboardEntryEntity>()

    val competitors = listOf(
      Triple("Quantum_Gamer", 1450, 100),
      Triple("StarLord88", 1420, 90),
      Triple("Brainiac", 1380, 90),
      Triple("NeoRunner", 1290, 85),
      Triple("AtlasX", 1210, 80),
      Triple("Valkyrie", 1140, 75),
      Triple("PixelPulse", 1020, 70),
      Triple("HyperNova", 980, 65)
    )

    val periods = listOf("DAILY", "WEEKLY", "ALL_TIME")
    val quizIds = listOf("quiz_science", "quiz_history", "quiz_geography", "quiz_popculture", "quiz_general")

    var counter = 1
    for (quizId in quizIds) {
      for (period in periods) {
        val scoreOffset = when (period) {
          "ALL_TIME" -> 200
          "WEEKLY" -> 100
          else -> 0
        }

        competitors.forEachIndexed { index, (username, baseScore, accuracy) ->
          val timeMs = 24000L + (index * 2500L)
          entries.add(
            LeaderboardEntryEntity(
              id = "lb_${quizId}_${period}_$counter",
              quizId = quizId,
              username = username,
              highScore = baseScore + scoreOffset - (index * 30),
              accuracyPct = (accuracy - (index * 2)).coerceAtLeast(60),
              period = period,
              totalTimeMs = timeMs,
              isLocalPlayer = false,
              avatarSeed = "$index",
              updatedAt = System.currentTimeMillis() - (index * 3600000L)
            )
          )
          counter++
        }
      }
    }

    return entries
  }
}
