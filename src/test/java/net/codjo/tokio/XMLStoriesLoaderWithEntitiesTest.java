/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.DatasetChecker;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import java.io.File;
/**
 *
 */
public class XMLStoriesLoaderWithEntitiesTest extends XMLStoriesLoaderTestCase {
    public void test_createEntitiesWithoutIncludeEntities()
          throws Exception {
        try {
            load(new File(STORIES_DIR + "createEntitiesWithoutIncludeEntities.tokio").toURL()
                  .toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(XMLStoryUtil.MISSING_ENTITIES_ERROR, e.getMessage());
        }
    }


    public void test_flatten_createEntitiesWithoutIncludeEntities()
          throws Exception {
        try {
            load(new File(STORIES_DIR + "createEntitiesWithoutIncludeEntitiesFlatten.tokio").toURL()
                  .toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(XMLStoryUtil.MISSING_ENTITIES_ERROR, e.getMessage());
        }
    }


    public void test_includeEntities() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntities.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntities(scenario);
    }


    public void test_flatten_includeEntities() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntities(scenario);
    }


    public void test_includeEntitiesWithSeveralParamToken()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createEntityWithSeveralParametersToken.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithSeveralParametersToken(scenario);
    }


    public void test_flatten_includeEntitiesWithSeveralParamToken()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createEntityWithSeveralParametersTokenFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithSeveralParametersToken(scenario);
    }


    public void test_includeEntitiesWithOrderClause()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "entitiesWithOrderClauseOutput.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithOrderClause(scenario);
    }


    public void test_flatten_includeEntitiesWithOrderClause()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "entitiesWithOrderClauseOutputFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithOrderClause(scenario);
    }


    public void test_includeEntitiesWithIdentityInsert()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "entitiesWithIdentityInsertInput.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithIdentityInsert(scenario);
    }


    public void test_flatten_includeEntitiesWithIdentityInsert()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "entitiesWithIdentityInsertInputFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithIdentityInsert(scenario);
    }


    public void test_includeSeveralEntitiesFile()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeSeveralEntitiesFile.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeSeveralEntitiesFile(scenario);
    }


    public void test_flatten_includeSeveralEntitiesFile()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeSeveralEntitiesFileFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeSeveralEntitiesFile(scenario);
    }


    public void test_createSeveralEntitiesWithRef()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createSeveralEntitiesWithRef.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertCreateSeveralEntitiesWithRef(scenario);
    }


    public void test_flatten_createSeveralEntitiesWithRef()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createSeveralEntitiesWithRefFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertCreateSeveralEntitiesWithRef(scenario);
    }


    public void test_includeEntitiesInOutput() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesInOutput.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesInOutput(scenario);
    }


    public void test_flatten_includeEntitiesInOutput() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesInOutputFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesInOutput(scenario);
    }


    public void test_includeEntitiesWithCopy() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesWithCopy.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithCopy(scenario);
    }


    public void test_flatten_includeEntitiesWithCopy() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesWithCopyFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithCopy(scenario);
    }


    public void test_includeEntitiesWithReplace()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesWithReplace.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithReplace(scenario);
    }


    public void test_flatten_includeEntitiesWithReplace()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesWithReplaceFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeEntitiesWithReplace(scenario);
    }


    public void test_createAndCopyEntities() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createAndCopyEntities.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1Replace'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1Replace'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1Entity1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2Entity1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_createAndCopyEntitiesWithReplace()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createAndCopyEntitiesWithReplace.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertCreateAndCopyEntitiesWithReplace(scenario);
    }


    public void test_flatten_createAndCopyEntitiesWithReplace()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createAndCopyEntitiesWithReplaceFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertCreateAndCopyEntitiesWithReplace(scenario);
    }


    public void test_copyBadRefEntity() throws Exception {
        try {
            load(new File(STORIES_DIR + "copyBadRefEntity.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals("L'entité 'unknownEntity' n'existe pas.", e.getMessage());
        }
    }


    public void test_flatten_copyBadRefEntity() throws Exception {
        try {
            load(new File(STORIES_DIR + "copyBadRefEntityFlatten.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals("L'entité 'unknownEntity' n'existe pas.", e.getMessage());
        }
    }


    public void test_createBadNameEntity() throws Exception {
        try {
            load(new File(STORIES_DIR + "createBadNameEntity.tokio").toURL().toString());
            fail();
        }
        catch (RuntimeException e) {
            assertEquals("L'entité 'unknownName' n'a pas été définie.", e.getMessage());
        }
    }


    public void test_includeEntitiesMultiTables()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeEntitiesMultiTables.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field1'/>";
        expected += "      <field name='FIELD2' value='firstValueParam'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field1'/>";
        expected += "      <field name='FIELD2' value='secondValueParam'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_createSeveralEntities() throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "createSeveralEntities.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='MULTI_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field1'/>";
        expected += "      <field name='FIELD2' value='multiValueParam'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    public void test_includeCopyRowInterEntities()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeCopyRowInterEntities.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeCopyRowInterEntities(scenario);
    }


    public void test_flatten_includeCopyRowInterEntities()
          throws Exception {
        XMLStoriesLoader loader =
              load(new File(STORIES_DIR + "includeCopyRowInterEntitiesFlatten.tokio").toURL()
                    .toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("UseCaseEntities");

        assertIncludeCopyRowInterEntities(scenario);
    }


    private void assertIncludeEntities(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' null='true'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }

//      <entity id="MyEntity">
//        <comment>MyEntity</comment>
//
//        <parameters>
//            <parameter name="param1"/>
//            <parameter name="param2"/>
//            <parameter name="param3"/>
//            <parameter name="param4" default="@param5@ test"/>
//            <parameter name="param5" default="@param6@ @param1@"/>
//            <parameter name="param6" default="en titi"/>
//        </parameters>
//
//        <body>
//            <table name="TABLE">
//                <row>
//                    <field name="FIELD1" value="field1"/>
//                    <field name="FIELD2_ONLY_PARAM" value="@param1@"/>
//                    <field name="FIELD3_BEFORE_PARAM" value="Before@param1@"/>
//                    <field name="FIELD4_AFTER_PARAM" value="@param1@After"/>
//                    <field name="FIELD5_MIDDLE_PARAM" value="Before@param1@After"/>
//                    <field name="FIELD6_BEFORE_NULL_PARAM" value="Before@param2@"/>
//                    <field name="FIELD7_MIDDLE_NULL_PARAM" value="Before@param2@After"/>
//                    <field name="FIELD8_AFTER_NULL_PARAM" value="@param2@After"/>
//                    <field name="FIELD9_EMPTY_PARAM" value="Before@param3@After"/>
//                    <field name="FIELD10" value="@param4@"/>
//                    <field name="FIELD11" value="@param5@"/>
//                    <field name="FIELD12" value="@param6@"/>
//                </row>
//            </table>
//        </body>
//
//    </entity>

//    <include-entities file="../entities/entitiesWithSeveralParametersToken.xml"/>
//
//    <input>
//        <create-entity name="MyEntity" id="entity1">
//            <parameter name="param1" value="valueParam1"/>
//            <parameter name="param2" null="true"/>
//            <parameter name="param3"/>
//        </create-entity>
//    </input>


    private void assertIncludeEntitiesWithSeveralParametersToken(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += " <table name='TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='field1'/>";
        expected += "      <field name='FIELD2_ONLY_PARAM' value='valueParam1'/>";
        expected += "      <field name='FIELD3_BEFORE_PARAM' value='BeforevalueParam1'/>";
        expected += "      <field name='FIELD4_AFTER_PARAM' value='valueParam1After'/>";
        expected += "      <field name='FIELD5_MIDDLE_PARAM' value='BeforevalueParam1After'/>";
        expected += "      <field name='FIELD6_BEFORE_NULL_PARAM' value='Before'/>";
        expected += "      <field name='FIELD7_MIDDLE_NULL_PARAM' value='BeforeAfter'/>";
        expected += "      <field name='FIELD8_AFTER_NULL_PARAM' value='After'/>";
        expected += "      <field name='FIELD9_EMPTY_PARAM' value='BeforeAfter'/>";
        expected += "      <field name='FIELD10' value=\"en titi valueParam1 test\"/>";
        expected += "      <field name='FIELD11' value=\"en titi valueParam1\"/>";
        expected += "      <field name='FIELD12' value=\"en titi\"/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertIncludeEntitiesWithOrderClause(Scenario scenario) {
        assertEquals("FIELD1", scenario.getInputDataSet().getTable("TABLE1").getOrderClause());
        assertEquals("FIELD1", scenario.getOutputDataSet().getTable("TABLE1").getOrderClause());
        assertEquals("FIELD2", scenario.getOutputDataSet().getTable("TABLE2").getOrderClause());
    }


    private void assertIncludeEntitiesWithIdentityInsert(Scenario scenario) {
        assertTrue(scenario.getInputDataSet().getTable("TABLE1").isIdentityInsert());
        assertFalse(scenario.getInputDataSet().getTable("TABLE2").isIdentityInsert());
    }


    private void assertIncludeSeveralEntitiesFile(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "  <table name='TABLE'>";
        expected += "       <row id='firstTableFirstRow'>";
        expected += "           <field name='FIELD1' value='field1'/>";
        expected += "           <field name='FIELD2' value='valueParam1'/>";
        expected += "       </row>";
        expected += "   </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertCreateSeveralEntitiesWithRef(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1Entity2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2Entity2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertIncludeEntitiesInOutput(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += " <table name='FIRST_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIRST_FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIRST_FIELD2' value='valueParam1Output'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += " <table name='SECOND_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='SECOND_FIELD1' value='valueParam2Output'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void assertIncludeEntitiesWithCopy(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1Replace'/>";
        expected += "      <field name='FIRST_FIELD2' value='field2Replace'/>";
        expected += "      <field name='FIRST_FIELD3' value='field3'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='newField1'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam2'/>";
        expected += "      <field name='FIRST_FIELD3' value='field3'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertIncludeEntitiesWithReplace(Scenario scenario) {
        String expected = "";
        expected += "<dataset>";
        expected += "  <table name='MY_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIELD1' value='valueField1'/>";
        expected += "      <field name='FIELD2' value='valueField2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='FIRST_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1Replace'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='FIRST_FIELD1' value='field1'/>";
        expected += "      <field name='FIRST_FIELD2' value='valueParam1Entity2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += " <table name='SECOND_TABLE'>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2Entity1'/>";
        expected += "    </row>";
        expected += "    <row>";
        expected += "      <field name='SECOND_FIELD1' value='valueParam2Entity2'/>";
        expected += "    </row>";
        expected += "  </table>";
        expected += "</dataset>";

        DatasetChecker.check(expected, scenario.getInputDataSet());
    }


    private void assertCreateAndCopyEntitiesWithReplace(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += " <table name='FIRST_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIRST_FIELD1' value='field1Replace'/>";
        expectedInput += "      <field name='FIRST_FIELD2' value='valueParam1Entity1'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += " <table name='SECOND_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='SECOND_FIELD1' value='valueParam2Entity1'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += " <table name='FIRST_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIRST_FIELD1' value='newValue'/>";
        expectedOutput += "      <field name='FIRST_FIELD2' value='valueParam1Entity1'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIRST_FIELD1' value='field1Replace'/>";
        expectedOutput += "      <field name='FIRST_FIELD2' value='newValueField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += " <table name='SECOND_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='SECOND_FIELD1' value='valueParam2Entity1'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void assertIncludeCopyRowInterEntities(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += " <table name='FIRST_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIRST_FIELD1' value='valueParam1'/>";
        expectedInput += "      <field name='FIRST_FIELD2' value='newField2'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIRST_FIELD1' value='valueParam1'/>";
        expectedInput += "      <field name='FIRST_FIELD2' value='newField2'/>";
        expectedInput += "      <field name='FIRST_FIELD3' value='newField3'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());
    }
}
