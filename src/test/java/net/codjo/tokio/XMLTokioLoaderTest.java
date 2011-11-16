/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.LoggerLocationVisitor;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.Table;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class XMLTokioLoaderTest extends XMLLoaderTestCase {
    private Map<String, String> properties;
    private StringBuilder stringBuilder = new StringBuilder();


    public void test_replaceAntVariables() throws Exception {
        Scenario scSrc = loader("XMLTokioLoaderTest.tokio", "replaceAntVariablesSrc");
        Scenario scRef = loader("XMLTokioLoaderTest.tokio", "replaceAntVariablesRef");

        assertEquals(scSrc.getComment(), scRef.getComment());
        assertEquals(scSrc.getInputTable("TABLE").getRow(0).getFields().get("FIELD").getValue(),
                     scRef.getInputTable("TABLE").getRow(0).getFields().get("FIELD").getValue());
        assertEquals(scSrc.getOutputTable("TABLE").getRow(0).getFields().get("FIELD").getValue(),
                     scRef.getOutputTable("TABLE").getRow(0).getFields().get("FIELD").getValue());
    }


    public void test_replaceAntVariablesWithInclude() throws Exception {
        Scenario scSrc = loader("XMLTokioLoaderWithIncludeTest.tokio", "nominal");
        Scenario scRef = loader("XMLTokioLoaderWithIncludeTest.tokio", "etalon");

        assertEquals(scRef.getInputTable("TABLE").getRow(0).getFields().get("FIELD").getValue(),
                     scSrc.getInputTable("TABLE").getRow(0).getFields().get("FIELD").getValue());
    }


    public void test_replaceGroup() throws Exception {
        Scenario scenario = loader("xmlTokioLoaderGroupTest.tokio", "xmlTokioLoaderGroupTest");

        assertEquals(3, scenario.getInputDataSet().getTableCount());
        assertNotNull(scenario.getInputDataSet().getTable("AP_TEST_DANS_GROUP"));
        assertNotNull(scenario.getInputDataSet().getTable("AP_FROM_ENTITY_GROUP"));
        assertEquals(1, scenario.getInputDataSet().getTable("AP_TEST_DANS_GROUP").getRowCount());
    }


    public void test_required_withInheritedCase() throws Exception {
        Scenario scenario = loader("required_withInheritedCase.tokio", "wife");

        assertEquals(2, scenario.getInputDataSet().getTableCount());

        Table table = scenario.getInputTable("AP_COUSIN");
        assertEquals(1, table.getRowCount());
    }


    public void test_location_requiredWithInheritedCase() throws Exception {
        Scenario scenario = loader("required_withInheritedCase.tokio", "wife");

        scenario.getInputTable("AP_COUSIN")
              .getRow(0).getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("row(required_withInheritedCase.tokio:15)\n"
                     + "case(required_withInheritedCase.tokio:12)\n"
                     + "\tcreate-entity(required_withInheritedCase.tokio:8)\n"
                     + "\t\trow-required(required_withInheritedCase.entities:8)\n", stringBuilder.toString());
    }


    public void test_location_requiredEntities() throws Exception {
        Table table = loader("required_withEntities.tokio", "nominal").getInputTable("AP_COUSIN");
        assertEquals(1, table.getRowCount());

        table.getRow(0).getLocationPointer().accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("create-entity(required_withEntities.tokio:8)\n"
                     + "\trow(required_withEntities.entities:7)\n"
                     + "\trow-required(required_withEntities.entities:15)\n",
                     stringBuilder.toString());

        table = loader("required_withEntities.tokio", "copy-entity").getInputTable("AP_COUSIN");
        assertEquals(2, table.getRowCount());

        stringBuilder = new StringBuilder();
        table.getRow(1).getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy-entity(required_withEntities.tokio:14)\n"
                     + "\tcreate-entity(required_withEntities.tokio:8)\n"
                     + "\t\trow(required_withEntities.entities:7)\n"
                     + "\t\trow-required(required_withEntities.entities:15)\n"
                     + "case(required_withEntities.tokio:12)\n"
                     + "\tcreate-entity(required_withEntities.tokio:8)\n"
                     + "\t\trow-required(required_withEntities.entities:15)\n",
                     stringBuilder.toString());
    }


    public void test_location_requiredWithComposedEntities() throws Exception {
        Scenario scenario = loader("required_withComposedEntities.tokio", "wife");

        scenario.getInputTable("AP_COUSIN").getRow(0).getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("row(required_withComposedEntities.tokio:15)\n"
                     + "case(required_withComposedEntities.tokio:12)\n"
                     + "\tcreate-entity(required_withComposedEntities.tokio:8)\n"
                     + "\t\tcreate-entity(required_withComposedEntities_parent.entities:8)\n"
                     + "\t\t\trow-required(required_withComposedEntities_child.entities:8)\n"
                     + "\t\trow-required(required_withComposedEntities_parent.entities:12)\n",
                     stringBuilder.toString());
    }


    @Override
    protected void setUp() throws Exception {
        properties = new HashMap<String, String>();
        properties.put("$key1$", "value1");
        properties.put("$key2$", "value2");
        properties.put("$key3$", "value3");
        properties.put("$key4$", "value4");
    }


    private Scenario loader(String resourceName, String scenarioName) throws Exception {
        return load(getClass().getResource(resourceName).getPath(), scenarioName);
    }


    private Scenario load(String fileName, String scenarioName) throws Exception {
        XMLTokioLoader loader = new XMLTokioLoader(new File(fileName), properties);
        return loader.getScenario(scenarioName);
    }
}
