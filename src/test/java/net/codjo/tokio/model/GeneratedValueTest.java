package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.datagenerators.GeneratorFactoryMock;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GeneratedValueTest {
    private GeneratorFactoryMock generatorFactory = new GeneratorFactoryMock();


    @Test
    public void test_transform() throws Exception {
        generatorFactory.mockGenerator("StringGenerator");
        GeneratedValue generatedValue = new GeneratedValue(new GeneratorConfiguration("StringGenerator"));

        assertEquals("StringGenerator(null)", generatedValue.getValue());

        generatedValue.transform(generatorFactory);

        assertEquals("StringGenerator_1", generatedValue.getValue());
        assertEquals("StringGenerator_1", generatedValue.getValue());
    }
}
