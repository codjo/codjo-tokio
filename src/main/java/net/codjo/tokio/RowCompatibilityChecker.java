package net.codjo.tokio;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.UniqueKey;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class RowCompatibilityChecker {

    public boolean matchUniqueKey(UniqueKey uniqueKey, Row row) {
        return hasSameFields(row, uniqueKey.toFieldList());
    }


    public boolean matchAllFields(Row requiredRow, Row row) {
        return hasSameFields(row, requiredRow.getFields().toFieldList());
    }


    public boolean checkIncompatibilityIgnoringUniqueKey(Row requiredRow, Row row) {
        for (Field keyField : getFieldsIgnoringUniqueKey(requiredRow)) {
            String fieldName = keyField.getName();
            if (row.containsField(fieldName) && !row.getFields().get(fieldName).isSame(keyField)) {
                return true;
            }
        }
        return false;
    }


    private boolean hasSameFields(Row row, List<Field> fields) {
        for (Field field : fields) {
            if (!hasSameField(row, field)) {
                return false;
            }
        }
        return true;
    }


    private boolean hasSameField(Row row, Field field) {
        String fieldName = field.getName();
        return row.containsField(fieldName) && row.getFields().get(fieldName).isSame(field);
    }


    private List<Field> getFieldsIgnoringUniqueKey(Row requiredRow) {
        List<Field> fields = new ArrayList<Field>(requiredRow.getFields().toFieldList());
        UniqueKey uniqueKey = requiredRow.getUniqueKey();
        if (uniqueKey != null) {
            fields.removeAll(uniqueKey.toFieldList());
        }
        return fields;
    }
}
