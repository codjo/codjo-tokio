package net.codjo.tokio;
import net.codjo.tokio.datagenerators.GeneratorFactory;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.GeneratedValue;
import net.codjo.tokio.model.ObjectValue;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import java.util.Iterator;

public class GeneratedValuesTansformer {
    private final GeneratorFactory generatorFactory;


    public GeneratedValuesTansformer() {
        this.generatorFactory = new GeneratorFactory();
    }


    public GeneratedValuesTansformer(GeneratorFactory generatorFactory) {
        this.generatorFactory = generatorFactory;
    }


    public void transform(DataSet dataset) {
        Iterator<Table> tables = dataset.tables();
        while (tables.hasNext()) {
            Table table = tables.next();
            for (Row row : table.getRows()) {
                Iterator<Field> fields = row.getFields().iterator();

                while (fields.hasNext()) {
                    Field field = fields.next();
                    for (ObjectValue objectValue : field.getValueObjectList()) {
                        if (GeneratedValue.class.isInstance(objectValue)) {
                            GeneratedValue generatedValue = (GeneratedValue)objectValue;
                            generatedValue.transform(generatorFactory);
                        }
                    }
                }
            }
        }
    }
}
