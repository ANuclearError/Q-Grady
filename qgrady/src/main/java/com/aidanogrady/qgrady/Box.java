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

    /**
     * Returns of the probability of the given output being produced by the
     * given input.
     *
     * @param input - input being exmained
     * @param output - outpu being examined
     * @return p(output, input)
     */
    public double prob(int[] input, int[] output) {
        int in = 0;
        for (int i : input) {
            in = in * 2 + i;
        }
        int out = 0;
        for (int o : output) {
            out = out * 2 + o;
        }
        return box[in][out];
    }

    /**
     * Returns of the probability of the given output being produced by the
     * given input.
     *
     * @param input - input being exmained
     * @param output - outpu being examined
     * @return p(output, input)
     */
    public double prob(int input, int output) {
        return 0.5;
    }
}
