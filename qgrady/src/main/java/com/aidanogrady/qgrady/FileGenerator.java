package com.aidanogrady.qgrady;

import java.io.*;
import java.util.*;

/**
 * The FileGenerator class handles the operations that convert the Box class
 * into a .prism file that can be used in the model checker.
 *
 * @author Aidan O'Grady
 * @since 0.6
 */
public class FileGenerator {
    /**
     * The non-local box to be converted.
     */
    private Box box;

    /**
     * The file the .prism model is to be saved to.
     */
    private File dest;

    /**
     * The lines to be written to the file.
     */
    private List<String> lines;

    /**
     * The input variable names.
     */
    private List<String> inputs;

    /**
     * The input variable names.
     */
    private List<String> outputs;

    private static final int RANGE = 2;

    private String ready = "ready";

    /**
     * Constructs a new FileGenerator object.
     *
     * @param box  the non-local box to be written.
     * @param dest  the file to be written to.
     */
    public FileGenerator(Box box, File dest) {
        this.box = box;
        this.dest = dest;
        lines = new ArrayList<>();
    }


    /**
     * Begins the file writing process.
     */
    public void write() {
        try {
            FileWriter fw = new FileWriter(dest);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Systematically generates all the lines that are to be written to the
     * prism file.
     */
    public void generateLines() {
        inputs = box.getInputs();
        outputs = box.getOutputs();
        lines.add(PrismMacros.MODEL_TYPE);
        lines.add(PrismMacros.EMPTY_LINE);
        inputs();
        lines.add(PrismMacros.MODULE + " OUTPUT");
        outputs();
        lines.add(PrismMacros.END_MODULE);
        lines.add(PrismMacros.EMPTY_LINE);
    }

    /**
     * Returns a list of strings that form the input parts of the generated
     * file.
     */
    private void inputs() {
        for(String input : inputs) {
            String module = PrismMacros.MODULE + " INPUT_" + input;
            lines.add(module);

            lines.add(PrismMacros.varDec(input, RANGE - 1, -1));

            String sync = "";

            String guard = PrismMacros.isEqual(input, -1);
            String action = PrismMacros.equalDist(input, RANGE);
            lines.add(PrismMacros.command(sync, guard, action));
            lines.add(PrismMacros.EMPTY_LINE);

            for(int i = 0; i < RANGE; i++) {
                sync = input + i;
                guard = PrismMacros.isEqual(input, i);
                action = PrismMacros.assign(input, i);
                lines.add(PrismMacros.command(sync, guard, action));
            }

            lines.add(PrismMacros.EMPTY_LINE);
            lines.add(PrismMacros.END_MODULE);
            lines.add(PrismMacros.EMPTY_LINE);
        }
    }


    /**
     * Handles the generation of the output part of the Prism model.
     */
    private void outputs() {
        lines.add(PrismMacros.varDec(ready, 1, 1));
        for(String output : outputs) {
            lines.add(PrismMacros.varDec(output, RANGE - 1, -1));
        }
        lines.add(PrismMacros.EMPTY_LINE);
        outputSyncs();
        lines.add(PrismMacros.EMPTY_LINE);
        reduced();
        lines.add(PrismMacros.EMPTY_LINE);
        normalised();
    }


    /**
     * Generates the output syncs for handling the ready transitions.
     */
    private void outputSyncs() {
        for(int i = 0; i < box.getNoOfOutputs(); i++) {
            for(int j = 0; j < RANGE; j++) {
                String sync = outputs.get(i) + j;

                String[] guards = new String[2];
                guards[0] = PrismMacros.isEqual(ready, 0);
                guards[1] = PrismMacros.isEqual(outputs.get(i), j);
                List<String> list = Arrays.asList(guards);
                String guard = PrismMacros.listToString(list, '&');

                String[] actions = new String[2];
                actions[0] = PrismMacros.assign(ready, 1);
                actions[1] = PrismMacros.assign(outputs.get(i), j);
                list = Arrays.asList(actions);
                String action = PrismMacros.listToString(list, '&');
                lines.add(PrismMacros.command(sync, guard, action));
            }
        }
    }


    /**
     * Adds the reduced probabilities (P(a|x)) to be added to the file.
     */
    private void reduced() {
        // The guard is the same in all cases, so generate it first.
        List<String> guards = new ArrayList<>();
        guards.add(PrismMacros.isEqual(ready, 1));
        for (String output : outputs) {
            guards.add(PrismMacros.isEqual(output, -1));
        }
        String guard = PrismMacros.listToString(guards, '&');

        for(int i = 0; i < box.getNoOfOutputs(); i++) { // Handle each output
            for(int j = 0; j < RANGE; j++) { // Handle each input possibility
                String sync = inputs.get(i) + j;

                List<String> probs = new ArrayList<>();
                for(int k = 0; k < RANGE; k++) { // P(k | j);
                    List<String> actions = new ArrayList<>();
                    actions.add(PrismMacros.assign(ready, 0));
                    actions.add(PrismMacros.assign(outputs.get(i), k));
                    String action = PrismMacros.listToString(actions, '&');
                    double prob = box.prob(i, j, i, k);
                    probs.add(PrismMacros.prob(prob, action));
                }

                String action = PrismMacros.listToString(probs, '+');
                lines.add(PrismMacros.command(sync, guard, action));
            }
        }
    }

    private void normalised() {
        int in = (int) Math.pow(RANGE, inputs.size());
        int out = (int) Math.pow(RANGE, outputs.size());
        for(int i = 0; i < outputs.size(); i++) {
            String sync;
            String guard;
            String action;
            for(int j = 0; j < in; j++) {
                int[] inBits = Box.intToBitArray(j, inputs.size());
                sync = inputs.get(i) + inBits[i];
                List<String> guards = new ArrayList<>();
                guards.add(PrismMacros.isEqual(ready, 1));
                guards.add(PrismMacros.isEqual(outputs.get(i), -1));

                // Add the inputs to guard.
                for (int k = 0; k < inputs.size(); k++) {
                    if(k != i) {
                        guards.add(PrismMacros.isEqual(inputs.get(i), inBits[k]));
                    }
                }

                guards.add("");
                for(int k = 0; k < out / 2; k++) {
                    int[] bits = Box.intToBitArray(k, outputs.size() - 1);
                    for(int l = 0; l < outputs.size(); l++) {
                        if(l != i) {
                            int bit;
                            if(l > i) {
                                bit = bits[l - 1];
                            } else {
                                bit = bits[l];
                            }
                            guards.remove(guards.size() - 1);
                            guards.add(PrismMacros.isEqual(outputs.get(i), bit));
                        }
                    }
                    guard = PrismMacros.listToString(guards, '&');

                    int[] outBits = new int[outputs.size()];
                    for (int l = 0; l < bits.length; l++) {
                        if(l >= i) {
                            outBits[l + 1] = bits[l];
                        } else {
                            outBits[l] = bits[l];
                        }
                    }
                    outBits[i] = 0;

                    List<String> actions = new ArrayList<>();
                    for(int l = 0; l < RANGE; l++) {
                        outBits[i] = l;
                        double prob = box.normalisedProb(inBits, outBits, i);
                        if(prob > 0) {
                            List<String> acts = new ArrayList<>();
                            acts.add(PrismMacros.assign(ready, 0));
                            acts.add(PrismMacros.assign(outputs.get(i), l));
                            String act = PrismMacros.listToString(acts, '&');
                            actions.add(PrismMacros.prob(prob, act));
                        }
                    }
                    action = PrismMacros.listToString(actions, '+');
                    lines.add(PrismMacros.command(sync, guard, action));
                }
            }
            lines.add(PrismMacros.EMPTY_LINE);
        }
    }
}
