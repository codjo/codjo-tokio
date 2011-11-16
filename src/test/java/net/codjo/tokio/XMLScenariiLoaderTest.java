/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import java.io.File;
import java.util.Iterator;
/**
 * Classe de test.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.17 $
 */
public class XMLScenariiLoaderTest extends XMLLoaderTestCase {
    private static final String SCENARII_DIR = "src/test/resources/test/scenarii/";


    public void test_constructor_by_URL() throws Exception {
        XMLScenariiLoader loader =
              load(XMLScenariiLoaderTest.class.getResource("/test/scenarii/ScenariiForTest.xml").toString());
        Scenario sc = loader.getScenario("ControleCoherence");
        checkControleCoherenceScenario(sc);
    }


    public void test_flatten_constructor_by_URL() throws Exception {
        XMLScenariiLoader loader =
              load(XMLScenariiLoaderTest.class.getResource("/test/scenarii/ScenariiForTestFlatten.xml").toString());
        Scenario sc = loader.getScenario("ControleCoherence");
        checkControleCoherenceScenario(sc);
    }


    public void test_getScenario() throws Exception {
        XMLScenariiLoader loader = load(new File(SCENARII_DIR + "ScenariiForTest.xml").toURL().toString());
        Scenario sc = loader.getScenario("ControleCoherence");
        checkControleCoherenceScenario(sc);
    }


