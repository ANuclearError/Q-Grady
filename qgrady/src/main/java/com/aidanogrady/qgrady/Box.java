package com.aidanogrady.qgrady;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The box class holds all the information that defines a non-local box,
 * containing the probability distribution, and handling the validity of the
 * non-local box as well.
 *
 * @author Aidan O'Grady
 * @since 0.4
 */
public class Box {
    private Map<Instance, Double> distribution;

    /**
     * The number of inputs in the set-up.
     */
    private int inputs;

    /**
     * The number of outputs in the set-up.
     */
    private int outputs;


    /**
     * Constructor.
     *
     * @param box - the probability distribution.
     */
    public Box(double[][] box) {
        distribution = new HashMap<Instance, Double>(0);
        inputs = (int) (Math.log(box.length) / Math.log(2));
        outputs = (int) (Math.log(box[0].length) / Math.log(2));

        for(int i = 0; i < box.length; i++) {
            int[] input = intToBitArray(i, inputs);
            for(int j = 0; j < box[i].length; j++) {
                int[] output = intToBitArray(j, outputs);
                Instance prob = new Instance(inputs, outputs);
                prob.setInput(input);
                prob.setOutput(output);
                distribution.put(prob, box[i][j]);
            }
        }
    }


    /**
     * Converts a given integer to an array of its bit representation of the
     * given size and returns.
     *
     * If the given size is larger than necessary, it will simply be filled
     * with 0s, such that intToBitArray(5, 5) returns [0, 0, 1, 0, 1].
     *
     * @param value  the number being converted
     * @param size  the size the array must fill.
     * @return array representation.
     */
    private int[] intToBitArray(int value, int size) {
        int[] array = new int[size];
        int index = size - 1;
        while(index >= 0) {
            array[index] = value % 2;
            value = value / 2;
            index--;
        }
        return array;
    }


    /**
     * Returns of the probability of the given output being produced by the
     * given input.
     *
     * @param input  input being examined
     * @param output  output being examined
     * @return p(output &#124; input)
     */
    public double prob(int[] input, int[] output) {
        Instance prob = new Instance(inputs, outputs);
        prob.setInput(input);
        prob.setOutput(output);
        return distribution.get(prob);
    }
}
