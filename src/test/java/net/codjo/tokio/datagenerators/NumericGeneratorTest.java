package net.codjo.tokio.datagenerators;
import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class NumericGeneratorTest {
    private NumericGenerator generator;


    @Test
    public void test_generate() throws Exception {
        generator = new NumericGenerator(8, 5);

        BigDecimal decimal1 = generator.generateValue();
        BigDecimal decimal2 = generator.generateValue();

        assertEquals(decimal1.add(new BigDecimal("0.00001")), decimal2);
    }


    @Test
    public void test_generate_maxDigits() throws Exception {
        generator = new NumericGenerator(1, 0);

        for (int i = 1; i <= 100; i++) {
            String generatedValue = String.valueOf(generator.generateValue());
            assertTrue("Erreur à la boucle " + i + " (" + generatedValue + ")", generatedValue.length() <= 2);
        }
    }
}
