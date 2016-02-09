package com.aidanogrady.qgrady;


import java.util.Arrays;

/**
 * @author Aidan O'Grady
 * @since 0.4
 */
public class Probability {
    private int[] input;
    private int[] output;

    public Probability(int inputSize, int outputSize) {
        input  = new int[inputSize];
        output = new int[outputSize];
    }

    public Probability(int[] input, int[] output) {
        this.input = input;
        this.output = output;
    }

    public void setInput(int[] input) {
        if(this.input.length == input.length)
            this.input = input;
    }

    public void setOutput(int[] output) {
        if(this.output.length == output.length)
            this.output = output;
    }

    public int[] getInput() {
        return input;
    }

    public int[] getOutput() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Probability that = (Probability) o;

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
