package test;

import algorithms.Backtracking;
import algorithms.SudokuSolver;
import structures.List;
import utils.SudokuIOHandling;
import utils.SudokuTestUtils;

public class BacktrackingTest {
    public static void main(String args[]) {
        String basePath = "puzzles/";
        String puzzleFile = basePath + "hard" + "_puzzles.txt";
        List<int[][]> puzzles = SudokuIOHandling.loadSudokuPuzzles(puzzleFile);
        int[][] puzzle = puzzles.get(0);
        SudokuSolver solver = new Backtracking();
        long totalTime = 0;
        try {
            System.out.println("Original Sudoku puzzle:");
            SudokuTestUtils.printBoard(puzzle);
            long startTime = System.nanoTime();
            int[][] solved = solver.solve(puzzle);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);

            if (solved != null && solver.isValidBoard(solved)) {
                System.out.println("Sudoku solved successfully!");
                SudokuTestUtils.printBoard(solved);
                System.out.printf("Avg Time: %.4f ms%n", totalTime / 1_000_000.0);
            } else {
                System.out.println("Failed to solve the Sudoku puzzle.");
            }
        } catch (Exception e) {
            System.out.printf("%s algorithm error message on hard difficulty puzzle: %s%n",
                    solver.getClass().getSimpleName(), e.getMessage());
        }
    }
}
