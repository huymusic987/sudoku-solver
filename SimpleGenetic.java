import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleGenetic {

    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int POPULATION_SIZE = 100;
    private static final double MUTATION_RATE = 0.5;
    private static final int MAX_GENERATIONS = 10;
    private static final Random RANDOM = new Random();

    private static class Individual {
        int[][] board;
        int fitness;

        public Individual(int[][] initialBoard) {
            this.board = generateRandomFilledBoard(initialBoard);
            this.fitness = calculateFitness(this.board);
        }
    }

    public static int[][] solveSudokuGA(int[][] initialBoard) {
        List<Individual> population = initializePopulation(initialBoard);
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            population.sort((a, b) -> Integer.compare(a.fitness, b.fitness));
            if (population.get(0).fitness == 0) {
                return population.get(0).board;
            }
            List<Individual> nextGeneration = new ArrayList<>();
            nextGeneration.addAll(population.subList(0, POPULATION_SIZE / 2));
            while (nextGeneration.size() < POPULATION_SIZE) {
                Individual parent1 = tournamentSelection(population);
                Individual parent2 = tournamentSelection(population);
                int[][] childBoard = crossover(parent1.board, parent2.board, initialBoard);
                mutate(childBoard, initialBoard);
                nextGeneration.add(new Individual(childBoard));
            }
            population = nextGeneration;
        }
        return population.get(0).board;
    }

    private static List<Individual> initializePopulation(int[][] initialBoard) {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new Individual(initialBoard));
        }
        return population;
    }

    private static int[][] generateRandomFilledBoard(int[][] initialBoard) {
        int[][] board = copyBoard(initialBoard);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == 0) {
                    List<Integer> possible = getPossibleValues(board, i, j);
                    board[i][j] = possible.isEmpty() ? RANDOM.nextInt(9) + 1
                            : possible.get(RANDOM.nextInt(possible.size()));
                }
            }
        }
        return board;
    }

    private static Individual tournamentSelection(List<Individual> population) {
        int tournamentSize = 5;
        List<Individual> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(RANDOM.nextInt(population.size())));
        }
        return tournament.stream().min((a, b) -> Integer.compare(a.fitness, b.fitness)).orElse(null);
    }

    private static int[][] crossover(int[][] parent1, int[][] parent2, int[][] initialBoard) {
        int[][] child = copyBoard(initialBoard);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (child[i][j] == 0) {
                    child[i][j] = RANDOM.nextBoolean() ? parent1[i][j] : parent2[i][j];
                }
            }
        }
        return child;
    }

    private static void mutate(int[][] board, int[][] initialBoard) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (initialBoard[i][j] == 0 && RANDOM.nextDouble() < MUTATION_RATE) {
                    List<Integer> possibleValues = getPossibleValues(board, i, j);
                    if (!possibleValues.isEmpty()) {
                        board[i][j] = possibleValues.get(RANDOM.nextInt(possibleValues.size()));
                    }
                }
            }
        }
    }

    private static int calculateFitness(int[][] board) {
        int conflicts = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            conflicts += countDuplicates(getRow(board, i));
            conflicts += countDuplicates(getColumn(board, i));
        }
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                conflicts += countDuplicates(getSubgrid(board, i * SUBGRID_SIZE, j * SUBGRID_SIZE));
            }
        }
        return conflicts;
    }

    private static int countDuplicates(int[] arr) {
        List<Integer> seen = new ArrayList<>();
        int duplicates = 0;
        for (int num : arr) {
            if (num != 0 && seen.contains(num)) {
                duplicates++;
            }
            seen.add(num);
        }
        return duplicates;
    }

    private static int[] getRow(int[][] board, int row) {
        return board[row];
    }

    private static int[] getColumn(int[][] board, int col) {
        int[] column = new int[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            column[i] = board[i][col];
        }
        return column;
    }

    private static int[] getSubgrid(int[][] board, int startRow, int startCol) {
        int[] subgrid = new int[GRID_SIZE];
        int index = 0;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                subgrid[index++] = board[startRow + i][startCol + j];
            }
        }
        return subgrid;
    }

    private static List<Integer> getPossibleValues(int[][] board, int row, int col) {
        List<Integer> possibleValues = new ArrayList<>();
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (isValidPlacement(board, num, row, col)) {
                possibleValues.add(num);
            }
        }
        return possibleValues;
    }

    private static boolean isValidPlacement(int[][] board, int num, int row, int col) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num)
                return false;
        }
        int startRow = row - row % SUBGRID_SIZE;
        int startCol = col - col % SUBGRID_SIZE;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[startRow + i][startCol + j] == num)
                    return false;
            }
        }
        return true;
    }

    private static int[][] copyBoard(int[][] source) {
        int[][] destination = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(source[i], 0, destination[i], 0, GRID_SIZE);
        }
        return destination;
    }
}