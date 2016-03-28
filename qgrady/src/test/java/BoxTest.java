import com.aidanogrady.qgrady.Box;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit testing of Box methods.
 *
 * @author Aidan O'Grady
 * @since 0.5.2
 */
public class BoxTest {
    private static Box box;
    private static double[][] pr;

    @BeforeClass
    public static void setup() {
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("x");
        inputs.add("y");
        outputs.add("a");
        outputs.add("b");
        // Perfectly valid PR box.
        pr = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };

        box = new Box(pr, inputs, outputs, 2, 2);
    }

    @Test
    public void convertListTest() {
        List<List<Double>> test = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Double> list = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                list.add(j / 10.0);
            }
            test.add(list);
        }
        double[][] res = Box.convertList(test);
        double[][] exp = new double[][] {
                {0.0, 0.1, 0.2},
                {0.0, 0.1, 0.2},
                {0.0, 0.1, 0.2}
        };
        assertArrayEquals(res, exp);
    }

    @Test
    public void testGetProbs() {
        double[][] res = box.getProbs();
        assertArrayEquals(res, pr);
    }

    @Test
    public void testInputs() {
        List<String> inputs = new ArrayList<>();
        inputs.add("x");
        inputs.add("y");
        assertEquals(inputs, box.getInputs());
        assertEquals(2, box.getNoOfInputs());
        assertEquals(2, box.getInputRange());

    }

    @Test
    public void testOutputs() {
        List<String> outputs = new ArrayList<>();
        outputs.add("a");
        outputs.add("b");
        assertEquals(outputs, box.getOutputs());
        assertEquals(2, box.getNoOfOutputs());
        assertEquals(2, box.getOutputRange());
    }


    @Test
    public void probTest() {
        int[][] options = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        for(int i = 0; i < options.length; i++) {
            for(int j = 0; j < options.length; j++) {
                assertEquals(box.prob(options[i], options[j]), pr[i][j], 0);
            }
        }
    }

    @Test
    public void probTestTwo() {
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    // In the PR box, all probabilities of p(a|x) == 0.5
                    assertEquals(box.prob(i, j, i, k), 0.5, 0);
                }
            }
        }
    }

    @Test
    public void probTestThree() {
        int[] in = {0, 0};
        int[] out = {1, 1};
        int index = 1;
        assertEquals(1.0, box.normalisedProb(in, out, index), 0);
        out = new int[] {0, 1};
        assertEquals(0.0, box.normalisedProb(in, out, index), 0);

    }
}
