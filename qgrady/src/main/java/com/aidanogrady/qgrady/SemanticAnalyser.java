package com.aidanogrady.qgrady;

import com.aidanogrady.qgrady.exceptions.InvalidRowException;
import com.aidanogrady.qgrady.exceptions.InvalidValueException;
import com.aidanogrady.qgrady.exceptions.InvalidVariableException;
import com.aidanogrady.qgrady.exceptions.SignallingException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Semantic Analyser ensures that the semantics of the language are
 * maintained. It checks the probability distribution to ensure that all
 * constraints are met.
 *
 * @author Aidan O'Grady
 * @version 0.3
 */
public class SemanticAnalyser {

    /**
     * List of words that are reserved in prism language, and thus cannot be
     * used as variables.
     *
     * Taken from:
     * http://www.prismmodelchecker.org/manual/ThePRISMLanguage/ModulesAndVariables
     */
    private static final List<String> KEYWORDS = Arrays.asList(
            "A", "bool", "clock", "const", "ctmc", "C", "double", "dtmc", "E",
            "endinit", "endinvariant", "endmodule", "endrewards", "endsystem",
            "false", "formula", "filter", "func", "F", "global", "G", "init",
            "invariant", "I", "int", "label", "max", "mdp", "min", "module",
            "X", "nondeterministic", "Pmax", "Pmin", "P", "probabilistic",
            "prob", "pta", "rate", "rewards", "Rmax", "Rmin", "R", "S",
            "stochastic", "system", "true", "U", "W");


    /**
     * Validates all variables to ensure that there are no conflicts within the
     * set-up or with Prism.
     * @param box  the box being analysed.
     * @throws InvalidVariableException
     */
    public static void validateVariables(Box box) throws
            InvalidVariableException {

        if(box.getNoOfOutputs() != box.getNoOfInputs()) {
            String msg = "Unequal number of inputs and outputs!";
            throw new InvalidVariableException(msg);
        }

        // Handle all inputs.
        List<String> strings = box.getInputs();
        for(String string : strings) {
            validateVariable(box, string, strings);
        }

        // Handle all inputs.
        strings = box.getOutputs();
        for(String string : strings) {
            validateVariable(box, string, strings);
        }
    }

    /**
     * Handles the validation of a single variable.
     * @param box  The box being analysed.
     * @param var  The variable name
     * @param vars  The variable list the var appears in.
     * @throws InvalidVariableException
     */
    private static void validateVariable(Box box, String var, List<String> vars)
            throws InvalidVariableException {
        boolean in = box.getInputs().contains(var);
        boolean out = box.getOutputs().contains(var);
        if (in && out) {
            String msg = "Variable " + var + " is both input and output.";
            throw new InvalidVariableException(msg);
        }

        if (KEYWORDS.contains(var)) {
            String msg = "Variable " + var + " is a Prism keyword.";
            throw new InvalidVariableException(msg);
        }

        if (Collections.frequency(vars, var) > 1) {
            String msg = "Variable " + var + " is used multiple times.";
            throw new InvalidVariableException(msg);

        }
    }

    /**
     * Determines whether there are any invalid values within the distribution.
     *
     * Since we are dealing with probabilities, any negative value or value
     * greater than 1 is forbidden.
     *
     * @param box  the 2D array extracted from parsing to checked.
     * @throws InvalidValueException
     */
    public static void validateValues(double[][] box) throws
            InvalidValueException
    {
        for (double[] row : box) {
            for (double value : row) {
                if (value > 1.0 || value < 0.0) {
                    String mgs = "Expected: between 0 and 1. Got: " + value;
                    throw new InvalidValueException(mgs);
                }
            }
        }
    }

