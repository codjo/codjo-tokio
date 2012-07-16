/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * Classe designant une table de data.
 *
 * @author $Author: crego $
 * @version $Revision: 1.10 $
 */
public class Table {
    private RowDictionary rowDictionary = new RowDictionary();
    private List<Row> rows = new ArrayList<Row>();
    private boolean identityInsert;
    private String name;
    private String orderClause;
    private boolean temporary;
    private boolean nullFirst = true;


    public Table(String name, RowDictionary rowDictionary) {
        setName(name);
        setRowDictionnary(rowDictionary);
    }


    public boolean isIdentityInsert() {
        return identityInsert;
    }


    public void setIdentityInsert(Boolean identityInsert) {
        this.identityInsert = (identityInsert != null && identityInsert);
    }


    public boolean isNullFirst() {
        return nullFirst;
    }


    public void setNullFirst(Boolean nullFirst) {
        this.nullFirst = (nullFirst != null && nullFirst);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getOrderClause() {
        return orderClause;
    }


    public void setOrderClause(String orderClause) {
        this.orderClause = orderClause;
    }


    void setRowDictionnary(RowDictionary rowDictionary) {
        this.rowDictionary = rowDictionary;
    }


    public boolean isTemporary() {
        return temporary;
    }


    public void setTemporary(Boolean temporary) {
        this.temporary = (temporary != null && temporary);
    }


    public Row getRow(int rowidx) {
        return rows.get(rowidx);
    }


    public Row getRowById(String rowId) {
        for (Row row : rows) {
            if (rowId.equals(row.getId())) {
                return row;
            }
        }
        return null;
    }


    public int getRowCount() {
        return rows.size();
    }


    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }


    public void addRow(Row arow) {
        String arowId = arow.getId();
        if (containsRow(arowId)) {
            throw new RuntimeException(computeDoubleRowIdMessage(arowId, name));
        }
        rowDictionary.registerRow(arow);
        rows.add(arow);
    }


    public static String computeDoubleRowIdMessage(String arowId, String tableName) {
        return "La table '" + tableName + "' contient déjà la ligne '" + arowId + "'.";
    }


    public boolean containsRow(String rowId) {
        if (rowDictionary.getRowById(rowId) == null) {
            return false;
        }
        for (Row row : rows) {
            if (rowId.equals(row.getId())) {
                return true;
            }
        }
        return false;
    }


    public void clear() {
        List<Row> copy = new ArrayList<Row>(rows);
        for (Row row : copy) {
            removeRow(row);
        }
    }


    public void removeField(String fieldName) {
        for (Row row : rows) {
            row.removeField(fieldName);
        }
    }


    public void removeRow(Row arow) {
        rowDictionary.unregisterRow(arow);
        rows.remove(arow);
    }


    public Iterator<Row> rows() {
        return Collections.unmodifiableList(rows).iterator();
    }


    public List<Row> getRowList() {
        return Collections.unmodifiableList(rows);
    }


    @Override
    public String toString() {
        return name;
    }
}
