package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.datagenerators.GeneratorFactory;

public class GeneratedValue implements ObjectValue {
    private GeneratorConfiguration configuration;
    private String generatedValue;


    public GeneratedValue(GeneratorConfiguration configuration) {
        this.configuration = configuration;
    }


    public String getValue() {
        if (generatedValue == null) {
            return configuration.getName() + "(" + configuration.getPrecision() + ")";
        }
        return generatedValue;
    }


    public boolean isGenerated() {
        return true;
    }


    public ObjectValue duplicate() {
        return new GeneratedValue(configuration);
    }


    public GeneratorConfiguration getConfiguration() {
        return configuration;
    }


    public void transform(GeneratorFactory generatorFactory) {
        generatedValue = String.valueOf(generatorFactory.getGenerator(configuration).generateValue());
    }
}
