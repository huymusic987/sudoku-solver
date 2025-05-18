RMIT SGS Vietnam - COSC2469|COSC2722 Algorithms and Analysis course
Group Project - Sudoku Solver
Documentation of Group 15

1. Watch the presentation of the Sudoku Solver Group Project of Group 15:
Link

2. Contribution Scores of members (Total 4 members * 5 points = 20 points)
Member 1: Pham Quang Huy (s3940676) – Contribution Score: 5 points (25%)
Responsibilities:
- Manage and coordinate tasks across all team members to ensure project completion and timely delivery.
- Supervise team communication and progress tracking.
- Construct, manage the workload of the project's report and presentation.

Member 2: Le Duc Huy (s4040502) – Contribution Score: 5 points (25%)
Responsibilities:
- Design and implement the Backtracking algorithm for Sudoku solving, including the main program and basic test of the algorithm.
- Provide a detailed explanation of the algorithm and its recursive structure.
- Conduct complexity analysis of Backtracking Algorithm, showing that the worst-case time complexity is O(9⁴⁵).
- Perform evaluation of correctness and efficiency by testing on multiple valid and unsolvable Sudoku boards.
- Analyze runtime and success rate across difficulty levels.

Member 3: Do Le Minh Quan (s4032589) – Contribution Score: 5 points (25%)
Responsibilities:
- Develop and implement the Constraint Satisfaction Problem (CSP) algorithm, including the main program and basic test of the algorithm.
- Describe supporting data structures, such as the domain lists and constraint matrices.
- Perform complexity analysis of Constraint Satisfaction Algorithm, analyse the result and discussion on its run time.
- Perform evaluation of correctness and efficiency by testing on multiple valid and unsolvable Sudoku boards.

Member 4: Le Quang Duy (s3880200) – Contribution Score: 5 points (25%)
Responsibilities:
- Develop and implement the Cultural Genetic algorithm, including the main program and basic test of the algorithm.
- Describe supporting data structures of the algorithm and constructing the List<T> and ArrayList class as based data structure.
- Perform complexity analysis of Genetic Algorithm, analyse the result and discussion on its run time.
- Perform evaluation of correctness and efficiency by testing on multiple valid and unsolvable Sudoku boards.
- Conduct the project video representation and README file for instruction of the project.

3. Instruction to run the project solutions:
a/ Folders Explanation:
- algorithms/: Contains the RMIT_Sudoku_Solver interface and its implementations (Backtracking, SimpleGenetic, ConstraintSatisfaction), each representing a different solving strategy.
- utils/: Provides utility classes for handling Sudoku puzzle input/output (SudokuIOHandling) and testing solvers (SudokuTestUtils).
- structures/: Includes a custom list interface (List.java) and its array-based implementation (ArrayList.java), used throughout the project.
- test/: Houses test classes (BacktrackingTest, SimpleGeneticTest, ConstraintSatisfactionTest) for verifying individual solver correctness, plus a benchmarking utility (SudokuBenchmark) to perform all three solving algorithms and compute their performances. 
- puzzles/: Stores text files with Sudoku puzzles of varying difficulties, used for testing and benchmarking. 
- README.txt: Contains necessary documentation and instructions to run the solution for the project. The list of contribution scores of each member and the presentation video link are also given in the file. 

b/ Detail Instruction:
- To view the detail implementations of every algorithms, locate to the algorithms/ package and view any alogrithms based on their file names.
- To view the implementation of data structures, utilities and the generated Sudoku puzzles (in .txt file), locate to the structures/, utils/ and puzzles/ packages.
- To run the all the neccessary tests, locate to the tests/ package containing the single test for 3 algorithms (name-based file) and 1 full test for all of 3 (SudokuBenchMark).
- Each of the test files within tests/ package have the own main function, run main to implement test for the associated files:
  -- BacktrackingTest, ConstraintSatisfactionTest and SimpleGenetic test runs the test to solve Sudoku boards based on that algorithm only, with details messages showing the algorithms' operation.
  -- SudokuBenchMark test run all the tests with all levels of difficulties to test the behaviour and total run time of all 3 algorithms to perform evaluation and discussion on them.
- Note: For the Simple Genetic Algorithm, feel free to adjust the 4 parameters POPULATION_SIZE, MUTATION_RATE, MAX_GENERATIONS and sort properties to observe different behavior of this algorithm:
  -- Adjust it in the constructor's arguments when create a new object, e.g. "RMIT_Sudoku_Solver SimpleGenetic = new SimpleGenetic(POPULATION_SIZE = 100, MUTATION_RATE = 0.2, MAX_GENERATIONS = 10, sort = "Bubble Sort");.
  -- Or adjust it in the SudokuBenchMark file with different test levels, e.g. "switch (difficulty) {case "hard" -> {populationSize *= 20; mutationRate *= 8; maxGeneration *= 2}}".
