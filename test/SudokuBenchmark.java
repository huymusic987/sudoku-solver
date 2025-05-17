package test;

import algorithms.Backtracking;
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
            List<int[][]> puzzles = SudokuIOHandling.loadSudokuPuzzles(puzzleFile);

            if (puzzles == null) {
                System.out.println("Error loading " + difficulty + " puzzles");
                continue;
            }

            System.out.println("\nTesting difficulty: " + difficulty);

            // Test Backtracking Solver
            SudokuSolver backtracking = new Backtracking();
            SudokuTestUtils.testSolver(backtracking, puzzles, difficulty, true);

            // Configure Genetic Solver parameters
            int populationSize = 10;
            double mutationRate = 0.1;
            int maxGenerations = 5;

            // Scale parameters based on difficulty
            switch (difficulty) {
                case "medium" -> {
                    populationSize *= 2;
                    mutationRate *= 2;
                    maxGenerations *= 2;
                }
                case "hard" -> {
                    populationSize *= 4;
                    mutationRate *= 3;
                    maxGenerations *= 3;
                }
                case "very_hard", "unsolvable" -> {
                    populationSize *= 8;
                    mutationRate *= 5;
                    maxGenerations *= 5;
                }
            }

            // Test Genetic Solver
            // SudokuSolver genetic = new SimpleGeneticSolver(populationSize, mutationRate,
            // maxGenerations);
            // SudokuTestUtils.testSolver(genetic, puzzles, difficulty);

            // Test Constraint Satisfaction Solver
            SudokuSolver csp = new ConstraintSatisfaction();
            SudokuTestUtils.testSolver(csp, puzzles, difficulty, true);
        }
    }
}