    public void test_getScenarioWithGenerated() throws Exception {
        XMLScenariiLoader loader = load(new File(
              SCENARII_DIR + "ScenariiForTestGenerated.xml").toURL().toString());
        Scenario sc = loader.getScenario("ControleCoherence");
        assertEquals("ControleCoherence", sc.getName());
        assertNotNull(sc.getInputTable("AP_VL_QUARANTINE"));
        assertNotNull(sc.getOutputTable("AP_VL_QUARANTINE"));

        // Verifie Input
        assertTrue(sc.getInputTable("AP_VL_QUARANTINE").isIdentityInsert());
        Iterator rowsIn = sc.getInputTable("AP_VL_QUARANTINE").rows();
        Row irow = (Row)rowsIn.next();
        assertEquals("vl.coherence.ok", irow.getId());
        assertEquals("generateNumeric(4,0)", irow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals(5, irow.getFields().size());

        irow = (Row)rowsIn.next();
        assertEquals("vl.coherence.nok", irow.getId());
        assertEquals("0071", irow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals(null, irow.getFields().get("NULLFIELDFORCED").getValue());
        assertEquals(6, irow.getFields().size());

        assertFalse(rowsIn.hasNext());

        // Verifie Output
        assertTrue(!sc.getOutputTable("AP_VL_QUARANTINE").isIdentityInsert());
        Iterator rowsOut = sc.getOutputTable("AP_VL_QUARANTINE").rows();
        Row orow = (Row)rowsOut.next();
        assertEquals("vl.coherence.ok", orow.getRefId());
        assertEquals("generateNumeric(4,0)", orow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals(5, orow.getFields().size());

        orow = (Row)rowsOut.next();
        assertEquals("vl.coherence.nok", orow.getRefId());
        assertEquals("0071", orow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals("1", orow.getFields().get("ANOMALY").getValue());
        assertEquals(null, orow.getFields().get("NULLFIELD").getValue());
        assertEquals(null, orow.getFields().get("NULLFIELDFORCED").getValue());
        assertEquals(7, orow.getFields().size());

        assertTrue(!rowsOut.hasNext());
    }


    public void test_flatten_getScenario() throws Exception {
        XMLScenariiLoader loader = load(new File(
              SCENARII_DIR + "ScenariiForTestFlatten.xml").toURL().toString());
        Scenario sc = loader.getScenario("ControleCoherence");
        checkControleCoherenceScenario(sc);
    }


    public void test_getScenarioWithNoCoherenceForNullAttribute()
          throws Exception {
        try {
            XMLScenariiLoader loader =
                  load(new File(SCENARII_DIR + "ScenariiForTestNullAttributeToFalse.xml")
                        .toURL().toString());
            loader.getScenario("ControleNoCoherenceForNullAttribute");
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(
                  "La balise <field> avec l'attribut 'null' doit être à true ou l'attribut 'null' ne doit pas être présent.",
                  e.getMessage());
        }
    }


    public void test_flatten_getScenarWithNoCoherenceForNullAttr()
          throws Exception {
        try {
            XMLScenariiLoader loader =
                  load(new File(SCENARII_DIR + "ScenariiForTestNullAttributeToFalseFlatten.xml")
                        .toURL().toString());
            loader.getScenario("ControleNoCoherenceForNullAttribute");
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(
                  "La balise <field> avec l'attribut 'null' doit être à true ou l'attribut 'null' ne doit pas être présent.",
                  e.getMessage());
        }
    }


    public void test_getScenarioWithNoCoherenceForAttributes()
          throws Exception {
        try {
            XMLScenariiLoader loader =
                  load(new File(SCENARII_DIR + "ScenariiForTestNullAttributeToTrue.xml")
                        .toURL().toString());
            loader.getScenario("ControleNoCoherenceForAttributes");
            fail();
        }
        catch (RuntimeException e) {
            assertEquals("La balise <field> avec l'attribut 'null' ne doit pas contenir d'attribut 'value'.",
                         e.getMessage());
        }
    }


    public void test_flatten_getScenarWithNoCoherenceForAttributes()
          throws Exception {
        try {
            XMLScenariiLoader loader =
                  load(new File(SCENARII_DIR + "ScenariiForTestNullAttributeToTrueFlatten.xml")
                        .toURL().toString());
            loader.getScenario("ControleNoCoherenceForAttributes");
            fail();
        }
        catch (RuntimeException e) {
            assertEquals("La balise <field> avec l'attribut 'null' ne doit pas contenir d'attribut 'value'.",
                         e.getMessage());
        }
    }


    public void test_getScenario_oldDtd() throws Exception {
        XMLScenariiLoader loader =
              load(new File(SCENARII_DIR + "ScenariiOldDtdForTest.xml").toURL().toString());
        Scenario sc = loader.getScenario("ControleCoherence");
        checkControleCoherenceScenario(sc);
    }


    public void test_getScenario_unknown() throws Exception {
        XMLScenariiLoader loader =
              load(new File((SCENARII_DIR + "ScenariiForTest.xml")).toURL().toString());
        try {
            loader.getScenario("A");
            fail("Le scenario 'A' est inconnu");
        }
        catch (java.util.NoSuchElementException e) {
        }
    }


    public void test_getScenarioWithComparators()
          throws Exception {
        XMLScenariiLoader loader =
              load(new File((SCENARII_DIR + "ScenariiForTest.xml")).toURL().toString());
        Scenario sc = loader.getScenario("ControleComparator");
        checkControleCoherenceComparators(sc);
    }


    public void test_flatten_getScenarioWithComparators()
          throws Exception {
        XMLScenariiLoader loader =
              load(new File((SCENARII_DIR + "ScenariiForTestFlatten.xml")).toURL().toString());
        Scenario sc = loader.getScenario("ControleComparator");
        checkControleCoherenceComparators(sc);
    }


    public void test_autocomplete() throws Exception {
        XMLScenariiLoader loader = load(new File(SCENARII_DIR + "autoComplete.xml").toURL().toString());

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


    public void test_autoComplete_withInheritId() throws Exception {
        XMLScenariiLoader loader = load(new File(
              SCENARII_DIR + "autoComplete_withInheritId.xml").toURL().toString());

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


    public void test_checkPropertiesFileFormat() throws Exception {
        File corruptedFile = new File(SCENARII_DIR + "../properties/file.corrupted");
        assertFalse(XMLPropertiesLoader.checkFileFormat(corruptedFile));

        File goodFile = new File(SCENARII_DIR + "../properties/dummy.properties");
        assertTrue(XMLPropertiesLoader.checkFileFormat(goodFile));
    }


    private XMLScenariiLoader load(String uri) throws Exception {
        XMLScenariiLoader loader = new XMLScenariiLoader();
        loader.parse(loadDocument(uri), uri, null);
        return loader;
    }


    private void checkControleCoherenceScenario(Scenario sc) {
        assertEquals("ControleCoherence", sc.getName());
        assertNotNull(sc.getInputTable("AP_VL_QUARANTINE"));
        assertNotNull(sc.getOutputTable("AP_VL_QUARANTINE"));

        // Verifie Input
        assertTrue(sc.getInputTable("AP_VL_QUARANTINE").isIdentityInsert());
        Iterator rowsIn = sc.getInputTable("AP_VL_QUARANTINE").rows();
        Row irow = (Row)rowsIn.next();
        assertEquals("vl.coherence.ok", irow.getId());
        assertEquals("6969", irow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals(5, irow.getFields().size());

        irow = (Row)rowsIn.next();
        assertEquals("vl.coherence.nok", irow.getId());
        assertEquals("0071", irow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals(null, irow.getFields().get("NULLFIELDFORCED").getValue());
        assertEquals(6, irow.getFields().size());

        assertFalse(rowsIn.hasNext());

        // Verifie Output
        assertTrue(!sc.getOutputTable("AP_VL_QUARANTINE").isIdentityInsert());
        Iterator rowsOut = sc.getOutputTable("AP_VL_QUARANTINE").rows();
        Row orow = (Row)rowsOut.next();
        assertEquals("vl.coherence.ok", orow.getRefId());
        assertEquals("6969", orow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals(5, orow.getFields().size());

        orow = (Row)rowsOut.next();
        assertEquals("vl.coherence.nok", orow.getRefId());
        assertEquals("0071", orow.getFields().get("CODE_SICOVAM").getValue());
        assertEquals("1", orow.getFields().get("ANOMALY").getValue());
        assertEquals(null, orow.getFields().get("NULLFIELD").getValue());
        assertEquals(null, orow.getFields().get("NULLFIELDFORCED").getValue());
        assertEquals(7, orow.getFields().size());

        assertTrue(!rowsOut.hasNext());
    }


    private void checkControleCoherenceComparators(Scenario sc) {
        assertEquals(7, sc.getOutputDataSet().getComparatorsNumber());
        assertEquals("1000", sc.getOutputDataSet().getComparator("CREATION_DATE").getParam());
        assertEquals("2000", sc.getOutputDataSet().getComparator("UPDATE_DATE").getParam());
        assertEquals("0.01", sc.getOutputDataSet().getComparator("AMOUNT").getParam());
        assertNull(sc.getOutputDataSet().getComparator("COMMENT").getParam());
        assertEquals("class net.codjo.tokio.model.ComparatorManager$DefaultComparator",
                     sc.getOutputDataSet().getComparator("TOTOLITOTO").getClass().toString());
        assertEquals("class net.codjo.tokio.model.BeforeLapsComparator",
                     sc.getOutputDataSet().getComparator("BEFORE_DATE").getClass().toString());
        assertEquals("class net.codjo.tokio.model.AfterLapsComparator",
                     sc.getOutputDataSet().getComparator("AFTER_DATE").getClass().toString());

        assertEquals(2, sc.getOutputDataSet().getTableCount());
    }
}
