import com.aidanogrady.qgrady.SemanticAnalyser;
import com.aidanogrady.qgrady.exceptions.InvalidValueException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * JUnit testing of SemanticAnalyser methods.
 */
public class SemanticAnalyserTest {
    static double[][] pr;
    static double[][] row;
    static double[][] val;
    static double[][] lowSum;
    static double[][] highSum;

    @BeforeClass
    public static void setup() {
        // Perfectly valid PR box.
        pr = new double[][]{
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };
        // Invalid PR box, 2nd row has 5 elements when the rest have 4.
        row = new double[][]{
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5, 0},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };
        // Invalid value box, 3rd row has a 1.5 when it should be >= 0, <= 1
        val = new double[][]{
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 1.5},
                {0, 0.5, 0.5, 0}
        };
        // Invalid sum box, 1st row sums to 0.6, not 1.
        lowSum = new double[][]{
                {0.5, 0, 0, 0.1},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };
        // Invalid sum box, 4th row sums to 1.1, not 1.
        highSum = new double[][]{
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.6, 0}
        };
    }

    @Test
    public void validateValuesTest() {
        try{
            SemanticAnalyser.validateValues(val);
        } catch (InvalidValueException e) {
            assertEquals(e.getMessage(), "Expected: between 0 and 1. Got: 1.5");
        }
    }
}
