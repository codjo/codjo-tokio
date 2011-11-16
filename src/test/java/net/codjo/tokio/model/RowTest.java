/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import static net.codjo.tokio.util.RowUtil.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import org.junit.Test;
/**
 * Test de Row.
 *
 * @author Boris
 * @version $Revision: 1.9 $
 */
public class RowTest {
    @Test
    public void testConstructor() {
        Row row = new Row((FieldMap)null);

        assertEquals(null, row.getId());
        assertEquals(null, row.getRefId());
        assertEquals(null, row.getFields());
    }


    @Test
    public void test_constructor_full() {
        Row row = new Row("id", "otherId", null);

        assertEquals("id", row.getId());
        assertEquals("otherId", row.getRefId());
        assertEquals(null, row.getFields());
    }


    @Test
    public void test_flattenWithInheritedRow() {
        // Construction de Ligne A
        FieldMap fields = new FieldMap();
        fields.put(new Field("COL_A", "a"));
        fields.put(new Field("COL_B", "b"));
        Row rowA = new Row("a", null, fields);

        // Construction de Ligne B (herite de A)
        fields.clear();
        fields.put(new Field("COL_A", "1"));
        fields.put(new Field("COL_C", "c"));
        Row rowB = new Row("b", "a", fields);

        // Construction du dictionnaire
        RowDictionary dico = new RowDictionary();
        dico.registerRow(rowA);
        dico.registerRow(rowB);

        // Verification des lignes de B
        rowB.flattenWithInheritedRow();
        assertNull("Heritage est cassé", rowB.getRefId());
        FieldMap rowBFields = rowB.getLocalDefinedFields();
        assertEquals("1", rowBFields.get("COL_A").getValue());
        assertEquals("b", rowBFields.get("COL_B").getValue());
        assertEquals("c", rowBFields.get("COL_C").getValue());
    }


    @Test
    public void test_getFields() {
        FieldMap fields = new FieldMap();
        fields.put(new Field("COL_A", "a"));
        Row row = new Row(null, null, fields);
        fields.clear();

        FieldMap mapRet = row.getFields();
        assertEquals(1, mapRet.size());
        assertEquals("a", mapRet.get("COL_A").getValue());
    }


    /**
     * Test que l'heritage entre ligne marche.
     */
    @Test
    public void test_inheritance() {
        // Construction de Ligne A
        FieldMap fields = new FieldMap();
        fields.put(new Field("COL_A", "a"));
        fields.put(new Field("COL_B", "b"));
        Row rowA = new Row("a", null, fields);

        // Construction de Ligne B (herite de A)
        fields.clear();
        fields.put(new Field("COL_A", "1"));
        fields.put(new Field("COL_C", "c"));
        Row rowB = new Row("b", "a", fields);

        // Construction du dictionnaire
        RowDictionary dico = new RowDictionary();
        dico.registerRow(rowA);
        dico.registerRow(rowB);

        // Verification des lignes de B
        FieldMap rowAFields = rowA.getFields();
        assertEquals("a", rowAFields.get("COL_A").getValue());
        assertEquals("b", rowAFields.get("COL_B").getValue());

        // Verification des lignes de B
        FieldMap rowBFields = rowB.getFields();
        assertEquals("1", rowBFields.get("COL_A").getValue());
        assertEquals("b", rowBFields.get("COL_B").getValue());
        assertEquals("c", rowBFields.get("COL_C").getValue());
    }


    @Test
    public void test_noUniqueKey() throws Exception {
        Row row = new Row(new FieldMap());

        assertNull(row.getUniqueKey());
    }


    @Test
    public void test_addUniqueKey() throws Exception {
        FieldMap fields = new FieldMap();
        fields.put(field("FIRSTNAME", "tintin"));
        fields.put(field("LASTNAME", "HERGE"));
        Row row = new Row(fields);

        row.addUniqueKey("LASTNAME");
        UniqueKey uniqueKey = row.getUniqueKey();
        assertEquals(1, uniqueKey.getFieldCount());
        assertSame(fields.get("LASTNAME"), uniqueKey.getField(0));
    }


    @Test
    public void test_addUniqueKey_missingField() throws Exception {
        Row row = new Row(new FieldMap());

        try {
            row.addUniqueKey("LASTNAME");
            fail();
        }
        catch (Exception e) {
            assertEquals("La valeur du field 'LASTNAME' doit être spécifiée dans la row.", e.getMessage());
        }
    }
}
