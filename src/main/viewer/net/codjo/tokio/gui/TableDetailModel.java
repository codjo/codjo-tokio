package net.codjo.tokio.gui;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
/**
 *
 */
public class TableDetailModel extends AbstractTableModel {
    private Table table;
    private List<String> fieldNames;


    public TableDetailModel(Table table) {
        this.table = table;
        Set<String> fields = new HashSet<String>();
        List<Row> rows = table.getRows();
        for (Row row : rows) {
            FieldMap map = row.getFields();
            Iterator<Field> iterator = map.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                fields.add(field.getName());
            }
        }
        fieldNames = new ArrayList<String>(fields);
        Collections.sort(fieldNames);
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Field.class;
    }


    @Override
    public String getColumnName(int column) {
        return fieldNames.get(column);
    }


    public int getRowCount() {
        return table.getRowCount();
    }


    public int getColumnCount() {
        return fieldNames.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        Row row = table.getRow(rowIndex);
        String fieldName = getColumnName(columnIndex);
        if (row.containsField(fieldName)) {
            return row.getFields().get(fieldName);
        }
        return new Field(fieldName, "");
    }
}
