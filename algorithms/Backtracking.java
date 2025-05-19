package algorithms;

public class Backtracking implements RMIT_Sudoku_Solver {
    private boolean enableDetailedLogs = false;
    private int stepCount = 0;

    @Override
    public int[][] solve(int[][] board) {
        if (!isValidBoard(board)) {
            throw new IllegalArgumentException("Invalid puzzle board input.");
        }

        long startTime = System.currentTimeMillis();
        stepCount = 0;

        if (BacktrackingRecursive(board, startTime)) {
            return board;
        } else {
            throw new RuntimeException("Backtracking failed to solve the puzzle.");
        }
    }

    // Time Complexity: O(1) — Each of the three loops runs a fixed 9 iterations
    private boolean isValidNumber(int[][] puzzle, int number, int row, int column) {
        // Check row: O(9) = O(1)
        for (int i = 0; i < 9; i++) {
            if (puzzle[row][i] == number) {
                return false;
            }
        }

        // Check column: O(9) = O(1)
        for (int i = 0; i < 9; i++) {
            if (puzzle[i][column] == number) {
                return false;
            }
        }

        // Check 3x3 box: O(3^2) = O(9) = O(1)
        int startRow = row - row % 3;
        int startCol = column - column % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (puzzle[i][j] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    // Time Complexity: O(9^K) — where K is the number of empty cells
    private boolean BacktrackingRecursive(int[][] puzzle, long startTime) {
        if (System.currentTimeMillis() - startTime > 120000) {
            throw new RuntimeException("Backtracking algorithm exceeded the time limit of 2 minutes");
        }

        // Iterate over each cell: O(81) = O(1) for fixed 9x9 board
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (puzzle[row][column] == 0) {
                    // Try numbers 1 to 9: 9 * O(1) = O(1)
                    for (int numberToTry = 1; numberToTry <= 9; numberToTry++) {
                        if (isValidNumber(puzzle, numberToTry, row, column)) { // O(1)
                            puzzle[row][column] = numberToTry;
                            if (enableDetailedLogs) {
                                System.out.printf("Step %d: Trying number %d at (%d, %d)%n",
                                        stepCount, numberToTry, row, column);
                                stepCount++;
                                printBoard(puzzle);
                            }
                            if (BacktrackingRecursive(puzzle, startTime)) { // Recursive call
                                return true;
                            } else {
                                puzzle[row][column] = 0; // Backtracking
                                if (enableDetailedLogs) {
                                    System.out.printf("Step %d: Backtracking at (%d, %d)%n",
                                            stepCount, row, column);
                                    printBoard(puzzle);
                                }
                            }
                        }
                    }
                    return false;
                }
            }
        }

        return true;
    }

    // AI Prompt: Write a Java method isValidBoard(int[][] board) that checks if a
    // 9×9 Sudoku board is valid. Avoid using nested loops. Validate rows, columns,
    // and 3×3 boxes using boolean arrays. Allow 0s for empty cells. Return true if
    // valid, false otherwise.

    // Time Complexity: O(1) — since board size is fixed at 9x9
    public boolean isValidBoard(int[][] board) {
        if (board.length != 9 || board[0].length != 9)
            return false;

        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];

        // Iterate once through 81 cells: O(81) = O(1)
        for (int k = 0; k < 81; k++) {
            int row = k / 9;
            int col = k % 9;

            int num = board[row][col];
            if (num == 0)
                continue; // skip empty cells

            if (num < 1 || num > 9)
                return false; // invalid digit

            int idx = num - 1;
            int boxIndex = (row / 3) * 3 + (col / 3);

            if (rows[row][idx] || cols[col][idx] || boxes[boxIndex][idx]) {
                return false; // duplicate found
            }

            rows[row][idx] = true;
            cols[col][idx] = true;
            boxes[boxIndex][idx] = true;
        }

        return true; // all checks passed
    }

    // Helper method to print the Sudoku board when running individual test
    public static void printBoard(int[][] board) {
        System.out.println("Sudoku Board:");
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("---------------------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}