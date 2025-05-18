package test;

import algorithms.Backtracking;
import algorithms.RMIT_Sudoku_Solver;
import structures.List;
import utils.SudokuIOHandling;

public class BacktrackingTest {
    public static void main(String args[]) {
        String basePath = "puzzles/";
        String puzzleFile = basePath + "medium" + "_puzzles.txt";
        List<int[][]> puzzles = SudokuIOHandling.loadSudokuPuzzles(puzzleFile);
        int[][] puzzle = puzzles.get(0);
        RMIT_Sudoku_Solver solver = new Backtracking();
        long totalTime = 0;
        try {
            System.out.println("Original Sudoku puzzle:");
            Backtracking.printBoard(puzzle);
            long startTime = System.nanoTime();
            int[][] solved = solver.solve(puzzle);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);

            if (solved != null && solver.isValidBoard(solved)) {
                System.out.println("Sudoku solved successfully!");
                Backtracking.printBoard(solved);
                System.out.printf("Avg Time: %.4f ms%n", totalTime / 1_000_000.0);
            } else {
                System.out.println("Failed to solve the Sudoku puzzle.");
            }
        } catch (Exception e) {
            System.out.printf("%s algorithm error message on medium difficulty puzzle: %s%n",
                    solver.getClass().getSimpleName(), e.getMessage());
        }
    }
}
