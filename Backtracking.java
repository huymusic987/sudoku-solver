import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Backtracking implements SudokuSolver {
    public static void main(String args[]) {
        String[] difficulties = { "easy", "medium", "hard", "very_hard", "unsolvable" };
        String basePath = "SudokuTest/";

        for (String difficulty : difficulties) {
            String puzzleFile = basePath + difficulty + "_puzzles.txt";
            List<int[][]> puzzles = SudokuTestUtils.loadSudokuPuzzles(puzzleFile);

            if (puzzles == null) {
                System.out.println("Error loading " + difficulty + " puzzles");
                continue;
            }

            System.out.println("\nTesting difficulty: " + difficulty);

            SudokuSolver backtracking = new Backtracking();

            SudokuTestUtils.testSolver(backtracking, puzzles, difficulty, true);
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

    @Override
    public int[][] solve(int[][] puzzle) {
        if (!isValidBoard(puzzle)) {
            throw new IllegalArgumentException("Invalid puzzle board input.");
        }

        long startTime = System.currentTimeMillis();

        if (BacktrackingRecursive(puzzle, startTime)) {
            return puzzle;
        } else {
            throw new RuntimeException("Backtracking failed to solve the puzzle.");
        }
    }

    private boolean isValidNumber(int[][] board, int number, int row, int column) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == number) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (board[i][column] == number) {
                return false;
            }
        }

        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = column - column % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean BacktrackingRecursive(int[][] board, long startTime) {
        if (System.currentTimeMillis() - startTime > 120000) {
            throw new RuntimeException("Backtracking exceeded time limit of 2 minutes");
        }

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board[row][column] == 0) {
                    for (int numberToTry = 1; numberToTry <= 9; numberToTry++) {
                        if (isValidNumber(board, numberToTry, row, column)) {
                            board[row][column] = numberToTry;
                            if (BacktrackingRecursive(board, startTime)) {
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

    @Override
    public boolean isValidBoard(int[][] board) {
        // Check rows
        for (int row = 0; row < 9; row++) {
            boolean[] seen = new boolean[10];
            for (int col = 0; col < 9; col++) {
                int val = board[row][col];
                if (val != 0) {
                    if (seen[val])
                        return false;
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
                    if (seen[val])
                        return false;
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
                            if (seen[val])
                                return false;
                            seen[val] = true;
                        }
                    }
                }
            }
        }

        return true;
    }
}