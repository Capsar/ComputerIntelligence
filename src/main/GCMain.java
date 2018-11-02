package main;

import assignment1.MainAssignment1;
import assignment1.NetworkResult;
import assignment1.NeuralNetwork;
import assignment1.Trainer;
import assignment3.*;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GCMain {

    public static final String INPUT = "/resources/grandChallenge/";
    public static final String OUTPUT = "./outputFiles/GC/";

    public static void main(String... args) {
        try {
            NetworkResult result = MainAssignment1.readFromFile("./tmp/trainerNetwork");
            NeuralNetwork network = result.getNetwork();
            Trainer.createOutputFile(network, INPUT + "GC_FeatureData.txt", OUTPUT + "GC_Classes.txt");
            int[] products = readProducts(OUTPUT + "GC_Classes.txt");
            printIntArray(products);

            Maze maze = Maze.createMaze(INPUT + "GC_Maze.txt");
            TSPData pd;
            try {
                pd = TSPData.readFromFile(OUTPUT + "GC_TSPData");
            } catch(Exception e) {
                e.printStackTrace();
                pd = TSPData.readSpecification(INPUT + "GC_Maze_Start-End.txt", INPUT + "GC_ProductCoordinates.txt");
                //parameters
                int threads = 8;
                int numberOfAnts = 100;
                int noGen = 200;
                double Q = 500;
                double evaporate = 0.1;
                AntColonyOptimization aco = new AntColonyOptimization(maze, threads, numberOfAnts, noGen, Q, evaporate);

                //Create TSPData matrix with all products
                pd.calculateRoutes(aco, products);
                pd.writeToFile(OUTPUT + "GC_TSPData");
            }

            //Check which classes to pickup
            //Select corresponding products

            int populationSize = 10000;
            int generations = 1000;
            GeneticAlgorithm ga = new GeneticAlgorithm(generations, populationSize, products);

            //run optimzation and write to file
            int[] solution = ga.solveTSP(pd);
            pd.writeActionFile(solution, OUTPUT + "Actions.txt");




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static int[] readProducts(String filePath) {
        int class1 = 3;
        int class2 = 4;
        int class3 = 6;

        try {
            File file = new File(filePath);
            Scanner scan = new Scanner(new FileReader(file));
            ArrayList<Integer> classificationList = new ArrayList<>();
            int numberOfProductsToPickup = 0;
            while(scan.hasNext()) {
                int classification = scan.nextInt();
                classificationList.add(classification);
                if(classification == class1 || classification == class2 || classification == class3)
                    numberOfProductsToPickup++;
            }
            int[] products = new int[numberOfProductsToPickup];
            int index = 0;
            for (int i = 0; i < classificationList.size(); i++) {
                int classification = classificationList.get(i);
                if (classification == class1 || classification == class2 || classification == class3) {
                    products[index] = i;
                    index++;
                }
            }
            return products;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new int[1];
    }


    public static void printDoubleArray(double[] list) {
        for (int j = 0; j < list.length; j++) {
            double x = list[j];
            System.out.print(x + ", ");
        }
        System.out.println("");
    }

    public static void printIntArray(int[] list) {
        for (int j = 0; j < list.length; j++) {
            int x = list[j];
            System.out.print(x + ", ");
        }
        System.out.println("");
    }

    public static void printDoubleArray(int[][] list) {
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
