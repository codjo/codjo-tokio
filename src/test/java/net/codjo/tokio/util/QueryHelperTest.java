/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.util;
import net.codjo.database.common.api.SQLFieldList;
import net.codjo.database.common.impl.sqlfield.DefaultSQLFieldList;
import fakedb.FakeDriver;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
/**
 * -
 *
 * @author $Author: crego $
 * @version $Revision: 1.10 $
 * @todo : Cette classe est la copie de la version se trouvant dans lib (a effacer dés que Common aura été
 * découpé)
 */
public class QueryHelperTest extends TestCase {
    private Connection con;
    private QueryHelper queryHelper;


    public void test_doSelectAll() throws SQLException {
        // aucun commit
        Object[][] matrix = {
              {"PERIOD"},
              {"200008"},
              {"200009"}
        };
        FakeDriver.getDriver().pushResultSet(matrix, "select * from AP_PERIOD");
        ResultSet rs = queryHelper.doSelectAll();

        rs.next();
        assertEquals(rs.getString("PERIOD"), "200008");

        rs.next();
        assertEquals(rs.getString("PERIOD"), "200009");
    }


    public void test_doSelect() throws SQLException {
        Object[][] matrix = {
              {"PERIOD"},
              {"200012"}
        };
        FakeDriver.getDriver().pushResultSet(matrix,
                                             "select * from AP_PERIOD where PERIOD=200012");
        queryHelper.setSelectorValue("PERIOD", "200012");
        ResultSet rs = queryHelper.doSelect();
        assertTrue("Ligne existe", rs.next());
        assertEquals(rs.getString("PERIOD"), "200012");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildSelectQuery() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildSelectQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "select C, B, A from MA_TABLE where A=? and C=?");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildSelectQuery_Star() {
        List columnList = null;

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildSelectQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "select * from MA_TABLE where A=? and C=?");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildUpdateQuery() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildUpdateQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "update MA_TABLE set C=? , B=? , A=? where A=? and C=?");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildUpdateQueryWithWhereClause() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String whereClause = "D='TITI'";

        String query =
              QueryHelper.buildUpdateQueryWithWhereClause("MA_TABLE", columnList,
                                                          whereList, whereClause);
        assertEquals(query,
                     "update MA_TABLE set C=? , B=? , A=? where A=? and C=? and D='TITI'");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildInsertStatement() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        String query = QueryHelper.buildInsertQuery("MA_TABLE", columnList);
        assertEquals(query, "insert into MA_TABLE (C, B, A) values (?, ?, ?)");
    }


    /**
     * A unit test for JUnit
     *
     * @throws SQLException Description of Exception
     */
    public void test_build_NoSelector() throws SQLException {
        // Construction
        SQLFieldList is = new DefaultSQLFieldList();
        is.addStringField("PERIOD");
        queryHelper = new QueryHelper("AP_PERIOD", con, is);

        // Insert pour de rire
        con.setAutoCommit(false);

        queryHelper.setInsertValue("PERIOD", "BOBO");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "insert into AP_PERIOD (PERIOD) values (BOBO) select @@identity");
        queryHelper.doInsert();

        queryHelper.setInsertValue("PERIOD", "BOBO2");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "insert into AP_PERIOD (PERIOD) values (BOBO2) select @@identity");
        queryHelper.doInsert();

        con.rollback();
    }


    /**
     * DOCUMENT ME!
     *
     * @throws SQLException Description of Exception
     */
    public void test_doSelect_BadID() throws SQLException {
        Object[][] matrix = {
              {"PERIOD"}
        };
        FakeDriver.getDriver().pushResultSet(matrix,
                                             "select * from AP_PERIOD where PERIOD=xxxx");
        queryHelper.setSelectorValue("PERIOD", "xxxx");
        ResultSet rs = queryHelper.doSelect();
        assertFalse("Enregistrement inconnue", rs.next());
    }


    public void test_insert_update_delete() throws SQLException {
        String str = "Bobo's Period";

        // Insert
        queryHelper.setInsertValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "insert into AP_PERIOD (PERIOD) values (Bobo's Period) select @@identity");
        BigDecimal id = queryHelper.doInsert();
        assertEquals(1, id.intValue());

        // Update
        queryHelper.setSelectorValue("PERIOD", str);
        queryHelper.setInsertValue("PERIOD", "nouveau");
        FakeDriver.getDriver()
              .pushUpdateConstraint("update AP_PERIOD set PERIOD=nouveau where PERIOD=Bobo's Period");
        queryHelper.doUpdate();
        // Delete
        queryHelper.setSelectorValue("PERIOD", "nouveau");
        FakeDriver.getDriver().pushUpdateConstraint("delete from AP_PERIOD where PERIOD=nouveau");
        queryHelper.doDelete();
    }


    public void test_insert_select_delete() throws SQLException {
        String str = "Bobo's Period";

        // Insert
        queryHelper.setInsertValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "insert into AP_PERIOD (PERIOD) values (Bobo's Period) select @@identity");
        queryHelper.doInsert();
        // Select
        queryHelper.setSelectorValue("PERIOD", str);
        Object[][] matrix = {
              {"PERIOD"},
              {str}
        };
        FakeDriver.getDriver().pushResultSet(matrix,
                                             "select * from AP_PERIOD where PERIOD=Bobo's Period");
        ResultSet rs = queryHelper.doSelect();
        rs.next();
        assertEquals(rs.getString("PERIOD"), str);
        // Delete
        queryHelper.setSelectorValue("PERIOD", str);
        FakeDriver.getDriver().pushUpdateConstraint("delete from AP_PERIOD where PERIOD="
                                                    + str);
        queryHelper.doDelete();
        // Check
        queryHelper.setSelectorValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "select * from AP_PERIOD where PERIOD=Bobo's Period");
        rs = queryHelper.doSelect();
        assertFalse("L'enregistrement est efface", rs.next());
    }


    public void test_insert_rollBack() throws SQLException {
        String str = "Bobo's Period";

        con.setAutoCommit(false);

        queryHelper.setInsertValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "insert into AP_PERIOD (PERIOD) values (Bobo's Period) select @@identity");
        queryHelper.doInsert();

        con.rollback();

        // Check
        queryHelper.setSelectorValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "select * from AP_PERIOD where PERIOD=Bobo's Period");
        ResultSet rs = queryHelper.doSelect();
        assertFalse("L'enregistrement est efface", rs.next());
    }


    @Override
    protected void setUp() throws Exception {
        Class.forName("fakedb.FakeDriver");
        con = DriverManager.getConnection("jdbc:fakeDriver");

        SQLFieldList selectById = new DefaultSQLFieldList();
        selectById.addStringField("PERIOD");

        SQLFieldList insertFields = new DefaultSQLFieldList();
        insertFields.addStringField("PERIOD");

        queryHelper = new QueryHelper("AP_PERIOD", con, insertFields, selectById);
    }


    @Override
    protected void tearDown() throws SQLException {
        con.close();
    }
}
