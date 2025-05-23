package test;

import algorithms.SimpleGenetic;
import algorithms.RMIT_Sudoku_Solver;
import structures.List;
import utils.SudokuIOHandling;
import utils.SudokuTestUtils;

public class SimpleGeneticTest {
    // ------------------------------------------------------------------------------------------------
    // Main code
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

            RMIT_Sudoku_Solver SimpleGenetic = new SimpleGenetic(100, 0.2,
                    10, "Merge Sort");

            SudokuTestUtils.testSolver(SimpleGenetic, puzzles, difficulty, true);
        }

        // Additional: Solve single puzzle in details to show algorithm's operation
        int[][] puzzle = {
                { 5, 3, 0, 0, 7, 0, 0, 0, 0 },
                { 6, 0, 0, 1, 9, 5, 0, 0, 0 },
                { 0, 9, 8, 0, 0, 0, 0, 6, 0 },
                { 8, 0, 0, 0, 6, 0, 0, 0, 3 },
                { 4, 0, 0, 8, 0, 3, 0, 0, 1 },
                { 7, 0, 0, 0, 2, 0, 0, 0, 6 },
                { 0, 6, 0, 0, 0, 0, 2, 8, 0 },
                { 0, 0, 0, 4, 1, 9, 0, 0, 5 },
                { 0, 0, 0, 0, 8, 0, 0, 7, 9 }
        };
        System.out.println("\nDetails of Algorithms:");
        System.out.println("Initial puzzle: ");
        SimpleGenetic.printBoard(puzzle);
        SimpleGenetic SimpleGeneticDetails = new SimpleGenetic(1000, 0.2,
                10, "Merge Sort");
        SimpleGeneticDetails.SolveDetails(puzzle, true);

    }
}