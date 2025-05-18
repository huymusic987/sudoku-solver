package algorithms;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import structures.ArrayList;
import structures.List;
import utils.SudokuIOHandling;
import utils.SudokuTestUtils;

public class ConstraintSatisfaction implements SudokuSolver {
    public static void main(String[] args) {
        String[] difficulties = { "easy", "medium", "hard", "very_hard", "unsolvable" };
        String basePath = "sudoku-solver/puzzles/";

        for (String difficulty : difficulties) {
            String puzzleFile = basePath + difficulty + "_puzzles.txt";
            List<int[][]> puzzles = SudokuIOHandling.loadSudokuBoards(puzzleFile);

            if (puzzles == null) {
                System.out.println("Error loading " + difficulty + " puzzles");
                continue;
            }

            System.out.println("\nTesting difficulty: " + difficulty);

            ConstraintSatisfaction csp = new ConstraintSatisfaction();

            SudokuTestUtils.testSolver(csp, puzzles, difficulty, true);
        }
    }

    private static final int GRID_SIZE = 9;

    // Funtion called sudoku solver return solved board or
    // return null whenever it exceed 2 minutes or an error is occured
    @Override
    public int[][] solve(int[][] board) {

        ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Future<Boolean> future = executor.submit(() -> constraintSatisfaction(board));

        try {
            boolean solved = future.get(2, TimeUnit.MINUTES);
            if (!isValidBoard(board)) {
                throw new IllegalArgumentException("Invalid puzzle board input.");
            }
            if (isValidBoard(board) && solved) {
                return board;
            }
        } catch (TimeoutException e) {
            System.out.println("Solver runtime exceed 2 minutes.");
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
        return null;
    }

    // Average Time Complexity: O(n^k)
    // n is the number of unassigned cells
    // k is the number of possible values for each cell
    // Worst Case: O(9^81)
    public static boolean constraintSatisfaction(int[][] board) {
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
            if (constraintSatisfaction(board)) {
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

    @Override
    public boolean isValidBoard(int[][] board) {
        // Check if the board is filled
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }

        // Check row constraint
        for (int row = 0; row < GRID_SIZE; row++) {
            boolean[] used = new boolean[GRID_SIZE + 1];
            for (int col = 0; col < GRID_SIZE; col++) {
                int num = board[row][col];
                if (used[num])
                    return false;
                used[num] = true;
            }
        }

        // Check column constraint
        for (int col = 0; col < GRID_SIZE; col++) {
            boolean[] used = new boolean[GRID_SIZE + 1];
            for (int row = 0; row < GRID_SIZE; row++) {
                int num = board[row][col];
                if (used[num])
                    return false;
                used[num] = true;
            }
        }

        // Check 3x3 subgrid constraint
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                boolean[] used = new boolean[GRID_SIZE + 1];
                for (int row = boxRow * 3; row < boxRow * 3 + 3; row++) {
                    for (int col = boxCol * 3; col < boxCol * 3 + 3; col++) {
                        int num = board[row][col];
                        if (used[num])
                            return false;
                        used[num] = true;
                    }
                }
            }
        }

        return true;
    }
}