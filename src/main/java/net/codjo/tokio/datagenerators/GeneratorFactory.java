package net.codjo.tokio.datagenerators;
import java.util.HashMap;
import java.util.Map;

public class GeneratorFactory {
    private final Map<GeneratorConfiguration, Generator> generators
          = new HashMap<GeneratorConfiguration, Generator>();


    public GeneratorFactory() {
    }


    public Generator getGenerator(GeneratorConfiguration configuration) {
        Generator generator = null;
        if (!generators.containsKey(configuration)) {
            String generatorType = configuration.getName();
            if (GeneratorConfiguration.GENERATE_NUMERIC.equals(generatorType)) {
                String[] precision = configuration.getPrecision().split(",");
                generator = new NumericGenerator(Integer.valueOf(precision[0]),
                                                 Integer.valueOf(precision[1]));
            }
            else if (GeneratorConfiguration.GENERATE_INT.equals(generatorType)) {
                generator = new LongGenerator(Integer.valueOf(configuration.getPrecision()));
            }
            else if (GeneratorConfiguration.GENERATE_STRING.equals(generatorType)) {
                generator = new StringGenerator(Integer.valueOf(configuration.getPrecision()));
            }
            else if (GeneratorConfiguration.GENERATE_DATE.equals(generatorType)) {
                generator = new DateGenerator();
            }
            else if (GeneratorConfiguration.GENERATE_BOOLEAN.equals(generatorType)) {
                generator = new BooleanGenerator();
            }
            else if (GeneratorConfiguration.GENERATE_DATETIME.equals(generatorType)) {
                generator = new DateTimeGenerator();
            }

            generators.put(configuration, generator);
        }
        else {
            generator = generators.get(configuration);
        }
        return generator;
    }
}
