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
     * The probability distribution in 2D array format.
     */
    private final double[][] probs;


    /**
     * The probability distribution.
     */
    private Map<Instance, Double> distribution;

    /**
     * The inputs of this box.
     */
    private List<String> inputs;

    /**
     * The outputs of this box.
     */
    private List<String> outputs;

    /**
     * Constructs a new box.
     *
     * @param inputs  the inputs of this box
     * @param outputs  the outputs of this box
     * @param probs  the probability distribution of this box.
     */
    public Box(List<String> inputs, List<String> outputs, double[][] probs) {
        distribution = new HashMap<>();
        this.inputs = inputs;
        this. outputs = outputs;
        this.probs = probs;
        for(int i = 0; i < probs.length; i++) {
            int[] input = intToBitArray(i, inputs.size());
            for(int j = 0; j < probs[i].length; j++) {
                int[] output = intToBitArray(j, outputs.size());
                Instance prob = new Instance(inputs.size(), outputs.size());
                prob.setInput(input);
                prob.setOutput(output);
                distribution.put(prob, probs[i][j]);
            }
        }
    }

    /**
     * Returns the probabilities of this box.
     * @return
     */
    public double[][] getProbs() {
        return probs;
    }

    /**
     * Returns the inputs of this setup.
     * @return inputs
     */
    public List<String> getInputs() {
        return inputs;
    }

    /**
     * Retrusn the number of inputs in this setup.
     * @return inputs size
     */
    public int getNoOfInputs() {
        return inputs.size();
    }


    /**
     * Returns the outputs of this setup.
     *
     * @return outputs
     */
    public List<String> getOutputs() {
        return outputs;
    }

    /**
     * Returns the number of outputs in this setup.
     *
     * @return outputs
     */
    public int getNoOfOutputs() {
        return outputs.size();
    }


    /**
     * Converts a List of Lists into a two-dimensional array. When parsing with
     * Cup, the List was preferred due to the ease of using of not having to
     * manually handle the dynamic array desired, hence this conversion method
     * to handle it instead.
     *
     * @param res - the distribution read in by the parser.
     * @return res as 2D array
     */
    public static double[][] convertList(List<List<Double>> res) {
        double[][] box = new double[res.size()][];
        for(int i = 0; i < res.size(); i++) {
            List<Double> row = res.get(i);
            box[i] = new double[row.size()];
            for(int j = 0; j < row.size(); j++) {
                box[i][j] = row.get(j);
            }
        }
        return box;
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
    public static int[] intToBitArray(int value, int size) {
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
        Instance prob = new Instance(inputs.size(), outputs.size());
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

            sum += prob;
        }

        // Need to perform some weird stuff to ensure that the reduced
        // probability is accurate.
        return sum / Math.pow(2, inputs.size() - 1);
    }

    /**
     * Returns the normalized probability distribution based on the given
     * input and output.
     * @param input - the known input values
     * @param outputIndex - the known output index
     * @param output - the known output to be normalised over.
     * @return probability
     */
    public double normalisedProb(int[] input, int[] output, int outputIndex) {
        int[] outputCopy = Arrays.copyOf(output, output.length);
        int range = 2;
        double sum = 0;
        for(int i = 0; i < range; i++) {
            outputCopy[outputIndex] = i;
            sum += prob(input, outputCopy);
        }
        return prob(input, output) / sum;
    }
}
