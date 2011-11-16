/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import junit.framework.TestCase;
/**
 * Description of the Class
 *
 * @author Boris
 * @version $Revision: 1.5 $
 */
public class RowDictionaryTest extends TestCase {
    RowDictionary dico;


    public void test_getRowById() {
        Row arow = new Row("a", null, null);
        dico.registerRow(arow);
        assertEquals(arow, dico.getRowById("a"));
    }


    public void test_getRowById_Unknown() {
        Row rowRet = dico.getRowById("unknown");
        assertNull(rowRet);
    }


    public void test_getRowById_null() {
        Row rowRet = dico.getRowById(null);
        assertNull(rowRet);
    }


    public void test_unregister() {
        // Construction de Ligne A
        FieldMap fields = new FieldMap();
        fields.putField("COL_A", "a", null);
        fields.putField("COL_B", "b", null);
        Row rowA = new Row("a", null, fields);

        // Construction de Ligne B (herite de A)
        fields.clear();
        fields.putField("COL_A", "1", null);
        fields.putField("COL_C", "c", null);
        Row rowB = new Row(null, "a", fields);

        // Construction du dictionnaire
        RowDictionary newDico = new RowDictionary();
        newDico.registerRow(rowA);
        newDico.registerRow(rowB);

        // Supprime rowA
        newDico.unregisterRow(rowA);

        assertNull("rowA n'est plus reference dans le dico", newDico.getRowById("a"));

        // Verification des lignes de B
        assertNull("Heritage est cassé", rowB.getRefId());
        FieldMap rowBFields = rowB.getLocalDefinedFields();
        assertEquals("1", rowBFields.get("COL_A").getValue());
        assertEquals("b", rowBFields.get("COL_B").getValue());
        assertEquals("c", rowBFields.get("COL_C").getValue());
    }


    @Override
    protected void setUp() {
        dico = new RowDictionary();
    }
}
