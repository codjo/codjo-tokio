/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.DatasetChecker;
import net.codjo.tokio.model.LoggerLocationVisitor;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.junit.Assert.assertThat;
public class XMLCasesLoaderTest extends XMLCasesLoaderTestCase {
    private StringBuilder stringBuilder = new StringBuilder();


    public void test_caseStandard() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseStandard.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkInputOutput(scenarii, "NominalCase");
        checkInputOutput(scenarii, "InheritCase");
    }


    public void test_flatten_caseStandart() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseStandardFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkInputOutput(scenarii, "NominalCase");
        checkInputOutput(scenarii, "InheritCase");
    }


    public void test_caseStandardWithGeneratedValues() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseStandardWithGenerated.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());

        Scenario nominalCase = scenarii.getScenario("NominalCase");

        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1'> <generateString precision='25'/></field>";
        expectedInput += "      <field name='FIELD2' value='valueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, nominalCase.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, nominalCase.getOutputDataSet());
    }


    public void test_properties() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseProperties.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        Scenario scenario = scenarii.getScenario("NominalCase");

        Properties etalon = new Properties();
        etalon.setProperty("user.name", "toto");
        etalon.setProperty("tralala", "youpi");
        assertEquals(etalon, scenario.getProperties());
    }


    public void test_compositeEntity() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseCompositeEntity.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        Scenario scenario = scenarii.getScenario("myCase");

        assertThat(scenario.getInputTable("AP_DUMMY").getRows(), equalsTo(
              "(id=fatherID.son.childDummy, content=[age=12, comment=with id])",
              "(id=null, inheritFrom=fatherID.son.childDummy, content=[age=12, comment=copy])",
              "(id=null, content=[age=12, comment=no id])",
              "(id=null, content=[age=8, comment=with id])",
              "(id=null, content=[age=8, comment=copy])",
              "(id=null, content=[age=8, comment=no id])",
              "(id=null, content=[age=6, comment=with id])",
              "(id=null, content=[age=6, comment=copy])",
              "(id=null, content=[age=6, comment=no id])",
              "(id=copyID.son.childDummy, content=[age=12, comment=with id])",
              "(id=null, inheritFrom=copyID.son.childDummy, content=[age=12, comment=copy])",
              "(id=null, content=[age=12, comment=no id])",
              "(id=null, content=[age=8, comment=with id])",
              "(id=null, content=[age=8, comment=copy])",
              "(id=null, content=[age=8, comment=no id])",
              "(id=null, content=[age=6, comment=with id])",
              "(id=null, content=[age=6, comment=copy])",
              "(id=null, content=[age=6, comment=no id])"
        ));
    }


    public void test_caseAddingRows() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseAddingRows.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkAddRows(scenarii);
    }


    public void test_flatten_caseAddingRows() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseAddingRowsFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkAddRows(scenarii);
    }


    public void test_caseCopyingRows() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseCopyingRows.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkCopyAuxiliaryFirstCase(scenarii.getScenario("AuxiliaryFirstCase"));
        checkCopyAuxiliarySecondCase(scenarii.getScenario("AuxiliarySecondCase"));
    }


    public void test_flatten_caseCopyingRows() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseCopyingRowsFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkCopyAuxiliaryFirstCase(scenarii.getScenario("AuxiliaryFirstCase"));
        checkCopyAuxiliarySecondCase(scenarii.getScenario("AuxiliarySecondCase"));
    }


    public void test_caseReplacingRows() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseReplacingRows.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkReplaceAuxiliaryFirstCase(scenarii.getScenario("AuxiliaryFirstCase"));
        checkReplaceAuxiliarySecondCase(scenarii.getScenario("AuxiliarySecondCase"));
    }


    public void test_flatten_caseReplacingRows() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseReplacingRowsFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkReplaceAuxiliaryFirstCase(scenarii.getScenario("AuxiliaryFirstCase"));
        checkReplaceAuxiliarySecondCase(scenarii.getScenario("AuxiliarySecondCase"));
    }


    public void test_flatten_caseReplacingRows_generated() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseReplacingRowsFlattenGenerated.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        Scenario scenario = scenarii.getScenario("AuxiliaryFirstCase");
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2'><genereatedString precision='30'/></field>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1NewRow'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2NewRow'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='replaceValueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1NewRow'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2NewRow'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
        checkReplaceAuxiliarySecondCase(scenarii.getScenario("AuxiliarySecondCase"));
    }


    public void test_caseRemovingRows() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseRemovingRows.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkRemoveRow(scenarii);
    }


    public void test_flatten_caseRemovingRows() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseRemovingRowsFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkRemoveRow(scenarii);
    }


    public void test_caseIncludingStories() throws Exception {
        assertCaseIncludesStories("caseIncludingStories.tokio");
    }


    public void test_flatten_caseIncludingStories() throws Exception {
        assertCaseIncludesStories("caseIncludingStoriesFlatten.tokio");
    }


    public void test_caseIncludingStoryInResources() throws Exception {
        assertCaseIncludesStories("caseIncludingStoryInResources.tokio");
    }


    public void test_caseIncludingNotExistingStroy() throws Exception {
        try {
            load(new File(CASES_DIR + "caseIncludingNotExistingStory.tokio").toURL().toString());
            fail("Le fichier inclus n'existant pas, il y aurait dû y avoir une erreur");
        }
        catch (Exception e) {
            assertEquals("notExistingStory.tokio n'existe pas", e.getMessage());
        }
    }


    public void test_caseIncludingEntities() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "caseIncludingEntities.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkIncludeEntitiesNominalCase(scenarii.getScenario("NominalCase"));
        checkIncludeEntitiesAuxiliaryCase(scenarii.getScenario("AuxiliaryCase"));
        checkIncludeEntitiesDefaultValueCase(scenarii.getScenario("DefaultValueCase"));
    }


    public void test_caseIncludingEntities_withNullField() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseIncludingEntities_withNullField.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());

        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' null='true'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenarii.getScenario("nominal").getInputDataSet());
    }


    public void test_caseIncludingEntitiesWithGeneratedParameters() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseIncludingEntitiesWithGenerated.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkIncludeEntitiesNominalCase(scenarii.getScenario("NominalCase"));
        checkIncludeEntitiesAuxiliaryCase(scenarii.getScenario("AuxiliaryCase"));
        Scenario scenario = scenarii.getScenario("DefaultValueCase");
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD_NULL' null=\"true\"/>";
        expectedInput += "      <field name='FIELD_EMPTY_STRING' value=''/>";
        expectedInput += "      <field name='FIELD1' value='ExplicitParam1'/>";
        expectedInput += "      <field name='FIELD2'><generateString precision=\"55\"/></field>";
        expectedInput += "      <field name='FIELD3' value='ExplicitParam3'/>";
        expectedInput
              += "      <field name='FIELD_DEFAULT_EMPTY_STRING'><generateString precision=\"50\"/></field>";
        expectedInput += "      <field name='FIELD_DEFAULT_NULL' null=\"true\"/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    public void test_flatten_caseIncludingEntities() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseIncludingEntitiesFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(3, scenarii.getScenarioCount());

        checkIncludeEntitiesNominalCase(scenarii.getScenario("NominalCase"));
        checkIncludeEntitiesAuxiliaryCase(scenarii.getScenario("AuxiliaryCase"));
        checkIncludeEntitiesDefaultValueCase(scenarii.getScenario("DefaultValueCase"));
    }


    public void test_caseIncludEntitiesDefaultValueError() throws Exception {
        try {
            load(new File(CASES_DIR + "caseIncludingEntitiesDefaultValueErrorCase.tokio").toURL().toString());
            fail("should get an exception if parameter is missing and has no default value");
        }
        catch (Exception exception) {
            assertEquals("Il n'existe pas de valeur par défaut pour le paramètre 'param1' non spécifié.",
                         exception.getMessage());
        }
    }


    public void test_flatten_caseIncludEntitiesDefaultValueError() throws Exception {
        try {
            load(new File(
                  CASES_DIR + "caseIncludingEntitiesDefaultValueErrorCaseFlatten.tokio").toURL().toString());
            fail("should get an exception if parameter is missing and has no default value");
        }
        catch (Exception exception) {
            assertEquals("Il n'existe pas de valeur par défaut pour le paramètre 'param1' non spécifié.",
                         exception.getMessage());
        }
    }


    public void testCaseIncludEntitiesWithInvalidParameter() throws Exception {
        try {
            load(new File(CASES_DIR + "caseIncludingEntitiesWithInvalidParameters.tokio").toURL().toString());
        }
        catch (TokioLoaderException e) {
            assertEquals("Le paramètre invalidParam n'est pas défini dans l'entité Entity", e.getMessage());
        }
    }


    public void testFlattenCaseIncludEntitiesWithInvalidParameter() throws Exception {
        try {
            load(new File(
                  CASES_DIR + "caseIncludingEntitiesWithInvalidParametersFlatten.tokio").toURL().toString());
        }
        catch (TokioLoaderException e) {
            assertEquals("Le paramètre invalidParam n'est pas défini dans l'entité Entity", e.getMessage());
        }
    }


    public void test_caseIncludingEntitiesInResources() throws Exception {
        XMLCasesLoader loader = load(
              new File(CASES_DIR + "caseIncludingEntitiesInResources.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkIncludeEntitiesNominalCase(scenarii.getScenario("NominalCase"));
        checkIncludeEntitiesAuxiliaryCase(scenarii.getScenario("AuxiliaryCase"));
    }


    public void test_caseIncludingEntitiesAndStories()
          throws Exception {
        XMLCasesLoader loader =
              load(new File(CASES_DIR + "caseIncludingEntitiesAndStories.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkIncludeEntitiesAndStoriesNominalCase(scenarii.getScenario("NominalCase"));
        checkIncludeEntitiesAndStoriesAuxiliaryCase(scenarii.getScenario("AuxiliaryCase"));
    }


    public void test_flatten_caseIncludingEntitiesAndStories()
          throws Exception {
        XMLCasesLoader loader =
              load(new File(CASES_DIR + "caseIncludingEntitiesAndStoriesFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkIncludeEntitiesAndStoriesNominalCase(scenarii.getScenario("NominalCase"));
        checkIncludeEntitiesAndStoriesAuxiliaryCase(scenarii.getScenario("AuxiliaryCase"));
    }


    public void test_hybrid_caseIncludingEntitiesAndStories()
          throws Exception {
        XMLCasesLoader loader =
              load(new File(CASES_DIR + "caseIncludingEntitiesAndStoriesHybrid.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkIncludeEntitiesAndStoriesNominalCase(scenarii.getScenario("NominalCase"));
        checkIncludeEntitiesAndStoriesAuxiliaryCase(scenarii.getScenario("AuxiliaryCase"));
    }


    public void test_caseIncludingStoryWithEntity()
          throws Exception {
        XMLCasesLoader loader =
              load(new File(CASES_DIR + "caseIncludingStoryWithEntity.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());

        checkIncludeStoryWithEntityNominalCase(scenarii.getScenario("NominalCase"));
    }


    public void test_flatten_caseIncludingStoryWithEntity()
          throws Exception {
        XMLCasesLoader loader =
              load(new File(CASES_DIR + "caseIncludingStoryWithEntityFlatten.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());

        checkIncludeStoryWithEntityNominalCase(scenarii.getScenario("NominalCase"));
    }


    public void test_caseStoryInWorkingDirWithEntityInResources()
          throws Exception {
        XMLCasesLoader loader =
              load(new File(CASES_DIR + "caseIncludingStoryInWorkingDirectoryWithEntityInResources.tokio")
                    .toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());

        checkIncludeStoryWithEntityNominalCase(scenarii.getScenario("NominalCase"));
    }


    public void test_caseStoryInResourcesWithEntityInResources()
          throws Exception {
        XMLCasesLoader loader =
              load(new File(CASES_DIR + "caseIncludingStoryInResourcesWithEntityInResources.tokio")
                    .toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());

        checkIncludeStoryWithEntityNominalCase(scenarii.getScenario("NominalCase"));
    }


    public void test_autoComplete() throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + "autoComplete.tokio").toURL().toString());

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


    public void test_autoComplete_withCopyRow() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "autoComplete_withCopyRow.tokio").toURL().toString());

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


    public void test_autoComplete_withInheritId() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "autoComplete_withInheritId.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("child");

        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());
        assertNull(scenario.getInputDataSet().getTable("MY_TABLE").getRow(3).isAutoComplete());

        assertTrue(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertFalse(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
        assertNull(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(2).isAutoComplete());
        assertTrue(scenario.getOutputDataSet().getTable("MY_TABLE").getRow(3).isAutoComplete());
    }


    public void test_autoComplete_withReplaceRow() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "autoComplete_withReplaceRow.tokio").toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(1, scenarii.getScenarioCount());
        Scenario scenario = scenarii.getScenario("nominal");

        assertFalse(scenario.getInputDataSet().getTable("MY_TABLE").getRow(0).isAutoComplete());
        assertTrue(scenario.getInputDataSet().getTable("MY_TABLE").getRow(1).isAutoComplete());
    }


    public void test_location_withInheritedCases() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "location_withInheritedCases.tokio").toURL().toString());

        Scenario scenario = loader.getScenarii().getScenario("extended2");

        stringBuilder = new StringBuilder();
        scenario.getInputTable("AP_TABLE").getRow(1).getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy(location_withInheritedCases.tokio:26)\n"
                     + "\tcase(location_withInheritedCases.tokio:23)\n"
                     + "\t\treplace(location_withInheritedCases.tokio:17)\n"
                     + "\t\t\tcase(location_withInheritedCases.tokio:14)\n"
                     + "\t\t\t\trow(location_withInheritedCases.tokio:8)\n", stringBuilder.toString());

        scenario = loader.getScenarii().getScenario("extended3");

        stringBuilder = new StringBuilder();
        scenario.getInputTable("AP_TABLE").getRow(0).getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("case(location_withInheritedCases.tokio:30)\n"
                     + "\tcase(location_withInheritedCases.tokio:23)\n"
                     + "\t\treplace(location_withInheritedCases.tokio:17)\n"
                     + "\t\t\tcase(location_withInheritedCases.tokio:14)\n"
                     + "\t\t\t\trow(location_withInheritedCases.tokio:8)\n"
                     + "create-entity(location_withInheritedCases.tokio:32)\n"
                     + "\trow-required(location_withInheritedCases.entities:6)\n", stringBuilder.toString());

        stringBuilder = new StringBuilder();
        scenario.getInputTable("AP_TABLE").getRow(1).getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("case(location_withInheritedCases.tokio:30)\n"
                     + "\tcopy(location_withInheritedCases.tokio:26)\n"
                     + "\t\tcase(location_withInheritedCases.tokio:23)\n"
                     + "\t\t\treplace(location_withInheritedCases.tokio:17)\n"
                     + "\t\t\t\tcase(location_withInheritedCases.tokio:14)\n"
                     + "\t\t\t\t\trow(location_withInheritedCases.tokio:8)\n"
                     + "create-entity(location_withInheritedCases.tokio:32)\n"
                     + "\trow-required(location_withInheritedCases.entities:6)\n", stringBuilder.toString());
    }


    public void test_location_casesWithEntities() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseIncludingEntities.tokio").toURL().toString());

        Scenario scenario = loader.getScenarii().getScenario("NominalCase");

        scenario.getInputTable("MY_TABLE")
              .getRow(0)
              .getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("create-entity(caseIncludingEntities.tokio:7)\n"
                     + "\trow(entities.xml:11)\n", stringBuilder.toString());
    }


    public void test_location_casesWithComposedEntities() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseCompositeEntity.tokio").toURL().toString());

        Scenario scenario = loader.getScenarii().getScenario("myCase");

        scenario.getInputTable("AP_DUMMY")
              .getRow(16)
              .getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy-entity(caseCompositeEntity.tokio:8)\n"
                     + "\tcreate-entity(caseCompositeEntity.tokio:7)\n"
                     + "\t\tcreate-entity(caseCompositeEntity_entity.xml:6)\n"
                     + "\t\t\tcopy(caseCompositeEntity_entityToInclude.xml:13)\n"
                     + "\t\t\t\trow(caseCompositeEntity_entityToInclude.xml:9)\n", stringBuilder.toString());
    }


    public void test_location_caseIncludingStories() throws Exception {
        XMLCasesLoader loader = load(new File(
              CASES_DIR + "caseIncludingStories.tokio").toURL().toString());

        Scenario scenario = loader.getScenarii().getScenario("NominalCase");

        scenario.getInputTable("MY_TABLE")
              .getRow(2)
              .getLocationPointer()
              .accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy(caseIncludingStories.tokio:10)\n"
                     + "\trow(referentialWithInput.tokio:8)\n", stringBuilder.toString());
    }


    private void assertCaseIncludesStories(String tokioFileName) throws Exception {
        XMLCasesLoader loader = load(new File(CASES_DIR + tokioFileName).toURL().toString());

        ScenarioList scenarii = loader.getScenarii();
        assertEquals(2, scenarii.getScenarioCount());

        checkIncludeStoriesNominalCase(scenarii);
        checkIncludeStoriesAuxiliaryCase(scenarii);
    }


    private void checkAddRows(ScenarioList scenarii) {
        Scenario auxiliaryCase = scenarii.getScenario("AuxiliaryCase");

        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1NewRow'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2NewRow'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, auxiliaryCase.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1NewRow'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2NewRow'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, auxiliaryCase.getOutputDataSet());
    }


    private void checkRemoveRow(ScenarioList scenarii) {
        Scenario scenario = scenarii.getScenario("AuxiliaryFirstCase");
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1NewRow'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2NewRow'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1NewRow'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2NewRow'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());

        scenario = scenarii.getScenario("AuxiliarySecondCase");
        expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkIncludeStoryWithEntityNominalCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='field1'/>";
        expectedInput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='copyValueField1'/>";
        expectedInput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='copyValueField1'/>";
        expectedOutput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkReplaceAuxiliarySecondCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2' value='replaceAgainValueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1NewRow'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2NewRow'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='replaceValueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='replaceValueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1NewRow'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2NewRow'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkReplaceAuxiliaryFirstCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2' value='replaceValueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1NewRow'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2NewRow'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='replaceValueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1NewRow'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2NewRow'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkCopyAuxiliaryFirstCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1'><generateNumeric precision='38,3'/></field>";
        expectedInput += "      <field name='FIELD2'><generateNumeric precision='38,3'/></field>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1NewRow'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2NewRow'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1'><generateNumeric precision='38,3'/></field>";
        expectedInput += "      <field name='FIELD2' value='newValueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1NewRow'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2NewRow'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='newValueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkCopyAuxiliarySecondCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1'><generateNumeric precision='38,3'/></field>";
        expectedInput += "      <field name='FIELD2'><generateNumeric precision='38,3'/></field>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1NewRow'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2NewRow'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1'><generateNumeric precision='38,3'/></field>";
        expectedInput += "      <field name='FIELD2' value='newValueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1'><generateNumeric precision='38,3'/></field>";
        expectedInput += "      <field name='FIELD2' value='copyValueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1NewRow'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2NewRow'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='newValueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='newValueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='copyValueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkInputOutput(ScenarioList scenarii, String storyId) {
        Scenario nominalCase = scenarii.getScenario(storyId);

        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, nominalCase.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueOutputField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, nominalCase.getOutputDataSet());
    }


    private void checkIncludeStoriesNominalCase(ScenarioList scenarii) {
        Scenario scenario = scenarii.getScenario("NominalCase");
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row0'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row0'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row1'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='copyValueField1Row0'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row0'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "  <table name='ANOTHER_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='ANOTHER_FIELD1' value='valueInputField1'/>";
        expectedInput += "      <field name='ANOTHER_FIELD2' value='valueInputField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='copyValueField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "  <table name='ANOTHER_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='ANOTHER_FIELD1' value='valueOutputField1'/>";
        expectedOutput += "      <field name='ANOTHER_FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkIncludeStoriesAuxiliaryCase(ScenarioList scenarii) {
        Scenario scenario = scenarii.getScenario("AuxiliaryCase");
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row0'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row0'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row1'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='copyValueField1Row0'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row0'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='copyValueField1Row0'/>";
        expectedInput += "      <field name='FIELD2' value='copyValueField2Row0'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "  <table name='ANOTHER_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='ANOTHER_FIELD1' value='valueInputField1'/>";
        expectedInput += "      <field name='ANOTHER_FIELD2' value='valueInputField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='copyValueField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueField1'/>";
        expectedOutput += "      <field name='FIELD2' value='copyValueField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "  <table name='ANOTHER_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='ANOTHER_FIELD1' value='valueOutputField1'/>";
        expectedOutput += "      <field name='ANOTHER_FIELD2' value='valueOutputField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "  <table name='AUXILIARY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='AUXILIARY_FIELD' value='auxiliary'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkIncludeEntitiesNominalCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='field1'/>";
        expectedInput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "  <table name='ANOTHER_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='ANOTHER_FIELD1' value='valueInputField1'/>";
        expectedInput += "      <field name='ANOTHER_FIELD2' value='valueInputField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkIncludeEntitiesDefaultValueCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD_NULL' null=\"true\"/>";
        expectedInput += "      <field name='FIELD_EMPTY_STRING' value=''/>";
        expectedInput += "      <field name='FIELD1' value='ExplicitParam1'/>";
        expectedInput += "      <field name='FIELD2' value='defaultParam2'/>";
        expectedInput += "      <field name='FIELD3' value='defaultParam2 ExplicitParam3'/>";
        expectedInput += "      <field name='FIELD_DEFAULT_EMPTY_STRING' value=''/>";
        expectedInput += "      <field name='FIELD_DEFAULT_NULL' null=\"true\"/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkIncludeEntitiesAuxiliaryCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='field1'/>";
        expectedInput += "      <field name='FIELD2' value='newValueInput'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "  <table name='ANOTHER_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='ANOTHER_FIELD1' value='valueInputField1'/>";
        expectedInput += "      <field name='ANOTHER_FIELD2' value='valueInputField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIELD2' value='newValueOutput'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkIncludeEntitiesAndStoriesNominalCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row0'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row0'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row1'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='field1'/>";
        expectedInput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "  <table name='ANOTHER_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='ANOTHER_FIELD1' value='valueInputField1'/>";
        expectedInput += "      <field name='ANOTHER_FIELD2' value='valueInputField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private void checkIncludeEntitiesAndStoriesAuxiliaryCase(Scenario scenario) {
        String expectedInput = "";
        expectedInput += "<dataset>";
        expectedInput += "  <table name='MY_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row0'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row0'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='valueField1Row1'/>";
        expectedInput += "      <field name='FIELD2' value='valueField2Row1'/>";
        expectedInput += "    </row>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='FIELD1' value='field1'/>";
        expectedInput += "      <field name='FIELD2' value='newValueInput'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "  <table name='ANOTHER_TABLE'>";
        expectedInput += "    <row>";
        expectedInput += "      <field name='ANOTHER_FIELD1' value='valueInputField1'/>";
        expectedInput += "      <field name='ANOTHER_FIELD2' value='valueInputField2'/>";
        expectedInput += "    </row>";
        expectedInput += "  </table>";
        expectedInput += "</dataset>";

        DatasetChecker.check(expectedInput, scenario.getInputDataSet());

        String expectedOutput = "";
        expectedOutput += "<dataset>";
        expectedOutput += "  <table name='MY_TABLE'>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIELD2' value='fieldValueParameter'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='valueField1'/>";
        expectedOutput += "      <field name='FIELD2' value='valueField2'/>";
        expectedOutput += "    </row>";
        expectedOutput += "    <row>";
        expectedOutput += "      <field name='FIELD1' value='field1'/>";
        expectedOutput += "      <field name='FIELD2' value='newValueOutput'/>";
        expectedOutput += "    </row>";
        expectedOutput += "  </table>";
        expectedOutput += "</dataset>";

        DatasetChecker.check(expectedOutput, scenario.getOutputDataSet());
    }


    private Matcher<Iterable<Row>> equalsTo(final String... expectedRows) {
        return new BaseMatcher<Iterable<Row>>() {
            public boolean matches(Object item) {
                List<Row> actualRows = (List<Row>)item;
                if (expectedRows.length != actualRows.size()) {
                    return false;
                }

                List<String> tempExpectedRows = new ArrayList<String>(Arrays.asList(expectedRows));
                for (Row actualRow : actualRows) {
                    String actualToString = actualRow.toString();
                    if (!tempExpectedRows.remove(actualToString)) {
                        return false;
                    }
                }

                return true;
            }


            public void describeTo(Description description) {
            }
        };
    }
}
