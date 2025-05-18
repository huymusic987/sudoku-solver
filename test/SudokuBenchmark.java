package test;

import algorithms.Backtracking;
import algorithms.ConstraintSatisfaction;
import algorithms.SimpleGenetic;
import algorithms.SudokuSolver;
import structures.List;
import utils.SudokuIOHandling;
import utils.SudokuTestUtils;

public class SudokuBenchmark {
    public static void main(String[] args) {
        String[] difficulties = { "easy", "medium", "hard", "very_hard", "unsolvable" };
        String basePath = "puzzles/";

        for (String difficulty : difficulties) {
            String puzzleFile = basePath + difficulty + "_puzzles.txt";
            List<int[][]> puzzles = SudokuIOHandling.loadSudokuBoards(puzzleFile);

            if (puzzles == null) {
                System.out.println("Error loading " + difficulty + " puzzles");
                continue;
            }

            System.out.println("\nTesting difficulty: " + difficulty);

            // Test Backtracking Solver
            SudokuSolver backtracking = new Backtracking();
            SudokuTestUtils.testSolver(backtracking, puzzles, difficulty, true);

            // Configure Genetic Solver parameters (easy)
            int populationSize = 20;
            double mutationRate = 0.2;
            int maxGenerations = 10;

            // Scale parameters based on difficulty
            switch (difficulty) {
                case "medium" -> {
                    populationSize *= 5;
                    mutationRate *= 2;
                    maxGenerations *= 1;
                }
                case "hard" -> {
                    populationSize *= 20;
                    mutationRate *= 8;
                    maxGenerations *= 2;
                }
                case "very_hard", "unsolvable" -> {
                    populationSize *= 30;
                    mutationRate *= 16;
                    maxGenerations *= 3;
                }
            }

            // Test Genetic Solver
            SudokuSolver genetic = new SimpleGenetic(populationSize, mutationRate, maxGenerations,
                    "Merge Sort");
            SudokuTestUtils.testSolver(genetic, puzzles, difficulty, true);

            // Test Constraint Satisfaction Solver
            SudokuSolver csp = new ConstraintSatisfaction();
            SudokuTestUtils.testSolver(csp, puzzles, difficulty, true);
        }
    }
}