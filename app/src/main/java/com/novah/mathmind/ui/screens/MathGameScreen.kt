package com.novah.mathmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.theme.NeuColors
import kotlinx.coroutines.delay
import kotlin.random.Random

private val answerPattern = Regex("^-?\\d*$")

/**
 * Generates a random math equation based on difficulty level.
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

    if (op == "÷") {
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
 * Math Challenge gameplay screen with neubrutalism design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathGameScreen(difficulty: String, navController: NavHostController) {
    var timeLeft by remember { mutableIntStateOf(60) }
    var score by remember { mutableIntStateOf(0) }
    var totalQuestions by remember { mutableIntStateOf(0) }
    var currentEquation by remember { mutableStateOf(generateEquation(difficulty)) }
    var userAnswer by remember { mutableStateOf("") }
    var gameOver by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = gameOver) {
        if (!gameOver) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            gameOver = true
        }
    }

    fun submitAnswer() {
        val parsed = userAnswer.toIntOrNull()
        totalQuestions++
        if (parsed == currentEquation.second) {
            score++
            feedbackText = "✓ Correct!"
        } else {
            feedbackText = "✗ Wrong! Answer: ${currentEquation.second}"
        }
        currentEquation = generateEquation(difficulty)
        userAnswer = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Math Challenge - ${difficulty.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeuColors.Yellow,
                    titleContentColor = NeuColors.Black,
                    navigationIconContentColor = NeuColors.Black
                ),
                modifier = Modifier.border(width = 3.dp, color = NeuColors.Black)
            )
        }
    ) { paddingValues ->
        if (gameOver) {
            // Results screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val shape = RoundedCornerShape(16.dp)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = 5.dp, y = 5.dp)
                            .clip(shape)
                            .background(NeuColors.Black)
                            .padding(32.dp)
                    ) { Spacer(Modifier.height(160.dp)) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(NeuColors.Green)
                            .border(3.dp, NeuColors.Black, shape)
                            .padding(32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⏰ Time's Up!",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Score: $score / $totalQuestions",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val accuracy = if (totalQuestions > 0) (score * 100 / totalQuestions) else 0
                            Text(
                                text = "Accuracy: $accuracy%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                NeuButton(
                    text = "Play Again",
                    color = NeuColors.Yellow,
                    onClick = {
                        timeLeft = 60
                        score = 0
                        totalQuestions = 0
                        feedbackText = ""
                        userAnswer = ""
                        currentEquation = generateEquation(difficulty)
                        gameOver = false
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeuButton(
                    text = "Back to Menu",
                    color = NeuColors.Pink,
                    onClick = { navController.popBackStack() }
                )
            }
        } else {
            // Active gameplay
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
                    val timerShape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .clip(timerShape)
                            .background(
                                if (timeLeft <= 10) NeuColors.Pink else NeuColors.Blue
                            )
                            .border(2.dp, NeuColors.Black, timerShape)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "⏱ ${timeLeft}s",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = NeuColors.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(timerShape)
                            .background(NeuColors.Green)
                            .border(2.dp, NeuColors.Black, timerShape)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Score: $score",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = NeuColors.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Equation display
                val eqShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(eqShape)
                        .background(NeuColors.Yellow)
                        .border(3.dp, NeuColors.Black, eqShape)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentEquation.first + " = ?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = NeuColors.Black
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(answerPattern)) {
                            userAnswer = newValue
                        }
                    },
                    label = { Text("Your Answer", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submitAnswer() }),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(3.dp, NeuColors.Black, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                NeuButton(
                    text = "Submit",
                    color = NeuColors.Green,
                    onClick = { submitAnswer() },
                    enabled = userAnswer.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (feedbackText.isNotEmpty()) {
                    val fbShape = RoundedCornerShape(8.dp)
                    val fbColor = if (feedbackText.startsWith("✓")) NeuColors.Green else NeuColors.Pink
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(fbShape)
                            .background(fbColor)
                            .border(2.dp, NeuColors.Black, fbShape)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feedbackText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeuColors.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Neubrutalism-styled button with solid shadow.
 */
@Composable
fun NeuButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val actualColor = if (enabled) color else color.copy(alpha = 0.5f)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .clip(shape)
                .background(NeuColors.Black)
        )
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = actualColor,
                contentColor = NeuColors.Black,
                disabledContainerColor = actualColor,
                disabledContentColor = NeuColors.Black.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .border(3.dp, NeuColors.Black, shape)
        ) {
            Text(text = text, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
    }
}
