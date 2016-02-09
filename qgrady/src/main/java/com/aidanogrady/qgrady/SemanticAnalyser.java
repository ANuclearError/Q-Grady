package com.aidanogrady.qgrady;

import com.aidanogrady.qgrady.exceptions.InvalidRowException;
import com.aidanogrady.qgrady.exceptions.InvalidValueException;
import com.aidanogrady.qgrady.exceptions.SignallingException;

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
     * Determines whether there are any rows in the distribution with the
     * wrong length.
     *
     * Every row must be of equal length, so we must determine if there are any
     * that aren't.
     *
     * @param box  the 2D array extracted from parsing to checked.
     * @throws InvalidRowException
     */
    public static void validateRowLengths(double[][] box) throws
            InvalidRowException
    {
        int size = box[0].length;
        for(int i = 1; i < box.length; i++) {
            int row = box[i].length;
            if (row != size) {
                String msg = i + ": Expected: " + size + " values, got: " + row;
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
            if(sum != 1.0) {
                String msg = i + ": Expected sum of 1.0, got " + sum;
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
     * TODO: make generic for various set-ups beyond (2, 2, 2).
     *
     * @param box  the set-up beign examined.
     * @throws SignallingException
     */
    public static void nonSignalling(Box box) throws SignallingException {
        for (int a = 0; a < 2; a++) {
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    for (int y_ = 0; y_ < 2; y_++) {
                        double sumB = 0;
                        double sumB_ = 0;
                        for (int b = 0; b < 2; b++) {
                            int[] in = {x, y};
                            int[] out = {a, b};
                            sumB += box.prob(in, out);
                            in = new int[] {x, y_};
                            sumB_ += box.prob(in, out);
                        }
                        if (sumB != sumB_)
                            throw new SignallingException("Shit.");
                    }
                }
            }
        }
    }
}
