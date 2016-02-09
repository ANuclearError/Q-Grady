package com.aidanogrady.qgrady;


import java.util.Arrays;

/**
 * The Instance class represents a particular result in the non-local box set-up.
 * It is a pairing of inputs and outputs that are possible in the given set-up.
 * This is used to allow for an easier grouping of inputs and outputs, in order
 * to cleanly map them to probabilities.
 *
 * @author Aidan O'Grady
 * @since 0.4
 */
public class Instance {
    /**
     * The inputs that produce this outcome.
     */
    private int[] input;

    /**
     * The outputs of this particular outcome.
     */
    private int[] output;

    /**
     * Constructor.
     *
     * @param inputSize - the number of inputs in this set-up.
     * @param outputSize - the number of outputs in this set-up.
     */
    public Instance(int inputSize, int outputSize) {
        input  = new int[inputSize];
        output = new int[outputSize];
    }

    /**
     * Sets the input to the given array.
     * @param input - the inputs of this instance.
     */
    public void setInput(int[] input) {
        if(this.input.length == input.length)
            this.input = input;
    }

    /**
     * Sets the output to the given array.
     * @param output - the outputs of this instance.
     */
    public void setOutput(int[] output) {
        if(this.output.length == output.length)
            this.output = output;
    }

    /**
     * Returns the inputs of this instance.
     * @return
     */
    public int[] getInput() {
        return input;
    }

    /**
     * Returns the inputs of this instance.
     * @return
     */
    public int[] getOutput() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instance that = (Instance) o;

        if (!Arrays.equals(input, that.input)) return false;
        return Arrays.equals(output, that.output);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(input);
        result = 31 * result + Arrays.hashCode(output);
        return result;
    }

    @Override
    public String toString() {
        return "Input: " + Arrays.toString(input) +
                ", Output: " + Arrays.toString(output);
    }
}
