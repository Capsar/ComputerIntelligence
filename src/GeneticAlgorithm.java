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
            genePool = fillParents(parents);

            genePool = createChildren(genePool);
        }
        printArrays(fitness, genePool);

        return genePool[0];
    }

    private int[][] createChildren(int[][] genePool) {
        int[][] newPopulation = new int[genePool.length][genePool[0].length];

        //Fill in the parents again in the population & safe last parent index.
        int lastParentIndex = 0;
        for (int i = 0; i < genePool.length; i++) {
            if (!isArrayAllNull(genePool[i])) {
                newPopulation[i] = genePool[i];
                lastParentIndex = i;
            }
        }
        int j = 1;
        //Start filling in new children right after the last parent.
        for (int i = lastParentIndex + 1; i < genePool.length; i++) {
            if (j <= lastParentIndex) {
                //Start filling with the best parent until genePool is full.
                newPopulation[i] = mutate4(genePool[j], genePool[j - 1]);
                j++;
            } else {
                //If genePool is not yet filled but all parents have made children, fill in the rest with random children.
                newPopulation[i] = newChromosome();
            }

        }


        return newPopulation;
    }

    private int[][] fillParents(int[][] parents) {
        int[][] newPopulation = new int[parents.length][parents[0].length];

        for (int i = 0; i < parents.length; i++) {
            if (!isArrayAllNull(parents[i])) {
                newPopulation[i] = parents[i];
            } else {
                break;
            }
        }
        return newPopulation;
    }

    private int[] mutate1(int[] parent, int numberOfSwaps) {
        int[] newChromosome = parent.clone();
        Random random = new Random();
        for (int i = 0; i < numberOfSwaps; i++) {
            int swap1 = random.nextInt(parent.length);
            int swap2 = random.nextInt(parent.length);
            if (swap1 == swap2)
                i--;
            else {
                swap(newChromosome, swap1, swap2);
            }

        }

        return newChromosome;
    }

    private int[] mutate2(int[] parent, int numberOfSwaps) {
        int[] newChromosome = parent.clone();
        Random random = new Random();
        for (int i = 0; i < numberOfSwaps; i++) {
            int swap1 = 1 + random.nextInt(parent.length - 1);
            int swap2 = swap1 - 1;
            swap(newChromosome, swap1, swap2);
        }
        return newChromosome;
    }

    private int[] mutate3() {
        return newChromosome();
    }

    private int[] mutate4(int[] parent1, int[] parent2) {
        int[][] neighbourList = createNeighbourList(parent1, parent2);
        int nextNode = (new Random().nextDouble() > 0.5) ? parent1[0] : parent2[0];
        int[] newChromosome = newNegativeArray(parent1.length);
        for (int i = 0; i < newChromosome.length; i++) {
            newChromosome[i] = nextNode;
            if (i == newChromosome.length - 1)
                break;

            clearNodeFromNeighbourList(nextNode, neighbourList);

            int newNode;
            if (isArrayAllNegativeOne(neighbourList[nextNode])) {
                newNode = getRandomUnUsedNode(newChromosome);
            } else {
                newNode = getNextNode(nextNode, neighbourList);
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

            int numberOfNeighboursOfNeighbour = size(neighbourList[neighbour]);

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
                for (int k = 0; k < neighbourList[j].length; k++) {
                    if (neighbourList[j][k] == -1 && !contains(neighbourList[j], left))
                        neighbourList[j][k] = left;
                    else if (neighbourList[j][k] == -1 && !contains(neighbourList[j], right))
                        neighbourList[j][k] = right;
                }
            }
        }
    }

    private boolean contains(int[] ints, int i) {
        for (int j : ints)
            if (j == i)
                return true;
        return false;
    }

    private void swap(int[] elements, int i1, int i2) {
        int temp = elements[i1];
        elements[i1] = elements[i2];
        elements[i2] = temp;
    }


    private int[] newChromosome() {
        int[] solution = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        return shuffle(solution).clone();
    }

    private int[][] parent(int[][] genePool) {
        int[][] parents = new int[genePool.length][genePool[0].length];
        int index = 0;
        for (int i = 0; i < genePool.length; i++) {
            double random = new Random().nextDouble();
            double gaussian = gaussian(i, genePool.length) / 1.5;
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
        for (int i = 1; i < genes.length; i++) {
            int start = genes[i - 1];
            int end = genes[i];
            int distance = pd.getDistances()[start][end];
            fitness += distance;
        }
        fitness = 1.0 / fitness;
        return fitness;
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
            System.out.print((float) fitness[i] + "=");
            for (int j = 0; j < genePool[0].length; j++) {
                int x = genePool[i][j];
                System.out.print(x + ", ");
            }
            System.out.println("");
        }
        System.out.println("");
    }


    private void printDoubleArray(double[] list) {
        for (int j = 0; j < list.length; j++) {
            double x = list[j];
            System.out.print(x + ", ");
        }
        System.out.println("");
    }

    private void printIntArray(int[] list) {
        for (int j = 0; j < list.length; j++) {
            int x = list[j];
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
