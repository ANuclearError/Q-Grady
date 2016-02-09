package com.aidanogrady.qgrady;

import com.aidanogrady.qgrady.exceptions.InvalidRowException;
import com.aidanogrady.qgrady.exceptions.InvalidValueException;

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
     * Determines whether there are any invalid values within the distribution.
     *
     * Since we are dealing with probabilities, any negative value or value
     * greater than 1 is forbidden.
     */
    public static void validateValues(double[][] box) throws InvalidValueException {
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
     * Determines whether there are any rows in the distribution with the
     * wrong length.
     *
     * Every row must be of equal length, so we must determine if there are any
     * that aren't.
     */
    public static void validateRowLengths(double[][] box) throws InvalidRowException {
        int size = box[0].length;
        for(int i = 1; i < box.length; i++) {
            int row = box[i].length;
            if (row != size) {
                String mgs = i + ": Expected: " + size + " values, got: " + row;
                throw new InvalidRowException(mgs);
            }
        }
    }

    /**
     * Determines whether there are rows that do not sum to one.
     *
     * Since we are dealing with probabilities, every row must sum up to one to
     * ensure that the probabilities are accurate.
     */
    public static void validateRowSums(double[][] box) throws InvalidRowException {
        double sum = 0;
        for(int i=0; i < box.length; i++) {
            for(int j=0; j < box[i].length; j++) {
                sum += box[i][j];
            }
            if(sum != 1.0) {
                throw new InvalidRowException(i + ": Expected 1, got " + sum);
            }
            sum = 0;
        }
    }
}
