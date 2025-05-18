package test;

import algorithms.Backtracking;
import algorithms.SudokuSolver;
import structures.List;
import utils.SudokuIOHandling;
import utils.SudokuTestUtils;

public class BacktrackingTest {
    public static void main(String args[]) {
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

            SudokuSolver backtracking = new Backtracking();

            SudokuTestUtils.testSolver(backtracking, puzzles, difficulty, true);
        }
    }
}
