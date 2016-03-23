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
     * The range from 0 of the input values.
     */
    private int inputRange;

    /**
     * The range from 0 of the input values.
     */
    private int outputRange;

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
     * @param inRange  the range of the given inputs
     * @param outRange the range of the given outputs
     * @param ins      the inputs of this box
     * @param outs     the outputs of this box
     */
    public Box(int inRange, int outRange, List<String> ins, List<String> outs) {
        this.inputs = ins;
        this.outputs = outs;
        this.inputRange = inRange;
        this.outputRange = outRange;
    }


    /**
     * Returns the inputs of this setup.
     *
     * @return inputs
     */
    public List<String> getInputs() {
        return inputs;
    }

    /**
     * Retrusn the number of inputs in this setup.
     *
     * @return inputs size
     */
    public int getNoOfInputs() {
        return inputs.size();
    }

    /**
     * Returns the range for the input values.
     *
     * @return inputRange
     */
    public int getInputRange() {
        return inputRange;
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
     * Returns the range for the output values.
     *
     * @return outputRange
     */
    public int getOutputRange() {
        return outputRange;
    }

    @Override
    public String toString() {
        return "Box{" +
                "inputRange=" + inputRange +
                ", outputRange=" + outputRange +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }
}
