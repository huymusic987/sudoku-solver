import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SudokuTestUtils {
    public static void testSolver(SudokuSolver solver, List<int[][]> puzzles, String difficulty, boolean printDetails) {
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
                } else if (solved != null && printDetails) {
                    System.out.printf("%s produced an invalid board for %s puzzle #%d:%n",
                            solver.getClass().getSimpleName(), difficulty, i + 1);
                    printBoard(solved);
                    reportBoardErrors(solved);
                }
            } catch (RuntimeException e) {
                System.out.printf("%s error message on %s puzzle #%d: %s%n",
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

    public static List<int[][]> loadSudokuPuzzles(String filename) {
        List<int[][]> boards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int[][] board = null;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (board != null && row == 9) {
                        boards.add(board);
                    }
                    board = new int[9][9];
                    row = 0;
                    continue;
                }

                String[] values = line.trim().split("\\s+");
                if (values.length != 9) {
                    System.out.println("Invalid row format in file " + filename + ": " + line);
                    return null;
                }

                if (board == null) {
                    board = new int[9][9];
                }

                for (int col = 0; col < 9; col++) {
                    try {
                        int value = Integer.parseInt(values[col]);
                        if (value < 0 || value > 9) {
                            System.out.println("Invalid number in file " + filename + ": " + value);
                            return null;
                        }
                        board[row][col] = value;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format in file " + filename + ": " + values[col]);
                        return null;
                    }
                }
                row++;

                if (row == 9) {
                    boards.add(board);
                    board = null;
                    row = 0;
                }
            }

            if (board != null && row == 9) {
                boards.add(board);
            }

            return boards.size() == 0 ? null : boards;
        } catch (IOException e) {
            System.out.println("Error reading file " + filename + ": " + e.getMessage());
            return null;
        }
    }

    public static int[][] copy(int[][] original) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                copy[i][j] = original[i][j];
            }
        }
        return copy;
    }
}