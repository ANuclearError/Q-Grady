package com.aidanogrady.qgrady;


import java.util.Arrays;

/**
 * Created by aidan on 08/02/2016.
 */
public class Probability {
    private int[] input;
    private int[] output;

    public Probability(int inputSize, int outputSize) {
        input  = new int[inputSize];
        output = new int[outputSize];
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
    public String toString() {
        return "Input: " + Arrays.toString(input) +
                ", Output: " + Arrays.toString(output);
    }
}
