/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import net.codjo.tokio.model.DatasetChecker;
import net.codjo.tokio.model.Entity;
import net.codjo.tokio.model.EntityDeclaration;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.Parameter;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.model.UniqueKey;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XMLEntitiesLoaderTest {
    private static final String ENTITIES_DIR = "src/test/resources/test/entities/";
    private XMLEntitiesLoader entitiesLoader = new XMLEntitiesLoader(new TokioConfiguration());
    private EntityList entityList = new EntityList();


    @Test
    public void test_loadEntity() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "entities.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("MyEntity");
        assertEquals("MyEntity", entity.getName());
        assertParameters(entity, "param1", "param2");
        assertEntityLoaded(entity);
    }


    @Test
    public void test_flatten_loadEntity() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "entitiesFlatten.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("MyEntity");
        assertEquals("MyEntity", entity.getName());
        assertParameters(entity, "param1", "param2");
        assertEntityLoaded(entity);
    }


    @Test
    public void test_loadEntityWithCopy() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "entitiesWithCopy.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("MyEntity");
        assertEquals("MyEntity", entity.getName());
        assertParameters(entity, "param1", "param2");
        assertEntityLoadedWithCopy(entity);
    }


    @Test
    public void test_flatten_loadEntityWithCopy() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "entitiesWithCopyFlatten.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("MyEntity");
        assertEquals("MyEntity", entity.getName());
        assertParameters(entity, "param1", "param2");
        assertEntityLoadedWithCopy(entity);
    }


    @Test
    public void test_badRefIdInEntity() throws Exception {
        try {
            final File fileName = new File(ENTITIES_DIR + "badRefInEntity.xml");
            entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                        new File(ENTITIES_DIR),
                                        fileName,
                                        entityList);
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(XMLEntitiesLoader.computeBadRefIdMessage("unknownRef"),
                         e.getMessage());
        }
    }


    @Test
    public void test_loadEntityWithGeneratedParameters()
          throws IOException, SAXException, ParserConfigurationException {
        final File fileName = new File(ENTITIES_DIR + "entityWithGeneratedParameters.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("MyEntity");
        assertEquals("MyEntity", entity.getName());

        assertEquals("generateNumeric(23,2)", entity.getDefaultValue("param1").getValue());
        assertEquals("generateString(255)", entity.getDefaultValue("param2").getValue());
        assertEquals("generateDate(null)", entity.getDefaultValue("param3").getValue());
        assertEquals(6, entity.getParameters().length);
    }


    @Test
    public void test_autoComplete() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "autoComplete.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("nominal");
        assertEquals("nominal", entity.getName());

        Table table = entity.getDataset().getTable("MY_TABLE");
        assertTrue(table.getRow(0).isAutoComplete());
        assertFalse(table.getRow(1).isAutoComplete());
        assertNull(table.getRow(2).isAutoComplete());
    }


    @Test
    public void test_autocomplete_withCopyRow() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "autoComplete_withCopyRow.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("nominal");
        assertEquals("nominal", entity.getName());

        Table table = entity.getDataset().getTable("MY_TABLE");
        assertTrue(table.getRow(0).isAutoComplete());
        assertFalse(table.getRow(1).isAutoComplete());
        assertNull(table.getRow(2).isAutoComplete());
    }


    @Test
    public void test_autocomplete_withReplaceRow() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "autoComplete_withReplaceRow.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("nominal");
        assertEquals("nominal", entity.getName());

        Table table = entity.getDataset().getTable("MY_TABLE");
        assertFalse(table.getRow(0).isAutoComplete());
        assertTrue(table.getRow(1).isAutoComplete());
    }


    @Test
    public void test_required() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "entitiesWithRequired.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("MyEntity");
        assertEquals("MyEntity", entity.getName());

        assertNotNull(entity.getDataset().getTable("TABLE1"));
        assertNotNull(entity.getRequiredDataSet().getTable("TABLE2"));
    }


    @Test
    public void test_createEntity() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "createEntity.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity father = entityList.getEntity("father");
        assertEquals(1, father.getEntityModelList().getEntities().size());

        EntityDeclaration sonEntityDeclaration = father.getEntityDeclarationList().get(0);
        Map<String, Parameter> declaredParameters = sonEntityDeclaration.getParameters();
        assertEquals(1, declaredParameters.size());
        assertEquals("newValue", declaredParameters.get("param2").getValue());

        Entity son = father.getEntityModelList().getEntity("son");
        assertNotNull(son);
        assertEquals(3, son.getParameters().length);
        assertEquals("value1", son.getParameter("param1").getValue());
        assertEquals("value2", son.getParameter("param2").getValue());
        assertNull(son.getParameter("param3"));
    }


    @Test
    public void test_createEntity_twoLevels() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "createEntity_twoLevels.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity father = entityList.getEntity("father");
        assertEquals(1, father.getEntityModelList().getEntities().size());

        EntityDeclaration sonEntityDeclaration = father.getEntityDeclarationList().get(0);
        Map<String, Parameter> sonDeclaredParameters = sonEntityDeclaration.getParameters();
        assertEquals(1, sonDeclaredParameters.size());
        assertEquals("newValue2", sonDeclaredParameters.get("param2").getValue());

        Entity son = father.getEntityModelList().getEntity("son");
        assertEquals(1, son.getEntityModelList().getEntities().size());
        assertEquals(3, son.getParameters().length);
        assertEquals("value1", son.getParameter("param1").getValue());
        assertEquals("value2", son.getParameter("param2").getValue());
        assertNull(son.getParameter("param3"));

        EntityDeclaration littleSonEntityDeclaration = son.getEntityDeclarationList().get(0);
        Map<String, Parameter> littleSonDeclaredParameters = littleSonEntityDeclaration.getParameters();
        assertEquals(1, littleSonDeclaredParameters.size());
        assertEquals("newValue5", littleSonDeclaredParameters.get("param5").getValue());

        Entity littleSon = son.getEntityModelList().getEntity("littleSon");
        assertEquals(3, littleSon.getParameters().length);
        assertEquals("value4", littleSon.getParameter("param4").getValue());
        assertEquals("value5", littleSon.getParameter("param5").getValue());
        assertNull(littleSon.getParameter("param6"));
    }


    @Test
    public void test_createEntity_inRequired() throws Exception {
        try {
            final File fileName = new File(ENTITIES_DIR + "createEntity_inRequired.xml");
            entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                        new File(ENTITIES_DIR),
                                        fileName,
                                        entityList);

            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals("Il n'est pas possible de créer des entités dans un required.\n"
                         + "create-entity(createEntity_inRequired.xml:6)",
                         e.getMessage());
        }
    }


    @Test
    public void test_requiredWithUniqueKey() throws Exception {
        final File fileName = new File(ENTITIES_DIR + "requiredWithUniqueKey.xml");
        entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                    new File(ENTITIES_DIR),
                                    fileName,
                                    entityList);

        Entity entity = entityList.getEntity("requiredWithUniqueKey");
        Table table = entity.getRequiredDataSet().getTable("AP_REQUIRED");
        assertEquals(2, table.getRowCount());

        UniqueKey uniqueKey = table.getRow(0).getUniqueKey();
        assertEquals(2, uniqueKey.getFieldCount());
        assertEquals("KEYFIELD1", uniqueKey.getField(0).getName());
        assertEquals("KEYFIELD2", uniqueKey.getField(1).getName());

        assertNull(table.getRow(1).getUniqueKey());
    }


    @Test
    public void test_requiredWithUniqueKey_missingField() throws Exception {
        try {
            final File fileName = new File(ENTITIES_DIR + "requiredWithUniqueKey_missingField.xml");
            entitiesLoader.loadEntities(XmlUtil.loadContent(fileName),
                                        new File(ENTITIES_DIR),
                                        fileName,
                                        entityList);
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals("Un champ spécifié dans une unique-key n'a pas de field associé.\n"
                         + "KEYFIELD2(requiredWithUniqueKey_missingField.xml:12)",
                         e.getMessage());
        }
    }


    private void assertParameters(Entity entity, String... values) {
        assertThat(Arrays.asList(entity.getParameters()), hasItems(values));
        assertEquals(values.length, entity.getParameters().length);
    }


    private void assertEntityLoaded(Entity entity) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='@param1@'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "  <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='@param2@'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, entity.getDataset());
    }


    private void assertEntityLoadedWithCopy(Entity entity) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='@param1@'/>";
        expected += "      <field name='FIRST_FIELD3' value='field3'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='newField1'/>";
        expected += "      <field name='FIRST_FIELD2' value='@param2@'/>";
        expected += "      <field name='FIRST_FIELD3' value='field3'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "  <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='@param2@'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, entity.getDataset());
    }
}
