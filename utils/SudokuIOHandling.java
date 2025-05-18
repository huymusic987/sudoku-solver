package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import structures.ArrayList;
import structures.List;

public class SudokuIOHandling {
    // AI Prompt: Write a Java method loadSudokuPuzzles(String filename) that reads
    // multiple 9×9 Sudoku boards from a file. Boards are separated by blank lines.
    // Each line has 9 space-separated numbers (0–9). Return the boards in the form
    // of List<int[][]>.
    // Validate format and values. On error, print a message and return null.

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
}
