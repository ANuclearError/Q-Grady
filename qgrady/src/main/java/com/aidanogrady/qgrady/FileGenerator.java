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

    /**
     * The ready variable for use in the outputs' module.
     */
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
        lines.add("");
        inputs();
        lines.add(PrismMacros.MODULE + " OUTPUT");
        outputs();
        lines.add(PrismMacros.END_MODULE);
        lines.add("");
    }

    /**
     * Adds a list of strings that form the input parts of the generated
     * file.
     */
    private void inputs() {
        for(String input : inputs) {
            String module = PrismMacros.MODULE + " INPUT_" + input;
            lines.add(module);

            lines.add(PrismMacros.varDec(input, box.getInputRange() - 1, -1));

            String sync = "";

            String guard = PrismMacros.isEqual(input, -1);
            String action = PrismMacros.equalDist(input, box.getInputRange());
            lines.add(PrismMacros.command(sync, guard, action));
            lines.add("");

            for(int i = 0; i < box.getInputRange(); i++) {
                sync = input + i;
                guard = PrismMacros.isEqual(input, i);
                action = PrismMacros.assign(input, i);
                lines.add(PrismMacros.command(sync, guard, action));
            }

            lines.add("");
            lines.add(PrismMacros.END_MODULE);
            lines.add("");
        }
    }


    /**
     * Handles the generation of the output part of the Prism model.
     */
    private void outputs() {
        lines.add(PrismMacros.varDec(ready, true));
        for(String output : outputs) {
            lines.add(PrismMacros.varDec(output, box.getOutputRange() - 1, -1));
        }
        lines.add("");
        outputSyncs();
        lines.add("");
        reduced();
        lines.add("");
        normalised();
    }


    /**
     * Generates the output syncs for handling the ready transitions.
     */
    private void outputSyncs() {
        for(int i = 0; i < box.getNoOfOutputs(); i++) {
            for(int j = 0; j < box.getOutputRange(); j++) {
                String sync = outputs.get(i) + j;

                String[] guards = new String[2];
                guards[0] = PrismMacros.isEqual(ready, false);
                guards[1] = PrismMacros.isEqual(outputs.get(i), j);
                List<String> list = Arrays.asList(guards);
                String guard = PrismMacros.listToString(list, '&');

                String[] actions = new String[2];
                actions[0] = PrismMacros.assign(ready, true);
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
        guards.add(PrismMacros.isEqual(ready, true));
        for (String output : outputs) {
            guards.add(PrismMacros.isEqual(output, -1));
        }
        String guard = PrismMacros.listToString(guards, '&');

        // Handle all inputs and outputs
        for(int i = 0; i < box.getNoOfOutputs(); i++) {
            for(int j = 0; j < box.getInputRange(); j++) {
                String sync = "";
                if(i < box.getNoOfInputs())
                    sync = inputs.get(i) + j;

                List<String> probs = new ArrayList<>();
                for(int k = 0; k < box.getOutputRange(); k++) { // P(k | j);
                    List<String> actions = new ArrayList<>();
                    actions.add(PrismMacros.assign(ready, false));
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

    /**
     * Handles the generation of the lines that provide the normalised
     * probability transitions.
     */
    private void normalised() {
        List<List<Integer>> lists = getAllLists(box.getNoOfOutputs());
        for (List<Integer> list : lists) {
            for (int i = 0; i < box.getInputRange(); i++) {
                normalised(list, i);
            }
            lines.add("");
        }
    }

    private void normalised(List<Integer> indices, int val) {
        String sync = inputs.get(indices.get(0)) + val;
        int iMax = (int) Math.pow(
                box.getInputRange(),
                box.getNoOfInputs() - indices.size()
        );
        int oMax = (int) Math.pow(
                box.getOutputRange(),
                box.getNoOfOutputs() - indices.size()
        );
        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < oMax; j++) {
                List<String> guards = new ArrayList<>();
                guards.addAll(inputGuards(indices, i));
                guards.add(PrismMacros.isEqual(ready, true));
                guards.addAll(outputGuards(indices, j));
                String guard = PrismMacros.listToString(guards, '&');

                int size = box.getNoOfInputs() - indices.size();
                int[] in = Box.intToArray(i, size, box.getInputRange());
                size = box.getNoOfOutputs() - indices.size();
                int[] out = Box.intToArray(j, size, box.getOutputRange());
                List<String> commands = commands(indices, in, out, val);

                String command = PrismMacros.listToString(commands, '+');
                lines.add(PrismMacros.command(sync, guard, command));
            }
        }
    }

    /**
     * Returns a list of guards required for the normalised probabilities that
     * require the input values.
     *
     * @param indices  the indices being normalised
     * @param val  the value of this iteration
     * @return  guards
     */
    private List<String> inputGuards(List<Integer> indices, int val) {
        List<String> guards = new ArrayList<>();
        int size = box.getNoOfInputs() - indices.size();
        int[] bits = Box.intToArray(val, size, box.getInputRange());

        // Ensure that all possible guards are accounted for.
        int step = 0;
        for (int i= 0; i < box.getNoOfInputs(); i++) {
            if (!indices.contains(i)) {
                guards.add(PrismMacros.isEqual(inputs.get(i), bits[step]));
                step++;
            }
        }
        return guards;
    }

    /**
     * Returns a list of guards required for the normalised probabilities that
     * require the outputs values.
     *
     * @param indices  the indices being normalised
     * @param val  the value of this iteration
     * @return  guards
     */
    private List<String> outputGuards(List<Integer> indices, int val) {
        List<String> guards = new ArrayList<>();
        int size = box.getNoOfOutputs() - indices.size();
        int[] bits = Box.intToArray(val, size, box.getOutputRange());
        int step = 0;
        for (int i = 0; i < box.getNoOfOutputs(); i++) {
            if(indices.contains(i))
                guards.add(PrismMacros.isEqual(outputs.get(i), -1));
            else {
                guards.add(PrismMacros.isEqual(outputs.get(i), bits[step]));
                step++;
            }
        }
        return guards;
    }

    private List<String> commands(List<Integer> indices, int[] in, int[] out, int val) {
        int index = indices.get(0);

        int[] indArray = new int[indices.size()];
        for (int i = 0; i < indArray.length; i++) {
            indArray[i] = indices.get(i);
        }

        List<String> commands = new ArrayList<>();
        int[] input = getArray(indices, in, box.getNoOfInputs());
        int[] output = getArray(indices, out, box.getNoOfOutputs());
        input[index] = val;

        for (int i = 0; i < box.getOutputRange(); i++) {
            output[index] = i;
            double prob = box.normalisedProb(input, output, indArray);
            if(prob > 0) { // Ignore transitions that can't happen.
                List<String> acts = new ArrayList<>();
                acts.add(PrismMacros.assign(ready, false));
                acts.add(PrismMacros.assign(outputs.get(index), i));
                String act = PrismMacros.listToString(acts, '&');
                commands.add(PrismMacros.prob(prob, act));
            }
        }
        return commands;
    }

    private int[] getArray(List<Integer> indices, int[] array, int size) {
        int[] input = new int[size];
        int step = 0;
        for (int i = 0; i < size; i++) {
            if (!indices.contains(i)) {
                input[i] = array[step];
                step++;
            }
        }
        return input;
    }

   private List<List<Integer>> getAllLists(int range) {
       List<List<Integer>> lists = new ArrayList<>();
       for (int i = 1; i < range; i++) {
           int max = (int) Math.pow(range, i);
           for (int j = 0; j < max; j++) {

               int[] array = Box.intToArray(j, i, range);
               List<Integer> list = new ArrayList<>();
               for (int a : array)
                   list.add(a);
               // Must ensure that elements such as [0, 0] aren't added.
               boolean add = true;
               for (int k = 0; k < range; k++) {
                   if (Collections.frequency(list, k) > 1)
                       add = false;
               }
               if (add)
                   lists.add(list);
           }
       }
       return lists;
   }
}
