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
/**
 * Description of the Class
 *
 * @author Boris
 * @version $Revision: 1.5 $
 */
public class RowDictionary {
    private List<Row> allRows = new ArrayList<Row>();
    private Map<String, Row> rowById = new HashMap<String, Row>();


    public Row getRowById(String rowId) {
        if (rowId == null) {
            return null;
        }
        return rowById.get(rowId);
    }


    public void registerRow(Row arow) {
        arow.setRowDictionnary(this);
        if (!allRows.contains(arow)) {
            allRows.add(arow);
        }
        if (arow.getId() == null) {
            return;
        }
        if (rowById.containsKey(arow.getId())) {
            throw new IllegalArgumentException("Identifiant de ligne existant " + arow.getId());
        }
        rowById.put(arow.getId(), arow);
    }


    public void unregisterRow(Row arow) {
        if (arow.getId() == null) {
            allRows.remove(arow);
            return;
        }
        for (Row current : allRows) {
            if (arow.getId().equals(current.getRefId())) {
                current.flattenWithInheritedRow();
            }
        }
        rowById.remove(arow.getId());
    }
}
