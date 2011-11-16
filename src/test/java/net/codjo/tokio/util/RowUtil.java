package net.codjo.tokio.util;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Location;
import net.codjo.tokio.model.LocationPointer;
import net.codjo.tokio.model.Row;

public class RowUtil {
    private RowUtil() {
    }


    public static Row row(Field... fields) {
        return row(null, null, fields);
    }


    public static Row row(Boolean autoComplete, Field... fields) {
        return row(autoComplete, null, fields);
    }


    public static Row row(String id, Field... fields) {
        return row(null, id, fields);
    }


    public static Row row(String[] uniqueKey, Field... fields) {
        Row row = row(fields);
        for (String field : uniqueKey) {
            row.addUniqueKey(field);
        }
        return row;
    }


    public static Row row(Boolean autoComplete, String id, Field... fields) {
        FieldMap fieldMap = new FieldMap();
        for (Field field : fields) {
            fieldMap.put(field);
        }
        Row row = new Row(id, null, autoComplete, fieldMap);
        row.setLocationPointer(new LocationPointer(new Location(RowUtil.class.getSimpleName(), 77, "row")));
        return row;
    }


    public static Field field(String key, String value) {
        return new Field(key, value);
    }


    public static Field nullField(String key) {
        return new Field(key);
    }


    public static Field generatedField(String key, GeneratorConfiguration configuration) {
        return new Field(key, configuration);
    }


    public static String[] uniqueKey(String... uniqueKey) {
        return uniqueKey;
    }
}
