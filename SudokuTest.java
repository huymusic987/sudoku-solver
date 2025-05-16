import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SudokuTest {
    public static void main(String[] args) {
        String[] difficulties = { "easy", "medium", "hard", "very_hard" };
        String basePath = "SudokuTest/";

        Backtracking backtracking = new Backtracking();


        int geneticCorrectCount = 0;
        long geneticTotalTime = 0;
        int population_size = 100;
        double mutation_rate = 0.2;
        int max_generations = 10;

        // Scale the parameters mathematically each iteration (easy -> very hard)
        CulturalGenetic CulturalGeneticSolver = new CulturalGenetic(population_size,
                mutation_rate, max_generations);

        // Test Backtracking, Genetic Algorithm, and Constraint Satisfaction for each
        // difficulty
        for (String difficulty : difficulties) {
            String puzzleFile = basePath + difficulty + "_puzzles.txt";
            List<int[][]> puzzles = loadSudokuPuzzles(puzzleFile);

            if (puzzles == null) {
                System.out.println("Error loading " + difficulty + " puzzles");
                continue;
            }

            int puzzleCount = puzzles.size();

            // Backtracking Algorithm Testing
            int backtrackCorrectCount = 0;
            long backtrackTotalTime = 0;

            for (int i = 0; i < puzzleCount; i++) {
                int[][] puzzle = copy(puzzles.get(i));
                long startTime = System.nanoTime();
                int[][] solvedBoard = backtracking.solve(puzzle);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                backtrackTotalTime += duration;

                if (solvedBoard != null && isValidBoard(solvedBoard)) {
                    backtrackCorrectCount++;
                }
            }

            double backtrackAvgTimeMs = (puzzleCount > 0) ? (backtrackTotalTime / 1_000_000.0) / puzzleCount : 0;
            System.out.printf("%s (Backtracking): %d/%d puzzles solved correctly, Average time: %.4f ms%n",
                    difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
                    backtrackCorrectCount, puzzleCount, backtrackAvgTimeMs);

            // Genetic Algorithm Testing

            for (int i = 0; i < puzzleCount; i++) {
                int[][] puzzle = copy(puzzles.get(i));
                long startTime = System.nanoTime();
                int[][] solvedPuzzle = CulturalGeneticSolver.solve(puzzle);
                // CulturalGenetic.printGAConfig(population_size, mutation_rate,
                // max_generations);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                geneticTotalTime += duration;

                if (solvedPuzzle != null && isValidBoard(solvedPuzzle)) {
                    geneticCorrectCount++;
                }
                population_size = population_size * 100;
                mutation_rate = mutation_rate * 2;
                max_generations = max_generations * 2;

            }

            double geneticAvgTimeMs = (puzzleCount > 0) ? (geneticTotalTime / 1_000_000.0) / puzzleCount : 0;
            System.out.printf("%s (Genetic Algorithm): %d/%d puzzles solved correctly, Average time: %.4f ms%n",
                    difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
                    geneticCorrectCount, puzzleCount, geneticAvgTimeMs);

            //Constraint Satisfaction Testing
            int constraintCorrectCount = 0;
            long constraintTotalTime = 0;

            for (int i = 0; i < puzzleCount; i++) {
                int[][] puzzle = copy(puzzles.get(i));
                long startTime = System.nanoTime();
                int[][] solved = ConstraintSatisfaction.solve(puzzle);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                constraintTotalTime += duration;

                if (solved != null && isValidBoard(puzzle)) {
                constraintCorrectCount++;
                }
            }

            double constraintAvgTimeMs = (puzzleCount > 0) ? (constraintTotalTime /
            1_000_000.0) / puzzleCount : 0;
            System.out.printf("%s (Constraint Satisfaction): %d/%d puzzles solved correctly, Average time: %.4f ms%n",
            difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
            constraintCorrectCount, puzzleCount, constraintAvgTimeMs);
        }

        // Process unsolvable puzzles
        String unsolvableFile = basePath + "unsolvable_puzzles.txt";
        List<int[][]> unsolvablePuzzles = loadSudokuPuzzles(unsolvableFile);
        if (unsolvablePuzzles == null) {
            System.out.println("Error loading unsolvable puzzles");
            return;
        }

        System.out.println("\nUnsolvable Puzzles (Backtracking):");
        for (int i = 0; i < unsolvablePuzzles.size(); i++) {
            int[][] copiedPuzzle = copy(unsolvablePuzzles.get(i));
            System.out.printf("Attempting unsolvable puzzle %d:%n", i + 1);
            long startTime = System.nanoTime();
            int[][] solvedBoard = backtracking.solve(copiedPuzzle);
            long endTime = System.nanoTime();
            double timeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Result: %s, Time: %.4f ms%n",
                    solvedBoard != null && isValidBoard(solvedBoard) ? "Returned true (unexpected)"
                            : "Returned false (expected)",
                    timeMs);
            if (solvedBoard != null) {
                System.out.println("Solved board (should be invalid):");
                printBoard(copiedPuzzle);
            }
        }

        System.out.println("\nUnsolvable Puzzles (Genetic Algorithm):");
        for (int i = 0; i < unsolvablePuzzles.size(); i++) {
            int[][] puzzle = copy(unsolvablePuzzles.get(i));
            System.out.printf("Attempting unsolvable puzzle %d:%n", i + 1);
            long startTime = System.nanoTime();
            int[][] solvedPuzzle = CulturalGeneticSolver.solve(puzzle);
            long endTime = System.nanoTime();
            double timeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Result: %s, Time: %.4f ms%n",
                    solvedPuzzle != null ? "Returned a board (unexpected)" : "Returned null (expected)", timeMs);
            if (solvedPuzzle != null) {
                System.out.println("Solved board (should be invalid):");
                printBoard(solvedPuzzle);
            }
        }

        System.out.println("\nUnsolvable Puzzles (Constraint Satisfaction):");
        for (int i = 0; i < unsolvablePuzzles.size(); i++) {
            int[][] puzzle = copy(unsolvablePuzzles.get(i));
            System.out.printf("Attempting unsolvable puzzle %d:%n", i + 1);
            long startTime = System.nanoTime();
            int[][] result = ConstraintSatisfaction.solve(puzzle);
            long endTime = System.nanoTime();
            double timeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Result: %s, Time: %.4f ms%n",
                    result != null ? "Returned true (unexpected)" : "Returned false (expected)", timeMs);
            if (result != null) {
                System.out.println("Solved board (should be invalid):");
                printBoard(result);
            }
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

    public static void printBoard(int[][] board) {
        System.out.println();
        for (int row = 0; row < 9; row++) {
            if ((row % 3 == 0) && (row != 0)) {
                System.out.println("-----------------------------");
            }
            for (int col = 0; col < 9; col++) {
                if ((col % 3 == 0) && (col != 0)) {
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
}