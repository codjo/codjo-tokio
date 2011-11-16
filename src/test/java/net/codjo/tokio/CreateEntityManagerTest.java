package net.codjo.tokio;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Entity;
import net.codjo.tokio.model.EntityDeclaration;
import net.codjo.tokio.model.EntityDictionary;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.ObjectValue;
import net.codjo.tokio.model.Parameter;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.model.UniqueKey;
import static net.codjo.tokio.util.RowUtil.field;
import static net.codjo.tokio.util.RowUtil.row;
import static net.codjo.tokio.util.RowUtil.uniqueKey;
import net.codjo.tokio.util.XmlUtil;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class CreateEntityManagerTest {
    private CreateEntityManager createEntityManager = new CreateEntityManager();
    private EntityDictionary entityDictionary = new EntityDictionary();
    private EntityDictionary requiredEntityDictionary = new EntityDictionary();
    private DataSet dataset = new DataSet();
    private DataSet requiredDataset = new DataSet();


    @Test
    public void test_minimal() throws Exception {
        String xmlContent = "<create-entity id='createEntity' name='minimal'/>";

        EntityList entityList = new EntityList();
        Entity entity = new Entity("minimal");
        entityList.addEntity(entity);
        DataSet entityDataset = entity.getDataset();
        entityDataset.addRow("AP_TABLE", row(field("COL1", "value1")));

        createEntityManager.process(dataset,
                                    requiredDataset,
                                    nodeFrom(xmlContent),
                                    entityList,
                                    entityDictionary,
                                    requiredEntityDictionary);

        assertEquals(1, dataset.getTableCount());
        Table table = dataset.getTable("AP_TABLE");

        assertEquals(1, table.getRowCount());
        Row row = table.getRow(0);

        assertEquals(1, row.getFieldCount());
        assertFieldEquals(row, "COL1", "value1");
    }


    @Test
    public void test_noId() throws Exception {
        String xmlContent = "<create-entity name='minimal'/>";

        EntityList entityList = new EntityList();
        Entity entity = new Entity("minimal");
        entityList.addEntity(entity);
        DataSet entityDataset = entity.getDataset();
        Row row1 = row(field("COL1", "value1"));
        row1.setId("myID");
        entityDataset.addRow("AP_TABLE", row1);

        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom(xmlContent),
                                    entityList,
                                    entityDictionary,
                                    requiredEntityDictionary);

        assertEquals(1, dataset.getTableCount());
        Table table = dataset.getTable("AP_TABLE");

        assertEquals(1, table.getRowCount());
        Row row = table.getRow(0);

        assertEquals(1, row.getFieldCount());
        assertFieldEquals(row, "COL1", "value1");
    }


    @Test
    public void test_process_entityContainingEntity() throws Exception {
        String xmlContent = "<create-entity name='father' id='john'>"
                            + "    <parameter name='firstName' value='John'/>"
                            + "</create-entity>";

        Entity sonModel = new Entity("son");
        sonModel.addParameter("age");
        sonModel.addParameter("fatherFirstName");
        sonModel.addParameter("nationality");
        sonModel.getDataset().addRow("AP_TABLE", row("sonRowId",
                                                     field("ID", "yes"),
                                                     field("AGE", "@age@"),
                                                     field("FATHER_FIRST_NAME", "@fatherFirstName@"),
                                                     field("NATIONALITY", "@nationality@")));

        Entity fatherModel = new Entity("father");
        fatherModel.addParameter("firstName");
        fatherModel.addParameter("nationality",
                                 new GeneratorConfiguration(GeneratorConfiguration.GENERATE_STRING, "3"));
        fatherModel.getEntityModelList().addEntity(sonModel);
        fatherModel.getDataset().addRow("AP_TABLE", row("fatherRowId",
                                                        field("FIRST_NAME", "@firstName@"),
                                                        field("NATIONALITY", "@nationality@")));

        EntityDeclaration sonDeclaration = new EntityDeclaration("son");
        sonDeclaration.setId("kevin");
        sonDeclaration.getParameters().put("age", new Parameter("12"));
        sonDeclaration.getParameters().put("fatherFirstName", new Parameter("@firstName@"));
        sonDeclaration.getParameters().put("nationality", new Parameter("@nationality@"));
        fatherModel.getEntityDeclarationList().add(sonDeclaration);

        EntityList entityModel = new EntityList();
        entityModel.addEntity(fatherModel);

        createEntityManager.process(dataset,
                                    requiredDataset,
                                    nodeFrom(xmlContent),
                                    entityModel,
                                    entityDictionary,
                                    requiredEntityDictionary);

        assertEquals(1, dataset.getTableCount());
        Table table = dataset.getTable("AP_TABLE");
        assertNotNull(table);
        assertEquals(2, table.getRowCount());

        ObjectValue fatherNationalityExpected = fatherModel.getParameter("nationality")
              .getValueObjectList().iterator().next();

        assertEquals("john.kevin.sonRowId", table.getRow(0).getId());
        Row row1 = table.getRow(0);
        assertFieldEquals(row1, "ID", "yes");
        assertFieldEquals(row1, "AGE", "12");
        assertFieldEquals(row1, "FATHER_FIRST_NAME", "John");
        assertSame(fatherNationalityExpected,
                   row1.getFields().get("NATIONALITY").getValueObjectList().iterator().next());

        assertEquals("john.fatherRowId", table.getRow(1).getId());
        Row row2 = table.getRow(1);
        assertFieldEquals(row2, "FIRST_NAME", "John");
        assertSame(fatherNationalityExpected,
                   row2.getFields().get("NATIONALITY").getValueObjectList().iterator().next());

        assertNotNull(dataset.getRow("john.kevin.sonRowId"));
    }


    @Test
    public void test_process_entityContainingEntity_twoLevels() throws Exception {
        Entity littleSon = new Entity("littleSon");
        littleSon.getDataset().addRow("AP_TABLE", row("rowLittleSon", field("NAME", "Little Kevin")));

        Entity son = new Entity("son");
        son.getEntityModelList().addEntity(littleSon);
        son.getEntityDeclarationList().add(new EntityDeclaration("littleSon", "littleKevin"));
        son.getDataset().addRow("AP_TABLE", row("rowSon", field("NAME", "Kevin")));
        son.getRequiredDataSet().addRow("AP_TABLE", row("", field("AGE", "12")));

        Entity father = new Entity("father");
        father.getEntityModelList().addEntity(son);
        father.getEntityDeclarationList().add(new EntityDeclaration("son", "kevin"));
        father.getDataset().addRow("AP_TABLE", row("rowFather", field("NAME", "John")));
        father.getRequiredDataSet().addRow("AP_TABLE", row("", field("AGE", "42")));

        EntityList entityModel = new EntityList();
        entityModel.addEntity(father);
        String xmlContent = "<create-entity name='father' id='john'/>";
        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom(xmlContent),
                                    entityModel,
                                    entityDictionary,
                                    requiredEntityDictionary);

        assertEquals(1, dataset.getTableCount());
        Table table = dataset.getTable("AP_TABLE");
        assertNotNull(table);
        assertEquals(3, table.getRowCount());

        assertEquals("john.kevin.littleKevin.rowLittleSon", table.getRow(0).getId());
        assertFieldEquals(table.getRow(0), "NAME", "Little Kevin");

        assertEquals("john.kevin.rowSon", table.getRow(1).getId());
        assertFieldEquals(table.getRow(1), "NAME", "Kevin");

        assertEquals("john.rowFather", table.getRow(2).getId());
        assertFieldEquals(table.getRow(2), "NAME", "John");

        Table tableInRequired = requiredDataset.getTable("AP_TABLE");
        assertEquals(2, tableInRequired.getRowCount());
        assertFieldEquals(tableInRequired.getRow(0), "AGE", "12");
        assertFieldEquals(tableInRequired.getRow(1), "AGE", "42");
    }


    @Test
    public void test_process_unknownParameter_twoLevels() throws Exception {
        Entity son = new Entity("son");
        son.getEntityDeclarationList().add(new EntityDeclaration("littleSon", "littleKevin"));

        Entity father = new Entity("father");
        father.getEntityModelList().addEntity(son);
        EntityDeclaration sonDeclaration = new EntityDeclaration("son", "kevin");
        sonDeclaration.getParameters().put("age", new Parameter(Entity.NULL_VALUE));
        father.getEntityDeclarationList().add(sonDeclaration);

        EntityList entityModel = new EntityList();
        entityModel.addEntity(father);
        String xmlContent = "<create-entity name='father' id='john'/>";
        try {
            createEntityManager.process(dataset,
                                        requiredDataset, nodeFrom(xmlContent),
                                        entityModel,
                                        entityDictionary,
                                        requiredEntityDictionary);
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals("Le paramètre age n'est pas défini dans l'entité son", e.getMessage());
        }
    }


    @Test
    public void test_replaceEntityDefaultParameters() throws Exception {
        Entity father = new Entity("father");
        father.addParameter("lastName", new Parameter(new GeneratorConfiguration("generateString", "10")));
        father.addParameter("name", new Parameter("John @lastName@"));
        father.getDataset().addRow("AP_TABLE", row("row1", field("NAME", "@name@")));

        EntityList entityList = new EntityList();
        entityList.addEntity(father);
        String xmlContent = "<create-entity name='father' id='john'/>";
        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom(xmlContent),
                                    entityList,
                                    entityDictionary,
                                    requiredEntityDictionary);

        Field field = dataset.getRow("john.row1").getFields().get("NAME");
        assertSame(getObjectValue(father.getParameter("lastName"), 0), getObjectValue(field, 1));
        assertEquals("John generateString(10)", field.getValue());
    }


    @Test
    public void test_replaceEntityDefaultParameters_concatenation() throws Exception {
        Entity father = new Entity("father");
        father.addParameter("firstName", new Parameter(new GeneratorConfiguration("generateString", "10")));
        father.addParameter("lastName", new Parameter(new GeneratorConfiguration("generateString", "10")));
        father.addParameter("name", new Parameter("@firstName@ @lastName@"));
        father.getDataset().addRow("AP_TABLE", row("row1", field("NAME", "@name@")));

        EntityList entityList = new EntityList();
        entityList.addEntity(father);
        String xmlContent = "<create-entity name='father' id='john'/>";
        createEntityManager.process(dataset,
                                    requiredDataset,
                                    nodeFrom(xmlContent),
                                    entityList,
                                    entityDictionary,
                                    requiredEntityDictionary);
        Field field = dataset.getRow("john.row1").getFields().get("NAME");
        assertSame(getObjectValue(father.getParameter("firstName"), 0), getObjectValue(field, 0));
        assertSame(getObjectValue(father.getParameter("lastName"), 0), getObjectValue(field, 2));
        assertEquals("generateString(10) generateString(10)", field.getValue());
    }


    @Test
    public void test_process_replaceWithParentParameters() throws Exception {
        Entity son = new Entity("son");
        son.addParameter("name");
        son.addParameter("adress");
        son.getDataset().addRow("AP_TABLE", row("row",
                                                field("NAME", "@name@"),
                                                field("ADRESS", "@adress@")));

        Entity father = new Entity("father");
        father.addParameter("lastName", new Parameter("Smith"));
        father.addParameter("adress", new Parameter(new GeneratorConfiguration("adressGenerator")));
        father.getEntityModelList().addEntity(son);
        EntityDeclaration sonEntityDeclaration = new EntityDeclaration("son", "kevin");
        sonEntityDeclaration.getParameters().put("name", new Parameter("Kevin @lastName@"));
        sonEntityDeclaration.getParameters().put("adress", new Parameter("Kevin, @adress@"));
        father.getEntityDeclarationList().add(sonEntityDeclaration);

        EntityList entityModel = new EntityList();
        entityModel.addEntity(father);
        String xmlContent = "<create-entity name='father' id='john'/>";
        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom(xmlContent),
                                    entityModel,
                                    entityDictionary, requiredEntityDictionary);

        Row row = dataset.getRow("john.kevin.row");
        FieldMap fieldMap = row.getFields();

        Iterator<ObjectValue> nameIterator = fieldMap.get("NAME").getValueObjectList().iterator();
        assertFieldEquals(row, "NAME", "Kevin Smith");
        assertEquals("Kevin ", nameIterator.next().getValue());
        assertEquals("Smith", nameIterator.next().getValue());
        assertFalse(nameIterator.hasNext());

        Iterator<ObjectValue> adressIterator = fieldMap.get("ADRESS").getValueObjectList().iterator();
        assertEquals("Kevin, ", adressIterator.next().getValue());
        assertSame(father.getParameter("adress").getValueObjectList().iterator().next(),
                   adressIterator.next());
        assertFalse(adressIterator.hasNext());
    }


    @Test
    public void test_createEntityWithRequired() throws Exception {
        dataset.addRow("AP_TABLE", row(field("LAST_NAME", "Smith")));
        EntityList entities = new EntityList();

        Entity first = new Entity("one");
        first.addParameter("toto");
        first.getDataset().addRow("AP_TABLE", row(field("LAST_NAME", "Weston")));
        first.getRequiredDataSet().addRow("AP_TABLE", row(field("LAST_NAME", "@toto@")));
        entities.addEntity(first);

        Entity second = new Entity("two");
        second.getDataset().addRow("AP_TABLE", row(field("LAST_NAME", "Smith")));
        second.getRequiredDataSet().addRow("AP_TABLE", row(field("LAST_NAME", "Weston")));
        entities.addEntity(second);

        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom("<create-entity name='one' id='john'>"
                                                              + "   <parameter name='toto' value='maVariable'/>"
                                                              + "</create-entity>"),
                                    entities, entityDictionary, requiredEntityDictionary);
        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom("<create-entity name='two' id='johnjohn'/>"),
                                    entities, entityDictionary, requiredEntityDictionary);

        assertEquals(3, dataset.getTable("AP_TABLE").getRowCount());
        assertEquals(2, requiredDataset.getTable("AP_TABLE").getRowCount());
        assertFieldEquals(requiredDataset.getTable("AP_TABLE").getRow(0),
                          "LAST_NAME",
                          "maVariable");
    }


    @Test
    public void test_createEntityWithRequired_rowWithUniqueKey() throws Exception {
        EntityList entities = new EntityList();

        Entity entity = new Entity("one");
        Row rowToCopy = row(uniqueKey("FIRST_NAME", "LAST_NAME"),
                            field("FIRST_NAME", "bill"),
                            field("LAST_NAME", "compton"),
                            field("GENDER", "Vampire"));
        rowToCopy.setAutoComplete(true);
        entity.getRequiredDataSet().addRow("AP_TABLE", rowToCopy);
        entities.addEntity(entity);

        createEntityManager.process(dataset,
                                    requiredDataset,
                                    nodeFrom("<create-entity name='one' id='john'/>"),
                                    entities, entityDictionary, requiredEntityDictionary);

        Table table = requiredDataset.getTable("AP_TABLE");
        assertEquals(1, table.getRowCount());
        Row newRow = table.getRow(0);

        assertEquals(3, newRow.getFieldCount());
        assertFieldsEqualsNotSame(rowToCopy, newRow, "FIRST_NAME");
        assertFieldsEqualsNotSame(rowToCopy, newRow, "LAST_NAME");
        assertFieldsEqualsNotSame(rowToCopy, newRow, "GENDER");
        UniqueKey uniqueKey = newRow.getUniqueKey();
        assertNotNull(uniqueKey);
        assertEquals(2, uniqueKey.getFieldCount());
        assertEquals(newRow.getFields().get("FIRST_NAME"), uniqueKey.getField(0));
        assertEquals(newRow.getFields().get("LAST_NAME"), uniqueKey.getField(1));
        assertEquals(rowToCopy.isAutoComplete(), newRow.isAutoComplete());
        assertEquals(rowToCopy.getComment(), newRow.getComment());
        assertEquals(rowToCopy.getId(), newRow.getId());
        assertEquals(rowToCopy.getRefId(), newRow.getRefId());
    }


    private void assertFieldsEqualsNotSame(Row rowToCopy, Row newRow, String oldFieldName) {
        Assert.assertNotSame(rowToCopy.getFields().get(oldFieldName), newRow.getFields().get(oldFieldName));
        Assert.assertEquals(rowToCopy.getFields().get(oldFieldName).getValue(),
                            newRow.getFields().get(oldFieldName).getValue());
    }


    @Test
    public void test_createEntity_twoEntitiesCreatingEntity() throws Exception {
        Entity child = new Entity("child");
        child.addParameter("firstName");
        child.getDataset().addRow("AP_TABLE", row("row", field("FIRST_NAME", "@firstName@")));

        Entity parent = new Entity("parent");
        parent.addParameter("firstName");
        parent.getEntityModelList().addEntity(child);
        EntityDeclaration childEntityDeclaration = new EntityDeclaration("child", "child");
        childEntityDeclaration.addParameter("firstName", new Parameter("@firstName@ Junior"));
        parent.getEntityDeclarationList().add(childEntityDeclaration);

        EntityList entityList = new EntityList();
        entityList.addEntity(parent);
        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom("<create-entity name='parent' id='john'>"
                                                              + "   <parameter name='firstName' value='John'/>"
                                                              + "</create-entity>"),
                                    entityList, entityDictionary, requiredEntityDictionary);
        createEntityManager.process(dataset,
                                    requiredDataset, nodeFrom("<create-entity name='parent' id='sarah'>"
                                                              + "   <parameter name='firstName' value='Sarah'/>"
                                                              + "</create-entity>"),
                                    entityList, entityDictionary, requiredEntityDictionary);

        assertEquals(2, dataset.getTable("AP_TABLE").getRowCount());
        assertFieldEquals(dataset.getRow("john.child.row"), "FIRST_NAME", "John Junior");
        assertFieldEquals(dataset.getRow("sarah.child.row"), "FIRST_NAME", "Sarah Junior");
    }


    private void assertFieldEquals(Row row, String fieldName, String expected) {
        FieldMap row1 = row.getFields();
        assertEquals(expected, row1.get(fieldName).getValue());
    }


    private ObjectValue getObjectValue(Parameter parameter, int index) {
        ObjectValue objectValue = null;
        Iterator<ObjectValue> iterator = parameter.getValueObjectList().iterator();
        for (int i = 0; i <= index; i++) {
            objectValue = iterator.next();
        }
        return objectValue;
    }


    private ObjectValue getObjectValue(Field field, int index) {
        ObjectValue objectValue = null;
        Iterator<ObjectValue> iterator = field.getValueObjectList().iterator();
        for (int i = 0; i <= index; i++) {
            objectValue = iterator.next();
        }
        return objectValue;
    }


    private static Node nodeFrom(String xmlContent)
          throws IOException, ParserConfigurationException, SAXException {
        Document document = XmlUtil.parse(null, xmlContent);
        return document.getFirstChild();
    }
}
