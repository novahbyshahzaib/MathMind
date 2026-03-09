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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.theme.NeuColors
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Game states for Number Memory.
 */
enum class NumberMemoryState {
    SHOWING,   // Displaying the number to memorize
    INPUT,     // User enters the number from memory
    CORRECT,   // User got it right, advancing
    WRONG,     // User got it wrong, game over
}

/**
 * Number Memory game with difficulty levels and neubrutalism design.
 * Player must memorize and recall increasingly longer number sequences.
 *
 * Difficulty affects starting digits and display time:
 * - Easy: Start with 3 digits, 3 seconds display
 * - Medium: Start with 4 digits, 2 seconds display
 * - Hard: Start with 5 digits, 1.5 seconds display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberMemoryScreen(navController: NavHostController, difficulty: String = "medium") {
    val startDigits = when (difficulty) {
        "easy" -> 3
        "medium" -> 4
        "hard" -> 5
        else -> 4
    }
    val baseDisplayMs = when (difficulty) {
        "easy" -> 3000L
        "medium" -> 2000L
        "hard" -> 1500L
        else -> 2000L
    }

    var currentLevel by remember { mutableIntStateOf(1) }
    var currentDigits by remember { mutableIntStateOf(startDigits) }
    var currentNumber by remember { mutableStateOf(generateRandomNumber(startDigits)) }
    var userInput by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(NumberMemoryState.SHOWING) }
    var highestLevel by remember { mutableIntStateOf(0) }

    // Display timer for showing the number
    LaunchedEffect(key1 = state, key2 = currentLevel) {
        if (state == NumberMemoryState.SHOWING) {
            // Display time increases slightly with level
            val displayTime = baseDisplayMs + (currentDigits - startDigits) * 300L
            delay(displayTime)
            state = NumberMemoryState.INPUT
        }
    }

    fun submitAnswer() {
        if (userInput == currentNumber) {
            state = NumberMemoryState.CORRECT
            if (currentLevel > highestLevel) highestLevel = currentLevel
        } else {
            state = NumberMemoryState.WRONG
            if (currentLevel - 1 > highestLevel) highestLevel = currentLevel - 1
        }
    }

    fun nextLevel() {
        currentLevel++
        currentDigits++
        currentNumber = generateRandomNumber(currentDigits)
        userInput = ""
        state = NumberMemoryState.SHOWING
    }

    fun restart() {
        currentLevel = 1
        currentDigits = startDigits
        currentNumber = generateRandomNumber(startDigits)
        userInput = ""
        state = NumberMemoryState.SHOWING
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Number Memory - ${difficulty.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeuColors.Green,
                    titleContentColor = NeuColors.Black,
                    navigationIconContentColor = NeuColors.Black
                ),
                modifier = Modifier.border(width = 3.dp, color = NeuColors.Black)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Level indicator
            val levelShape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .clip(levelShape)
                    .background(NeuColors.Blue)
                    .border(2.dp, NeuColors.Black, levelShape)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Level $currentLevel • $currentDigits digits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeuColors.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            when (state) {
                NumberMemoryState.SHOWING -> {
                    // Show the number to memorize
                    val numShape = RoundedCornerShape(16.dp)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = 5.dp, y = 5.dp)
                                .clip(numShape)
                                .background(NeuColors.Black)
                                .padding(40.dp)
                        ) { Spacer(Modifier) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(numShape)
                                .background(NeuColors.Yellow)
                                .border(3.dp, NeuColors.Black, numShape)
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentNumber,
                                fontSize = if (currentDigits <= 6) 42.sp else 32.sp,
                                fontWeight = FontWeight.Black,
                                color = NeuColors.Black,
                                textAlign = TextAlign.Center,
                                letterSpacing = 4.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Memorize this number!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                NumberMemoryState.INPUT -> {
                    // Input field for recall
                    Text(
                        text = "What was the number?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) userInput = it },
                        label = { Text("Enter the number", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submitAnswer() }),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(3.dp, NeuColors.Black, RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    NeuButton(
                        text = "Submit",
                        color = NeuColors.Green,
                        onClick = { submitAnswer() },
                        enabled = userInput.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                NumberMemoryState.CORRECT -> {
                    // Correct answer feedback
                    val correctShape = RoundedCornerShape(16.dp)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = 5.dp, y = 5.dp)
                                .clip(correctShape)
                                .background(NeuColors.Black)
                                .padding(32.dp)
                        ) { Spacer(Modifier) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(correctShape)
                                .background(NeuColors.Green)
                                .border(3.dp, NeuColors.Black, correctShape)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "✓ Correct!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = NeuColors.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Number: $currentNumber",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = NeuColors.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    NeuButton(
                        text = "Next Level →",
                        color = NeuColors.Yellow,
                        onClick = { nextLevel() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                NumberMemoryState.WRONG -> {
                    // Wrong answer / game over
                    val wrongShape = RoundedCornerShape(16.dp)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = 5.dp, y = 5.dp)
                                .clip(wrongShape)
                                .background(NeuColors.Black)
                                .padding(32.dp)
                        ) { Spacer(Modifier) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(wrongShape)
                                .background(NeuColors.Pink)
                                .border(3.dp, NeuColors.Black, wrongShape)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "✗ Wrong!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = NeuColors.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Correct: $currentNumber",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NeuColors.Black
                                )
                                Text(
                                    text = "Your answer: $userInput",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NeuColors.Black.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Highest Level: $highestLevel",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = NeuColors.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    NeuButton(
                        text = "Try Again",
                        color = NeuColors.Yellow,
                        onClick = { restart() },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NeuButton(
                        text = "Back to Menu",
                        color = NeuColors.Blue,
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/** Generates a random number string with the specified number of digits. */
private fun generateRandomNumber(digits: Int): String {
    val sb = StringBuilder()
    // First digit should not be 0
    sb.append(Random.nextInt(1, 10))
    for (i in 1 until digits) {
        sb.append(Random.nextInt(0, 10))
    }
    return sb.toString()
}
