/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import java.io.StringWriter;
import junit.framework.TestCase;
import net.codjo.tokio.model.ComparatorConverter;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import net.codjo.tokio.util.Util;
/**
 * Description of the Class
 *
 * @author $Author: catteao $
 * @version $Revision: 1.14 $
 */
public class XMLScenariiRecorderTest extends TestCase {

    public void test_printXML() throws Exception {
        Scenario sc = new Scenario("bobo", "commentaire");
        XMLScenariiRecorder scenarii =
              new XMLScenariiRecorder("TestTU", new ScenarioList(new Scenario[]{sc}));

        StringWriter writer = new StringWriter();
        scenarii.printXml(writer);

        Util.compare(Util.flatten("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                                  + "<!DOCTYPE Scenarii SYSTEM \"scenarii.dtd\">"
                                  + "<Scenarii name=\"TestTU\">" + "<Scenario id=\"bobo\">"
                                  + "<comment>commentaire</comment>" + "<input/><etalon/>" + "</Scenario>"
                                  + "</Scenarii>"), Util.flatten(writer.toString()));
    }


    public void test_printXML_comparator() throws Exception {
        Scenario sc = new Scenario("bobo", "commentaire");
        sc.getOutputDataSet().addComparator("COLONNE",
                                            ComparatorConverter.newComparator("0.01"));
        XMLScenariiRecorder scenarii =
              new XMLScenariiRecorder("TestTU", new ScenarioList(new Scenario[]{sc}));

        StringWriter writer = new StringWriter();
        scenarii.printXml(writer);

        Util.compare(Util.flatten("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                                  + "<!DOCTYPE Scenarii SYSTEM \"scenarii.dtd\">"
                                  + "<Scenarii name=\"TestTU\">" + "<Scenario id=\"bobo\">"
                                  + "<comment>commentaire</comment>" + "<input/>" + "<etalon>"
                                  + "<comparators>" + "<comparator field=\"COLONNE\" precision=\"0.01\"/>"
                                  + "</comparators>" + "</etalon>" + "</Scenario>" + "</Scenarii>"),
                     Util.flatten(writer.toString()));
    }


    public void test_printXML_2scenarii() throws Exception {
        Scenario sc1 = new Scenario("bobo", null);
        Scenario sc2 = new Scenario("bobo2", "comment2");
        XMLScenariiRecorder scenarii =
              new XMLScenariiRecorder("TestTU", new ScenarioList(new Scenario[]{sc1, sc2}));

        StringWriter writer = new StringWriter();
        scenarii.printXml(writer);

        Util.compare(Util.flatten("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                                  + "<!DOCTYPE Scenarii SYSTEM \"scenarii.dtd\">"
                                  + "<Scenarii name=\"TestTU\">"
                                  + "<Scenario id=\"bobo\"><input/><etalon/></Scenario>"
                                  + "<Scenario id=\"bobo2\"><comment>comment2</comment><input/><etalon/></Scenario>"
                                  + "</Scenarii>"), Util.flatten(writer.toString()));
    }


    public void test_printXML_scenario_table_row()
          throws Exception {
        Scenario sc = new Scenario("bobo", null);
        Row row = new Row(null, null, "une ligne", false, new FieldMap());
        row.setFieldValue("CODE_SICOVAM", "יטא", "false", null);
        sc.addInputRow("TABLE", row);
        XMLScenariiRecorder scenarii =
              new XMLScenariiRecorder("TestTU", new ScenarioList(new Scenario[]{sc}));

        StringWriter writer = new StringWriter();
        scenarii.printXml(writer);

        Util.compare(Util.flatten("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                                  + "<!DOCTYPE Scenarii SYSTEM \"scenarii.dtd\">"
                                  + "<Scenarii name=\"TestTU\">" + "<Scenario id=\"bobo\">" + "<input>"
                                  + "<table name=\"TABLE\" nullFirst=\"on\">" + "<row comment=\"une ligne\">"
                                  + "<field name=\"CODE_SICOVAM\" value=\"יטא\"/>" + "</row>" + "</table>"
                                  + "</input>" + "<etalon/>" + "</Scenario>" + "</Scenarii>"),
                     Util.flatten(writer.toString()));
    }
}
