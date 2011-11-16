/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.Iterator;
import junit.framework.TestCase;
/**
 */
public class DataSetTest extends TestCase {
    private DataSet dataset;
    private Row rowA;


    public void test_addRow() {
        dataset.addRow("TableA", rowA);

        Iterator iterator = dataset.tables();
        assertTrue(iterator.hasNext());
        Table tableA = (Table)iterator.next();
        assertEquals("TableA", tableA.getName());
        assertFalse(iterator.hasNext());

        iterator = tableA.rows();
        assertTrue(iterator.hasNext());
        Row row = (Row)iterator.next();
        assertEquals(rowA, row);
        assertFalse(iterator.hasNext());
    }


    public void test_getTable() {
        dataset.addRow("TableA", rowA);

        assertNotNull(dataset.getTable("TableA"));
        assertEquals("TableA", dataset.getTable("TableA").getName());
        assertNull(dataset.getTable("unknown"));
    }


    public void test_getTable_ignoreCase() {
        dataset.addRow("TableA", rowA);

        assertNotNull(dataset.getTable("tablea"));
        assertEquals("TableA", dataset.getTable("tablea").getName());
    }


    @Override
    protected void setUp() {
        dataset = new DataSet();
        rowA = new Row(new FieldMap());
    }
}
