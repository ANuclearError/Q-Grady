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
    private static final String EMPTY_LINE = "";

    private static final String MODEL_TYPE = "dtmc";

    private static final String MODULE = "module ";

    private static final String END_MODULE = "endmodule";

    private static final String VAR_DEC = "\tVAR : [-1..1] init - 1;";

    private static final String COMMAND = "\t[SYNC] GUARD -> ACTION;";

    private static final String COIN_TOSS =
            "0.5 : (VAR' =  0) + 0.5 : (VAR' = 1)";

    private static final String ASSIGN = "(VAR' = VAL)";

    private static final String EQ_NEG_ONE = "VAR = -1";

    private static final String NEQ_NEG_ONE = "VAR != -1";

    /*
     * These are the placeholders in the above Strings to be replaced by the
     * non-local box set-up.
     */
    private static final String SYNC = "SYNC";

    private static final String GUARD = "GUARD";

    private static final String ACTION = "ACTION";

    private static final String VAR = "VAR";

    private static final String VAL = "VAL";


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
            lines.addAll(input(i));
            lines.add(END_MODULE);
            lines.add(EMPTY_LINE);
        }
        lines.add(MODULE + "OUTPUT");
        for(int i = 0; i < box.getOutputs(); i++) {
            lines.addAll(output(i));
        }
        lines.add(END_MODULE);
        lines.add(EMPTY_LINE);
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
     * @param index  the input being generated
     * @return lines
     */
    private List<String> input(int index) {
        List<String> lines = new ArrayList<>();
        String var = Character.toString(inputs[index]);
        lines.add(VAR_DEC.replaceAll("VAR", var));
        lines.add(EMPTY_LINE);

        // The initial coin toss action.
        lines.add(coinToss(index));
        for(int i = 0; i < outputs.length; i++) {
            if(index != i) {
                String sync = Character.toString(outputs[i]);
                lines.add(coinToss(index, sync));
            }
        }
        lines.add(EMPTY_LINE);

        // The sync actions on the input
        for(int i = 0; i < 2; i++) {
            String val = i + "";
            String sync = var + val;
            String guard = var + " = " + val;
            String action = ASSIGN.replaceAll(VAR, var)
                    .replaceAll(VAL, val);
            lines.add(COMMAND.replaceAll(SYNC, sync)
                .replaceAll(GUARD, guard)
                .replaceAll(ACTION, action));
        }
        return lines;
    }

    /**
     * Generates the coin toss line for input with the given index.
     *
     * This is the coin toss that acts as the potential first action of the
     * model. Thus, it requires an addition to the guard to ensure that all
     * inputs are uninitialized for this action to occur.
     *
     * @param index  the input being handled
     * @return line
     */
    private String coinToss(int index) {
        String in = Character.toString(inputs[0]);
        String guard = EQ_NEG_ONE.replaceAll(VAR, in);
        for(int i = 1; i < inputs.length; i++) {
            in = Character.toString(inputs[i]);
            guard += " & " + EQ_NEG_ONE.replaceAll(VAR, in);
        }
        in = Character.toString(inputs[index]);
        String toss = COIN_TOSS.replaceAll(VAR, in);
        return COMMAND.replaceAll(SYNC, "")
                .replaceAll(GUARD, guard)
                .replaceAll(ACTION, toss);

    }


    /**
     * Generates the coin toss line for input with the given index.
     *
     * This is a coin toss for an action further down the path, meaning that
     * an output has just recently been chosen. This syncs on that output to
     * produce the next action in the path.
     *
     * @param index  the input being chosen.
     * @param sync  the output that causes the sync.
     * @return
     */
    private String coinToss(int index, String sync) {
        String in = Character.toString(inputs[index]);
        String guard = EQ_NEG_ONE.replaceAll(VAR, in);
        in = Character.toString(inputs[index]);
        String toss = COIN_TOSS.replaceAll(VAR, in);
        return COMMAND.replaceAll(SYNC, sync)
                .replaceAll(GUARD, guard)
                .replaceAll(ACTION, toss);
    }


    /**
     * Returns a generated list of lines that handles the actions related to
     * the output with the given index.
     *
     * @param index  the output being worked on.
     * @return  lines
     */
    private List<String> output(int index) {
        List<String> lines = new ArrayList<>();
        String var = Character.toString(outputs[index]);
        lines.add(VAR_DEC.replaceAll(VAR, var));
        String guard = NEQ_NEG_ONE.replaceAll(VAR, var);
        String action = ASSIGN.replaceAll(VAR, var)
                .replaceAll(VAL, var);
        lines.add(COMMAND.replaceAll(SYNC, var)
                .replaceAll(GUARD, guard)
        .replaceAll(ACTION, action));
        lines.add(EMPTY_LINE);
        return lines;
    }
}
