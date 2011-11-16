/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.DatabaseQueryHelper;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.confidential.DatabaseTranscoder;
import static net.codjo.database.common.api.structure.SqlField.field;
import static net.codjo.database.common.api.structure.SqlField.fieldName;
import static net.codjo.database.common.api.structure.SqlTable.table;
import static net.codjo.database.common.api.structure.SqlTable.temporaryTable;
import net.codjo.test.common.DateUtil;
import net.codjo.test.common.LogString;
import net.codjo.tokio.foreignkeys.ForeignKeyMetadata;
import net.codjo.tokio.model.Comparator;
import net.codjo.tokio.model.ComparatorConverter;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.tableorder.TableOrder;
import net.codjo.tokio.tableorder.TableOrderBuilder;
import fakedb.FakeDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import static java.sql.Types.BIT;
import static java.sql.Types.DATE;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.REAL;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import static java.util.GregorianCalendar.HOUR_OF_DAY;
import static java.util.GregorianCalendar.MONTH;
import static java.util.GregorianCalendar.YEAR;
import java.util.List;
import java.util.StringTokenizer;
import org.junit.After;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
public class JDBCScenarioTest {
    private Row rowA;
    private Scenario scenario;
    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    private static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private Connection connection;
    private final DatabaseFactory databaseFactory = new DatabaseFactory();
    private final DatabaseHelper databaseHelper = databaseFactory.createDatabaseHelper();
    private final DatabaseQueryHelper databaseQueryHelper = databaseFactory.getDatabaseQueryHelper();
    private final DatabaseTranscoder transcoder = databaseFactory.createDatabaseTranscoder();
    private final JdbcFixture jdbcFixture = JdbcFixture.newFixture();
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);
    private static final DateFormat DATETIME_FORMAT = DateFormat
          .getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private String datetime = transcoder.transcodeSqlFieldType("datetime");


    @Before
    public void setUp() throws Exception {
        Class.forName("fakedb.FakeDriver");
        jdbcFixture.doSetUp();
        jdbcFixture.create(temporaryTable("AP_VL"), "COL_DATE date null,"
                                                    + " COL_DATE_HEURE " + datetime + " null,"
                                                    + " COL_NUMBER numeric(3,2) null,"
                                                    + " COL_STR varchar(255) null");

        scenario = new Scenario("test", null);
        rowA = createRow("rowA",
                         "COL_STR=une string;"
                         + "COL_NUMBER=1.23;"
                         + "COL_DATE=2002-12-20;"
                         + "COL_DATE_HEURE=2002-12-25 12:30:00");
        scenario.addInputRow("AP_VL", rowA);
        scenario.addOutputRow("AP_VL", new Row("rowB", "rowA", new FieldMap()));
        scenario.getInputTable("AP_VL").setTemporary(true);
        connection = DriverManager.getConnection("jdbc:fakeDriver");
    }


    @After
    public void tearDown() throws Exception {
        jdbcFixture.doTearDown();
    }


    @Test
    public void test_convertValue() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);

        assertEquals(Timestamp.valueOf("2002-12-25 12:30:00"),
                     jdbcScenario.convertValue("2002-12-25 12:30:00", TIMESTAMP));
        assertEquals(java.sql.Date.valueOf("2002-12-20"),
                     jdbcScenario.convertValue("2002-12-20", DATE));
        assertEquals(new java.math.BigDecimal("5.25"),
                     jdbcScenario.convertValue("5.25", NUMERIC));
        assertEquals(Double.valueOf("3.14159"),
                     jdbcScenario.convertValue("3.14159", DOUBLE));
        assertEquals(Double.valueOf("3.14159"),
                     jdbcScenario.convertValue("3.14159", REAL));
        assertEquals(Boolean.TRUE, jdbcScenario.convertValue("true", BIT));
        assertEquals(Boolean.FALSE, jdbcScenario.convertValue("false", BIT));
        assertEquals("bobo", jdbcScenario.convertValue("bobo", VARCHAR));
        assertEquals(DateUtil.createDateString(new java.util.Date()),
                     jdbcScenario.convertValue("TODAY", DATE).toString());
        assertTrue(new java.util.Date().after(
              (java.sql.Date)jdbcScenario.convertValue("TODAY-86400000", DATE)));
        assertTrue(new java.util.Date().before(
              (java.sql.Date)jdbcScenario.convertValue("TODAY+86400000", DATE)));

        assertNotNull(jdbcScenario.convertValue("null", VARCHAR));

        java.util.Date today = new java.util.Date();
        Period todayPeriod;

        todayPeriod = new Period(today);
        assertEquals(todayPeriod.toString(),
                     jdbcScenario.convertValue("CURRENT_PERIOD", VARCHAR));

        todayPeriod = new Period(today);
        todayPeriod.subMonth(1);
        assertEquals(todayPeriod.toString(),
                     jdbcScenario.convertValue("CURRENT_PERIOD-1", VARCHAR));

        todayPeriod = new Period(today);
        todayPeriod.addMonth(1);
        assertEquals(todayPeriod.toString(),
                     jdbcScenario.convertValue("CURRENT_PERIOD+1", VARCHAR));
    }


    @Test
    public void test_convertValue_Date() {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);

        String expectedDatePlus1Hour = getRelativeDate(new Date(), HOUR_OF_DAY, 1, DD_MM_YYYY_HH_MM_SS);
        String actualDatePlus1Hour = formatDateTime((java.sql.Date)jdbcScenario.convertValue("TODAY+1H", DATE));
        assertEquals(expectedDatePlus1Hour, actualDatePlus1Hour);

        String expectedDatePlus1Day = getRelativeDate(new Date(), GregorianCalendar.DATE, 1, DD_MM_YYYY);
        String actualDatePlus1Day = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY+1D", DATE));
        assertEquals(expectedDatePlus1Day, actualDatePlus1Day);

        String expectedDatePlus10Month = getRelativeDate(new Date(), MONTH, 10, DD_MM_YYYY);
        String actualDatePlus10Month = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY+10M", DATE));
        assertEquals(expectedDatePlus10Month, actualDatePlus10Month);

        String expectedDatePlus100Year = getRelativeDate(new Date(), YEAR, 100, DD_MM_YYYY);
        String actualDatePlus100Year = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY+100Y", DATE));
        assertEquals(expectedDatePlus100Year, actualDatePlus100Year);

        String expectedDateMoins13Hour = getRelativeDate(new Date(), HOUR_OF_DAY, -13, DD_MM_YYYY_HH_MM_SS);
        String actualDateMoins13Hour = formatDateTime((java.sql.Date)jdbcScenario.convertValue("TODAY-13H", DATE));
        assertEquals(expectedDateMoins13Hour, actualDateMoins13Hour);

        String expectedDateMoins11Day = getRelativeDate(new Date(), GregorianCalendar.DATE, -11, DD_MM_YYYY);
        String actualDateMoins11Day = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY-11D", DATE));
        assertEquals(expectedDateMoins11Day, actualDateMoins11Day);

        String expectedDateMoins11Month = getRelativeDate(new Date(), MONTH, -11, DD_MM_YYYY);
        String actualDateMoins11Month = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY-11M", DATE));
        assertEquals(expectedDateMoins11Month, actualDateMoins11Month);

        String expectedDateMoins11Year = getRelativeDate(new Date(), YEAR, -11, DD_MM_YYYY);
        String actualDateMoins11Year = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY-11Y", DATE));
        assertEquals(expectedDateMoins11Year, actualDateMoins11Year);
    }


    @Test
    public void test_convertValue_Date_WithoutHourMinuteSecond() {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);

        String expectedDate = getRelativeDate(new Date(), GregorianCalendar.DATE, 0, DD_MM_YYYY);
        String actualDate = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY_DATE", Types.DATE));
        assertEquals(expectedDate, actualDate);

        String expectedDatePlus1Day = getRelativeDate(new Date(), GregorianCalendar.DATE, 1, DD_MM_YYYY);
        String actualDatePlus1Day = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY_DATE+1D", Types.DATE));
        assertEquals(expectedDatePlus1Day, actualDatePlus1Day);

        String expectedDatePlus10Month = getRelativeDate(new Date(), MONTH, 10, DD_MM_YYYY);
        String actualDatePlus10Month = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY_DATE+10M", Types.DATE));
        assertEquals(expectedDatePlus10Month, actualDatePlus10Month);

        String expectedDatePlus100Year = getRelativeDate(new Date(), YEAR, 100, DD_MM_YYYY);
        String actualDatePlus100Year = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY_DATE+100Y", Types.DATE));
        assertEquals(expectedDatePlus100Year, actualDatePlus100Year);

        String expectedDateMoins11Day = getRelativeDate(new Date(), GregorianCalendar.DATE, -11, DD_MM_YYYY);
        String actualDateMoins11Day = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY_DATE-11D", Types.DATE));
        assertEquals(expectedDateMoins11Day, actualDateMoins11Day);

        String expectedDateMoins11Month = getRelativeDate(new Date(), MONTH, -11, DD_MM_YYYY);
        String actualDateMoins11Month = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY_DATE-11M", Types.DATE));
        assertEquals(expectedDateMoins11Month, actualDateMoins11Month);

        String expectedDateMoins11Year = getRelativeDate(new Date(), YEAR, -11, DD_MM_YYYY);
        String actualDateMoins11Year = formatDate((java.sql.Date)jdbcScenario.convertValue("TODAY_DATE-11Y", Types.DATE));
        assertEquals(expectedDateMoins11Year, actualDateMoins11Year);
    }


    @Test
    public void test_convertValue_Timestamp() {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);

        String curentDatePlus1Hour = getRelativeDate(new Date(), HOUR_OF_DAY, 1, YYYY_MM_DD_HH_MM_SS);
        assertEquals(curentDatePlus1Hour,
                     jdbcScenario.convertValue("TODAY+1H", TIMESTAMP).toString().substring(0, 19));

        String curentDatePlus1Day = getRelativeDate(new Date(), GregorianCalendar.DATE, 1, YYYY_MM_DD);
        assertEquals(curentDatePlus1Day,
                     jdbcScenario.convertValue("TODAY+1D", TIMESTAMP).toString().substring(0, 10));

        String curentDatePlus10Month = getRelativeDate(new Date(), MONTH, 10, YYYY_MM_DD);
        assertEquals(curentDatePlus10Month,
                     jdbcScenario.convertValue("TODAY+10M", TIMESTAMP).toString().substring(0, 10));
        String curentDatePlus100Year = getRelativeDate(new Date(), YEAR, 100, YYYY_MM_DD);
        assertEquals(curentDatePlus100Year,
                     jdbcScenario.convertValue("TODAY+100Y", TIMESTAMP).toString().substring(0, 10));

        String curentDateMoins13Hour = getRelativeDate(new Date(), HOUR_OF_DAY, -13, YYYY_MM_DD_HH_MM_SS);
        assertEquals(curentDateMoins13Hour,
                     jdbcScenario.convertValue("TODAY-13H", TIMESTAMP).toString().substring(0, 19));

        String curentDateMoins11Day = getRelativeDate(new Date(), GregorianCalendar.DATE, -11, YYYY_MM_DD);
        assertEquals(curentDateMoins11Day,
                     jdbcScenario.convertValue("TODAY-11D", TIMESTAMP).toString().substring(0, 10));

        String curentDateMoins11Month = getRelativeDate(new Date(), MONTH, -11, YYYY_MM_DD);
        assertEquals(curentDateMoins11Month,
                     jdbcScenario.convertValue("TODAY-11M", TIMESTAMP).toString().substring(0, 10));

        String curentDateMoins11Year = getRelativeDate(new Date(), YEAR, -11, YYYY_MM_DD);
        assertEquals(curentDateMoins11Year,
                     jdbcScenario.convertValue("TODAY-11Y", TIMESTAMP).toString().substring(0, 10));
    }


    @Test
    public void test_convertValue_Timestamp_WithoutTime() {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);

        String curentDate = getRelativeDate(new Date(), GregorianCalendar.DATE, 0, YYYY_MM_DD) + " 00:00:00.0";
        assertEquals(curentDate,
                     jdbcScenario.convertValue("TODAY_DATE", TIMESTAMP).toString());

        String curentDatePlus1Day = getRelativeDate(new Date(), GregorianCalendar.DATE, 1, YYYY_MM_DD) + " 00:00:00.0";
        assertEquals(curentDatePlus1Day,
                     jdbcScenario.convertValue("TODAY_DATE+1D", TIMESTAMP).toString());

        String curentDatePlus10Month = getRelativeDate(new Date(), MONTH, 10, YYYY_MM_DD) + " 00:00:00.0";
        assertEquals(curentDatePlus10Month,
                     jdbcScenario.convertValue("TODAY_DATE+10M", TIMESTAMP).toString());

        String curentDatePlus100Year = getRelativeDate(new Date(), YEAR, 100, YYYY_MM_DD) + " 00:00:00.0";
        assertEquals(curentDatePlus100Year,
                     jdbcScenario.convertValue("TODAY_DATE+100Y", TIMESTAMP).toString());

        String curentDateMoins11Day = getRelativeDate(new Date(), GregorianCalendar.DATE, -11, YYYY_MM_DD)
                                      + " 00:00:00.0";
        assertEquals(curentDateMoins11Day,
                     jdbcScenario.convertValue("TODAY_DATE-11D", TIMESTAMP).toString());

        String curentDateMoins11Month = getRelativeDate(new Date(), MONTH, -11, YYYY_MM_DD) + " 00:00:00.0";
        assertEquals(curentDateMoins11Month,
                     jdbcScenario.convertValue("TODAY_DATE-11M", TIMESTAMP).toString());

        String curentDateMoins11Year = getRelativeDate(new Date(), YEAR, -11, YYYY_MM_DD) + " 00:00:00.0";
        assertEquals(curentDateMoins11Year,
                     jdbcScenario.convertValue("TODAY_DATE-11Y", TIMESTAMP).toString());
    }


    @Test
    public void test_insertInputDb() throws Exception {
        new JDBCScenario(scenario).insertInputInDb(jdbcFixture.getConnection());

        String select = databaseQueryHelper.buildSelectQuery(temporaryTable("AP_VL"),
                                                             "COL_DATE",
                                                             "COL_DATE_HEURE",
                                                             "COL_NUMBER",
                                                             "COL_STR");
        ResultSet resultSet = jdbcFixture.executeQuery(select);

        assertTrue(resultSet.next());
        assertDate("20/12/2002", resultSet, 1);
        assertDatetime("25/12/2002 12:30:00", resultSet, 2);
        assertEquals("1.23", resultSet.getString(3));
        assertEquals("une string", resultSet.getString(4));
    }


    @Test
    public void test_insertInputDb_withForeignKey() throws Exception {
        jdbcFixture.create(temporaryTable("AP_VL_FATHER"), "COL_DATE date null,"
                                                           + " COL_DATE_HEURE " + datetime + " null,"
                                                           + " COL_NUMBER numeric(3,2) null,"
                                                           + " COL_STR varchar(255) null");

        scenario.addInputRow("AP_VL_FATHER", new Row(null, "rowA", new FieldMap()));
        scenario.getInputTable("AP_VL_FATHER").setTemporary(true);
        final TableOrder tableOrder = new TableOrder() {
            public List<String> buildTablesDeleteOrder() {
                return Arrays.asList("AP_VL", "AP_VL_FATHER");
            }


            public List<String> buildTablesInsertOrder() {
                return Arrays.asList("AP_VL_FATHER", "AP_VL");
            }
        };
        TableOrderBuilder tableOrderBuilder = new TableOrderBuilder() {
            public TableOrder get(Connection connection, DataSet dataSet) {
                return tableOrder;
            }
        };
        new JDBCScenario(scenario, tableOrderBuilder).insertInputInDb(jdbcFixture.getConnection());

        String select = databaseQueryHelper.buildSelectQuery(temporaryTable("AP_VL"), "COL_STR");
        ResultSet resultSet = jdbcFixture.executeQuery(select);
        assertTrue(resultSet.next());
        assertEquals("une string", resultSet.getString(1));

        select = databaseQueryHelper.buildSelectQuery(temporaryTable("AP_VL_FATHER"), "COL_STR");
        resultSet = jdbcFixture.executeQuery(select);
        assertTrue(resultSet.next());
        assertEquals("une string", resultSet.getString(1));
    }


    @Test
    public void test_insertInputDb_withForeignKey_noTemporaryTable() throws Exception {
        jdbcFixture.create(table("AP_VL_BIS"), "COL_DATE date null,"
                                               + " COL_DATE_HEURE " + datetime + " null,"
                                               + " COL_NUMBER numeric(3,2) null,"
                                               + " COL_STR varchar(255) null");

        jdbcFixture.create(table("AP_VL_BIS_FATHER"), "COL_DATE date null,"
                                                      + " COL_DATE_HEURE " + datetime + " null,"
                                                      + " COL_NUMBER numeric(3,2) null,"
                                                      + " COL_STR varchar(255) null");

        scenario.addInputRow("AP_VL_BIS", new Row("rowBis", "rowA", new FieldMap()));
        scenario.addInputRow("AP_VL_BIS_FATHER", new Row(null, "rowBis", new FieldMap()));
        final TableOrder tableOrder = new TableOrder() {
            public List<String> buildTablesDeleteOrder() {
                return Arrays.asList("AP_VL", "AP_VL_BIS", "AP_VL_BIS_FATHER");
            }


            public List<String> buildTablesInsertOrder() {
                return Arrays.asList("AP_VL_BIS_FATHER", "AP_VL_BIS", "AP_VL");
            }
        };
        TableOrderBuilder tableOrderBuilder = new TableOrderBuilder() {
            public TableOrder get(Connection connection, DataSet dataSet) {
                return tableOrder;
            }
        };
        new JDBCScenario(scenario, tableOrderBuilder).insertInputInDb(jdbcFixture.getConnection());

        String select = databaseQueryHelper.buildSelectQuery(table("AP_VL_BIS"), "COL_STR");
        ResultSet resultSet = jdbcFixture.executeQuery(select);
        assertTrue(resultSet.next());
        assertEquals("une string", resultSet.getString(1));

        select = databaseQueryHelper.buildSelectQuery(table("AP_VL_BIS_FATHER"), "COL_STR");
        resultSet = jdbcFixture.executeQuery(select);
        assertTrue(resultSet.next());
        assertEquals("une string", resultSet.getString(1));
    }


    @Test
    public void test_insertInputDb_foreignKey_notEmpty() throws Exception {
        jdbcFixture.create(table("AP_VL_BIS"), "COL_DATE date null,"
                                               + " COL_DATE_HEURE " + datetime + " null,"
                                               + " COL_NUMBER numeric(3,2) null,"
                                               + " COL_STR varchar(255) null");
        jdbcFixture.create(table("AP_VL_BIS_FATHER"), "COL_DATE date null,"
                                                      + " COL_DATE_HEURE " + datetime + " null,"
                                                      + " COL_NUMBER numeric(3,2) null,"
                                                      + " COL_STR varchar(255) null");

        String insert;
        insert = databaseQueryHelper.buildInsertQuery(table("AP_VL_BIS_FATHER"),
                                                      field("COL_STR", "uniqueValue1"));
        jdbcFixture.executeUpdate(insert);
        insert = databaseQueryHelper.buildInsertQuery(table("AP_VL_BIS_FATHER"),
                                                      field("COL_STR", "uniqueValue2"));
        jdbcFixture.executeUpdate(insert);
        insert = databaseQueryHelper
              .buildInsertQuery(table("AP_VL_BIS"), field("COL_STR", "uniqueValue1"));
        jdbcFixture.executeUpdate(insert);

        scenario.addInputRow("AP_VL_BIS", new Row("rowBis", "rowA", new FieldMap()));
        scenario.addInputRow("AP_VL_BIS_FATHER", new Row(null, "rowBis", new FieldMap()));

        final TableOrder tableOrder = new TableOrder() {
            public List<String> buildTablesDeleteOrder() {
                return Arrays.asList("AP_VL", "AP_VL_BIS", "AP_VL_BIS_FATHER");
            }


            public List<String> buildTablesInsertOrder() {
                return Arrays.asList("AP_VL_BIS_FATHER", "AP_VL_BIS", "AP_VL");
            }
        };
        TableOrderBuilder tableOrderBuilder = new TableOrderBuilder() {
            public TableOrder get(Connection connection, DataSet dataSet) {
                return tableOrder;
            }
        };
        new JDBCScenario(scenario, tableOrderBuilder).insertInputInDb(jdbcFixture.getConnection());

        String select = databaseQueryHelper.buildSelectQuery(table("AP_VL_BIS"), "COL_STR");
        ResultSet resultSet = jdbcFixture.executeQuery(select);
        assertTrue(resultSet.next());
        assertEquals("une string", resultSet.getString(1));

        select = databaseQueryHelper.buildSelectQuery(table("AP_VL_BIS_FATHER"), "COL_STR");
        resultSet = jdbcFixture
              .executeQuery(select);
        assertTrue(resultSet.next());
        assertEquals("une string", resultSet.getString(1));
    }


    @Test
    public void test_insertInputDb_nullCase() throws Exception {
        scenario.getInputTable("AP_VL").getRow(0).setFieldValue("COL_DATE", null, "true", null);
        new JDBCScenario(scenario).insertInputInDb(jdbcFixture.getConnection());

        String select = databaseQueryHelper.buildSelectQuery(temporaryTable("AP_VL"),
                                                             "COL_DATE",
                                                             "COL_DATE_HEURE",
                                                             "COL_NUMBER",
                                                             "COL_STR");
        ResultSet resultSet = jdbcFixture.executeQuery(select);
        assertTrue(resultSet.next());
        assertEquals(null, resultSet.getDate(1));
        assertDatetime("25/12/2002 12:30:00", resultSet, 2);
        assertEquals("1.23", resultSet.getString(3));
        assertEquals("une string", resultSet.getString(4));
    }


    @Test
    public void test_insertInputDb_noDelete() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());
        jdbcScenario.setDeleteBeforeInsert(false);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        String select = databaseQueryHelper.buildSelectQuery(temporaryTable("AP_VL"),
                                                             "COL_DATE", "COL_DATE_HEURE", "COL_NUMBER",
                                                             "COL_STR");
        ResultSet resultSet = jdbcFixture.executeQuery(select);

        assertTrue(resultSet.next());
        assertTrue(resultSet.next());
        assertDate("20/12/2002", resultSet, 1);
        assertDatetime("25/12/2002 12:30:00", resultSet, 2);
        assertEquals("1.23", resultSet.getString(3));
        assertEquals("une string", resultSet.getString(4));
    }


    @Test
    public void test_insertInputDb_identityInsert() throws Exception {
        if (databaseHelper.isIdentityInsertAllowed()) {
            jdbcFixture.create(table("TOKIO_TABLE"), "COL_DATE date null,"
                                                     + " COL_DATE_HEURE " + datetime + " null,"
                                                     + " COL_NUMBER numeric(3,2) null,"
                                                     + " COL_STR varchar(255) null,"
                                                     + " COL_IDENTITY numeric(3) identity not null");
//            jdbcFixture.advanced().executeAddColumn(
//                  table("AP_VL"),
//                  fieldDefinition("COL_IDENTITY", "numeric(3) identity not null"));

            scenario = new Scenario("test", null);
            rowA = createRow("rowA",
                             "COL_STR=une string;"
                             + "COL_NUMBER=1.23;"
                             + "COL_DATE=2002-12-20;"
                             + "COL_DATE_HEURE=2002-12-25 12:30:00");
            scenario.addInputRow("TOKIO_TABLE", rowA);
            scenario.getInputDataSet().getRow("rowA").setFieldValue("COL_IDENTITY", "1", null, null);
            try {
                new JDBCScenario(scenario).insertInputInDb(jdbcFixture.getConnection());
                fail();
            }
            catch (SQLException e) {
                ;
            }
        }
    }


    @Test
    public void test_insertInputDb_identityInsert_override() throws Exception {
        if (databaseHelper.isIdentityInsertAllowed()) {
            jdbcFixture.create(table("TOKIO_TABLE"), "COL_DATE date null,"
                                                     + " COL_DATE_HEURE " + datetime + " null,"
                                                     + " COL_NUMBER numeric(3,2) null,"
                                                     + " COL_STR varchar(255) null,"
                                                     + " COL_IDENTITY numeric(3) identity not null");

            scenario = new Scenario("test", null);
            rowA = createRow("rowA",
                             "COL_STR=une string;"
                             + "COL_NUMBER=1.23;"
                             + "COL_DATE=2002-12-20;"
                             + "COL_DATE_HEURE=2002-12-25 12:30:00");
            scenario.addInputRow("TOKIO_TABLE", rowA);
            scenario.getInputDataSet().getRow("rowA").setFieldValue("COL_IDENTITY", "1", null, null);
            JDBCScenario jdbcScenario = new JDBCScenario(scenario);
            jdbcScenario.setInputTableIdentityInsert("TOKIO_TABLE", true);
            jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

            String select = databaseQueryHelper.buildSelectQuery(table("TOKIO_TABLE"),
                                                                 "COL_DATE", "COL_DATE_HEURE", "COL_NUMBER",
                                                                 "COL_STR", "COL_IDENTITY");
            ResultSet resultSet = jdbcFixture.executeQuery(select);
            assertTrue(resultSet.next());
            assertEquals("2002-12-20", resultSet.getDate(1).toString());
            assertEquals("2002-12-25 12:30:00.0", resultSet.getTimestamp(2).toString());
            assertEquals("1.23", resultSet.getString(3));
            assertEquals("une string", resultSet.getString(4));
            assertEquals("1", resultSet.getString(5));
        }
    }


    @Test
    public void test_sortRows() throws Exception {
        FieldMap fieldsC = new FieldMap();
        fieldsC.putField("COL_STR", "une string", null);
        fieldsC.putField("COL_NUMBER", "1.24", null);
        fieldsC.putField("COL_DATE", "2002-12-20", null);
        fieldsC.putField("COL_DATE_HEURE", "2002-12-25 12:30:00", null);
        Row rowC = new Row(null, null, fieldsC);
        scenario.addInputRow("AP_VL", rowC);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(rowA);
        expected.add(rowC);

        assertEquals(expected, jdbcsc.sortRows(scenario.getInputTable("AP_VL").getRows(),
                                               "COL_NUMBER, COL_DATE",
                                               jdbcFixture.getConnection(),
                                               "AP_VL",
                                               true));
    }


    @Test
    public void test_sortRows_inheritence() throws Exception {
        jdbcFixture.create(temporaryTable("AP_VL_BIS"), "ID numeric(3) null,"
                                                        + " COL numeric(3) null");
        jdbcFixture.create(temporaryTable("AP_VL_ETALON"), "COL_DATE date null,"
                                                           + " COL_DATE_HEURE " + datetime + " null,"
                                                           + " COL_NUMBER numeric(3,2) null,"
                                                           + " COL_STR varchar(255) null,"
                                                           + " ID numeric(3) null,"
                                                           + " COL numeric(3) null");

        FieldMap fields;
        Row row;
        scenario = new Scenario("", "");
        // table AP_VL_BIS
        fields = new FieldMap();
        fields.putField("ID", "1", null);
        fields.putField("COL", "A1", null);
        row = new Row("rowA", null, fields);
        scenario.addInputRow("AP_VL_BIS", row);
        fields = new FieldMap();
        fields.putField("ID", "2", null);
        fields.putField("COL", "A2", null);
        row = new Row("rowB", null, fields);
        scenario.addInputRow("AP_VL_BIS", row);
        fields = new FieldMap();
        fields.putField("ID", "10", null);
        fields.putField("COL", "A10", null);
        row = new Row("rowC", null, fields);
        scenario.addInputRow("AP_VL_BIS", row);

        // table AP_VL_ETALON
        fields = new FieldMap();
        fields.putField("COL", "QB", null);
        Row rowQ2 = new Row(null, "rowB", fields);
        scenario.addInputRow("AP_VL_ETALON", rowQ2);
        fields = new FieldMap();
        fields.putField("COL", "QA", null);
        Row rowQ1 = new Row(null, "rowA", fields);
        scenario.addInputRow("AP_VL_ETALON", rowQ1);
        fields = new FieldMap();
        fields.putField("COL", "QC", null);
        Row rowQ10 = new Row(null, "rowC", fields);
        scenario.addInputRow("AP_VL_ETALON", rowQ10);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(rowQ1);
        expected.add(rowQ2);
        expected.add(rowQ10);

        List result =
              jdbcsc.sortRows(scenario.getInputTable("AP_VL_ETALON").getRows(),
                              "ID",
                              jdbcFixture.getConnection(),
                              "AP_VL_ETALON",
                              true);

        assertEquals(expected, result);
    }


    /**
     * Test que la methode de tri marche pour des clefs multiple.
     */
    @Test
    public void test_sortRows_multiKey() throws Exception {
        FieldMap fieldsC = new FieldMap();
        fieldsC.putField("COL_STR", "une string1", null);
        fieldsC.putField("COL_DATE", "2002-12-20", null);
        Row rowC = new Row("rowC", "rowA", fieldsC);
        scenario.addInputRow("AP_VL", rowC);

        FieldMap fieldsD = new FieldMap();
        fieldsD.putField("COL_DATE", "2002-12-17", null);
        Row rowD = new Row("rowD", "rowC", fieldsD);
        scenario.addInputRow("AP_VL", rowD);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(rowA);
        expected.add(rowD);
        expected.add(rowC);

        assertEquals(expected,
                     jdbcsc.sortRows(scenario.getInputTable("AP_VL").getRows(),
                                     "COL_STR, COL_DATE",
                                     jdbcFixture.getConnection(),
                                     "AP_VL",
                                     true));
    }


    /**
     * Test que la methode de tri marche même si une lignes ne définit pas la colonne de tri.
     */
    @Test
    public void test_sortRows_nullKey() throws Exception {
        FieldMap fieldsC = new FieldMap();
        fieldsC.putField("COL_STR", "une string", null);
        fieldsC.putField("COL_DATE", "2002-12-20", null);
        fieldsC.putField("COL_DATE_HEURE", "2002-12-25 12:30:00", null);
        Row rowC = new Row(null, null, fieldsC);
        scenario.addInputRow("AP_VL", rowC);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(rowC);
        expected.add(rowA);

        assertEquals(expected,
                     jdbcsc.sortRows(scenario.getInputTable("AP_VL").getRows(),
                                     "COL_NUMBER",
                                     jdbcFixture.getConnection(),
                                     "AP_VL",
                                     true));
    }


    /**
     * Test que la methode de trie marche même si deux lignes ont la même valeur sur les colonnes de tri.
     */
    @Test
    public void test_sortRows_sameKey() throws Exception {
        FieldMap fieldsC = new FieldMap();
        fieldsC.putField("COL_DATE", "2002-12-21", null);
        Row rowC = new Row("rowC", "rowA", fieldsC);
        scenario.addInputRow("AP_VL", rowC);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(rowA);
        expected.add(rowC);

        assertEquals(expected,
                     jdbcsc.sortRows(scenario.getInputTable("AP_VL").getRows(),
                                     "COL_NUMBER",
                                     jdbcFixture.getConnection(),
                                     "AP_VL",
                                     true));
    }


    @Test
    public void test_sortRows_negativeNumber() throws Exception {
        FieldMap fields = new FieldMap();
        fields.putField("COL_NUMBER", "81.56", null);
        Row rowB = new Row(fields);
        scenario.addInputRow("AP_VL", rowB);

        fields = new FieldMap();
        fields.putField("COL_NUMBER", "-125.24", null);
        Row rowC = new Row(fields);
        scenario.addInputRow("AP_VL", rowC);

        fields = new FieldMap();
        fields.putField("COL_NUMBER", "0.24", null);
        Row rowD = new Row(fields);
        scenario.addInputRow("AP_VL", rowD);

        fields = new FieldMap();
        fields.putField("COL_NUMBER", "-7213.54", null);
        Row rowE = new Row(fields);
        scenario.addInputRow("AP_VL", rowE);

        fields = new FieldMap();
        fields.putField("COL_NUMBER", null, null);
        Row rowF = new Row(fields);
        scenario.addInputRow("AP_VL", rowF);

        fields = new FieldMap();
        fields.putField("COL_NUMBER", "0.241", null);
        Row rowG = new Row(fields);
        scenario.addInputRow("AP_VL", rowG);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(rowF);
        expected.add(rowE);
        expected.add(rowC);
        expected.add(rowD);
        expected.add(rowG);
        expected.add(rowA);
        expected.add(rowB);

        assertEquals(expected,
                     jdbcsc.sortRows(scenario.getInputTable("AP_VL").getRows(),
                                     "COL_NUMBER",
                                     jdbcFixture.getConnection(),
                                     "AP_VL",
                                     true));
    }


    @Test
    public void test_sortRows_multiColumns() throws Exception {
        Row row1 = createRow("row1", "COL_STR2=null;COL_STR=a;COL_NUMBER=null");
        Row row2 = createRow("row2", "COL_STR2=null;COL_STR=a;COL_NUMBER=-125.24");
        Row row3 = createRow("row3", "COL_STR2=ENV;COL_STR=a;COL_NUMBER=25.24");

        scenario.addInputRow("AP_VL", row2);
        scenario.addInputRow("AP_VL", row1);
        scenario.addInputRow("AP_VL", row3);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(row1);
        expected.add(row2);
        expected.add(rowA);
        expected.add(row3);

        assertArrayEquals(expected.toArray(),
                          jdbcsc.sortRows(scenario.getInputTable("AP_VL").getRows(),
                                          "COL_STR2,COL_STR,COL_NUMBER",
                                          jdbcFixture.getConnection(),
                                          "AP_VL",
                                          true).toArray());
    }


    @Test
    public void test_sortRows_multiColmns_sameOrderClauseValues() throws Exception {
        jdbcFixture.create(temporaryTable("AP_VL_BIS"), "COL_STR varchar(255) null,"
                                                        + " COL_STR2 varchar(255) null");

        Row row1 = createRow("row1", "COL_STR2=A;COL_STR=B");
        Row row2 = createRow("row2", "COL_STR2=A;COL_STR=null");
        Row row3 = createRow("row3", "COL_STR2=A;COL_STR=sdfh");
        Row row4 = createRow("rowA", "COL_STR=une string;"
                                     + "COL_NUMBER=1.23;"
                                     + "COL_DATE=2002-12-20;"
                                     + "COL_DATE_HEURE=2002-12-25 12:30:00");

        scenario = new Scenario("test", null);
        scenario.addInputRow("AP_VL_BIS", row1);
        scenario.addInputRow("AP_VL_BIS", row2);
        scenario.addInputRow("AP_VL_BIS", row3);
        scenario.addInputRow("AP_VL_BIS", row4);

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        List<Row> expected = new ArrayList<Row>();
        expected.add(row4);
        expected.add(row1);
        expected.add(row2);
        expected.add(row3);

        assertArrayEquals(expected.toArray(),
                          jdbcsc.sortRows(
                                scenario.getInputTable("AP_VL_BIS").getRows(),
                                "COL_STR2",
                                jdbcFixture.getConnection(),
                                "AP_VL_BIS",
                                true)
                                .toArray());
    }


    @Test
    public void test_verifyNoChanges() throws Exception {
        new JDBCScenario(scenario).insertInputInDb(jdbcFixture.getConnection());

        scenario = new Scenario("test", null);
        scenario.addInputRow("AP_VL", rowA);
        scenario.getInputTable("AP_VL").setTemporary(true);

        FieldMap fields = newFieldMap("COL_STR", "n'est pas pris en compte");
        scenario.addOutputRow("AP_VL", new Row("rowB", "rowA", fields));
        scenario.getOutputTable("AP_VL").setOrderClause("COL_NUMBER");

        assertTrue(new JDBCScenario(scenario).verifyNoChanges(jdbcFixture.getConnection()));
    }


    @Test
    public void test_verifyNoChanges_noInputCorresponding() throws Exception {
        new JDBCScenario(scenario).insertInputInDb(jdbcFixture.getConnection());
        jdbcFixture.create(temporaryTable("AP_BOBO"),
                           "COL_STR varchar(255) null, COL_DATE " + datetime + " null");

        scenario = new Scenario("test", null);
        scenario.addInputRow("AP_VL", rowA);
        scenario.getInputTable("AP_VL").setTemporary(true);

        scenario.addOutputRow("AP_VL",
                              new Row("rowB", "rowA", newFieldMap("COL_STR", "n'est pas pris en compte")));
        scenario.getOutputTable("AP_VL").setOrderClause("COL_NUMBER");

        // TableInfo AP_BOBO non présent dans les inputs
        scenario.addOutputRow("AP_BOBO",
                              new Row(null, "rowA", newFieldMap("COL_STR", "n'est pas pris en compte")));
        scenario.getOutputTable("AP_BOBO").setOrderClause("COL_DATE");
        scenario.getOutputTable("AP_BOBO").setTemporary(true);

        assertTrue(new JDBCScenario(scenario).verifyNoChanges(jdbcFixture.getConnection()));
    }


    @Test
    public void test_verifyOutputs() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        assertTrue(jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    @Test
    public void test_verifyOutputs_NullCase() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        scenario.getInputTable("AP_VL").getRow(0).setFieldValue("COL_DATE", null, "true", null);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        scenario.getOutputTable("AP_VL").getRow(0).setFieldValue("COL_DATE", null, "true", null);

        assertTrue(jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    /**
     * Bug de tokio qd on met des lignes en plus dans le fichier au nx de output.
     */
    @Test
    public void test_verifyOutputs_bug() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        scenario.addOutputRow("AP_VL", new Row("rowC", "rowB", new FieldMap()));

        assertFalse("Nombre de ligne différente, mais 1er ligne OK",
                    jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    @Test
    public void test_verifyOutputs_moreRowsInEtalon() throws Exception {
        Scenario badEtalon = createBadEtalon();
        JDBCScenario jdbcScenario = new JDBCScenario(badEtalon);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        assertFalse(jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    @Test
    public void test_verifyOutputs_noRowsInDB() throws Exception {
        assertFalse(new JDBCScenario(scenario)
              .verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    @Test
    public void test_verifyOutputs_different() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        String update = databaseQueryHelper
              .buildUpdateQuery(temporaryTable("AP_VL"), field("COL_NUMBER", 1.24));
        jdbcFixture.executeUpdate(update);

        assertFalse("COL_NUMBER est different",
                    jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    @Test
    public void test_verifyOutputs_differentWithMessage() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        String update = databaseQueryHelper
              .buildUpdateQuery(temporaryTable("AP_VL"), field("COL_NUMBER", 1.24));
        jdbcScenario.getScenario().getOutputDataSet()
              .addComparator("COL_NUMBER", new SimpleComparator("Parce que !!!"));

        jdbcFixture.executeUpdate(update);
        jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER");

        assertTrue("Raison attendue dans le message",
                   jdbcScenario.getLastVerifyOutputsReport().contains("Parce que !!!\n"));
    }


    @Test
    public void test_verifyOutputs_differentNbOfLine() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        String insert = databaseQueryHelper.buildInsertQuery(temporaryTable("AP_VL"), fieldName("COL_DATE"));
        PreparedStatement preparedStatement = jdbcFixture.getConnection().prepareStatement(insert);
        preparedStatement.setDate(1, java.sql.Date.valueOf("2002-12-20"));
        preparedStatement.execute();

        assertFalse("Nombre de ligne différente, mais 1er ligne OK",
                    jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    /**
     * Test qui verifie que Tokio trie les outputs correctement.
     */
    @Test
    public void test_verifyOutputs_sortOutput() throws Exception {
        JDBCScenario jdbcScenario = new JDBCScenario(scenario);
        jdbcScenario.insertInputInDb(jdbcFixture.getConnection());

        String insert = databaseQueryHelper.buildInsertQuery(temporaryTable("AP_VL"),
                                                             fieldName("COL_DATE"),
                                                             fieldName("COL_DATE_HEURE"),
                                                             fieldName("COL_NUMBER"),
                                                             fieldName("COL_STR"));
        PreparedStatement preparedStatement = jdbcFixture.getConnection().prepareStatement(insert);
        preparedStatement.setDate(1, java.sql.Date.valueOf("2002-12-20"));
        preparedStatement.setTimestamp(2, Timestamp.valueOf("2002-12-25 12:30:00"));
        preparedStatement.setBigDecimal(3, new java.math.BigDecimal(2));
        preparedStatement.setString(4, "une string");
        preparedStatement.execute();
        preparedStatement.close();

        preparedStatement = jdbcFixture.getConnection().prepareStatement(insert);
        preparedStatement.setDate(1, java.sql.Date.valueOf("2002-12-20"));
        preparedStatement.setTimestamp(2, Timestamp.valueOf("2002-12-25 12:30:00"));
        preparedStatement.setBigDecimal(3, new java.math.BigDecimal(3));
        preparedStatement.setString(4, "une string");
        preparedStatement.execute();
        preparedStatement.close();

        // Ligne avec COL_NUMBER = 3
        FieldMap fields = new FieldMap();
        fields.putField("COL_NUMBER", "3.00", null);
        scenario.addOutputRow("AP_VL", new Row("row3", "rowB", fields));
        // Ligne avec COL_NUMBER = 2
        fields.putField("COL_NUMBER", "2.00", null);
        scenario.addOutputRow("AP_VL", new Row("row2", "rowB", fields));
        scenario.getOutputDataSet().addComparator("COL_NUMBER", ComparatorConverter.newComparator("0"));

        assertTrue(jdbcScenario.verifyOutputs(jdbcFixture.getConnection(), "AP_VL", "COL_NUMBER"));
    }


    /**
     * Test qui verifie que Tokio trie les outputs correctement les varchar ne contenant que des chiffres de
     * taille différente. ex : "3" &gt; "02" en base et " 3" &lt; "02" avec tokio
     */
    @Test
    public void test_verifyOutputs_sortOutput_number_bug() throws Exception {
        scenario = new Scenario("tszdfest", null);

        FieldMap fieldsA = new FieldMap();
        fieldsA.putField("COL_STR", "02", null);
        rowA = new Row("rowA", null, fieldsA);
        scenario.addInputRow("AP_VL", rowA);

        FieldMap fieldsB = new FieldMap();
        fieldsB.putField("COL_STR", "3", null);
        rowA = new Row("rowB", null, fieldsB);
        scenario.addInputRow("AP_VL", rowA);

        scenario.addOutputRow("AP_VL", new Row("rowC", null, fieldsA));
        scenario.addOutputRow("AP_VL", new Row("rowD", null, fieldsB));

        FakeDriver.getDriver().pushResultSet(new Object[][]{
              {"COL_STR"},
              buildRowForSortTest("02"),
              buildRowForSortTest("3")
        }, "select * from AP_VL order by COL_STR");

        Object[][] tableDef =
              {
                    {},
                    new Object[]{null, null, null, "COL_STR", VARCHAR}
              };
        FakeDriver.getDriver().pushResultSet(tableDef,
                                             "FakeDatabaseMetaData.getColumns(null, null, AP_VL, null)");

        JDBCScenario jdbcsc = new JDBCScenario(scenario);

        assertTrue(jdbcsc.verifyOutputs(connection, "AP_VL", "COL_STR"));
    }


    @Test
    public void test_insertInputInDb_transformer() throws Exception {
        LogString log = new LogString();
        JDBCScenario jdbcScenario =
              new JDBCScenario(new Scenario(null, null),
                               new TableOrderBuilderMock(),
                               new SQLFieldListBuilderMock(),
                               new ImplicitDataTransformerMock(
                                     new LogString("ImplicitDataTransformer", log)),
                               new GeneratedValuesTansformerMock(
                                     new LogString("GeneratedValuesTransformer", log)));

        jdbcScenario.insertInputInDb(connection);

        log.assertContent("GeneratedValuesTransformer.transform(), ImplicitDataTransformer.transform()");
    }


    private Object[] buildRowForSortTest(String value) {
        return new Object[]{value};
    }


    private Scenario createBadEtalon() {
        Scenario story = new Scenario("test", null);

        FieldMap fieldsA = new FieldMap();
        fieldsA.putField("COL_STR", "une string", null);
        fieldsA.putField("COL_NUMBER", "1.23", null);
        fieldsA.putField("COL_DATE", "2002-12-20", null);
        fieldsA.putField("COL_DATE_HEURE", "2002-12-25 12:30:00", null);

        rowA = new Row("rowA", null, fieldsA);

        story.addInputRow("AP_VL", rowA);

        story.addOutputRow("AP_VL", new Row("rowB", "rowA", new FieldMap()));
        story.addOutputRow("AP_VL", new Row("myRow", "rowA", new FieldMap()));
        story.getInputTable("AP_VL").setTemporary(true);
        return story;
    }


    private Row createRow(String id, String values) {
        return createRow(id, values, null);
    }


    private Row createRow(String id, String values, Boolean autoComplete) {
        return createRow(id, null, values, autoComplete);
    }


    private Row createRow(String id, String refId, String values, Boolean autoComplete) {
        FieldMap fields = new FieldMap();
        if (values != null && !"".equals(values)) {
            for (StringTokenizer tokenizer = new StringTokenizer(values, ";"); tokenizer.hasMoreTokens();) {
                String columnAndValue = tokenizer.nextToken();
                StringTokenizer columnElements = new StringTokenizer(columnAndValue, "=");
                String columnName = columnElements.nextToken();
                String columnValue = columnElements.nextToken();
                fields.putField(columnName,
                                ("null".equalsIgnoreCase(columnValue) ? null : columnValue),
                                null);
            }
        }
        return new Row(id, refId, autoComplete, fields);
    }


    private FieldMap newFieldMap(String name, String value) {
        FieldMap fields = new FieldMap();
        fields.putField(name, value, null);
        return fields;
    }


    private String formatDate(Date date) {
        return new SimpleDateFormat(DD_MM_YYYY).format(date);
    }


    private String formatDateTime(Date date) {
        return new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS).format(date);
    }


    private String getRelativeDate(Date date, int dateField, int amount, String format) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(dateField, amount);
        return new SimpleDateFormat(format).format(calendar.getTime());
    }


    private void assertDatetime(String expectedDatetime, ResultSet resultSet, int index)
          throws ParseException, SQLException {
        assertEquals(DATETIME_FORMAT.parse(expectedDatetime), resultSet.getTimestamp(index));
    }


    private void assertDate(String expectedDate, ResultSet resultSet, int index)
          throws ParseException, SQLException {
        assertEquals(DATE_FORMAT.parse(expectedDate), resultSet.getDate(index));
    }


    private static class SimpleComparator implements Comparator {
        private String reason;


        private SimpleComparator(String reason) {
            this.reason = reason;
        }


        public void setParam(String param) {
        }


        public String getParam() {
            return null;
        }


        public String getTypeAssert() {
            return null;
        }


        public boolean isEqual(Object expected, Object actual, int sqlType) {
            return false;
        }


        public String getReason() {
            return reason;
        }
    }

    private class TableOrderBuilderMock implements TableOrderBuilder {
        public TableOrder get(Connection connection, DataSet dataSet) {
            return new TableOrder() {
                public List<String> buildTablesDeleteOrder() {
                    return Collections.emptyList();
                }


                public List<String> buildTablesInsertOrder() {
                    return Collections.emptyList();
                }
            };
        }
    }

    private static class ImplicitDataTransformerMock extends ImplicitDataTransformer {
        private final LogString log;


        private ImplicitDataTransformerMock(LogString log) {
            super(new ForeignKeyMetadata(), new SQLFieldListBuilderMock());
            this.log = log;
        }


        @Override
        public void transform(Connection connection, DataSet dataset) throws SQLException {
            log.call("transform");
        }
    }

    private static class GeneratedValuesTansformerMock extends GeneratedValuesTansformer {
        private final LogString log;


        private GeneratedValuesTansformerMock(LogString log) {
            this.log = log;
        }


        @Override
        public void transform(DataSet dataset) {
            log.call("transform");
        }
    }
}
