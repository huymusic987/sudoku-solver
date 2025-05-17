import java.util.Random;

public class SimpleGenetic {

    //------------------------------------------------------------------------------------------------
    // Supporting properties
    private static final int generation_display = 1;
    private static final boolean generation_flag = false;
    private static final boolean merge_sort = true;

    // Sudoku board-type properties
    // Should be constant throughout the whole program
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final Random RANDOM = new Random();

    // Tunable parameters to optimize solving algorithm
    // The Complexity of this Genetic Algorithm is defined by 2 key manually-tunable parameters
    // Which are POPULATION_SIZE and MAX_GENERATIONS
    // Lets call their size are the notations P, M and G, respectively
    int POPULATION_SIZE = 0; // Number of solving candidates in 1 generation
    double MUTATION_RATE = 0.0; // Lower mutation for easy puzzles
    int MAX_GENERATIONS = 0; // Fewer generations needed for easy puzzles

    //------------------------------------------------------------------------------------------------
    // Data Structure 1: 2D Integer Array 
    // Main data structure representing the Sudoku board elements, 1st & 2nd dimension is row & column
    // The shape of main Sudoku board is of shape (GRID_SIZE, GRIZ_SIZE) where GRID_SIZE is fixed dim value
    // The shape of 9 subgrids within Sudoku board is of shape (SUBGRID_SIZE, SUBGRID_SIZE)
    // Accessing each element in the Sudoku board by accessing its index in 1st and 2nd axis of the tensor
    // E.g.: Retrieve tensor[2][3] will access element at row 2, column 3 of 2D tensor of shape (9, 9)

    //------------------------------------------------------------------------------------------------
    // Data Structure 2: Individual class
    // Consist of 2 properties: 2D Integer Array dtype - Sudoku board and Integer dtype - Fitness calculation 
    // Representing single Sudoku solver with appropriate solved element filled into the initial board
    // Fitness is added to indicate the potential colision number of error in the solving Sudoku board
    private static class Individual {
        int[][] board;
        int fitness;

        public Individual(int[][] puzzle) {
            this.board = generateRandomFilledBoard(puzzle);
            this.fitness = calculateFitness(this.board);
        }
    }
    //--------------------------------------------------------------------------------------
    // Data Structure 3: List<Individual>
    // List data structure where elements are of dtype Individual, declared from Individual class
    // Access the Individual dtype object and its properties by Object.properties

    //----------------------------------------------------------------------------------------
    // Constructor - accepting 3 parameters to declare the SimpleGeneticSudokuSolver object
    public SimpleGenetic(int POPULATION_SIZE, double MUTATION_RATE, int MAX_GENERATIONS) {
        this.POPULATION_SIZE = POPULATION_SIZE;
        this.MUTATION_RATE = MUTATION_RATE;
        this.MAX_GENERATIONS = MAX_GENERATIONS;
    }

    //--------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------
    // Method 1: solve(int[][] puzzle)
    // Data Structure: 2D Integer Array
    // Time Complexity: O(G * P(log(P)))
    // Space Complexity: O(P)
    // Main method of the program: Accept 2D Integer Array Sudoku Puzzle -> Solve it -> Return the solution
    public int[][] solve(int[][] puzzle) {
        List<Individual> population = initializePopulation(puzzle);

        //printGAConfig(POPULATION_SIZE, MUTATION_RATE, MAX_GENERATIONS);
        //if(merge_sort) {System.out.println("Implement Merge Sort Algorithm for the fitness in population list");}
        //else {System.out.println("Implement Bubble Sort Algorithm for the fitness in population list");}
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            if (merge_sort) {MergeSortPopulation(population);} // Use scratch sort
            else {BubbleSortPopulation(population);}

            if (population.get(0).fitness == 0) {
                //System.out.println("Solution found at generation: " + generation + " \n");
                //printBoard(population.get(0).board);
                return population.get(0).board;
            }

            List<Individual> nextGeneration = new ArrayList<>();
            // Keep the fittest (replacement for subList)
            for (int i = 0; i < POPULATION_SIZE / 2; i++) {
                nextGeneration.add(population.get(i));
            }

            while (nextGeneration.size() < POPULATION_SIZE) {
                Individual parent1 = tournamentSelection(population);
                Individual parent2 = tournamentSelection(population);
                int[][] childBoard = crossover(parent1.board, parent2.board, puzzle);
                mutate(childBoard, puzzle);
                nextGeneration.add(new Individual(childBoard));
            }

            population = nextGeneration;
            if (generation % generation_display == 0) {
                if (generation_flag) {
                    System.out.println("Generation " + generation + ", Best Fitness: " + population.get(0).fitness);
                }
            }
        }

