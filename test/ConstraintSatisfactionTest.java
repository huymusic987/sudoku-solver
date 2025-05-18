package test;

import algorithms.ConstraintSatisfaction;
import algorithms.SudokuSolver;
import structures.List;
import utils.SudokuIOHandling;
import utils.SudokuTestUtils;

public class ConstraintSatisfactionTest {
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

            SudokuSolver csp = new ConstraintSatisfaction();

            SudokuTestUtils.testSolver(csp, puzzles, difficulty, true);
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
        ConstraintSatisfaction csp = new ConstraintSatisfaction();
        System.out.println("\n----------------------");
        System.out.println("Sudoku puzzle example:");
        csp.printBoard(puzzle);
        System.out.println("----------------------");
        System.out.println("Solved solution!");
        csp.solve(puzzle);
        csp.printBoard(puzzle);
    }
}