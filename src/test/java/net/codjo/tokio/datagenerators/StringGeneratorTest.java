package net.codjo.tokio.datagenerators;
import junit.framework.TestCase;
import org.junit.Test;
/**
 *
 */
public class StringGeneratorTest extends TestCase {

    @Test
    public void test_generateString() throws Exception {
        int length = 5;
        StringGenerator generator = new StringGenerator();
        String str = generator.generateString(length);

        assertNotNull(str);
        assertTrue(length >= str.length());
    }


    @Test
    public void test_generateLotOfStrings() throws Exception {
        for (int i = 0; i < 100000; i++) {
            test_generateString();
        }
    }
}
