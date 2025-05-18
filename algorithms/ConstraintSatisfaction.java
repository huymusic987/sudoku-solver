package algorithms;

import structures.ArrayList;
import structures.List;

public class ConstraintSatisfaction implements SudokuSolver {
    private static final int GRID_SIZE = 9;

    // Funtion called sudoku solver return solved board or
    // return null whenever it exceed 2 minutes or an error is occured
    @Override
    public int[][] solve(int[][] board) {
        if (!isValidBoard(board)) {
            throw new IllegalArgumentException("Invalid puzzle board input.");
        }

        long startTime = System.currentTimeMillis();

        if (constraintSatisfaction(board, startTime)) {
            return board;
        } else {
            throw new RuntimeException("Constraint Satisfaction failed to solve the puzzle.");
        }
    }

    // Average Time Complexity: O(n^k)
    // n is the number of unassigned cells
    // k is the number of possible values for each cell
    // Worst Case: O(9^81)
    public static boolean constraintSatisfaction(int[][] board, long startTime) {
        if (System.currentTimeMillis() - startTime > 120000) {
            throw new RuntimeException("Constraint Satisfaction exceeded time limit of 2 minutes");
        }

        // Find the most constrained cell (cell with the fewest possible values)
        int[] cell = findMostConstrainedCell(board);
        if (cell == null) {
            return true;
        }

        int row = cell[0];
        int col = cell[1];
        List<Integer> possibleValues = getPossibleValues(board, row, col);

        // Try each possible value
        for (int i = 0; i < possibleValues.size(); i++) {
            int value = possibleValues.get(i);
            board[row][col] = value;
            if (constraintSatisfaction(board, startTime)) {
                return true;
            }
            board[row][col] = 0;
        }

        return false;
    }

    // Average Time Complexity: O(81) = O(1)
    // Iterate through the sudoku board to find most
    // constrained cells with fewest possible values
    private static int[] findMostConstrainedCell(int[][] board) {
        int[] result = null;
        int minOptions = GRID_SIZE + 1;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    List<Integer> possibleValues = getPossibleValues(board, row, col);
                    if (possibleValues.size() < minOptions) {
                        minOptions = possibleValues.size();
                        result = new int[] { row, col };
                    }
                }
            }
        }

        return result;
    }

    // Average Time Complexity: O(27) = O(1)
    // Return a list of possible values by checking
    // row, column and 3x3 sub-grid constrain
    private static List<Integer> getPossibleValues(int[][] board, int row, int col) {
        boolean[] used = new boolean[GRID_SIZE + 1];

        // Time Complexity: O(9)
        // Mark numbers used in the row
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] != 0) {
                used[board[row][i]] = true;
            }
        }

        // Time Complexity: O(9)
        // Mark numbers used in the column
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] != 0) {
                used[board[i][col]] = true;
            }
        }

        // Time Complexity: O(9)
        // Mark numbers used in the 3x3 subgrid
        int localBoxRow = row - row % 3;
        int localBoxCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[localBoxRow + i][localBoxCol + j] != 0) {
                    used[board[localBoxRow + i][localBoxCol + j]] = true;
                }
            }
        }
        // Average Time Complexity: O(1)
        // Collect all unused numbers
        List<Integer> possibleValues = new ArrayList<>();
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (!used[num]) {
                possibleValues.add(num);
            }
        }

        return possibleValues;
    }

    //AI prompt: write a java function named isValidBoard that check 
    //if a 2D array sudoku board input is corrected without duplicated values
    //and satisfied sudoku rules
    @Override
    public boolean isValidBoard(int[][] board) {
        int n = 9;
        // Check rows and columns
        for (int i = 0; i < n; i++) {
            boolean[] rowUsed = new boolean[n + 1];
            boolean[] colUsed = new boolean[n + 1];
            for (int j = 0; j < n; j++) {
                int rowVal = board[i][j];
                int colVal = board[j][i];
                if (rowVal != 0) {
                    if (rowUsed[rowVal]) return false;
                    rowUsed[rowVal] = true;
                }
                if (colVal != 0) {
                    if (colUsed[colVal]) return false;
                    colUsed[colVal] = true;
                }
            }
        }
        // Check 3x3 subgrids
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                boolean[] boxUsed = new boolean[n + 1];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        int val = board[boxRow * 3 + i][boxCol * 3 + j];
                        if (val != 0) {
                            if (boxUsed[val]) return false;
                            boxUsed[val] = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void printBoard(int[][] board) {
        System.out.println("Sudoku Board:");
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("- - - + - - - + - - -");
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