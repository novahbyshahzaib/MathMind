package com.novah.mathmind.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.random.Random

private val ANSWER_PATTERN = Regex("^-?\\d*$")

/**
 * Generates a random math equation based on difficulty level.
 * Returns a Pair of the equation string and the correct integer answer.
 *
 * - Easy: single-digit operands (1-9), all four operations
 * - Medium: double-digit operands (10-99), all four operations
 * - Hard: mixed double/triple digit operands, complex operations
 *
 * Division always produces a whole-number result to avoid fractions.
 */
fun generateEquation(difficulty: String): Pair<String, Int> {
    val operators = listOf("+", "-", "×", "÷")
    val op = operators[Random.nextInt(operators.size)]

    var a: Int
    var b: Int

    when (difficulty) {
        "easy" -> {
            a = Random.nextInt(1, 10)
            b = Random.nextInt(1, 10)
        }
        "medium" -> {
            a = Random.nextInt(10, 100)
            b = Random.nextInt(10, 100)
        }
        "hard" -> {
            a = Random.nextInt(50, 500)
            b = Random.nextInt(10, 200)
        }
        else -> {
            a = Random.nextInt(1, 10)
            b = Random.nextInt(1, 10)
        }
    }

    // For division, ensure clean integer results
    if (op == "÷") {
        // Make 'a' a multiple of 'b' so the answer is a whole number
        b = when (difficulty) {
            "easy" -> Random.nextInt(1, 10)
            "medium" -> Random.nextInt(2, 20)
            "hard" -> Random.nextInt(2, 50)
            else -> Random.nextInt(1, 10)
        }
        val multiplier = when (difficulty) {
            "easy" -> Random.nextInt(1, 10)
            "medium" -> Random.nextInt(2, 10)
            "hard" -> Random.nextInt(2, 20)
            else -> Random.nextInt(1, 10)
        }
        a = b * multiplier
    }

    val answer = when (op) {
        "+" -> a + b
        "-" -> a - b
        "×" -> a * b
        "÷" -> a / b
        else -> 0
    }

    return Pair("$a $op $b", answer)
}

/**
 * Math Challenge gameplay screen.
 * Features a 60-second countdown timer, score tracking,
 * random equation generation, and a final results view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathGameScreen(difficulty: String, navController: NavHostController) {
    // Game state variables
    var timeLeft by remember { mutableIntStateOf(60) }
    var score by remember { mutableIntStateOf(0) }
    var totalQuestions by remember { mutableIntStateOf(0) }
    var currentEquation by remember { mutableStateOf(generateEquation(difficulty)) }
    var userAnswer by remember { mutableStateOf("") }
    var gameOver by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }

    // Countdown timer: ticks every second until time runs out
    LaunchedEffect(key1 = gameOver) {
        if (!gameOver) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            gameOver = true
        }
    }

    /** Checks the user's answer against the correct answer and updates score */
    fun submitAnswer() {
        val parsed = userAnswer.toIntOrNull()
        totalQuestions++
        if (parsed == currentEquation.second) {
            score++
            feedbackText = "✓ Correct!"
        } else {
            feedbackText = "✗ Wrong! Answer: ${currentEquation.second}"
        }
        // Generate next question and clear input
        currentEquation = generateEquation(difficulty)
        userAnswer = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Math Challenge - ${difficulty.replaceFirstChar { it.uppercase() }}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (gameOver) {
            // ---- Final Results Screen ----
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Time's Up!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Score: $score / $totalQuestions",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Show accuracy percentage
                val accuracy = if (totalQuestions > 0) (score * 100 / totalQuestions) else 0
                Text(
                    text = "Accuracy: $accuracy%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Play Again button restarts the game
                Button(onClick = {
                    timeLeft = 60
                    score = 0
                    totalQuestions = 0
                    feedbackText = ""
                    userAnswer = ""
                    currentEquation = generateEquation(difficulty)
                    gameOver = false
                }) {
                    Text("Play Again", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Back to Menu")
                }
            }
        } else {
            // ---- Active Gameplay Screen ----
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Timer and Score row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏱ $timeLeft s",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (timeLeft <= 10) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Score: $score",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Display the current equation
                Text(
                    text = currentEquation.first + " = ?",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Answer input field
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { newValue ->
                        // Allow only digits and a leading minus sign
                        if (newValue.isEmpty() || newValue.matches(ANSWER_PATTERN)) {
                            userAnswer = newValue
                        }
                    },
                    label = { Text("Your Answer") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submitAnswer() }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = { submitAnswer() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = userAnswer.isNotEmpty()
                ) {
                    Text("Submit", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback text (Correct / Wrong)
                if (feedbackText.isNotEmpty()) {
                    Text(
                        text = feedbackText,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (feedbackText.startsWith("✓")) MaterialTheme.colorScheme.tertiary
                               else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
