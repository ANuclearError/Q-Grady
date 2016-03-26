public double prob(int inputIndex, int input, int outputIndex, int output) {
    double sum = 0.0;
    // Go through all possibilities.
    int inMax = (int) Math.pow(inputRange, inputs.size());
    int outMax = (int) Math.pow(outputRange, outputs.size());
    for(int i = 0; i < inMax; i++) {
        for(int j = 0; j < outMax; j++) {
            int[] in = Box.intToArray(i, inputs.size(), inputRange);
            int[] out = Box.intToArray(j, outputs.size(), outputRange);
            if(in[inputIndex] == input  && out[outputIndex] == output)
                sum += prob(in, out);
        }
    }
    return sum / Math.pow(inputRange, inputs.size() - 1);
}