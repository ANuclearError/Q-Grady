package com.aidanogrady.qgrady;

import java.io.*;
import java.util.ArrayList;
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

        // Generate the input declarations.
        char input = 'z';
        for(int i = 0; i < box.getInputs(); i++) {
            inputLines.add(0, "\t" + input + ": [-1..1] init -1;");
            input--;
        }
        input++; // Reset so char starts at the last added character.

        // Generate 'coin toss' input selection.
        String line = "\t[] " + input + "=-1";
        input++;
        for(int i = 1; i < box.getInputs(); i++) {
            line += " & " + input + "=-1";
            input++;
        }
        line += " -> ";
        int outcomes = (int) Math.pow(2, box.getInputs());
        double prob = 1.0 / outcomes;
        for(int i = 0; i < outcomes; i++) {
            line += prob + " : ";
        }
        line += ";";
        inputLines.add(line);
        return inputLines;
    }
}
