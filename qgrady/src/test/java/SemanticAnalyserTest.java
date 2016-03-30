import com.aidanogrady.qgrady.Box;
import com.aidanogrady.qgrady.SemanticAnalyser;
import com.aidanogrady.qgrady.exceptions.InvalidRowException;
import com.aidanogrady.qgrady.exceptions.InvalidValueException;
import com.aidanogrady.qgrady.exceptions.InvalidVariableException;
import com.aidanogrady.qgrady.exceptions.SignallingException;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit testing of SemanticAnalyser methods.
 *
 * @author Aidan O'Grady
 * @since 0.5.2
 */
public class SemanticAnalyserTest {
    static double[][] pr;
    static double[][] highVal;
    static double[][] lowVal;
    static double[][] lowRow;
    static double[][] highRow;
    static double[][] lowSum;
    static double[][] highSum;

    @BeforeClass
    public static void setup() {
        // Perfectly valid PR box.
        pr = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };
        // Invalid value box, 3rd row has a 1.5 when it should be >= 0, <= 1
        highVal = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 1.5},
                {0, 0.5, 0.5, 0}
        };
        lowVal = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, -1},
                {0, 0.5, 0.5, 0}
        };
        // Invalid PR box, 2nd row has 5 elements when the rest have 4.
        highRow = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5, 0},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };
        lowRow = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };
        // Invalid sum box, 4th row sums to 1.1, not 1.
        highSum = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.6},
                {0, 0.5, 0.5, 0}
        };
        // Invalid sum box, 1st row sums to 0.6, not 1.
        lowSum = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.1, 0}
        };
    }

    @Test
    public void validateVariablesTest() {
        List<String> inputs = Arrays.asList("x", "y");
        List<String> outputs = Arrays.asList("x", "b");
        Box box = new Box(pr, inputs, outputs, 2, 2);
        try {
            SemanticAnalyser.validateVariables(box);
        } catch (InvalidVariableException e) {
            assertEquals(e.getMessage(), "Variable x is both input and output.");
        }
        outputs.set(0, "b");
        try {
            SemanticAnalyser.validateVariables(box);
        } catch (InvalidVariableException e) {
            assertEquals(e.getMessage(), "Variable b is used multiple times.");
        }
        inputs.set(0, "dtmc");
        try {
            SemanticAnalyser.validateVariables(box);
        } catch (InvalidVariableException e) {
            assertEquals(e.getMessage(), "Variable dtmc is a Prism keyword.");
        }
    }

    @Test
    public void validateUnequalVariablesTest() {
        List<String> inputs = Arrays.asList("x", "y");
        List<String> outputs = new ArrayList<>();
        outputs.add("a");
        outputs.add("b");
        outputs.add("c");
        Box box = new Box(pr, inputs, outputs, 2, 2);
        try {
            SemanticAnalyser.validateVariables(box);
        } catch (InvalidVariableException e) {
            assertEquals(e.getMessage(), "Unequal number of inputs and outputs!");
        }

    }

    @Test
    public void validateRowAmountsTest() {
        List<String> inputs = Arrays.asList("x", "y");
        List<String> outputs = Arrays.asList("x", "b");
        Box box = new Box(pr, inputs, outputs, 3, 3);
        try {
            SemanticAnalyser.validateRowAmount(box);
        } catch (InvalidRowException e) {
            assertEquals(e.getMessage(), "Set up has 2 inputs with range 3. Expected matrix to have 9 rows, got 4.");
        }


    }

    @Test
    public void validateHigherValuesTest() {
        try {
            SemanticAnalyser.validateValues(highVal);
        } catch (InvalidValueException e) {
            assertEquals(e.getMessage(), "Error in row 2, column 3: expected between 0 and 1, got 1.5");
        }
    }

    @Test
    public void validateLowerValuesTest() {
        try {
            SemanticAnalyser.validateValues(lowVal);
        } catch (InvalidValueException e) {
            assertEquals(e.getMessage(), "Error in row 2, column 3: expected between 0 and 1, got -1.0");
        }
    }

    @Test
    public void validateGreaterRowLengthsTest() {
        try {
            List<String> inputs = new ArrayList<>();
            List<String> outputs = new ArrayList<>();
            inputs.add("x");
            outputs.add("y");
            inputs.add("a");
            outputs.add("b");

            Box box = new Box(highRow, inputs, outputs, 2, 2);

            SemanticAnalyser.validateRowLengths(box);
        } catch (InvalidRowException e) {
            assertEquals(e.getMessage(), "Error in row 1: Expected 4 values, got 5");
        }
    }

    @Test
    public void validateSmallerRowLengthsTest() {
        try {
            List<String> inputs = new ArrayList<>();
            List<String> outputs = new ArrayList<>();
            inputs.add("x");
            outputs.add("y");
            inputs.add("a");
            outputs.add("b");

            Box box = new Box(lowRow, inputs, outputs, 2, 2);

            SemanticAnalyser.validateRowLengths(box);
        } catch (InvalidRowException e) {
            assertEquals(e.getMessage(), "Error in row 1: Expected 4 values, got 3");
        }
    }

    @Test
    public void validateGreaterRowSumTest() {
        try {
            SemanticAnalyser.validateRowSums(highSum);
        } catch (InvalidRowException e) {
            assertEquals(e.getMessage(), "Error in row 2: Expected sum of 1.0, got 1.1");
        }
    }

    @Test
    public void validateSmallerRowSumTest() {
        try {
            SemanticAnalyser.validateRowSums(lowSum);
        } catch (InvalidRowException e) {
            assertEquals(e.getMessage(), "Error in row 3: Expected sum of 1.0, got 0.6");
        }
    }

    @Test
    public void validateNonsignalling() {
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("x");
        outputs.add("y");
        inputs.add("a");
        outputs.add("b");

        Box box = new Box(pr, inputs, outputs, 2, 2);
        try {
            SemanticAnalyser.nonSignalling(box);
        } catch (SignallingException e) {
            fail();
        }
    }

    @Test
    public void validateSignalling() {
        double[][] signalling = {
                {0.5, 0.5, 0, 0},
                {0, 0, 0.5, 0.5},
                {0.5, 0.5, 0, 0},
                {0, 0, 0.5, 0,5}
        };
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("x");
        outputs.add("y");
        inputs.add("a");
        outputs.add("b");
        Box box = new Box(signalling, inputs, outputs, 2, 2);
        try {
            SemanticAnalyser.nonSignalling(box);
            fail();
        } catch (SignallingException e) {
            assertEquals(e.getMessage(), "Signalling found");
        }
    }

    @Test
    public void allIsWellTest() {
        try {
            List<String> inputs = new ArrayList<>();
            List<String> outputs = new ArrayList<>();
            inputs.add("x");
            outputs.add("y");
            inputs.add("a");
            outputs.add("b");
            Box box = new Box(pr, inputs, outputs, 2, 2);

            SemanticAnalyser.validateVariables(box);
            SemanticAnalyser.validateValues(pr);
            SemanticAnalyser.validateRowAmount(box);
            SemanticAnalyser.validateRowLengths(box);
            SemanticAnalyser.validateRowSums(pr);
            SemanticAnalyser.nonSignalling(box);
        } catch(Exception e) {
            fail();
        }
    }
}
