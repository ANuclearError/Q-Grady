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
    private String[] inputs;

    /**
     * The input variable names.
     */
    private String[] outputs;

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
        generateVariables();
        lines.add(PrismMacros.MODEL_TYPE);
        lines.add(PrismMacros.EMPTY_LINE);
        for(int i = 0; i < box.getInputs(); i++) {
            input(inputs[i]);
        }
        lines.add(PrismMacros.MODULE + " OUTPUT");
        output();
        lines.add(PrismMacros.END_MODULE);
        lines.add(PrismMacros.EMPTY_LINE);
    }

    /**
     * Generates the variable names based on the number of inputs and outputs to
     * satisfy.
     */
    private void generateVariables() {
        inputs = new String[box.getInputs()];
        char input = 'z';
        for (int i = inputs.length - 1; i > -1; i--) {
            inputs[i] = Character.toString(input);
            input--;
        }
        outputs = new String[box.getOutputs()];
        char output = 'a';
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = Character.toString(output);
            output++;
        }
    }


    /**
     * Returns a list of strings that form the input parts of the generated
     * file.
     *
     * @param input  the input being generated
     * @return lines
     */
    private void input(String input) {
        String module = PrismMacros.MODULE + " INPUT_" + input;
        lines.add(module);

        lines.add(PrismMacros.varDec(input, RANGE - 1));

        String sync = "";
        String guard = PrismMacros.isEqual(input, -1);
        String action = PrismMacros.equalDist(input, RANGE);
        lines.add(PrismMacros.command(sync, guard, action));

        for(int i = 0; i < RANGE; i++) {
            sync = input + i;
            guard = PrismMacros.isEqual(input, i);
            action = PrismMacros.assign(input, i);
            lines.add(PrismMacros.command(sync, guard, action));
        }

        lines.add(PrismMacros.END_MODULE);
        lines.add(PrismMacros.EMPTY_LINE);
    }


    /**
     * Handles the generation of the output part of the Prism model.
     */
    private void output() {
        lines.add(PrismMacros.varDec(ready, 1));
        for(int i = 0; i < box.getOutputs(); i++) {
            lines.add(PrismMacros.varDec(outputs[i], RANGE - 1));
        }
        lines.add(PrismMacros.EMPTY_LINE);
        for(int i = 0; i < box.getOutputs(); i++) {
            for(int j = 0; j < RANGE; j++) {
                String sync = outputs[i] + j;

                String[] guards = new String[2];
                guards[0] = PrismMacros.isEqual(ready, 0);
                guards[1] = PrismMacros.isEqual(outputs[i], j);
                List<String> list = Arrays.asList(guards);
                String guard = PrismMacros.listToString(list, '&');

                String[] actions = new String[2];
                actions[0] = PrismMacros.assign(ready, 1);
                actions[1] = PrismMacros.assign(outputs[i], j);
                list = Arrays.asList(actions);
                String action = PrismMacros.listToString(list, '&');
                lines.add(PrismMacros.command(sync, guard, action));
            }
        }
    }
}
