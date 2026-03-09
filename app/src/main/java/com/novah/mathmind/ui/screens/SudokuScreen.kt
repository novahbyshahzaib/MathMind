package com.novah.mathmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.theme.NeuColors
import kotlin.random.Random

// ─────────────────────────────────────────────────────────
//  Sudoku Generator using a backtracking algorithm
// ─────────────────────────────────────────────────────────

fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
    for (c in 0 until 9) {
        if (board[row][c] == num) return false
    }
    for (r in 0 until 9) {
        if (board[r][col] == num) return false
    }
    val startRow = (row / 3) * 3
    val startCol = (col / 3) * 3
    for (r in startRow until startRow + 3) {
        for (c in startCol until startCol + 3) {
            if (board[r][c] == num) return false
        }
    }
    return true
}

fun fillBoard(board: Array<IntArray>): Boolean {
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            if (board[row][col] == 0) {
                val numbers = (1..9).shuffled()
                for (num in numbers) {
                    if (isValid(board, row, col, num)) {
                        board[row][col] = num
                        if (fillBoard(board)) return true
                        board[row][col] = 0
                    }
                }
                return false
            }
        }
    }
    return true
}

fun generateSudoku(difficulty: String = "medium"): Pair<Array<IntArray>, Array<IntArray>> {
    val solution = Array(9) { IntArray(9) }
    fillBoard(solution)
    val puzzle = Array(9) { row -> solution[row].copyOf() }

    val cellsToRemove = when (difficulty) {
        "easy" -> 30
        "medium" -> 40
        "hard" -> 55
        else -> 40
    }

    var removed = 0
    val positions = (0 until 81).toMutableList().apply { shuffle() }
    for (pos in positions) {
        if (removed >= cellsToRemove) break
        val row = pos / 9
        val col = pos % 9
        if (puzzle[row][col] != 0) {
            puzzle[row][col] = 0
            removed++
        }
    }

    return Pair(puzzle, solution)
}

fun findConflicts(board: Array<IntArray>, row: Int, col: Int): Set<Pair<Int, Int>> {
    val conflicts = mutableSetOf<Pair<Int, Int>>()
    val num = board[row][col]
    if (num == 0) return conflicts

    for (c in 0 until 9) {
        if (c != col && board[row][c] == num) conflicts.add(Pair(row, c))
    }
    for (r in 0 until 9) {
        if (r != row && board[r][col] == num) conflicts.add(Pair(r, col))
    }
    val startRow = (row / 3) * 3
    val startCol = (col / 3) * 3
    for (r in startRow until startRow + 3) {
        for (c in startCol until startCol + 3) {
            if ((r != row || c != col) && board[r][c] == num) conflicts.add(Pair(r, c))
        }
    }
    return conflicts
}

