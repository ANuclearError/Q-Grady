private void normalised(List<Integer> indices, int val) {
    String sync = inputs.get(indices.get(0)) + val;
    int iMax = (int) Math.pow(box.getInputRange(),
        box.getNoOfInputs() - indices.size());
    int oMax = (int) Math.pow(box.getOutputRange(),
            box.getNoOfOutputs() - indices.size());

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