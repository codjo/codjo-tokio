package net.codjo.tokio.datagenerators;
import junit.framework.TestCase;
import org.junit.Test;
/**
 *
 */
public class BooleanGeneratorTest extends TestCase {
    private BooleanGenerator generator = new BooleanGenerator();


    @Test
    public void test_generateBoolean() throws Exception {
        Boolean bool = generator.generateBoolean();

        assertNotNull(bool);
        assertTrue(bool || !bool);
    }
}
