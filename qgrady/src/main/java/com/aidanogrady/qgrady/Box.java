package com.aidanogrady.qgrady;

import java.util.*;

/**
 * The box class holds all the information that defines a non-local box,
 * containing the probability distribution, and handling the validity of the
 * non-local box as well.
 *
 * @author Aidan O'Grady
 * @since 0.4
 */
public class Box {

    /**
     * The probability distribution.
     */
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
        distribution = new HashMap<>();
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
     * Returns the number of inputs in this setup.
     *
     * @return inputs
     */
    public int getInputs() {
        return inputs;
    }


    /**
     * Returns the number of inputs in this setup.
     *
     * @return outputs
     */
    public int getOutputs() {
        return outputs;
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


    /**
     * Returns the probability of the given single output and its value based on
     * the given single input and it's value.
     *
     * This is currently horrific and terrifying.
     *
     * @param inputIndex  the index of the input being examined.
     * @param input  the value of the input.
     * @param outputIndex  the index of the output being examined.
     * @param output  the value of the output.
     * @return  p(output | input).
     */
    public double prob(int inputIndex, int input, int outputIndex, int output) {
        double sum = 0.0;
        // Iterate through map
        // Extract all where input[inputIndex] == input (and same for output)
        Map<Instance, int[]> horribleMap = new HashMap<>();
        distribution.forEach((k, v) -> {
            int[] in = k.getInput();
            int[] out = k.getOutput();
            if(in[inputIndex] == input && out[outputIndex] == output) {
                horribleMap.put(k, k.getInput());
            }
        });

        // Because Maps don't all us to do by themselves, need to convert
        // the values collection into a set to remove repeats.
        Set<int[]> horribleSet = new HashSet<>(horribleMap.values());

        // Sum every matching input, then multiply them to get probability.
        for(int[] in : horribleSet) {
            double prob = 0.0;
            for(Map.Entry<Instance, int[]> entry : horribleMap.entrySet()) {
                if(Arrays.equals(in, entry.getValue())) {
                    prob += distribution.get(entry.getKey());
                }
            }
            if(sum == 0.0) { // Check that we're not multiplying by 0.
                sum += prob;
            } else {
                sum *= prob;
            }
        }
        return sum;
    }

    /**
     * Returns the normalized probability distribution based on the given
     * input and output.
     * @param input - the known input values
     * @param outputIndex - the known output index
     * @param output - the known output value
     * @return
     */
    public double normalisedProb(int[] input, int[] output, int outputIndex) {
        int[] outputCopy = output;
        outputCopy[outputIndex] = 0;
        double sum = prob(input, outputCopy);
        outputCopy[outputIndex] = 1;
        sum += prob(input, outputCopy);
        return prob(input, output) / sum;
    }
}
