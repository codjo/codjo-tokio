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
 */
public class DataSet {
    private RowDictionary rowDictionary = new RowDictionary();
    private List<Table> tables = new ArrayList<Table>();
    private ComparatorManager comparators = new ComparatorManager();


    public DataSet() {
    }


    public Table getTable(String tableName) {
        for (Table item : tables) {
            if (tableName.equalsIgnoreCase(item.getName())) {
                return item;
            }
        }
        return null;
    }


    public int getComparatorsNumber() {
        return comparators.size();
    }


    public Comparator getComparator(String field) {
        return comparators.getComparator(field);
    }


    public Row getRow(String rowId) {
        return rowDictionary.getRowById(rowId);
    }


    public void addRow(String tableName, Row row) {
        buildTable(tableName).addRow(row);
    }


    public void addComparator(String field, Comparator comparator) {
        comparators.addComparator(field, comparator);
    }


    public Table buildTable(String tableName) {
        Table table = getTable(tableName);
        if (table == null) {
            table = new Table(tableName, rowDictionary);
            tables.add(table);
        }
        return table;
    }


    public void clear() {
        for (Iterator i = tables.iterator(); i.hasNext();) {
            Table table = (Table)i.next();
            table.clear();
            i.remove();
        }
    }


    public Iterator<Table> tables() {
        return Collections.unmodifiableCollection(tables).iterator();
    }


    public int getTableCount() {
        return tables.size();
    }


    public Iterator comparators() {
        return comparators.iterator();
    }


    void setRowDictionnary(RowDictionary rowDictionary) {
        this.rowDictionary = rowDictionary;
    }
}
