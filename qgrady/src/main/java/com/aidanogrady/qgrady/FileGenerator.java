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
    private char[] inputs;

    /**
     * The input variable names.
     */
    private char[] outputs;

    /*
     * The following are strings that are used throughout the file generation
     * phase. Any instance of VAR or NUM indicates placeholders that will be
     * replaced by actual content.
     */
    private static final String COIN_TOSS =
            "\t[] VAR = -1 -> 0.5 : (VAR' =  0) + 0.5 : (VAR' = 1);";

    private static final String EMPTY_LINE = "";

    private static final String MODEL_TYPE = "dtmc";

    private static final String MODULE = "module ";

    private static final String END_MODULE = "endmodule";

    private static final String VARIABLE_DECLARATION = ": [-1..1] init - 1;";

    private static final String INPUT_SYNC =
            "\t[sync_INNUM] IN = NUM -> (IN' = -1);";

    private static final String OUTPUT_SYNC =
            "\t[sync_INNUM] OUT = -1 -> PROBS;";

    private static final String PROB = "PROB : (OUT' = NUM)";


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
        lines.add(MODEL_TYPE);
        lines.add(EMPTY_LINE);
        for(int i = 0; i < box.getInputs(); i++) {
            String name = "INPUT_" + Character.toUpperCase(inputs[i]);
            lines.add(MODULE + name);
            lines.addAll(input(inputs[i]));
            lines.add(END_MODULE);
            lines.add(EMPTY_LINE);
        }

        for(int i = 0; i < box.getOutputs(); i++) {
            String name = "OUTPUT_" + Character.toUpperCase(outputs[i]);
            lines.add(MODULE + name);
            lines.addAll(output(outputs[i], inputs[i], i));
            lines.add(END_MODULE);
            lines.add(EMPTY_LINE);
        }
    }


    /**
     * Generates the variable names based on the number of inputs and outputs to
     * satisfy.
     */
    private void generateVariables() {
        inputs = new char[box.getInputs()];
        char input = 'z';
        for (int i = inputs.length - 1; i > -1; i--) {
            inputs[i] = input;
            input--;
        }
        outputs = new char[box.getOutputs()];
        char output = 'a';
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = output;
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
    private List<String> input(char input) {
        List<String> lines = new ArrayList<>();
        lines.add("\t" + input + VARIABLE_DECLARATION);
        String in = input + "";
        lines.add(COIN_TOSS.replaceAll("VAR", in));
        for(int i = 0; i < 2; i++) {
            String num = i + "";
            lines.add(INPUT_SYNC.replaceAll("IN", in)
                    .replaceAll("NUM", num));
        }
        return lines;
    }


    /**
     * Returns a list of strings that form the output parts of the generated
     * file.
     *
     * @param output  the input being generated
     * @return lines
     */
    private List<String> output(char output, char input, int index) {
        List<String> lines = new ArrayList<>();
        lines.add("\t" + output + VARIABLE_DECLARATION);
        for(int i = 0; i < 2; i++) {
            String in = input + "";
            String out = output + "";
            String probs = "";
            List<String> list = new ArrayList<>();
            for(int j = 0; j < 2; j++) {
                String num = j + "";
                String prob =  box.prob(index, i, index, j) + "";
                list.add(PROB.replaceAll("PROB", prob)
                        .replaceAll("OUT", out)
                        .replaceAll("NUM", num));
            }
            Iterator<String> iterator = list.iterator();
            if(iterator.hasNext()) {
                probs += iterator.next();
            }
            while(iterator.hasNext()) {
                probs += " + " + iterator.next();
            }
            String num = i + "";
            String line = OUTPUT_SYNC.replaceAll("IN", in)
                    .replaceAll("OUT", out)
                    .replaceAll("NUM", num)
                    .replaceAll("PROBS", probs);
            lines.add(line);
        }
        return lines;
    }
}
