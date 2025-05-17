import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConstraintSatisfaction {
        public static void main(String[] args) {
        
        //Load solvable sudoku
        String[] difficulties = { "easy", "medium", "hard", "very_hard" };
        String basePath = "sudoku-solver/SudokuTest/";

        for (String difficulty : difficulties) {
            String puzzleFile = basePath + difficulty + "_puzzles.txt";
            List<int[][]> puzzles = SudokuTest.loadSudokuPuzzles(puzzleFile);

            if (puzzles == null) {
                System.out.println("Error loading " + difficulty + " puzzles");
                continue;
            }
            int puzzleCount = puzzles.size();
            int constraintCorrectCount = 0;
            long constraintTotalTime = 0;

            for (int i = 0; i < puzzleCount; i++) {
                int[][] puzzle = SudokuTest.copy(puzzles.get(i));
                long startTime = System.nanoTime();
                boolean solved = constraintSatisfaction(puzzle);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                constraintTotalTime += duration;

                if (solved && isSolved(puzzle)) {
                System.out.print("The board was solved correctly!");
                SudokuTest.printBoard(puzzle);
                constraintCorrectCount++;
                }
            }

            double constraintAvgTimeMs = (puzzleCount > 0) ? (constraintTotalTime /
            1_000_000.0) / puzzleCount : 0;
            System.out.printf("%s (Constraint Satisfaction): %d/%d puzzles solved correctly, Average time: %.4f ms%n",
            difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
            constraintCorrectCount, puzzleCount, constraintAvgTimeMs);
            System.out.println("--------------------------------------------------------------------------------------");
        }

        // Load unsolvable puzzles
        String unsolvableFile = basePath + "unsolvable_puzzles.txt";
        List<int[][]> unsolvablePuzzles = SudokuTest.loadSudokuPuzzles(unsolvableFile);
        if (unsolvablePuzzles == null) {
            System.out.println("Error loading unsolvable puzzles");
            return;
        }

        //Test unsolvable sudoku
        System.out.println("\nUnsolvable Puzzles (Constraint Satisfaction):");
        for (int i = 0; i < unsolvablePuzzles.size(); i++) {
            int[][] puzzle = SudokuTest.copy(unsolvablePuzzles.get(i));
            System.out.printf("Attempting unsolvable puzzle %d:%n", i + 1);
            long startTime = System.nanoTime();
            boolean result = constraintSatisfaction(puzzle);
            long endTime = System.nanoTime();
            double timeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Result: %s, Time: %.4f ms%n",
                    !result ? "Returned true (unexpected)" : "Returned false (expected)", timeMs);
            if (!result) {
                System.out.println("Solved board (should be invalid):");
                SudokuTest.printBoard(puzzle);
            }
        }
    }
    
    private static final int GRID_SIZE = 9;

    public static int[][] solve(int[][] board) {
        final int[][] copiedBoard = SudokuTest.copy(board);

        ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Future<Boolean> future = executor.submit(() -> constraintSatisfaction(copiedBoard));

        try {
            boolean solved = future.get(2, TimeUnit.MINUTES);
            if (!isSolved(board) && solved) {
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

    // Average complexity: O(n)
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

    // Average Complexity: O(n^3)
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

    // Average Complexity: O(n)
    private static List<Integer> getPossibleValues(int[][] board, int row, int col) {
        boolean[] used = new boolean[GRID_SIZE + 1];

        // Mark numbers used in the row
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] != 0) {
                used[board[row][i]] = true;
            }
        }

        // Mark numbers used in the column
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] != 0) {
                used[board[i][col]] = true;
            }
        }

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

        // Collect all unused numbers
        List<Integer> possibleValues = new ArrayList<>();
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (!used[num]) {
                possibleValues.add(num);
            }
        }

        return possibleValues;
    }

    public static boolean isSolved(int[][] board) {
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
                if (used[num]) return false;
                used[num] = true;
            }
        }
        
        // Check column constraint
        for (int col = 0; col < GRID_SIZE; col++) {
            boolean[] used = new boolean[GRID_SIZE + 1];
            for (int row = 0; row < GRID_SIZE; row++) {
                int num = board[row][col];
                if (used[num]) return false;
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
                        if (used[num]) return false;
                        used[num] = true;
                    }
                }
            }
        }
        
        return true;
    }
}