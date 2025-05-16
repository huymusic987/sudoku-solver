public class Backtracking {

    public int[][] solve(int[][] puzzle) {
        if (!isValidBoard(puzzle)) {
            return null;
        }
        if (BacktrackingRecursive(puzzle)) {
            return puzzle;
        } else {
            return null;
        }
    }

    private static boolean isNumberInRow(int[][] board, int number, int row) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == number) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInColumn(int[][] board, int number, int column) {
        for (int i = 0; i < 9; i++) {
            if (board[i][column] == number) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInBox(int[][] board, int number, int row, int column) {
        int localBoxRow = row - row % 3;
        int localBoxColumn = column - column % 3;

        for (int i = localBoxRow; i < localBoxRow + 3; i++) {
            for (int j = localBoxColumn; j < localBoxColumn + 3; j++) {
                if (board[i][j] == number) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isValidPlacement(int[][] board, int number, int row, int column) {
        return !isNumberInRow(board, number, row) &&
                !isNumberInColumn(board, number, column) &&
                !isNumberInBox(board, number, row, column);
    }

    private static boolean BacktrackingRecursive(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board[row][column] == 0) {
                    for (int numberToTry = 1; numberToTry <= 9; numberToTry++) {
                        if (isValidPlacement(board, numberToTry, row, column)) {
                            board[row][column] = numberToTry;
                            if (BacktrackingRecursive(board)) {
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

    private static boolean isValidBoard(int[][] board) {
        // Check rows
        for (int row = 0; row < 9; row++) {
            boolean[] seen = new boolean[10];
            for (int col = 0; col < 9; col++) {
                int val = board[row][col];
                if (val != 0) {
                    if (seen[val]) {
                        return false; // Duplicate in row
                    }
                    seen[val] = true;
                }
            }
        }
        // Check columns
        for (int col = 0; col < 9; col++) {
            boolean[] seen = new boolean[10];
            for (int row = 0; row < 9; row++) {
                int val = board[row][col];
                if (val != 0) {
                    if (seen[val]) {
                        return false; // Duplicate in column
                    }
                    seen[val] = true;
                }
            }
        }
        // Check 3x3 boxes
        for (int boxRow = 0; boxRow < 9; boxRow += 3) {
            for (int boxCol = 0; boxCol < 9; boxCol += 3) {
                boolean[] seen = new boolean[10];
                for (int i = boxRow; i < boxRow + 3; i++) {
                    for (int j = boxCol; j < boxCol + 3; j++) {
                        int val = board[i][j];
                        if (val != 0) {
                            if (seen[val]) {
                                return false; // Duplicate in box
                            }
                            seen[val] = true;
                        }
                    }
                }
            }
        }
        return true;
    }
}