package assignment3;


import assignment3.sort.QuickSort;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * TSP problem solver using genetic algorithms.
 */
public class GeneticAlgorithm {

    private int generations;
    private int popSize;
    private int[] products = null;
    private TSPData pd;

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
     * Constructs a new 'genetic algorithm' object.
     *
     * @param generations the amount of generations.
     * @param popSize     the population size.
     * @param products    the products to be picked up.
     */
    public GeneticAlgorithm(int generations, int popSize, int[] products) {
        this.generations = generations;
        this.popSize = popSize;
        this.products = products;
    }


    /**
     * Assignment 2.b
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //parameters
        int populationSize = 10000;
        int generations = 1000;
        String persistFile = "./tmp/productMatrixDist";

        //setup optimization
        TSPData tspData = null;
        try {
            tspData = TSPData.readFromFile(persistFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load file.");
        }


        GeneticAlgorithm ga = new GeneticAlgorithm(generations, populationSize);

        //run optimzation and write to file
        int[] solution = ga.solveTSP(tspData);
        tspData.writeActionFile(solution, "./outputFiles/TSP solution.txt");
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
        this.pd = pd;
        double[] fitness = new double[popSize];
        int[][] genePool = new int[popSize][newChromosome().length];
        double shortest = Double.MAX_VALUE;

        for (int i = 0; i < popSize; i++) {
            genePool[i] = newChromosome();
            double distance = calculateDistance(genePool[i]);
            if (distance < shortest)
                shortest = distance;
        }
        System.out.println("genePool filled with randomly ordered chromosomes.");

        for (int gen = 0; gen < generations; gen++) {
            for (int pop = 0; pop < popSize; pop++)
                fitness[pop] = calculateFitness(shortest, genePool[pop]);

            //Sort the genePool according to the fitness list
            QuickSort.quickSort(genePool, fitness);
            shortest = calculateDistance(genePool[0]);

            //Select the chromosomes with the highest fitness.
            genePool = cumulativeSelection(genePool, fitness);

            genePool = createChildren(genePool);

            if (gen % 100 == 0)
                System.out.println("Generation: " + gen);
        }


        for (int i = 0; i < popSize; i++) {
            double distance = calculateDistance(genePool[i]);
            if (distance < shortest)
                shortest = distance;
        }

        for (int pop = 0; pop < popSize; pop++)
            fitness[pop] = calculateFitness(shortest, genePool[pop]);
        QuickSort.quickSort(genePool, fitness);


        printArrays(fitness, genePool);

        return genePool[0];
    }

    private int[][] cumulativeSelection(int[][] genePool, double[] fitness) {
        int[][] selection = new int[genePool.length][genePool[0].length];
        double totalFitness = Arrays.stream(fitness).sum();
        double cumulative = 0;
        int index = 0;
        for (int i = fitness.length - 1; i >= 0; i--) {
            double fitnessRatio = fitness[i] / totalFitness;
            cumulative += fitnessRatio;
            if (new Random().nextDouble() <= cumulative) {
                selection[index] = genePool[i];
                index++;
            }
        }
        return selection;
    }

    private int[][] createChildren(int[][] genePool) {
        int[][] newPopulation = new int[genePool.length][genePool[0].length];
        int parentSize = 0;
        for (int i = 0; i < genePool.length - 1; i++) {
            if (isArrayAllNull(genePool[i + 1]))
                parentSize = i;
        }

        int index = 1;
        for (int i = 0; i < genePool.length; i++) {
            if (isArrayAllNull(genePool[index]))
                index = 1;

            int parent1 = new Random().nextInt(parentSize);
            int parent2 = new Random().nextInt(parentSize);
            newPopulation[i] = edgeRecombinationCrossover(genePool[parent1], genePool[parent2]);
            index++;
        }
        return newPopulation;
    }

    private int[] edgeRecombinationCrossover(int[] parent1, int[] parent2) {
        int[][] neighbourList = createNeighbourList(parent1, parent2);
        int nextNode = (new Random().nextDouble() > 0.5) ? parent1[0] : parent2[0];
        int[] newChromosome = newNegativeArray(parent1.length);
        for (int i = 0; i < newChromosome.length; i++) {
            newChromosome[i] = nextNode;
            if (i == newChromosome.length - 1)
                break;

            clearNodeFromNeighbourList(nextNode, neighbourList);

            int newNode;
            int q = indexOf(parent1, nextNode);
            if (q == -1 || isArrayAllNegativeOne(neighbourList[q])) {
                newNode = getRandomUnUsedNode(newChromosome);
            } else {
                newNode = getNextNode(q, neighbourList);
            }
            nextNode = newNode;
        }

        return newChromosome;
    }

    private int getNextNode(int node, int[][] neighbourList) {
        int[] neighbours = neighbourList[node];
        int neighboursSize = Integer.MAX_VALUE;

        int[] ties = new int[neighbours.length];
        for (int i = 0; i < ties.length; i++)
            ties[i] = -1;
        int tiesIndex = 0;
        for (int j = 0; j < neighbours.length; j++) {
            int neighbour = neighbours[j];
            if (neighbour == -1)
                continue;


            int q = indexOf(neighbours, neighbour);
            int numberOfNeighboursOfNeighbour = size(neighbourList[q]);

            if (numberOfNeighboursOfNeighbour <= neighboursSize) {
                neighboursSize = numberOfNeighboursOfNeighbour;
                ties[tiesIndex] = neighbour;
                tiesIndex++;
            }
        }

        return ties[new Random().nextInt(size(ties))];
    }

    private int getRandomUnUsedNode(int[] chromosome) {
        int size = chromosome.length - size(chromosome);
        int[] unUsedNodes = new int[size];
        size--;
        for (int i : newChromosome()) {
            if (!contains(chromosome, i)) {
                unUsedNodes[size] = i;
                size--;
            }
        }
        return unUsedNodes[new Random().nextInt(unUsedNodes.length)];
    }


    private void clearNodeFromNeighbourList(int node, int[][] neighbourList) {
        for (int i = 0; i < neighbourList.length; i++)
            for (int j = 0; j < neighbourList[i].length; j++)
                if (neighbourList[i][j] == node)
                    neighbourList[i][j] = -1;
    }

    private int[][] createNeighbourList(int[] parent1, int[] parent2) {
        int[][] neighbourList = new int[parent1.length][4];
        for (int i = 0; i < neighbourList.length; i++)
            for (int j = 0; j < neighbourList[0].length; j++)
                neighbourList[i][j] = -1;

        fillNeighbourList(parent1, neighbourList);
        fillNeighbourList(parent2, neighbourList);
        return neighbourList;
    }

    private void fillNeighbourList(int[] parent, int[][] neighbourList) {
        for (int i = 0; i < parent.length; i++) {
            int j = parent[i];
            int left;
            int right;
            if (i == 0) {
                left = parent[parent.length - 1];
                right = parent[i + 1];
            } else if (i == parent.length - 1) {
                left = parent[i - 1];
                right = parent[0];
            } else {
                left = parent[i - 1];
                right = parent[i + 1];
            }
            if (left != -1 && right != -1) {
                int q = indexOf(parent, j);
                for (int k = 0; k < neighbourList[q].length; k++) {
                    if (neighbourList[q][k] == -1 && !contains(neighbourList[q], left))
                        neighbourList[q][k] = left;
                    else if (neighbourList[q][k] == -1 && !contains(neighbourList[q], right))
                        neighbourList[q][k] = right;
                }
            }
        }
    }

    private double calculateFitness(double shortest, int[] genes) {
        double distance = calculateDistance(genes);
        return shortest / distance;
    }

    private double calculateDistance(int[] genes) {
        double totalDistance = 0;
        totalDistance += pd.getStartDistances()[genes[0]];
        for (int i = 1; i < genes.length; i++) {
            int start = genes[i - 1];
            int end = genes[i];
            int distance = pd.getDistances()[start][end];
            totalDistance += distance;
        }
        totalDistance += pd.getEndDistances()[genes[genes.length - 1]] + genes.length;
        return totalDistance;

    }

    private int indexOf(int[] parents, int j) {
        for (int i = 0; i < parents.length; i++) {
            if (parents[i] == j)
                return i;
        }
        return -1;
    }

    
    public static boolean contains(int[] ints, int i) {
        for (int j : ints)
            if (j == i)
                return true;
        return false;
    }

    private int[] newChromosome() {
        int[] solution = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        if (products != null)
            solution = products.clone();

        return shuffle(solution).clone();
    }

    private boolean isArrayAllNull(int[] list) {
        int total = 0;
        for (int i : list) total += i;
        return total == 0;
    }

    private boolean isArrayAllNegativeOne(int[] list) {
        int total = 0;
        for (int i : list) total += i;
        return total == -1 * list.length;
    }

    private int size(int[] neighbours) {
        int size = 0;
        for (int i : neighbours)
            if (i != -1)
                size++;
        return size;
    }

    private int[] newNegativeArray(int length) {
        int[] array = new int[length];
        for (int i = 0; i < length; i++)
            array[i] = -1;
        return array;
    }

    private void printArrays(double[] fitness, int[][] genePool) {
        for (int i = 0; i < genePool.length; i++) {
            System.out.print("Length=" + calculateDistance(genePool[i]) + " Fitness=" + (float) fitness[i] + " Gene=");
            for (int j = 0; j < genePool[0].length; j++) {
                int x = genePool[i][j];
                System.out.print(x + ", ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

}
