/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.test.common.DateUtil;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Logger;
/**
 * Construction d'un scenario
 *
 * @author $Author: crego $
 * @version $Revision: 1.9 $
 */
public class ScenarioBuilder {
    private static final Logger LOG = Logger.getLogger(ScenarioBuilder.class);
    private Scenario scenario = new Scenario("Automatic", "création automatique");
    private Map tableLoadWhereClause = new HashMap();
    private String[] tableToLoad = {};
    private String today;


    public ScenarioBuilder() {
    }


    public static void printXml(PrintWriter writer, ScenarioList scs)
          throws ParserConfigurationException, TransformerException {
        XMLScenariiRecorder recorder = new XMLScenariiRecorder("AUTOMATIC", scs);
        recorder.printXml(writer);
    }


    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }


    public void setTableLoadWhereClause(Map tableLoadWhereClause) {
        this.tableLoadWhereClause = tableLoadWhereClause;
    }


    public void setTableToLoad(String[] tableToLoad) {
        this.tableToLoad = tableToLoad;
    }


    public Scenario getScenario() {
        return scenario;
    }


    public Map getTableLoadWhereClause() {
        return tableLoadWhereClause;
    }


    public String[] getTableToLoad() {
        return tableToLoad;
    }


    public void loadInputs(Connection con) throws SQLException {
        today = DateUtil.createDateString(new java.util.Date());
        scenario.getInputDataSet().clear();
        for (String aTableToLoad : tableToLoad) {
            loadTable(con, aTableToLoad, scenario.getInputDataSet());
        }
    }


    public void loadOutputs(Connection con) throws SQLException {
        scenario.getOutputDataSet().clear();
        for (String aTableToLoad : tableToLoad) {
            loadTable(con, aTableToLoad, scenario.getOutputDataSet());
        }
    }


    public void printXml(PrintWriter writer)
          throws ParserConfigurationException, TransformerException {
        printXml(writer, new ScenarioList(getScenario()));
    }


    private String buildWhereClause(final String tableName) {
        String whereClause = (String)getTableLoadWhereClause().get(tableName);
        if ("".equals(whereClause) || whereClause == null) {
            whereClause = "";
        }
        else {
            whereClause = "where " + whereClause;
        }

        return whereClause;
    }


    private String convert(Object objectValue) {
        String value = objectValue.toString();
        if (value.startsWith(today)) {
            return VariableName.TODAY;
        }
        return value;
    }


    private void loadTable(Connection con, String tableName, DataSet dataSet)
          throws SQLException {
        dataSet.buildTable(tableName);
        Statement stmt = con.createStatement();
        try {
            String whereClause = buildWhereClause(tableName);
            String selectQuery = "select * from " + tableName + " " + whereClause;

            LOG.info("Chargement de la table " + tableName + " avec la requete : "
                     + selectQuery);
            ResultSet rs = stmt.executeQuery(selectQuery);
            ResultSetMetaData rsmd = rs.getMetaData();

            // Get Header
            int colmumnCount = rsmd.getColumnCount();
            String[] columns = new String[colmumnCount];
            for (int i = 0; i < colmumnCount; i++) {
                columns[i] = rsmd.getColumnName(i + 1);
            }

            // Get Content
            while (rs.next()) {
                FieldMap row = new FieldMap();
                for (int i = 0; i < colmumnCount; i++) {
                    Object val = rs.getObject(i + 1);
                    if (val != null) {
                        row.putField(columns[i], convert(val), null);
                    }
                }
                dataSet.addRow(tableName, new Row(row));
            }
        }
        finally {
            stmt.close();
        }
    }
}
