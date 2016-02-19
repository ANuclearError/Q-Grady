import com.aidanogrady.qgrady.Box;
import org.junit.*;
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
        // Perfectly valid PR box.
        pr = new double[][] {
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0.5, 0, 0, 0.5},
                {0, 0.5, 0.5, 0}
        };

        box = new Box(pr);
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
                    // In the PR box, all probabilities of p(a|x) == 0.25
                    assertEquals(box.prob(i, j, i, k), 0.25, 0);
                }
            }
        }
    }
}
