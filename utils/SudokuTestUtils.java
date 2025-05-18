package utils;

import algorithms.SudokuSolver;
import structures.List;

public class SudokuTestUtils {
    public static void testSolver(SudokuSolver solver, List<int[][]> puzzles, String difficulty,
            boolean printErrorDetails) {
        int correctCount = 0;
        long totalTime = 0;
        int puzzleCount = puzzles.size();
        boolean hasException = false;

        for (int i = 0; i < puzzleCount; i++) {
            int[][] puzzle = copy(puzzles.get(i));
            try {
                long startTime = System.nanoTime();
                int[][] solved = solver.solve(puzzle);
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);

                if (solved != null && solver.isValidBoard(solved)) {
                    correctCount++;
                } else if (solved != null && printErrorDetails) {
                    System.out.printf("%s produced an invalid board for %s puzzle #%d:%n",
                            solver.getClass().getSimpleName(), difficulty, i + 1);
                }
            } catch (RuntimeException e) {
                System.out.printf("%s algorithm error message on %s puzzle #%d: %s%n",
                        solver.getClass().getSimpleName(), difficulty, i + 1, e.getMessage());
                hasException = true;
            }
        }

        if (!hasException) {
            double avgTimeMs = puzzleCount > 0 ? totalTime / 1_000_000.0 / puzzleCount : 0;
            System.out.printf("%s: %d/%d solved correctly, Avg Time: %.4f ms%n",
                    solver.getClass().getSimpleName(),
                    correctCount, puzzleCount, avgTimeMs);
        }
    }

    public static int[][] copy(int[][] original) {
        int[][] copyboard = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                copyboard[i][j] = original[i][j];
            }
        }
        return copyboard;
    }
}