        //System.out.println("Generation number: " + MAX_GENERATIONS);
        //System.out.println("Population size: " + POPULATION_SIZE);
        //System.out.println("Mutation rate: " + MUTATION_RATE);
        //System.out.println("Maximum generations reached. Best fitness: " + population.get(0).fitness);
        return population.get(0).board;
    }

    //---------------------------------------------------------------------------
    // Support Method 1: BubbleSortPopulation(List<Individual> population)
    // Data Structure: void
    // Time Complexity: O(N^2)
    // Space Complexity: O(1)
    // Bubble Sort implmentation to sort a List<Inidividual> dtype base on the Individual.fitness
    private void BubbleSortPopulation(List<Individual> population) {
        int n = population.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (population.get(j).fitness > population.get(j + 1).fitness) {
                    Individual temp = population.get(j);
                    population.set(j, population.get(j + 1));
                    population.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    //----------------------------------------------------------------------------------
    // Support Method 1: MergeSortPopulation(List<Inidividual> population)
    // Data Structure: void
    // Time Complexity: O(N * log(N))
    // Space Complexity: O(N)
    // Merge Sort Implementation to sort the List<Individual> population
    private void MergeSortPopulation(List<Individual> population) {
        if (population.size() > 1) {
            int n = population.size();
            int middle = n / 2;

            // Create 2 zeros sub population lists of individual from the population list
            List<Individual> SubPopulation1 = new ArrayList<Individual>();
            List<Individual> SubPopulation2 = new ArrayList<Individual>();

            // Append the half list to SubPop1 and remains to SubPop2
            for (int i = 0; i < middle; i++) {
                SubPopulation1.add(population.get(i));
            }

            for (int i = middle; i < n; i++) {
                SubPopulation2.add(population.get(i));
            }

            // Apply divide and conquer paradigm, recursively merge sort 2 sub lists
            MergeSortPopulation(SubPopulation1);
            MergeSortPopulation(SubPopulation2);

            // Merge the 2 sorted SubPopulation1 and SubPopulation2 into 1 sorted list
            MergePopulation(SubPopulation1, SubPopulation2, population);
        }
    }

    // Support Method for MergeSortPopulation: MergePopulation(sub1, sub2, dest) - List<Individual> dtype
    private void MergePopulation(List<Individual> sub1, List<Individual> sub2, List<Individual> dest) {
        int p1 = 0, p2 = 0, pDest = 0;
        while (p1 < sub1.size() && p2 < sub2.size()) {
            if (sub1.get(p1).fitness <= sub2.get(p2).fitness) {
                dest.set(pDest, sub1.get(p1));
                p1++;
            } else {
                dest.set(pDest, sub2.get(p2));
                p2++;
            }
            pDest++;
        }

        while (p1 < sub1.size()) {
            dest.set(pDest++, sub1.get(p1++));
        }

        while (p2 < sub2.size()) {
            dest.set(pDest++, sub2.get(p2++));
        }
    }

    //------------------------------------------------------------------------------------------------
    // Method 2: initializePopulation(int[][] puzzle)
    // Data Structure: List<Individual>
    // Time Complexity: O(P)
    // Space Compledxity: O(P)
    // Initialize the population list, where each Individual are single Sudoku board and its fitness value
    private List<Individual> initializePopulation(int[][] puzzle) {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new Individual(puzzle));
        }
        return population;
    }

    //------------------------------------------------------------------------------------------------
    // Method 3: generateRandomFilledBoard(int[][] puzzle)
    // Data Structure: 2D Integer Array
    // Time Complexity: O(1)
    // Space Complexity: O(1)
    // Generate the random solving filled Sudoku board, which may be incorrect or incorrect
    private static int[][] generateRandomFilledBoard(int[][] puzzle) {
        int[][] board = copyBoard(puzzle);
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE; i++) {
            numbers.add(i);
        }
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == 0) {
                    List<Integer> possible = getPossibleValues(board, i, j);
                    if (!possible.isEmpty()) {
                        board[i][j] = possible.get(RANDOM.nextInt(possible.size()));
                    } else {
                        board[i][j] = numbers.get(RANDOM.nextInt(numbers.size())); // Fallback
                    }
                }
            }
        }
        return board;
    }

    //------------------------------------------------------------------------------------------------
    // Method 4: tournamentSelection(List<Individual> population)
    // Data Structure: Individual
    // Time Complexity: O(1)
    // Space Complexity: O(1)
    private static Individual tournamentSelection(List<Individual> population) {
        int tournamentSize = 5;
        List<Individual> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(RANDOM.nextInt(population.size())));
        }
        Individual fittest = tournament.get(0);
        for (int i = 1; i < tournament.size(); i++) {
            if (tournament.get(i).fitness < fittest.fitness) {
                fittest = tournament.get(i);
            }
        }
        return fittest;
    }

    //------------------------------------------------------------------------------------------------
    // Method 5: crossover(int[][] parent1, int[][] parent2, int[][] puzzle)
    // Data Structure: 2D Integer Array
    // Time Complexity: O(1)
    // Space Complexity: O(1)
    // Cross over operation of GAs, combine the potential great solving traits of 2 parents to child solution 
    private static int[][] crossover(int[][] parent1, int[][] parent2, int[][] puzzle) {
        int[][] child = copyBoard(puzzle);
        Random random = new Random();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (child[i][j] == 0) {
                    child[i][j] = random.nextBoolean() ? parent1[i][j] : parent2[i][j];
                }
            }
        }
        return child;
    }

    //------------------------------------------------------------------------------------------------
    // Method 6: mutate(int[][] board, int[][] puzzle)
    // Data Structure: dtype - void method, no dtype return
    // Time Complexity: O(1)
    // Space Complexity: O(1)
    // Given tunable parameter MUTATION_RATE, randomly fill possible value if variation of mutation is low
    private void mutate(int[][] board, int[][] puzzle) {
        Random random = new Random();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (puzzle[i][j] == 0 && random.nextDouble() < MUTATION_RATE) {
                    List<Integer> possibleValues = getPossibleValues(board, i, j);
                    if (!possibleValues.isEmpty()) {
                        board[i][j] = possibleValues.get(random.nextInt(possibleValues.size()));
                    }
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------
    // Method 7: calculateFitness(int[][] board) -> fitness = 0 means correct Sudoku solution
    // Data Structure: dtype - integer
    // Time Complexity: O(1)
    // Space Complexity: O(1)
    // Calculate the fitness of current board solution - the violation of errors based on Sudoku rules
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

    //------------------------------------------------------------------------------------------------
    // Methods: all helper methods
    // Time Complexity: O(1)
    // Space Complexity: O(1)

    // Help Method 1: countDuplicates(int[] arr)
    // Data Structure: Integer
    // Count any duplicate cell in the given array
    private static int countDuplicates(int[] arr) {
        List<Integer> seen = new ArrayList<>(); // Use your ArrayList
        int duplicates = 0;
        for (int num : arr) {
            if (num != 0) {
                boolean found = false;
                for (int i = 0; i < seen.size(); i++) {  // Iterate with index
                    if (seen.get(i).equals(num)) { // Use .equals() for comparison
                        found = true;
                        break;
                    }
                }
                if (found) {
                    duplicates++;
                }
                seen.add(num);
            }
        }
        return duplicates;
    }

    // Helper Method 2: getRow(int[][] board, int row)
    // Data Structure: 1D Integer Array
    // Get all the elements within the row of the board of shape (1x9, )
    private static int[] getRow(int[][] board, int row) {
        int[] r = new int[GRID_SIZE];
        System.arraycopy(board[row], 0, r, 0, GRID_SIZE);
        return r;
    }

    // Helper Method 3: getColumn(int[][] board, int col)
    // Data Structure: 1D Integer Array
    // Get all the elemtns within the column of the board of shape (9x1, )
    private static int[] getColumn(int[][] board, int col) {
        int[] column = new int[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            column[i] = board[i][col];
        }
        return column;
    }

    // Helper Method 4: getSubgrid(int[][] board, int startRow, int startCol)
    // Data Structure: 1D Integer Array
    // Get all elements within the 3x3 Subgrid of the board, order is all column 1st -> all row 2nd
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

    // Helper Method 5: getPossibleValues(int[][] board, int row, int col)
    // Data Structure: List of Integer 
    // Get all possible value for each position of Sudoku board, filled value should strictly follow Sudoku rule
    private static List<Integer> getPossibleValues(int[][] board, int row, int col) {
        List<Integer> possibleValues = new ArrayList<>();
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (isValidPlacement(board, num, row, col)) {
                possibleValues.add(num);
            }
        }
        return possibleValues;
    }

    // Helper Method 6: isValidPlacement(int[][] board, int num, int row, int col)
    // Data Structure: Boolean (True or False)
    // Check if there is only single representation of number in Sudoku rows, columns and subgrids
    private static boolean isValidPlacement(int[][] board, int num, int row, int col) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) return false;
        }
        int startRow = row - row % SUBGRID_SIZE;
        int startCol = col - col % SUBGRID_SIZE;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[startRow + i][startCol + j] == num) return false;
            }
        }
        return true;
    }

    // Helper Method 7: copyBoard(int[][] source)
    // Data Structure 1: 2D Integer Array
    // Copy the Sudoku board to another board
    private static int[][] copyBoard(int[][] source) {
        int[][] destination = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(source[i], 0, destination[i], 0, GRID_SIZE);
        }
        return destination;
    }

    // Helper Method 8: printBoard(int[][] board)
    // Data Structure: void
    // Print the board in 9x9 representation
    public static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    // Helper Method 9: printGAConfig(int POPULATION_SIZE, double MUTATION_RATE, int MAX_GENERATIONS)
    public static void printGAConfig(int POPULATION_SIZE, double MUTATION_RATE, int MAX_GENERATIONS) {
        System.out.println("\nPopulation size: " + POPULATION_SIZE);
        System.out.println("Mutation rate: " + MUTATION_RATE);
        System.out.println("Maximum generations: " + MAX_GENERATIONS);
    }


    //------------------------------------------------------------------------------------------------
    // Main code
    public static void main(String[] args) {

        SimpleGenetic RMIT_Genetic_Sudoku_Solver = new SimpleGenetic(300, 
                                                    0.2, 20);
        int[][] puzzle = {
            {0, 1, 0, 0, 0, 9, 5, 6, 4},
            {8, 7, 9, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 2, 3, 0, 0, 0},
            {0, 5, 1, 0, 7, 8, 0, 0, 0},
            {4, 6, 0, 0, 0, 1, 0, 8, 9},
            {7, 0, 8, 0, 0, 0, 3, 1, 5},
            {6, 0, 0, 2, 0, 4, 9, 0, 0},
            {1, 0, 4, 8, 0, 0, 0, 0, 3},
            {0, 8, 7, 3, 6, 0, 1, 4, 0}
        };
        System.out.println("Initial puzzle: \n");
        printBoard(puzzle);
        RMIT_Genetic_Sudoku_Solver.solve(puzzle);
        System.out.println("\nSolved puzzle: \n");
        printBoard(RMIT_Genetic_Sudoku_Solver.solve(puzzle));

    }
}