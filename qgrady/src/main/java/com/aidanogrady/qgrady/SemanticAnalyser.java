package com.aidanogrady.qgrady;

/**
 * The Semantic Analyser ensures that the semantics of the language are
 * maintained. It checks the probability distribution to ensure that all
 * constraints are met.
 */
public class SemanticAnalyser {
    /**
     * The probability distribution.
     */
    double[][] box;

    /**
     * Constructor
     * @param box - the probability distribution being checked.
     * @throws Exception
     */
    public SemanticAnalyser(double[][] box) throws Exception {
        this.box = box;
    }

    /**
     * Determines whether there are any invalid values within the distribution.
     *
     * Since we are dealing with probabilities, any negative value or value
     * greater than 1 is forbidden.
     */
    private void validateValues() {
        for(int i=0; i < box.length; i++) {
            for(int j=0; j < box[i].length; j++) {
                if(box[i][j] > 1.0 || box[i][j] < 0.0)
                    System.out.println("Row " + i + ", column " + j + " is not right");
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
    private void validateRowLengths() {
        int size = box[0].length;
        for(int i = 1; i < box.length; i++) {
            if (box[i].length != size)
                 System.out.println("Row " + i + "'s length is not right.");
        }
    }

    /**
     * Determines whether there are rows that do not sum to one.
     *
     * Since we are dealing with probabilities, every row must sum up to one to
     * ensure that the probabilities are accurate.
     */
    private void validateRowSums() {
        int sum = 0;
        for(int i=0; i < box.length; i++) {
            for(int j=0; j < box[i].length; j++) {
                sum += box[i][j];
            }
            if(sum != 1.0)
                System.out.println("Row " + i + " does not sum to 1");
            sum = 0;
        }
    }
}
