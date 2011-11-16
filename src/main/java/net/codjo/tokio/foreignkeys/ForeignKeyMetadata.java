package net.codjo.tokio.foreignkeys;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForeignKeyMetadata {
    private final Map<String, List<ForeignKey>> tableToForeignKeys = new HashMap<String, List<ForeignKey>>();


    public List<ForeignKey> findForeignKeys(Connection connection, String tableName) {
        if (tableToForeignKeys.containsKey(tableName)) {
            return tableToForeignKeys.get(tableName);
        }

        List<ForeignKey> foreignKeys;
        try {
            foreignKeys = doFindForeignKey(connection, tableName);
        }
        catch (SQLException e) {
            throw new IllegalArgumentException(
                  "Problème d'accès à la base et/ou la table " + tableName + " n'existe pas !!!");
        }
        tableToForeignKeys.put(tableName, foreignKeys);
        return foreignKeys;
    }


    private List<ForeignKey> doFindForeignKey(Connection connection, String tableName) throws SQLException {
        ResultSet resultSet = connection.getMetaData()
              .getImportedKeys(connection.getCatalog(), null, tableName);
        try {
            ForeignKeyBuilder builder = new ForeignKeyBuilder();
            while (resultSet.next()) {
                builder.set(resultSet.getString("FK_NAME"),
                            resultSet.getString("FKTABLE_NAME"),
                            resultSet.getString("FKCOLUMN_NAME"),
                            resultSet.getString("PKTABLE_NAME"),
                            resultSet.getString("PKCOLUMN_NAME"));
            }
            return builder.get();
        }
        finally {
            resultSet.close();
        }
    }


    static class ForeignKeyBuilder {
        private final List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
        private String lastFkName;
        private ForeignKey foreignKey;


        public List<ForeignKey> get() {
            return foreignKeys;
        }


        public void set(String fkName,
                        String fromTableName,
                        String fromColName,
                        String toTableName,
                        String toColName) {
            if (lastFkName == null || !fkName.equals(lastFkName)) {
                lastFkName = fkName;
                foreignKey = new ForeignKey();
                foreignKey.setFromTableName(fromTableName);
                foreignKey.setToTableName(toTableName);
                foreignKeys.add(foreignKey);
            }

            foreignKey.addFromColumnName(fromColName);
            foreignKey.addToColumnName(toColName);
        }
    }
}
