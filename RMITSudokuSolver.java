import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RMITSudokuSolver {

        public static void main(String[] args) {
                String[] difficulties = { "easy", "medium", "hard", "very_hard" };
                String basePath = "SudokuTest/";

                // Process solvable puzzles
                for (String difficulty : difficulties) {
                        String puzzleFile = basePath + difficulty + "_puzzles.txt";
                        String solutionFile = basePath + difficulty + "_solutions.txt";

                        List<int[][]> puzzles = loadSudokuPuzzles(puzzleFile);
                        List<int[][]> solutions = loadSudokuPuzzles(solutionFile);

                        if (puzzles == null || solutions == null || puzzles.size() != solutions.size()) {
                                System.out.println("Error loading " + difficulty + " puzzles or solutions");
                                continue;
                        }

                        int correctCount = 0;
                        long totalTime = 0;
                        int puzzleCount = puzzles.size();

                        for (int i = 0; i < puzzleCount; i++) {
                                int[][] puzzle = deepCopy(puzzles.get(i));
                                long startTime = System.nanoTime();
                                boolean solved = solveBoard(puzzle);
                                long endTime = System.nanoTime();
                                long duration = endTime - startTime;
                                totalTime += duration;

                                if (solved && validateSolution(puzzle, solutions.get(i))) {
                                        correctCount++;
                                }
                        }

                        double avgTimeMs = (puzzleCount > 0) ? (totalTime / 1_000_000.0) / puzzleCount : 0;
                        System.out.printf("%s: %d/%d puzzles solved correctly, Average time: %.4f ms%n",
                                        difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
                                        correctCount, puzzleCount, avgTimeMs);
                }

                // Process unsolvable puzzles
                String unsolvableFile = basePath + "unsolvable_puzzles.txt";
                List<int[][]> unsolvablePuzzles = loadSudokuPuzzles(unsolvableFile);
                if (unsolvablePuzzles == null) {
                        System.out.println("Error loading unsolvable puzzles");
                        return;
                }

                System.out.println("\nUnsolvable Puzzles:");
                for (int i = 0; i < unsolvablePuzzles.size(); i++) {
                        int[][] puzzle = deepCopy(unsolvablePuzzles.get(i));
                        System.out.printf("Attempting unsolvable puzzle %d:%n", i + 1);
                        long startTime = System.nanoTime();
                        boolean result = solveBoard(puzzle);
                        long endTime = System.nanoTime();
                        double timeMs = (endTime - startTime) / 1_000_000.0;
                        System.out.printf("Result: %s, Time: %.4f ms%n",
                                        result ? "Returned true (unexpected)" : "Returned false (expected)", timeMs);
                        if (result) {
                                System.out.println("Solved board (should be invalid):");
                                printBoard(puzzle);
                        }
                }
        }

        // Load all puzzles or solutions from a file
        private static List<int[][]> loadSudokuPuzzles(String filename) {
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
                                                        System.out.println("Invalid number in file " + filename + ": "
                                                                        + value);
                                                        return null;
                                                }
                                                board[row][col] = value;
                                        } catch (NumberFormatException e) {
                                                System.out.println("Invalid number format in file " + filename + ": "
                                                                + values[col]);
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

                        return boards.isEmpty() ? null : boards;
                } catch (IOException e) {
                        System.out.println("Error reading file " + filename + ": " + e.getMessage());
                        return null;
                }
        }

        // Create a deep copy of a board
        private static int[][] deepCopy(int[][] original) {
                int[][] copy = new int[9][9];
                for (int i = 0; i < 9; i++) {
                        System.arraycopy(original[i], 0, copy[i], 0, 9);
                }
                return copy;
        }

        private static boolean isValidInitialBoard(int[][] board) {
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

        public static boolean validateSolution(int[][] result, int[][] expected) {
                for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                                if (result[i][j] != expected[i][j]) {
                                        return false;
                                }
                        }
                }
                return true;
        }

        private static void printBoard(int[][] board) {
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

        private static boolean isNumberInRow(int[][] board, int number, int row) {
                for (int i = 0; i < 9; i++) {
                        if (board[row][i] == number) {
                                return true;
                        }
                }
                return false;
        }

        private static boolean isNumberInColumn(int[][] board, int number, int column) {
                for (int i = 0; i < 9; i++) {
                        if (board[i][column] == number) {
                                return true;
                        }
                }
                return false;
        }

        private static boolean isNumberInBox(int[][] board, int number, int row, int column) {
                int localBoxRow = row - row % 3;
                int localBoxColumn = column - column % 3;

                for (int i = localBoxRow; i < localBoxRow + 3; i++) {
                        for (int j = localBoxColumn; j < localBoxColumn + 3; j++) {
                                if (board[i][j] == number) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        private static boolean isValidPlacement(int[][] board, int number, int row, int column) {
                return !isNumberInRow(board, number, row) &&
                                !isNumberInColumn(board, number, column) &&
                                !isNumberInBox(board, number, row, column);
        }

        private static boolean solveBoard(int[][] board) {
                if (!isValidInitialBoard(board)) {
                        return false;
                }
                return solveBoardRecursive(board);
        }

        private static boolean solveBoardRecursive(int[][] board) {
                for (int row = 0; row < 9; row++) {
                        for (int column = 0; column < 9; column++) {
                                if (board[row][column] == 0) {
                                        for (int numberToTry = 1; numberToTry <= 9; numberToTry++) {
                                                if (isValidPlacement(board, numberToTry, row, column)) {
                                                        board[row][column] = numberToTry;
                                                        if (solveBoardRecursive(board)) {
                                                                return true;
                                                        } else {
                                                                board[row][column] = 0;
                                                        }
                                                }
                                        }
                                        return false;
                                }
                        }
                }
                return true;
        }
}