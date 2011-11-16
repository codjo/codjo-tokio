package net.codjo.tokio.datagenerators;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class GeneratorFactoryTest {
    private GeneratorFactory factory = new GeneratorFactory();


    @Test
    public void test_getGenerator() throws Exception {
        assertGenerator(BigDecimal.class,
                        generatorConfiguration(GeneratorConfiguration.GENERATE_NUMERIC, "6,4"));

        assertGenerator(Long.class,
                        generatorConfiguration(GeneratorConfiguration.GENERATE_INT, "3"));

        assertGenerator(String.class,
                        generatorConfiguration(GeneratorConfiguration.GENERATE_STRING, "4"));

        assertGenerator(Date.class,
                        generatorConfiguration(GeneratorConfiguration.GENERATE_DATE));

        assertGenerator(Boolean.class,
                        generatorConfiguration(GeneratorConfiguration.GENERATE_BOOLEAN));

        assertGenerator(Timestamp.class,
                        generatorConfiguration(GeneratorConfiguration.GENERATE_DATETIME));
    }


    private void assertGenerator(Class expected, GeneratorConfiguration conf) {
        Generator generator = factory.getGenerator(conf);
        assertSame(generator, factory.getGenerator(conf));
        assertEquals(expected, generator.generateValue().getClass());
    }


    private GeneratorConfiguration generatorConfiguration(String name) {
        return new GeneratorConfiguration(name);
    }


    private static GeneratorConfiguration generatorConfiguration(String name, String precision) {
        return new GeneratorConfiguration(name, precision);
    }
}
