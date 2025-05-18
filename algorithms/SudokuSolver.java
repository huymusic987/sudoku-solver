package algorithms;

public interface SudokuSolver {
    int[][] solve(int[][] puzzle);

    boolean isValidBoard(int[][] board);
}
