import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SudokuTest2 {
    public static void main(String[] args) {
        String[] difficulties = { "easy", "medium", "hard", "very_hard", "unsolvable" };
        String basePath = "SudokuTest/";

        Backtracking backtracking = new Backtracking();

        int population_size = 10;
        double mutation_rate = 0.1;
        int max_generations = 5;

        SimpleGenetic SimpleGeneticSolver = new SimpleGenetic(population_size,
                mutation_rate, max_generations);

        // Test
        // Backtracking, Genetic and Constraint Satisfaction Algorithms
        // for each difficulty

        for (String difficulty : difficulties) {
            String puzzleFile = basePath + difficulty + "_puzzles.txt";
            List<int[][]> puzzles = loadSudokuPuzzles(puzzleFile);

            if (puzzles == null) {
                System.out.println("Error loading " + difficulty + " puzzles");
                continue;
            }

            int puzzleCount = puzzles.size();

            // =============== Backtracking Algorithm Testing ==============

            int backtrackCorrectCount = 0;
            long backtrackTotalTime = 0;
            boolean backtrackHasError = false;

            for (int i = 0; i < puzzleCount; i++) {
                int[][] puzzle = copy(puzzles.get(i));
                try {
                    long startTime = System.nanoTime();
                    int[][] solvedBoard = backtracking.solve(puzzle);
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;
                    backtrackTotalTime += duration;

                    if (solvedBoard != null && backtracking.isValidBoard(solvedBoard)) {
                        backtrackCorrectCount++;
                    }
                } catch (RuntimeException e) {
                    backtrackHasError = true;
                    System.out.printf("Backtracking returned error message on %s puzzle #%d: %s%n", difficulty, i + 1,
                            e.getMessage());
                }
            }

            if (!backtrackHasError) {
                double backtrackAvgTimeMs = (puzzleCount > 0) ? (backtrackTotalTime / 1_000_000.0) / puzzleCount : 0;
                System.out.printf("%s (Backtracking): %d/%d puzzles solved correctly, Average time: %.4f ms%n",
                        difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
                        backtrackCorrectCount, puzzleCount, backtrackAvgTimeMs);
            }

            // ======================= Genetic Algorithm Testing =======================
            int geneticCorrectCount = 0;
            long geneticTotalTime = 0;

            for (int i = 0; i < puzzleCount; i++) {
                int[][] puzzle = copy(puzzles.get(i));
                long startTime = System.nanoTime();
                int[][] solvedPuzzle = SimpleGeneticSolver.solve(puzzle);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                geneticTotalTime += duration;

                if (solvedPuzzle != null && isValidBoard(solvedPuzzle)) {
                    geneticCorrectCount++;
                }

            }

            // Scale the parameters up for SimpleGeneticSolver by level (Easy -> Very Hard)
            // Time may be scaled up a lot due to MAX_GENERATIONS and POPULATION_SIZE
            SimpleGeneticSolver.MAX_GENERATIONS *= 5;
            SimpleGeneticSolver.MUTATION_RATE *= 5;
            SimpleGeneticSolver.POPULATION_SIZE *= 2;

            double geneticAvgTimeMs = (puzzleCount > 0) ? (geneticTotalTime / 1_000_000.0) / puzzleCount : 0;
            System.out.printf("%s (Genetic Algorithm): %d/%d puzzles solved correctly, Average time: %.4f ms%n",
                    difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
                    geneticCorrectCount, puzzleCount, geneticAvgTimeMs);

            // ================== Constraint Satisfaction Testing =========================
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