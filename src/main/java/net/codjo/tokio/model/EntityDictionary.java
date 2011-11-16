/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
/**
 *
 */
public class EntityDictionary {
    private Map<String, Map<String, List<Row>>> entityToRows = new HashMap<String, Map<String, List<Row>>>();
    private Map<String, String> tableNameToOrderClause = new HashMap<String, String>();
    private Map<String, Boolean> tableNameToIdentityInsert = new HashMap<String, Boolean>();


    public void addRow(String entityId, String tableName, Row newRow) {
        if (!entityToRows.containsKey(entityId)) {
            entityToRows.put(entityId, new HashMap<String, List<Row>>());
        }
        Map<String, List<Row>> tableToRows = entityToRows.get(entityId);
        if (!tableToRows.containsKey(tableName)) {
            tableToRows.put(tableName, new ArrayList<Row>());
        }
        tableToRows.get(tableName).add(newRow);
    }


    public String[] getTableNames(String entity) {
        Set<String> names = new TreeSet<String>();

        for (String key : entityToRows.keySet()) {
            if (key.equals(entity) || key.startsWith(entity + ".")) {
                Map<String, List<Row>> tables = entityToRows.get(key);
                names.addAll(tables.keySet());
            }
        }

        return names.toArray(new String[names.size()]);
    }


    public Row[] getRows(String entity, String tableName) {
        List<Row> rows = new ArrayList<Row>();

        for (String key : entityToRows.keySet()) {
            if ((key != null) && (key.equals(entity) || key.startsWith(entity + "."))) {
                Map<String, List<Row>> tables = entityToRows.get(key);
                List<Row> rowList = tables.get(tableName);
                if (rowList != null && !rowList.isEmpty()) {
                    rows.addAll(rowList);
                }
            }
        }
        return rows.toArray(new Row[rows.size()]);
    }


    public boolean contains(String entity) {
        if (entityToRows.containsKey(entity)) {
            return true;
        }
        for (String key : entityToRows.keySet()) {
            if ((key != null) && (key.equals(entity) || key.startsWith(entity + "."))) {
                return true;
            }
        }
        return false;
    }


    public void setOrderClause(String tableName, String orderClause) {
        tableNameToOrderClause.put(tableName, orderClause);
    }


    public String getOrderClause(String tableName) {
        return tableNameToOrderClause.get(tableName);
    }


    public void setIdentityInsert(String tableName, boolean identityInsert) {
        tableNameToIdentityInsert.put(tableName, identityInsert);
    }


    public boolean isIdentityInsert(String tableName) {
        return tableNameToIdentityInsert.get(tableName);
    }
}
