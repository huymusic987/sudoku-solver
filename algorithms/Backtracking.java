package algorithms;

public class Backtracking implements SudokuSolver {
    @Override
    public int[][] solve(int[][] board) {
        if (!isValidBoard(board)) {
            throw new IllegalArgumentException("Invalid puzzle board input.");
        }

        long startTime = System.currentTimeMillis();

        if (BacktrackingRecursive(board, startTime)) {
            return board;
        } else {
            throw new RuntimeException("Backtracking failed to solve the puzzle.");
        }
    }

    private boolean isValidNumber(int[][] board, int number, int row, int column) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == number) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (board[i][column] == number) {
                return false;
            }
        }

        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = column - column % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean BacktrackingRecursive(int[][] board, long startTime) {
        if (System.currentTimeMillis() - startTime > 120000) {
            throw new RuntimeException("Backtracking exceeded time limit of 2 minutes");
        }

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board[row][column] == 0) {
                    for (int numberToTry = 1; numberToTry <= 9; numberToTry++) {
                        if (isValidNumber(board, numberToTry, row, column)) {
                            board[row][column] = numberToTry;
                            if (BacktrackingRecursive(board, startTime)) {
                                return true;
                            } else {
                                board[row][column] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }

        return true;
    }

    // AI Prompt: Write a function in java, called isValidBoard that accepts a 2D
    // array which represents a sudoku board, validate the board whether it follow
    // Sudoku rules or not and return a boolean. Avoid using nested loops
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
}