import com.aidanogrady.qgrady.Box;
import com.aidanogrady.qgrady.SemanticAnalyser;
import com.aidanogrady.qgrady.exceptions.InvalidRowException;
import com.aidanogrady.qgrady.exceptions.InvalidValueException;
import com.aidanogrady.qgrady.exceptions.SignallingException;
import org.junit.*;
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
    public void validateHigherValuesTest() {
        try {
            SemanticAnalyser.validateValues(highVal);
        } catch (InvalidValueException e) {
            assertEquals(e.getMessage(), "Expected: between 0 and 1. Got: 1.5");
        }
    }

    @Test
    public void validateLowerValuesTest() {
        try {
            SemanticAnalyser.validateValues(lowVal);
        } catch (InvalidValueException e) {
            assertEquals(e.getMessage(), "Expected: between 0 and 1. Got: -1.0");
        }
    }

    @Test
    public void validateGreaterRowLengthsTest() {
        try {
            SemanticAnalyser.validateRowLengths(highRow);
        } catch (InvalidRowException e) {
            assertEquals(e.getMessage(), "Error in row 1: Expected: 4 values, got: 5");
        }
    }

    @Test
    public void validateSmallerRowLengthsTest() {
        try {
            SemanticAnalyser.validateRowLengths(lowRow);
        } catch (InvalidRowException e) {
            assertEquals(e.getMessage(), "Error in row 1: Expected: 4 values, got: 3");
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
        Box box = new Box(pr);
        try {
            SemanticAnalyser.nonSignalling(box);
        } catch (SignallingException e) {
            fail();
        }
    }

    @Test
    public void validateSignalling() {
        double[][] signalling = {
                {0.4, 0, 0, 0.6},
                {0.4, 0, 0, 0.6},
                {0.4, 0, 0, 0.6},
                {0, 0.4, 0.6, 0}
        };
        Box box = new Box(signalling);
        try {
            SemanticAnalyser.nonSignalling(box);
        } catch (SignallingException e) {
            assertEquals(e.getMessage(), "Signalling found.");
        }
    }

    @Test
    public void allIsWellTest() {
        try {
            SemanticAnalyser.validateValues(pr);
            SemanticAnalyser.validateRowLengths(pr);
            SemanticAnalyser.validateRowSums(pr);
        } catch(Exception e) {
            fail();
        }
    }
}
