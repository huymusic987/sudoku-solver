package algorithms;

public class Backtracking implements SudokuSolver {
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

    private boolean isValidNumber(int[][] puzzle, int number, int row, int column) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (puzzle[row][i] == number) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (puzzle[i][column] == number) {
                return false;
            }
        }

        // Check 3x3 box
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

    private boolean BacktrackingRecursive(int[][] puzzle, long startTime) {
        if (System.currentTimeMillis() - startTime > 120000) {
            throw new RuntimeException("Backtracking exceeded time limit of 2 minutes");
        }

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (puzzle[row][column] == 0) {
                    for (int numberToTry = 1; numberToTry <= 9; numberToTry++) {
                        if (isValidNumber(puzzle, numberToTry, row, column)) {
                            puzzle[row][column] = numberToTry;
                            if (enableDetailedLogs) {
                                System.out.printf("Step %d: Trying number %d at (%d, %d)%n",
                                        stepCount, numberToTry, row, column);
                                stepCount++;
                                printBoard(puzzle);
                            }
                            if (BacktrackingRecursive(puzzle, startTime)) {
                                return true;
                            } else {
                                puzzle[row][column] = 0;
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
    public boolean isValidBoard(int[][] board) {
        if (board.length != 9 || board[0].length != 9)
            return false;

        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];

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