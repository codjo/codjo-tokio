package net.codjo.tokio;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.SQLFieldList;
import net.codjo.database.common.repository.builder.SQLFieldListBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DefaultSQLFieldListBuilder implements SQLFieldListBuilder {
    private final Map<Key, SQLFieldList> tableToFields = new HashMap<Key, SQLFieldList>();
    private final DatabaseFactory databaseFactory = new DatabaseFactory();


    public SQLFieldList get(Connection connection,
                            String catalog,
                            String tableName,
                            boolean temporaryTable) throws SQLException {
        DefaultSQLFieldListBuilder.Key key = new Key(catalog, tableName, temporaryTable);
        SQLFieldList sqlFieldList = tableToFields.get(key);
        if (sqlFieldList == null) {
            sqlFieldList = databaseFactory.createSQLFieldList(connection,
                                                              catalog,
                                                              tableName,
                                                              temporaryTable);
            tableToFields.put(key, sqlFieldList);
        }
        return sqlFieldList;
    }


    static class Key {
        private String catalog;
        private String tableName;
        private boolean temporary;


        Key(String catalog, String tableName, boolean temporary) {
            this.catalog = catalog;
            this.tableName = tableName;
            this.temporary = temporary;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key)o;

            if (temporary != key.temporary) {
                return false;
            }
            if (catalog != null ? !catalog.equals(key.catalog) : key.catalog != null) {
                return false;
            }
            if (tableName != null ? !tableName.equals(key.tableName) : key.tableName != null) {
                return false;
            }

            return true;
        }


        @Override
        public int hashCode() {
            int result = catalog != null ? catalog.hashCode() : 0;
            result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
            result = 31 * result + (temporary ? 1 : 0);
            return result;
        }
    }
}
