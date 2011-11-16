package net.codjo.tokio.datagenerators;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class LongGeneratorTest {
    private LongGenerator generator;


    @Test
    public void test_generate() throws Exception {
        generator = new LongGenerator(5);

        assertEquals(new Long(1), generator.generateValue());
        assertEquals(new Long(2), generator.generateValue());
    }


    @Test
    public void test_generate_maxDigits() throws Exception {
        generator = new LongGenerator(2);

        for (int i = 1; i <= 100; i++) {
            String generatedValue = String.valueOf(generator.generateValue());
            assertTrue("Erreur à la boucle " + i + " (" + generatedValue + ")", generatedValue.length() <= 2);
        }
    }
}
