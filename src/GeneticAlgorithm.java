import sort.QuickSort;

import java.io.IOException;
import java.util.Random;

/**
 * TSP problem solver using genetic algorithms.
 */
public class GeneticAlgorithm {

    private int generations;
    private int popSize;

    /**
     * Constructs a new 'genetic algorithm' object.
     *
     * @param generations the amount of generations.
     * @param popSize     the population size.
     */
    public GeneticAlgorithm(int generations, int popSize) {
        this.generations = generations;
        this.popSize = popSize;
    }

    /**
     * Assignment 2.b
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //parameters
        int populationSize = 1000;
        int generations = 100000;
        String persistFile = "./tmp/productMatrixDist";

        //setup optimization
        TSPData tspData = null;
        try {
            tspData = TSPData.readFromFile(persistFile);
        } catch (Exception e) {
            System.err.println("Failed to load file.");
        }


        GeneticAlgorithm ga = new GeneticAlgorithm(generations, populationSize);

        //run optimzation and write to file
        int[] solution = ga.solveTSP(tspData);
        tspData.writeActionFile(solution, "./data/TSP solution.txt");
    }

    /**
     * Knuth-Yates shuffle, reordering a array randomly
     *
     * @param chromosome array to shuffle.
     */
    private int[] shuffle(int[] chromosome) {
        int n = chromosome.length;
        for (int i = 0; i < n; i++) {
            int r = i + (int) (Math.random() * (n - i));
            int swap = chromosome[r];
            chromosome[r] = chromosome[i];
            chromosome[i] = swap;
        }
        return chromosome;
    }

    /**
     * This method should solve the TSP.
     *
     * @param pd the TSP data.
     * @return the optimized product sequence.
     */
    public int[] solveTSP(TSPData pd) {

        double[] fitness = new double[popSize];
        int[][] genePool = new int[popSize][newChromosome().length];

        for (int i = 0; i < popSize; i++) {
            genePool[i] = newChromosome();
        }
        System.out.println("genePool filled with randomly ordered chromosomes.");

        for (int gen = 0; gen < generations; gen++) {
            for (int pop = 0; pop < popSize; pop++) {
                fitness[pop] = calculateFitness(pd, genePool[pop]);
            }

            //Sort the genePool according to the fitness list
            QuickSort.quickSort(genePool, fitness);

            //Select the chromosomes with the highest fitness.
            int[][] parents = parent(genePool);

            //Fill gene pool with fresh new chromosomes.
            genePool = createChildren(parents);
        }

        printArrays(fitness, genePool);
        return genePool[0];
    }

    private int[][] createChildren(int[][] parents) {
        int[][] newPopulation = new int[parents.length][parents[0].length];

        for (int i = 0; i < parents.length; i++) {
            if(!isEmpty(parents[i])) {
                newPopulation[i] = parents[i];
            } else {
                newPopulation[i] = newChromosome();
            }
        }


        return newPopulation;
    }

    private int[] newChromosome() {
        int[] solution = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 17};
        return shuffle(solution).clone();
    }

    private int[][] parent(int[][] genePool) {
        int[][] parents = new int[genePool.length][genePool[0].length];
        int index = 0;
        for (int i = 0; i < genePool.length; i++) {
            double random = new Random().nextDouble();
            double gaussian = gaussian(i, genePool.length);
            if (random < gaussian) {
                parents[index] = genePool[i];
                index++;
            }
        }

        return parents;
    }

    private double gaussian(double index, double listLength) {
        double sigma = 0.4f;
        double mean = 0;
        double normalization = (float) (1.0f / (sigma * Math.sqrt(2 * Math.PI)));
        double x = index / listLength;
        double power = Math.pow(((x - mean) / sigma), 2.0);
        double gaussian = (normalization * Math.pow(Math.E, -0.5 * power));
        return gaussian;
    }

    private double calculateFitness(TSPData pd, int[] genes) {
        double fitness = 0;
        boolean p = false;
        for (int i = 1; i < genes.length; i++) {
            p = !p;
            if (p) {
                fitness += genes[i - 1] - genes[i];
            } else {
                fitness += genes[i - 1] + genes[i];
            }

//            int start = genes[i - 1];
//            int end = genes[i];
//            int distance = pd.getDistances()[start][end];
//            fitness += distance;
        }
        fitness = 1.0 / fitness;
        return fitness;
    }

    private boolean isEmpty(int[] list) {
        int total = 0;
        for (int i : list) total += i;
        return total == 0;
    }

    private void printArrays(double[] fitness, int[][] genePool) {
        for (int i = 0; i < genePool.length; i++) {
            System.out.print((float) fitness[i] + "=");
            for (int j = 0; j < genePool[0].length; j++) {
                int x = genePool[i][j];
                System.out.print(x + ", ");
            }
            System.out.println("");
        }
        System.out.println("");
    }


    private void printArray(double[] list) {
        for (int j = 0; j < list.length; j++) {
            double x = list[j];
            System.out.print(x + ", ");
        }
        System.out.println("");
    }

    private void printDoubleArray(int[][] list) {
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[0].length; j++) {
                int x = list[i][j];
                System.out.print(x + ", ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

}
