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
 * @version $Revision: 1.8 $
 */
public class ScenarioTest extends TestCase {
    private static final String NAME = "name";
    private static final String COMMENT = "commentaire de scenario";
    Row rowA;
    Scenario scenario;


    @Override
    protected void setUp() {
        scenario = new Scenario(NAME, COMMENT);
        rowA = new Row("rowA", null, new FieldMap());
    }


    @Override
    protected void tearDown() {
    }


    public void testGetter() {
        assertEquals(NAME, scenario.getName());
        assertEquals(COMMENT, scenario.getComment());
    }


    public void test_inherit() {
        FieldMap fields = new FieldMap();

        // Ligne A
        fields.putField("name", "a", null);
        fields.putField("kind", "rowA", null);
        rowA = new Row("rowA", null, fields);

        // Ligne B qui herite de A
        fields.clear();
        fields.putField("name", "b", null);
        Row rowInheritFromA = new Row("rowB", "rowA", fields);

        // Positionne les lignes dans le scenario
        scenario.addInputRow("TABLE", rowA);
        scenario.addOutputRow("ETALON", rowInheritFromA);

        // Test ligne A
        Table table = scenario.getInputTable("TABLE");
        Row row1 = table.rows().next();
        assertEquals(2, row1.getFields().size());
        assertEquals("a", row1.getFields().get("name").getValue());
        assertEquals("rowA", row1.getFields().get("kind").getValue());

        // Test ligne B
        Table etalon = scenario.getOutputTable("ETALON");
        Row row2 = etalon.rows().next();

        FieldMap bFields = row2.getFields();
        assertEquals(2, bFields.size());
        assertEquals("b", bFields.get("name").getValue());
        assertEquals("rowA", bFields.get("kind").getValue());
    }


    public void test_ouptut() {
        scenario.addOutputRow("TableA", rowA);

        Iterator iterator = scenario.outputTables();
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


    public void test_inputs() {
        scenario.addInputRow("TableA", rowA);

        Iterator iterator = scenario.inputTables();
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
}
