package com.novah.mathmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.random.Random

// ─────────────────────────────────────────────────────────
//  Sudoku Generator using a backtracking algorithm
// ─────────────────────────────────────────────────────────

/**
 * Checks if placing [num] at position [row],[col] is valid
 * according to Sudoku rules (unique in row, column, and 3×3 box).
 */
fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
    // Check row
    for (c in 0 until 9) {
        if (board[row][c] == num) return false
    }
    // Check column
    for (r in 0 until 9) {
        if (board[r][col] == num) return false
    }
    // Check 3x3 subgrid
    val startRow = (row / 3) * 3
    val startCol = (col / 3) * 3
    for (r in startRow until startRow + 3) {
        for (c in startCol until startCol + 3) {
            if (board[r][c] == num) return false
        }
    }
    return true
}

/**
 * Fills the board completely using a backtracking algorithm.
 * Tries numbers 1-9 in a shuffled order for randomization.
 * Returns true if the board was successfully filled.
 */
fun fillBoard(board: Array<IntArray>): Boolean {
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            if (board[row][col] == 0) {
                val numbers = (1..9).shuffled()
                for (num in numbers) {
                    if (isValid(board, row, col, num)) {
                        board[row][col] = num
                        if (fillBoard(board)) return true
                        board[row][col] = 0 // Backtrack
                    }
                }
                return false // No valid number found, trigger backtrack
            }
        }
    }
    return true // Board is fully filled
}

/**
 * Generates a playable Sudoku puzzle by creating a full solution
 * and then removing cells. The number of removed cells determines difficulty.
 * Returns a Pair of (puzzle board, solution board).
 */
fun generateSudoku(difficulty: String = "medium"): Pair<Array<IntArray>, Array<IntArray>> {
    // Create and fill a complete valid board
    val solution = Array(9) { IntArray(9) }
    fillBoard(solution)

    // Deep copy the solution to create the puzzle
    val puzzle = Array(9) { row -> solution[row].copyOf() }

    // Determine how many cells to remove based on difficulty
    val cellsToRemove = when (difficulty) {
        "easy" -> 30
        "medium" -> 40
        "hard" -> 55
        else -> 40
    }

    // Remove cells randomly
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

/**
 * Finds all cells that conflict with the value at [row],[col].
 * Returns a set of (row, col) pairs that have the same value
 * in the same row, column, or 3x3 subgrid.
 */
fun findConflicts(board: Array<IntArray>, row: Int, col: Int): Set<Pair<Int, Int>> {
    val conflicts = mutableSetOf<Pair<Int, Int>>()
    val num = board[row][col]
    if (num == 0) return conflicts

    // Check row conflicts
    for (c in 0 until 9) {
        if (c != col && board[row][c] == num) {
            conflicts.add(Pair(row, c))
        }
    }
    // Check column conflicts
    for (r in 0 until 9) {
        if (r != row && board[r][col] == num) {
            conflicts.add(Pair(r, col))
        }
    }
    // Check 3x3 subgrid conflicts
    val startRow = (row / 3) * 3
    val startCol = (col / 3) * 3
    for (r in startRow until startRow + 3) {
        for (c in startCol until startCol + 3) {
            if ((r != row || c != col) && board[r][c] == num) {
                conflicts.add(Pair(r, c))
            }
        }
    }
    return conflicts
}

/**
 * Full Sudoku game screen with a 9×9 interactive grid.
 * Features real-time conflict highlighting and auto-solve.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(navController: NavHostController) {
    // Generate a new puzzle on first composition
    var puzzleState by remember {
        val (puzzle, solution) = generateSudoku("medium")
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

    /** Updates the board when the user inputs a number */
    fun placeNumber(num: Int) {
        val cell = selectedCell ?: return
        val (row, col) = cell
        // Only allow editing non-initial cells
        if (puzzleState.initialBoard[row][col] != 0) return

        puzzleState.board[row][col] = num

        // Recalculate all conflicts across the board
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

        // Trigger recomposition by creating a new state
        puzzleState = puzzleState.copy(
            board = puzzleState.board.map { it.toMutableList() }.toMutableList()
        )
    }

    /** Clears the selected cell */
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

    /** Auto-solves the puzzle by copying the solution to the board */
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

    /** Checks if the current board matches the solution */
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
                title = { Text("Sudoku") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSolved) {
                Text(
                    text = "🎉 Puzzle Solved!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // 9x9 Sudoku Grid
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(2.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                Column {
                    for (row in 0 until 9) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0 until 9) {
                                val value = puzzleState.board[row][col]
                                val isInitial = puzzleState.initialBoard[row][col] != 0
                                val isSelected = selectedCell == Pair(row, col)
                                val isConflict = conflictCells.contains(Pair(row, col))

                                // Determine cell background color
                                val bgColor = when {
                                    isConflict -> Color(0x40FF0000) // Red tint for conflicts
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }

                                // Determine border thickness for 3x3 subgrid visual separation
                                val rightBorder = if ((col + 1) % 3 == 0 && col < 8) 2.dp else 0.5.dp
                                val bottomBorder = if ((row + 1) % 3 == 0 && row < 8) 2.dp else 0.5.dp

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(bgColor)
                                        .border(0.5.dp, Color.Gray)
                                        .then(
                                            if ((col + 1) % 3 == 0 && col < 8)
                                                Modifier.padding(end = 1.dp)
                                            else Modifier
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
                                            fontWeight = if (isInitial) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isConflict -> Color.Red
                                                isInitial -> MaterialTheme.colorScheme.onSurface
                                                else -> MaterialTheme.colorScheme.primary
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

            // Number input buttons (1-9)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (num in 1..9) {
                    FilledTonalButton(
                        onClick = { placeNumber(num) },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = num.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = { clearCell() }) {
                    Text("Clear")
                }
                Button(onClick = {
                    if (checkSolution()) {
                        isSolved = true
                    }
                }) {
                    Text("Check")
                }
                Button(onClick = { autoSolve() }) {
                    Text("Auto-Solve")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // New Game button
            OutlinedButton(onClick = {
                val (puzzle, solution) = generateSudoku("medium")
                puzzleState = SudokuState(
                    board = puzzle.map { it.toMutableList() }.toMutableList(),
                    solution = solution,
                    initialBoard = Array(9) { r -> puzzle[r].copyOf() }
                )
                selectedCell = null
                conflictCells = emptySet()
                isSolved = false
            }) {
                Text("New Game")
            }
        }
    }
}

/**
 * Holds the state of a Sudoku game.
 * @param board The current mutable board the player is editing
 * @param solution The complete valid solution for checking/auto-solve
 * @param initialBoard The original puzzle with pre-filled (immutable) cells
 */
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
