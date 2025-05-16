public class ConstraintSatisfaction {
    private static final int GRID_SIZE = 9;

    public static int[][] solve(int[][] board) {
        //check if the solved board not contains duplicated cells
        if (!isSolved(board)) {
            return null;
        }
        //
        if (constraintSatisfaction(board)) {
            return board;
        }
        return null;
    }

    // Average complexity: O(n)
    public static boolean constraintSatisfaction(int[][] board) {
        // Find the most constrained cell (cell with the fewest possible values)
        int[] cell = findMostConstrainedCell(board);
        if (cell == null) {
            return true;
        }

        int row = cell[0];
        int col = cell[1];
        List<Integer> possibleValues = getPossibleValues(board, row, col);

        // Try each possible value
        for (int i = 0; i < possibleValues.size(); i++) {
            int value = possibleValues.get(i);
            board[row][col] = value;
            if (constraintSatisfaction(board)) {
                return true;
            }
            board[row][col] = 0;
        }

        return false;
    }

    // Average Complexity: O(n^3)
    private static int[] findMostConstrainedCell(int[][] board) {
        int[] result = null;
        int minOptions = GRID_SIZE + 1;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    List<Integer> possibleValues = getPossibleValues(board, row, col);
                    if (possibleValues.size() < minOptions) {
                        minOptions = possibleValues.size();
                        result = new int[] { row, col };
                    }
                }
            }
        }

        return result;
    }

    // Average Complexity: O(n)
    private static List<Integer> getPossibleValues(int[][] board, int row, int col) {
        boolean[] used = new boolean[GRID_SIZE + 1];

        // Mark numbers used in the row
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] != 0) {
                used[board[row][i]] = true;
            }
        }

        // Mark numbers used in the column
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] != 0) {
                used[board[i][col]] = true;
            }
        }

        // Mark numbers used in the 3x3 subgrid
        int localBoxRow = row - row % 3;
        int localBoxCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[localBoxRow + i][localBoxCol + j] != 0) {
                    used[board[localBoxRow + i][localBoxCol + j]] = true;
                }
            }
        }

        // Collect all unused numbers
        List<Integer> possibleValues = new ArrayList<>();
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (!used[num]) {
                possibleValues.add(num);
            }
        }

        return possibleValues;
    }

    public static boolean isSolved(int[][] board) {
        // Check if the board is filled
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        
        // Check row constraint
        for (int row = 0; row < GRID_SIZE; row++) {
            boolean[] used = new boolean[GRID_SIZE + 1];
            for (int col = 0; col < GRID_SIZE; col++) {
                int num = board[row][col];
                if (used[num]) return false;
                used[num] = true;
            }
        }
        
        // Check column constraint
        for (int col = 0; col < GRID_SIZE; col++) {
            boolean[] used = new boolean[GRID_SIZE + 1];
            for (int row = 0; row < GRID_SIZE; row++) {
                int num = board[row][col];
                if (used[num]) return false;
                used[num] = true;
            }
        }
        
        // Check 3x3 subgrid constraint
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                boolean[] used = new boolean[GRID_SIZE + 1];
                for (int row = boxRow * 3; row < boxRow * 3 + 3; row++) {
                    for (int col = boxCol * 3; col < boxCol * 3 + 3; col++) {
                        int num = board[row][col];
                        if (used[num]) return false;
                        used[num] = true;
                    }
                }
            }
        }
        
        return true;
    }
}