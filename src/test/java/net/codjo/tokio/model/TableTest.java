/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.Iterator;
import junit.framework.TestCase;
/**
 * Classe de test.
 *
 * @author Boris
 * @version $Revision: 1.9 $
 */
public class TableTest extends TestCase {
    public void testGetter() {
        Table table = new Table("name", new RowDictionary());
        assertEquals("name", table.getName());
        assertFalse(table.rows().hasNext());
    }


    public void test_rows() {
        Table table = new Table("name", new RowDictionary());
        Row rowA = new Row(new FieldMap());
        Row rowB = new Row(new FieldMap());
        table.addRow(rowA);
        table.addRow(rowB);

        Iterator iter = table.rows();
        assertTrue(iter.hasNext());
        assertEquals(rowA, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(rowB, iter.next());
        assertFalse(iter.hasNext());
    }
}
