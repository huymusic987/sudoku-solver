package utils;

import algorithms.SudokuSolver;
import structures.ArrayList;
import structures.List;

public class SudokuTestUtils {
    public static void testSolver(SudokuSolver solver, List<int[][]> boards, String difficulty,
            boolean printErrorDetails) {
        int correctCount = 0;
        long totalTime = 0;
        int puzzleCount = boards.size();
        boolean hasException = false;

        for (int i = 0; i < puzzleCount; i++) {
            int[][] puzzle = copy(boards.get(i));
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
                    printBoard(solved);
                    reportBoardErrors(solved);
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

    public static void printBoard(int[][] board) {
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

    public static void reportBoardErrors(int[][] board) {
        int totalErrors = 0;
        ArrayList<String> errorMessages = new ArrayList<>();

        // Check rows
        for (int i = 0; i < 9; i++) {
            int[] counts = new int[10]; // Index 0 unused, 1-9 for values
            for (int j = 0; j < 9; j++) {
                int value = board[i][j];
                if (value >= 1 && value <= 9) {
                    counts[value]++;
                    if (counts[value] > 1) {
                        errorMessages
                                .add(String.format("Duplicate value %d in row %d, column %d", value, i + 1, j + 1));
                        totalErrors++;
                    }
                }
            }
        }

        // Check columns
        for (int j = 0; j < 9; j++) {
            int[] counts = new int[10];
            for (int i = 0; i < 9; i++) {
                int value = board[i][j];
                if (value >= 1 && value <= 9) {
                    counts[value]++;
                    if (counts[value] > 1) {
                        errorMessages
                                .add(String.format("Duplicate value %d in column %d, row %d", value, j + 1, i + 1));
                        totalErrors++;
                    }
                }
            }
        }

        // Check 3x3 subgrids
        for (int block = 0; block < 9; block++) {
            int[] counts = new int[10];
            int startRow = (block / 3) * 3;
            int startCol = (block % 3) * 3;
            for (int i = startRow; i < startRow + 3; i++) {
                for (int j = startCol; j < startCol + 3; j++) {
                    int value = board[i][j];
                    if (value >= 1 && value <= 9) {
                        counts[value]++;
                        if (counts[value] > 1) {
                            errorMessages.add(String.format("Duplicate value %d in 3x3 subgrid at row %d, column %d",
                                    value, i + 1, j + 1));
                            totalErrors++;
                        }
                    }
                }
            }
        }

        // Print error details
        if (totalErrors > 0) {
            System.out.println("Errors found in the board:");

            for (int i = 0; i < errorMessages.size(); i++) {
                System.out.println("- " + errorMessages.get(i));
            }

            System.out.printf("Total errors: %d%n%n", totalErrors);
        } else {
            System.out.println("No specific errors detected (board may be incomplete or invalid for other reasons).%n");
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