/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.DatasetChecker;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import net.codjo.tokio.model.Table;
import java.io.File;
import java.util.Properties;
/**
 *
 */
public class XMLStoriesLoaderTest extends XMLStoriesLoaderTestCase {

    public void test_referentialStandard() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "referentialStandard.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("Referential");

        assertEquals(0, scenario.getOutputDataSet().getTableCount());

        assertReferential(scenario);
    }


    public void test_flatten_referentialStandard() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "referentialStandardFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("Referential");

        assertEquals(0, scenario.getOutputDataSet().getTableCount());

        assertReferential(scenario);
    }


    public void test_properties() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "includeProperties.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        Scenario scenario = scenarii.getScenario("Referential");

        Properties etalon = new Properties();
        etalon.setProperty("user.name", "toto");
        etalon.setProperty("tralala", "youpi");
        assertEquals(etalon, scenario.getProperties());
    }


    public void test_flatten_properties() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includePropertiesFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        Scenario scenario = scenarii.getScenario("Referential");

        Properties etalon = new Properties();
        etalon.setProperty("user.name", "toto");
        etalon.setProperty("tralala", "youpi");
        assertEquals(etalon, scenario.getProperties());
    }


    public void test_include() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "useCaseWithInclude.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCase");

        assertInclude(scenario);
    }


    public void test_flatten_include() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "useCaseWithIncludeFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCase");

        assertInclude(scenario);
    }


    public void test_includeWithCopy() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "includeWithCopy.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseCopy");

        assertIncludeWithCopy(scenario);
    }


    public void test_flatten_includeWithCopy() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeWithCopyFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseCopy");

        assertIncludeWithCopy(scenario);
    }


    public void test_replace() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "includeWithReplace.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseReplace");

        assertReplace(scenario);
    }


    public void test_flatten_replace() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeWithReplaceFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseReplace");

        assertReplace(scenario);
    }


    public void test_replaceWithBadRowId() throws Exception {
        try {
            load(new File(STORIES_DIR + "replaceWithBadRowId.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(XMLDatasetUtil.computeBadRowIdMessage("badId", "MY_TABLE"), e.getMessage());
        }
    }


    public void test_flatten_replaceWithBadRowId() throws Exception {
        try {
            load(new File(STORIES_DIR + "replaceWithBadRowIdFlatten.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(XMLDatasetUtil.computeBadRowIdMessage("badId", "MY_TABLE"), e.getMessage());
        }
    }


    public void test_replaceWithBadFieldName() throws Exception {
        try {
            load(new File(STORIES_DIR + "replaceWithBadFieldName.tokio").toURL().toString());
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals("Le champ 'BAD_FIELD_NAME' associé à la ligne 'refRow' n'existe pas.\n"
                         + "field(replaceWithBadFieldName.tokio:9)",
                         e.getMessage());
        }
    }


    public void test_flatten_replaceWithBadFieldName() throws Exception {
        try {
            load(new File(STORIES_DIR + "replaceWithBadFieldNameFlatten.tokio").toURL().toString());
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals("Le champ 'BAD_FIELD_NAME' associé à la ligne 'refRow' n'existe pas.\n"
                         + "BAD_FIELD_NAME(replaceWithBadFieldNameFlatten.tokio:8)",
                         e.getMessage());
        }
    }


    public void test_includeDoubleRowId() throws Exception {
        try {
            load(new File(STORIES_DIR + "includeWithDoubleRowId.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(Table.computeDoubleRowIdMessage("refRow", "MY_TABLE"), e.getMessage());
        }
    }


    public void test_flatten_includeDoubleRowId() throws Exception {
        try {
            load(new File(STORIES_DIR + "includeWithDoubleRowIdFlatten.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(Table.computeDoubleRowIdMessage("refRow", "MY_TABLE"), e.getMessage());
        }
    }


    public void test_severalIncludes() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "includeSeveralRefs.tokio").toURL().toString());

        assertSeveralIncludes(loader);
    }


    public void test_flatten_severalIncludes() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeSeveralRefsFlatten.tokio").toURL().toString());

        assertSeveralIncludes(loader);
    }


    public void test_hybrid_severalIncludes() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeSeveralRefsHybrid.tokio").toURL().toString());

        assertSeveralIncludes(loader);
    }


    public void test_includeWithCascade() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "includeWithCascade.tokio").toURL().toString());

        assertSeveralIncludes(loader);
    }


    public void test_flatten_includeWithCascade() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeWithCascadeFlatten.tokio").toURL().toString());

        assertSeveralIncludes(loader);
    }


    public void test_includeWithRemove() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "includeWithRemove.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseRemove");

        assertIncludeWithRemove(scenario);
    }


    public void test_flatten_includeWithRemove() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeWithRemoveFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseRemove");

        assertIncludeWithRemove(scenario);
    }


    public void test_removeWithBadId() throws Exception {
        try {
            load(new File(STORIES_DIR + "removeWithBadId.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(XMLDatasetUtil.computeBadRowIdMessage("badId", "MY_TABLE"), e.getMessage());
        }
    }


    public void test_flatten_removeWithBadId() throws Exception {
        try {
            load(new File(STORIES_DIR + "removeWithBadIdFlatten.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(XMLDatasetUtil.computeBadRowIdMessage("badId", "MY_TABLE"), e.getMessage());
        }
    }


    public void test_copyOutputFromInput() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "copyOutputFromInput.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("CopyOutputFromInput");

        assertCopyOutputFromInput(scenario);
    }


    public void test_flatten_copyOutputFromInput() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "copyOutputFromInputFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("CopyOutputFromInput");

        assertCopyOutputFromInput(scenario);
    }


    public void test_generator() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "withGenerator.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("Generator");

        assertEquals(0, scenario.getOutputDataSet().getTableCount());

        String expected = "";
        expected += "<dataset>"
                    + "    <table name='MY_TABLE_SPEC'>"
                    + "        <row>"
                    + "            <field name='SPEC_FIELD1'>"
                    + "                <generateNumeric precision='23,5'/>"
                    + "            </field>"
                    + "            <field name='SPEC_FIELD2'>"
                    + "                <generateString precision='6'/>"
                    + "            </field>"
                    + "            <field name='SPEC_FIELD3'>"
                    + "                <generateDate/>"
                    + "            </field>"
                    + "            <field name='SPEC_FIELD4'>"
                    + "                <generateTimestamp/>"
                    + "            </field>"
                    + "            <field name='SPEC_FIELD5'>"
                    + "                <generateBoolean/>"
                    + "            </field>"
                    + "        </row>"
                    + "    </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_includeWithGenerator() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "useCaseWithIncludeAndGenerator.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCase");

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1'><generateString precision='30'/></field>";
        expected += "      <field name='FIELD2' value='valueField2Row0'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_SPEC'>";
        expected += "    <row>";
        expected += "      <field name='SPEC_FIELD1'><generateString precision='25'/></field>";
        expected += "      <field name='SPEC_FIELD2' value='valueSpecField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_replaceWithGenerated() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeWithReplaceAndGenerated.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseReplace");

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1'><generateString precision='30'/></field>";
        expected += "      <field name='FIELD2' value='valueField2UseCaseReplace'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_SPEC'>";
        expected += "    <row>";
        expected += "      <field name='SPEC_FIELD1' value='valueSpecField1'/>";
        expected += "      <field name='SPEC_FIELD2' value='valueSpecField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_autocomplete() throws Exception {
        XMLStoriesLoader loader = load(new File(STORIES_DIR + "autoComplete.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("nominal");

        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());

        assertTrue(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertFalse(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertNull(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());
    }


    public void test_autocomplete_withCopyEntity() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "autoComplete_withCopyEntity.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("nominal");

        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());
        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(3).isAutoComplete());
        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(4).isAutoComplete());
        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(5).isAutoComplete());
        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(6).isAutoComplete());
    }


    public void test_autocomplete_withCopyRow() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "autoComplete_withCopyRow.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("nominal");

        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());

        assertNull(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertTrue(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertNull(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());
    }


    public void test_entityWithNoId() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "autoComplete_withCreateEntity.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("nominal");

        Table table = scenario.getInputDataSet().getTable("MY_TABLE");
        assertEquals(7, table.getRowCount());
        assertNull(table.getRow(0).getId());
        assertNull(table.getRow(1).getId());
        assertNull(table.getRow(2).getId());
        assertNotNull(table.getRow(3).getId());
        assertEquals("MyEntity.titi", table.getRow(3).getId());
        assertNull(table.getRow(4).getId());
        assertNull(table.getRow(5).getId());
        assertNotNull(table.getRow(6).getId());
        assertTrue(table.getRow(3).getId().endsWith(".titi"));
    }


    public void test_autocomplete_withCreateEntity() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "autoComplete_withCreateEntity.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("nominal");

        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());
        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(3).isAutoComplete());
    }


    public void test_autocomplete_withReplaceRow() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "autoComplete_withReplaceRow.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("nominal");

        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
    }


    public void test_entitiesWithRequired() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeEntitiesWithRequired.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertEquals(1, scenarii.getScenarioCount());

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='TABLE1'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='TABLE2'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1Entity1'/>";
        expected += "      <field name='FIELD2' value='valueParam2Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1Entity1'/>";
        expected += "      <field name='FIELD2' value='valueParam2Entity2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }

     public void test_entitiesWithRequiredAndIdentityInsert() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeEntitiesWithRequiredAndIdentityInsert.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='TABLE1'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='TABLE2' identityInsert='true'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1Entity1'/>";
        expected += "      <field name='FIELD2' value='valueParam2Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1Entity1'/>";
        expected += "      <field name='FIELD2' value='valueParam2Entity2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_entitiesWithRequiredCollision() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeEntitiesWithRequiredCollision.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertEquals(1, scenarii.getScenarioCount());

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='TABLE1'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='TABLE2'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1'/>";
        expected += "      <field name='FIELD2' value='valueParam2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_entitiesWithRequiredExplicit() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeEntitiesWithRequiredExplicit.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertEquals(1, scenarii.getScenarioCount());

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='TABLE1'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='TABLE2'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1Entity1'/>";
        expected += "      <field name='FIELD2' value='valueParam2Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1Entity2'/>";
        expected += "      <field name='FIELD2' value='valueParam2Entity2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_entitiesWithRequiredAutocomplete() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeEntitiesWithRequiredAutocomplete.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertEquals(1, scenarii.getScenarioCount());

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='TABLE1'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='TABLE2'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueParam1'/>";
        expected += "      <field name='FIELD2' value='valueParam2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_entitiesWithRequiredGenerate() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeEntitiesWithRequiredGenerate.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertEquals(1, scenarii.getScenarioCount());

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='TABLE1'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field4'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='TABLE2'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1'><generateString precision='255'/></field>";
        expected += "      <field name='FIELD2' value='valueParam2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_entitiesWithRequiredCopy() throws Exception {
        XMLStoriesLoader loader = load(new File(
              STORIES_DIR + "includeEntitiesWithRequiredCopy.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertEquals(1, scenarii.getScenarioCount());

        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='TABLE1'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='field4'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='field4'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += " <table name='TABLE2'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueParam1'/>";
        expectedInput += "      <field name='FIELD2' value='valueParam2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='TABLE1'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field4'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += " <table name='TABLE2'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueParam1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueParam2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void assertCopyOutputFromInput(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row1'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row2'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueField1Row1'/>";
        expectedOutput += "      <field name='FIELD2' value='newValue'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueField1Row2'/>";
        expectedOutput += "      <field name='FIELD2' value='valueField2Row2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void assertIncludeWithRemove(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_SPEC'>";
        expected += "    <row>";
        expected += "      <field name='SPEC_FIELD1' value='valueSpecField1'/>";
        expected += "      <field name='SPEC_FIELD2' value='valueSpecField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertReplace(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1UseCaseReplace'/>";
        expected += "      <field name='FIELD2' value='valueField2UseCaseReplace'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_SPEC'>";
        expected += "    <row>";
        expected += "      <field name='SPEC_FIELD1' value='valueSpecField1'/>";
        expected += "      <field name='SPEC_FIELD2' value='valueSpecField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertIncludeWithCopy(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row0'/>";
        expected += "      <field name='FIELD2' value='valueField2Row0'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1UseCase'/>";
        expected += "      <field name='FIELD2' value='valueField2Row0'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1UseCaseCopy'/>";
        expected += "      <field name='FIELD2' value='valueField2Row0'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_SPEC'>";
        expected += "    <row>";
        expected += "      <field name='SPEC_FIELD1' value='valueSpecField1'/>";
        expected += "      <field name='SPEC_FIELD2' value='valueSpecField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertInclude(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row0'/>";
        expected += "      <field name='FIELD2' value='valueField2Row0'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_SPEC'>";
        expected += "    <row>";
        expected += "      <field name='SPEC_FIELD1' value='valueSpecField1'/>";
        expected += "      <field name='SPEC_FIELD2' value='valueSpecField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertReferential(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row0'/>";
        expected += "      <field name='FIELD2' value='valueField2Row0'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertSeveralIncludes(XMLStoriesLoader loader) {
        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("SeveralIncludes");

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row0'/>";
        expected += "      <field name='FIELD2' value='valueField2Row0'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1Row1'/>";
        expected += "      <field name='FIELD2' value='valueField2Row1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1RefBis'/>";
        expected += "      <field name='FIELD2' value='valueField2RefBis'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='value1SeveralIncludes'/>";
        expected += "      <field name='FIELD2' value='value2SeveralIncludes'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_BIS'>";
        expected += "    <row>";
        expected += "      <field name='BIS1' value='bis1'/>";
        expected += "      <field name='BIS2' value='bis2'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='BIS1' value='bis1SeveralIncludes'/>";
        expected += "      <field name='BIS2' value='bis2SeveralIncludes'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MY_TABLE_SPEC'>";
        expected += "    <row>";
        expected += "      <field name='SPEC_FIELD1' value='valueSpecField1'/>";
        expected += "      <field name='SPEC_FIELD2' value='valueSpecField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }
}
