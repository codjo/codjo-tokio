package net.codjo.tokio;
import net.codjo.database.common.api.SQLFieldList;
import net.codjo.database.common.impl.sqlfield.DefaultSQLFieldList;
import net.codjo.database.common.repository.builder.SQLFieldListBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLFieldListBuilderMock implements SQLFieldListBuilder {
    private static final String DEFAULT_TABLE = "#DEFAULT_TABLE#";
    private final Map<String, DefaultSQLFieldList> tableToFields = new HashMap<String, DefaultSQLFieldList>();


    public SQLFieldList get(Connection connection, String catalog, String tableName, boolean temporaryTable)
          throws SQLException {
        return tableToFields.get(tableName);
    }


    public void mockField(String fieldName, int fieldType) {
        mockField(DEFAULT_TABLE, fieldName, fieldType);
    }


    public void mockField(String tableName, String fieldName, int fieldType) {
        DefaultSQLFieldList sqlFieldList = tableToFields.get(tableName);
        if (sqlFieldList == null) {
            sqlFieldList = new DefaultSQLFieldList();
            tableToFields.put(tableName, sqlFieldList);
        }
        sqlFieldList.addField(fieldName, fieldType);
    }
}
