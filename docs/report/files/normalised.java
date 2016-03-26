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