    /**
     * Determines where there are the correct number of rows as expected in the
     * probability distribution based on the intended range of the input values
     * and the number of inputs in the set-up.
     *
     * @param box  the box being checked.
     * @throws InvalidRowException
     */
    public static void validateRowAmount(Box box) throws InvalidRowException {
        int inputs = box.getNoOfInputs();
        int range = box.getInputRange();
        int expected = (int) Math.pow(range, inputs);
        int result = box.getProbs().length;
        if (result != expected) {
            String msg = "Set up has " + inputs + " inputs with range " + range
                    + ". Expected matrix to have " + expected + " rows, got "
                    + result + ".";
            throw new InvalidRowException(msg);
        }
    }

    /**
     * Determines whether there are any rows in the distribution with the
     * wrong length.
     *
     * Every row must be of equal length, so we must determine if there are any
     * that aren't.
     *
     * @param box  the 2D array extracted from parsing to checked.
     * @throws InvalidRowException
     */
    public static void validateRowLengths(Box box) throws
            InvalidRowException
    {
        int inputs = box.getNoOfOutputs();
        int range = box.getOutputRange();
        int ex = (int) Math.pow(range, inputs);
        double[][] matrix = box.getProbs();
        for(int i = 1; i < matrix.length; i++) {
            int row = matrix[i].length;
            if (row != ex) {
                String msg = "Error in row " + i + ": Expected " + ex;
                msg += " values, got " + row;
                throw new InvalidRowException(msg);
            }
        }
    }


    /**
     * Determines whether there are rows that do not sum to one.
     *
     * Since we are dealing with probabilities, every row must sum up to exactly
     * 1.0 to ensure that the probabilities are accurate, otherwise the set-up
     * makes no sense.
     *
     * @param box  the 2D array extracted from parsing to checked.
     * @throws InvalidRowException
     */
    public static void validateRowSums(double[][] box) throws
            InvalidRowException
    {
        double sum = 0;
        for(int i=0; i < box.length; i++) {
            for(int j=0; j < box[i].length; j++) {
                sum += box[i][j];
            }
            if(sum < 0.99999 || sum > 1.0) {
                String msg = "Error in row " + i + ": Expected sum of 1.0, got " + sum;
                throw new InvalidRowException(msg);
            }
            sum = 0;
        }
    }


    /**
     * A valid non-local box must fulfill the 'non-signalling' property. This
     * property can be summarized as 'the input of one party cannot influence
     * the output of another party's output'.
     *
     * @param box  the set-up being examined.
     * @throws SignallingException
     */
    public static void nonSignalling(Box box) throws SignallingException {
        System.out.println();
        int inputRange = box.getInputRange();
        int outputRange = box.getOutputRange();
        int inputSize = box.getNoOfInputs();
        int outputSize = box.getNoOfOutputs();
        int inMax = (int) Math.pow(inputRange, inputSize);
        int outMax = (int) Math.pow(outputRange, outputSize);

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < inMax; j++) {
                for (int k = 0; k < outMax; k++) {
                    int[] in = Box.intToArray(j, inputSize, inputRange);
                    int[] out = Box.intToArray(k, outputSize, outputRange);
                    System.out.println(i + "\t" + Arrays.toString(in) + "\t" + Arrays.toString(out));
                    nonSignalling(box, in, out, i);
                }
            }
        }
    }

    /**
     * Handles the non-signalling condition for a specific combination.
     *
     * Late changes to the algorithm meant that repeated checks were no longe
     * carried out.
     * @param box  The box being analysed.
     * @param in  The fixed inputs of this check.
     * @param out  The fixed outputs of this check.
     * @param index  The index that is being checked for signalling.
     * @throws SignallingException
     */
    private static void nonSignalling(Box box, int[] in, int[] out, int index)
            throws SignallingException {
        double[] sums = new double[box.getInputRange()];

        for (int i = 0; i < box.getInputRange(); i++) {
            in[index] = i;
            for (int j = 0; j < box.getOutputRange(); j++) {
                out[index] = j;
                sums[i] += box.prob(in, out);
            }
        }

        for (int i = 0; i < sums.length; i++) {
            for (int j = (i + 1); j < sums.length; j++) {
                if(sums[i] != sums[j])
                    throw new SignallingException("Signalling found");
            }
        }
    }
}
