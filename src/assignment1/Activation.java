package assignment1;

/**
 * Class that contains the activation functions for perceptrons
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Activation {

    /**
     * Step activation function which returns 1 if input >= 0 or -1 if not.
     * @param input the input for the activation function
     * @return the result of the activation function
     */
    public static double Step(double input) {
        if (input >= 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public static double Sign(double input) {
        if (input >= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public static double Sigmoid(double input) {
        return 1 / (1 + Math.pow(Math.E, (0 - input)));
    }

    public static double Lineair(double input) {
        return input;
    }
}
