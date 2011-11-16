package net.codjo.tokio;
import net.codjo.database.common.api.SQLFieldList;
import net.codjo.database.common.impl.sqlfield.SQLField;
import net.codjo.database.common.repository.builder.SQLFieldListBuilder;
import net.codjo.tokio.foreignkeys.ForeignKey;
import net.codjo.tokio.foreignkeys.ForeignKeyMetadata;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImplicitDataTransformer {
    private final ForeignKeyMetadata foreignKeyMetadata;
    private final SQLFieldListBuilder sqlFieldListBuilder;


    public ImplicitDataTransformer(SQLFieldListBuilder sqlFieldListBuilder) {
        this(new ForeignKeyMetadata(), sqlFieldListBuilder);
    }


    public ImplicitDataTransformer(ForeignKeyMetadata foreignKeyMetadata,
                                   SQLFieldListBuilder sqlFieldListBuilder) {
        this.foreignKeyMetadata = foreignKeyMetadata;
        this.sqlFieldListBuilder = sqlFieldListBuilder;
    }


    public void transform(Connection connection, DataSet dataset) throws SQLException {
        for (Table table : toCollection(dataset.tables())) {
            for (Row row : toCollection(table.rows())) {
                if (row.isAutoComplete() == null && row.getRefId() != null) {
                    row.setAutoComplete(dataset.getRow(row.getRefId()).isAutoComplete());
                }

                if (Boolean.TRUE.equals(row.isAutoComplete())) {
                    completeRow(connection, table, row);
                    addRowsIfNeeded(connection, dataset, table, row);
                }
            }
        }
    }


    private static <T> List<T> toCollection(Iterator<T> it) {
        List<T> result = new ArrayList<T>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }


    private void completeRow(Connection connection, Table table, Row row) throws SQLException {
        SQLFieldList sqlFieldList = sqlFieldListBuilder.get(connection,
                                                            connection.getCatalog(),
                                                            table.getName(),
                                                            false);
        for (SQLField field : sqlFieldList.getValues()) {
            String fieldName = field.getName();
            if (!row.containsField(fieldName)) {
                String defaultValue = AutoCompleteConstants.getDefaultValue(field.getSQLType());
                row.setFieldValue(fieldName, defaultValue, null, null);
            }
        }
    }


    private void addRowsIfNeeded(Connection connection, DataSet dataset, Table table, Row row)
          throws SQLException {
        List<ForeignKey> foreignKeyList = foreignKeyMetadata.findForeignKeys(connection, table.getName());
        for (ForeignKey foreignKey : foreignKeyList) {
            if (row.getFields().fieldNameSet().containsAll(foreignKey.getFromColumnNames())
                && !fieldsAreAllNull(row, foreignKey.getFromColumnNames())
                && !existsRowInToTable(foreignKey, dataset, row)) {
                TableRow createdTableRow = createRowInToTable(foreignKey, dataset, row);
                completeRow(connection, createdTableRow.getTable(), createdTableRow.getRow());
                addRowsIfNeeded(connection, dataset, createdTableRow.getTable(), createdTableRow.getRow());
            }
        }
    }


    private boolean fieldsAreAllNull(Row row, List<String> columnNames) {
        FieldMap fieldMap = row.getFields();
        for (String columnName : columnNames) {
            String fieldValue = fieldMap.get(columnName).getValue();
            if (fieldValue != null && !"".equals(fieldValue)) {
                return false;
            }
        }
        return true;
    }


    private boolean existsRowInToTable(ForeignKey foreignKey, DataSet dataset, Row fromRow) {
        Table toTable = dataset.getTable(foreignKey.getToTableName());
        if (toTable == null) {
            return false;
        }

        for (Iterator<Row> toRowIt = toTable.rows(); toRowIt.hasNext();) {
            Row toRow = toRowIt.next();

            boolean existRowInTable = true;
            for (int i = 0; i < foreignKey.getFromColumnNames().size(); i++) {
                String fromColName = foreignKey.getFromColumnNames().get(i);
                String toColName = foreignKey.getToColumnNames().get(i);

                if (toRow.getFields().containsField(toColName)) {
                    String toValue = toRow.getFields().get(toColName).getValue();
                    String fromValue = fromRow.getFields().get(fromColName).getValue();

                    existRowInTable = (fromValue == null && toValue == null) ||
                                      (fromValue != null && fromValue.equals(toValue));
                }
                else {
                    existRowInTable = false;
                }

                if (!existRowInTable) {
                    break;
                }
            }

            if (existRowInTable) {
                return true;
            }
        }
        return false;
    }


    private TableRow createRowInToTable(ForeignKey foreignKey, DataSet dataset, Row fromRow) {
        FieldMap fields = new FieldMap();
        for (int i = 0; i < foreignKey.getFromColumnNames().size(); i++) {
            String fromColName = foreignKey.getFromColumnNames().get(i);
            String toColName = foreignKey.getToColumnNames().get(i);

            String fromValue = fromRow.getFields().get(fromColName).getValue();
            fields.put(new Field(toColName, fromValue));
        }
        Row createdRow = new Row(null, null, true, fields);
        dataset.addRow(foreignKey.getToTableName(), createdRow);
        return new TableRow(dataset.getTable(foreignKey.getToTableName()), createdRow);
    }


    class TableRow {
        private final Table table;
        private final Row row;


        TableRow(Table table, Row row) {
            this.table = table;
            this.row = row;
        }


        public Table getTable() {
            return table;
        }


        public Row getRow() {
            return row;
        }
    }
}
