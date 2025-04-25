public class RMITSudokuSolver {

    private static final int GRID_SIZE = 9;
    private static final int BOX_SIZE = 3;

    public static void main(String[] args) {

        int[][] board = {
                { 0, 2, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 6, 0, 0, 0, 0, 3 },
                { 0, 7, 4, 0, 8, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 3, 0, 0, 2 },
                { 0, 8, 0, 0, 4, 0, 0, 1, 0 },
                { 6, 0, 0, 5, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1, 0, 7, 8, 0 },
                { 5, 0, 0, 0, 0, 9, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 4, 0 }
        };

        System.out.println("===== Original board =====");
        printResult(board);
        long start = System.nanoTime();
        if (solveBoard(board)) {
            long end = System.nanoTime();
            System.out.println("Solved successfully!");
            System.out.println("Time taken: " + (end - start) / 1_000_000 + " ms");
        } else {
            System.out.println("Unsolvable board :(");
        }

        printResult(board);
    }

    private static void printResult(int[][] board) {
        System.out.println();
        for (int row = 0; row < GRID_SIZE; row++) {
            if ((row % BOX_SIZE == 0) && (row != 0)) {
                System.out.println("-----------------------------");
            }
            for (int col = 0; col < GRID_SIZE; col++) {
                if ((col % BOX_SIZE == 0) && (col != 0)) {
                    System.out.print("|");
                }
                final int cellValue = board[row][col];
                System.out.print(" ");
                if (cellValue == 0) {
                    System.out.print(" ");
                } else {
                    System.out.print(cellValue);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static boolean isNumberInRow(int[][] board, int number, int row) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == number) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInColumn(int[][] board, int number, int column) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][column] == number) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInBox(int[][] board, int number, int row, int column) {
        int localBoxRow = row - row % BOX_SIZE;
        int localBoxColumn = column - column % BOX_SIZE;

        for (int i = localBoxRow; i < localBoxRow + BOX_SIZE; i++) {
            for (int j = localBoxColumn; j < localBoxColumn + BOX_SIZE; j++) {
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

    private static boolean solveBoard(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                if (board[row][column] == 0) {
                    for (int numberToTry = 1; numberToTry <= GRID_SIZE; numberToTry++) {
                        if (isValidPlacement(board, numberToTry, row, column)) {
                            board[row][column] = numberToTry;

                            if (solveBoard(board)) {
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
}
