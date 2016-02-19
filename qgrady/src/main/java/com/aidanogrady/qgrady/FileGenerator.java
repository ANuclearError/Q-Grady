package com.aidanogrady.qgrady;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * The type of the .prism model.
     */
    private static final String MODEL_TYPE = "dtmc";

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
     * Systematically generates all the lines that are to be written to the
     * prism file.
     */
    public void generateLines() {
        lines.add(MODEL_TYPE);
        lines.add("");
        lines.add("module M1");
        lines.addAll(inputLines());
        lines.add("endmodule");
        lines.add("");
        lines.add("module M2");
        lines.add("endmodule");

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
     * Returns the lines generated that handle the input part of the model.
     *
     * @return inputLines
     */
    private List<String> inputLines() {
        List<String> inputLines = new ArrayList<>();

        char[] inputs = new char[box.getInputs()];
        char input = 'z';
        for(int i = inputs.length - 1; i > -1; i--) {
            inputs[i] = input;
            input--;
        }

        for(char in : inputs) {
            inputLines.add(0, "\t" + in + ": [-1..1] init -1;");
        }

        // Generate 'coin toss' input selection.
        // LHS
        String line = "\t[] " + inputs[0] + "=-1";
        for(int i = 1; i < inputs.length; i++) {
            line += " & " + inputs[i] + "=-1";
        }

        line += " -> ";

        // RHS
        int outcomes = (int) Math.pow(2, box.getInputs());
        double prob = 1.0 / outcomes;
        line += prob + " : (" + inputs[0] + "'=0)";
        for(int i = 1; i < inputs.length; i++) {
            line += " & (" + inputs[i] + "'=0)";
        }
        for(int i = 1; i < outcomes; i++) {
            int[] vals = intToBitArray(i, box.getInputs());
            line += " + " + prob + " : (" + inputs[0] + "'=" + vals[0] + ")";
            for(int j = 1; j < inputs.length; j++) {
                line += " & (" + inputs[j] + "'=" + vals[j] + ")";
            }
        }
        line += ";";
        inputLines.add(line);
        return inputLines;
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
}
