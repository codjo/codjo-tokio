package net.codjo.tokio.datagenerators;
import java.util.HashMap;
import java.util.Map;

public class GeneratorFactoryMock extends GeneratorFactory {
    private final Map<String, Generator> keyToGenerator = new HashMap<String, Generator>();


    @Override
    public Generator getGenerator(GeneratorConfiguration configuration) {
        return keyToGenerator.get(configuration.getName());
    }


    public void mockGenerator(String key) {
        mockGenerator(key, null);
    }


    public void mockGenerator(final String key, final Object value) {
        keyToGenerator.put(key, new Generator() {
            int cpt = 0;


            public Object generateValue() {
                if (value == null) {
                    cpt++;
                    return key + "_" + cpt;
                }
                return value;
            }
        });
    }
}