/**
 * Full Sudoku game screen with neubrutalism design and difficulty support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(navController: NavHostController, difficulty: String = "medium") {
    var puzzleState by remember {
        val (puzzle, solution) = generateSudoku(difficulty)
        mutableStateOf(
            SudokuState(
                board = puzzle.map { it.toMutableList() }.toMutableList(),
                solution = solution,
                initialBoard = Array(9) { r -> puzzle[r].copyOf() }
            )
        )
    }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var conflictCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var isSolved by remember { mutableStateOf(false) }

    fun placeNumber(num: Int) {
        val cell = selectedCell ?: return
        val (row, col) = cell
        if (puzzleState.initialBoard[row][col] != 0) return

        puzzleState.board[row][col] = num

        val allConflicts = mutableSetOf<Pair<Int, Int>>()
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (puzzleState.board[r][c] != 0) {
                    val cellConflicts = findConflicts(puzzleState.board.map { it.toIntArray() }.toTypedArray(), r, c)
                    if (cellConflicts.isNotEmpty()) {
                        allConflicts.addAll(cellConflicts)
                        allConflicts.add(Pair(r, c))
                    }
                }
            }
        }
        conflictCells = allConflicts
        puzzleState = puzzleState.copy(
            board = puzzleState.board.map { it.toMutableList() }.toMutableList()
        )
    }

    fun clearCell() {
        val cell = selectedCell ?: return
        val (row, col) = cell
        if (puzzleState.initialBoard[row][col] != 0) return
        puzzleState.board[row][col] = 0
        puzzleState = puzzleState.copy(
            board = puzzleState.board.map { it.toMutableList() }.toMutableList()
        )
        conflictCells = emptySet()
    }

    fun autoSolve() {
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                puzzleState.board[r][c] = puzzleState.solution[r][c]
            }
        }
        puzzleState = puzzleState.copy(
            board = puzzleState.board.map { it.toMutableList() }.toMutableList()
        )
        conflictCells = emptySet()
        isSolved = true
    }

    fun checkSolution(): Boolean {
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (puzzleState.board[r][c] != puzzleState.solution[r][c]) return false
            }
        }
        return true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sudoku - ${difficulty.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeuColors.Blue,
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
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSolved) {
                val solvedShape = RoundedCornerShape(8.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(solvedShape)
                        .background(NeuColors.Green)
                        .border(3.dp, NeuColors.Black, solvedShape)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 Puzzle Solved!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = NeuColors.Black
                    )
                }
            }

            // 9x9 Sudoku Grid with neubrutalism border
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(3.dp, NeuColors.Black)
            ) {
                Column {
                    for (row in 0 until 9) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0 until 9) {
                                val value = puzzleState.board[row][col]
                                val isInitial = puzzleState.initialBoard[row][col] != 0
                                val isSelected = selectedCell == Pair(row, col)
                                val isConflict = conflictCells.contains(Pair(row, col))

                                val bgColor = when {
                                    isConflict -> NeuColors.Pink.copy(alpha = 0.4f)
                                    isSelected -> NeuColors.Yellow.copy(alpha = 0.5f)
                                    else -> Color.Transparent
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(bgColor)
                                        .border(
                                            width = if ((col + 1) % 3 == 0 && col < 8 || (row + 1) % 3 == 0 && row < 8) 2.dp else 0.5.dp,
                                            color = NeuColors.Black
                                        )
                                        .clickable {
                                            if (!isInitial) {
                                                selectedCell = Pair(row, col)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (value != 0) {
                                        Text(
                                            text = value.toString(),
                                            fontSize = 18.sp,
                                            fontWeight = if (isInitial) FontWeight.Black else FontWeight.Bold,
                                            color = when {
                                                isConflict -> Color(0xFFFF0000)
                                                isInitial -> NeuColors.Black
                                                else -> NeuColors.Blue
                                            },
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Number input buttons with neubrutalism style
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (num in 1..9) {
                    val btnShape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(btnShape)
                            .background(NeuColors.Yellow)
                            .border(2.dp, NeuColors.Black, btnShape)
                            .clickable { placeNumber(num) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NeuColors.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NeuSmallButton("Clear", NeuColors.Orange) { clearCell() }
                NeuSmallButton("Check", NeuColors.Green) {
                    if (checkSolution()) isSolved = true
                }
                NeuSmallButton("Solve", NeuColors.Pink) { autoSolve() }
            }

            Spacer(modifier = Modifier.height(8.dp))

            NeuSmallButton("New Game", NeuColors.Blue) {
                val (puzzle, solution) = generateSudoku(difficulty)
                puzzleState = SudokuState(
                    board = puzzle.map { it.toMutableList() }.toMutableList(),
                    solution = solution,
                    initialBoard = Array(9) { r -> puzzle[r].copyOf() }
                )
                selectedCell = null
                conflictCells = emptySet()
                isSolved = false
            }
        }
    }
}

@Composable
private fun NeuSmallButton(text: String, color: Color, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.dp, y = 2.dp)
                .clip(shape)
                .background(NeuColors.Black)
        )
        Button(
            onClick = onClick,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = NeuColors.Black
            ),
            modifier = Modifier
                .border(2.dp, NeuColors.Black, shape),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

data class SudokuState(
    val board: MutableList<MutableList<Int>>,
    val solution: Array<IntArray>,
    val initialBoard: Array<IntArray>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SudokuState) return false
        return board == other.board
    }
    override fun hashCode(): Int = board.hashCode()
}
