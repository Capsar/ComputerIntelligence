package sort;

import java.util.Arrays;
import java.util.Random;

public class QuickSort {

    private static int numberOfSwaps = 0;

    public static void main(String... args) {
        double[] elements = new double[20000];
        double q = elements.length;
        for (int i = 0; i < elements.length; i++)
            elements[i] = q--;


        int[][] list = new int[20000][17];
        int[] l = new int[] {2342345,234,523,452,346,345,2345,234,6,47,58,67,6234,2,412,345,67,9,636,545,34,1,4546,78,9,876,543,4,567,89,876,54,32,3,4567,8,9,876,54,34,56,78,9,876,54,3,4,3,56,7,65,43,5,6,54};
        for (int i = 0; i < list.length; i++)
            list[i] = l;

        System.out.println("Begin: " + Arrays.toString(elements));
        long time = System.nanoTime()/1000;
        quickSort(list, elements);
        long timeAfter = System.nanoTime()/1000;
        System.out.println(timeAfter-time);
        System.out.println("Number of swaps: " + numberOfSwaps);
        System.out.println("End: " + Arrays.toString(elements));

    }

    public static int[][] quickSort(int[][] list, double[] elements) {
        sort(list, elements, 0, elements.length - 1);
        return list;
    }

    private static void sort(int[][] list, double[] elements, int low, int high) {
        if (low < high + 1) {
            int p = partition(list, elements, low, high);
            sort(list, elements, low, p - 1);
            sort(list, elements, p + 1, high);
        }
    }

    private static int partition(int[][] list, double[] elements, int low, int high) {
        int pivot = pivot(low, high);
        swap(list, elements, low, pivot);
        int border = low + 1;
        for (int i = border; i <= high; i++) {
            if (elements[i] > elements[low]) {
                swap(list, elements, i, border++);
            }
        }
        swap(list, elements, low, border - 1);

        return border - 1;
    }

    private static int pivot(int low, int high) {
        return new Random().nextInt(high-low+1) + low;
    }

    private static void swap(int[][] list, double[] elements, int i1, int i2) {
        numberOfSwaps++;
        swap(elements, i1, i2);
        swap(list, i1, i2);
    }

    private static void swap(double[] elements, int i1, int i2) {
        double temp = elements[i1];
        elements[i1] = elements[i2];
        elements[i2] = temp;
    }

    private static void swap(int[][] list, int i1, int i2) {
        int[] temp = list[i1];
        list[i1] = list[i2];
        list[i2] = temp;
    }

}
