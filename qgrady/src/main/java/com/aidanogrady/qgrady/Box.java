package com.aidanogrady.qgrady;

import java.util.List;

/**
 * The box class holds all the information that defines a non-local box,
 * containing the probability distribution, and handling the validity of the
 * non-local box as well.
 *
 * @author Aidan O'Grady
 * @since 0.2
 */
public class Box {

    /**
     * The probability distribution.
     */
    double[][] box;

    /**
     * Constructor.
     *
     * @param box - the probability distribution.
     */
    public Box(double[][] box) {
        this.box = box;
    }

    /**
     * Constructor.
     *
     * @param box - the probability distribution.
     */
    public Box(List<List<Double>> box) {
        this.box = new double[box.size()][];
        for(int i = 0; i < box.size(); i++) {
            List<Double> row = box.get(i);
            this.box[i] = new double[row.size()];
            for(int j = 0; j < row.size(); j++) {
                this.box[i][j] = row.get(i);
            }
        }
    }

}
