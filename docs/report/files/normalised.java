public double normalisedProb(int[] input, int[] output, int index)
{
    int[] outputCopy = Arrays.copyOf(output, output.length);
    double sum = 0;
    for(int i = 0; i < outputRange; i++) {
        outputCopy[index] = i;
        sum += prob(input, outputCopy);
    }
    return prob(input, output) / sum;
}

public double normalisedProb(int[] input, int[] output, int[] indices)
{
    double sum = 0;
    int size = indices.length;
    int[] inputCopy = Arrays.copyOf(input, input.length);
    for (int i = 1; i < indices.length; i++) {
        for (int j = 0; j < inputRange; j++) {
            inputCopy[indices[i]] = j;
            int max = (int) Math.pow(outputRange, size);
            int[] outputCopy = Arrays.copyOf(output, output.length);
            for (int k = 0; k < max; k++) {
                int[] bits = Box.intToArray(k, size, outputRange);
                for (int l = 0; l < size; l++) {
                    outputCopy[indices[l]] = bits[l];
                }
                sum += prob(inputCopy, outputCopy);
            }
        }
    }
    return sum / Math.pow(outputRange, size - (outputs.size() - size));
} 

public double normalisedProb(int[] input, int[] output, int[] indices)
{
    int[] outputCopy = Arrays.copyOf(output, output.length);
    for (int index : indices) {
        for (int i = 0; i < outputRange; i++) {
            outputCopy[index] = i;
            sum += prob(input, outputCopy);
        }
    }
    return prob(input, output) / sum;
}

