package com.aidanogrady.qgrady;

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


}
