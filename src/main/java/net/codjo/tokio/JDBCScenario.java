/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.DatabaseQueryHelper;
import net.codjo.database.common.api.DatabaseQueryHelper.SelectType;
import net.codjo.database.common.api.SQLFieldList;
import net.codjo.database.common.api.structure.SqlField;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.database.common.repository.builder.SQLFieldListBuilder;
import net.codjo.test.common.DateUtil;
import net.codjo.tokio.model.Comparator;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.LocationPointer;
import net.codjo.tokio.model.LoggerLocationVisitor;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.tableorder.DefaultTableOrderBuilder;
import net.codjo.tokio.tableorder.TableOrder;
import net.codjo.tokio.tableorder.TableOrderBuilder;
import net.codjo.tokio.util.TokioLog;
/**
 * Classe responsable du positionnement (et de la verification) d'un scenario de test dans un environnement JDBC.
 *
 * @author $Author: catteao $
 * @version $Revision: 1.32 $
 */
public class JDBCScenario {
    private boolean deleteBeforeInsert = true;
    private String lastVerifyOutputsReport;
    private Scenario scenario;
    private Properties propertiesBackup = null;
    private static final String RELATIVE_DATE_REGEXP = "(TODAY)(([+-])([1-9][0-9]*)([DMYHms]))?";
    private static final String RELATIVE_DATE_WITHOUT_TIME_REGEXP
          = "(TODAY_DATE)(([+-])([1-9][0-9]*)([DMYHms]))?";
    private static final int AMOUNT_GROUP_REGEXP = 4;
    private static final int SIGN_GROUP_REGEXP = 3;
    private static final int DATE_FIELD_GROUP_REGEXP = 5;
    private final DatabaseFactory databaseFactory = new DatabaseFactory();
    private final DatabaseHelper databaseHelper = databaseFactory.createDatabaseHelper();
    private final DatabaseQueryHelper databaseQueryHelper = databaseFactory.getDatabaseQueryHelper();
    private final TableOrderBuilder tableOrderBuilder;
    private SQLFieldListBuilder sqlFieldListBuilder;
    private ImplicitDataTransformer implicitDataTransformer;
    private GeneratedValuesTansformer generatedValuesTansformer;


    public JDBCScenario(Scenario sc) {
        this(sc, new DefaultTableOrderBuilder());
    }


    public JDBCScenario(Scenario sc, TableOrderBuilder tableOrderBuilder) {
        this(sc, tableOrderBuilder, new DefaultSQLFieldListBuilder());
    }


    public JDBCScenario(Scenario sc,
                        TableOrderBuilder tableOrderBuilder,
                        SQLFieldListBuilder sqlFieldListBuilder) {
        this(sc,
             tableOrderBuilder,
             sqlFieldListBuilder,
             new ImplicitDataTransformer(sqlFieldListBuilder),
             new GeneratedValuesTansformer());
    }


    public JDBCScenario(Scenario sc,
                        TableOrderBuilder tableOrderBuilder,
                        SQLFieldListBuilder sqlFieldListBuilder,
                        ImplicitDataTransformer implicitDataTransformer,
                        GeneratedValuesTansformer generatedValuesTansformer) {
        setScenario(sc);
        this.tableOrderBuilder = tableOrderBuilder;
        this.sqlFieldListBuilder = sqlFieldListBuilder;
        this.implicitDataTransformer = implicitDataTransformer;
        this.generatedValuesTansformer = generatedValuesTansformer;
    }


    public void setDeleteBeforeInsert(boolean deleteBeforeInsert) {
        this.deleteBeforeInsert = deleteBeforeInsert;
    }


    public void setInputTableIdentityInsert(String tableName, boolean insert) {
        getScenario().getInputDataSet().getTable(tableName).setIdentityInsert(insert);
    }


    public boolean isDeleteBeforeInsert() {
        return deleteBeforeInsert;
    }


    public String getLastVerifyOutputsReport() {
        return lastVerifyOutputsReport;
    }


    public Scenario getScenario() {
        return scenario;
    }


    public void insertInputInDb(Connection con) throws SQLException {
        generatedValuesTansformer.transform(getScenario().getInputDataSet());
        implicitDataTransformer.transform(con, getScenario().getInputDataSet());
        insertDataSetInDb(con, getScenario().getInputDataSet());
    }


    public void setPropertiesUp() {
        if (propertiesBackup == null) {
            propertiesBackup = (Properties)System.getProperties().clone();
        }
        System.setProperties(scenario.getProperties());
    }


    public void tearPropertiesDown() {
        System.setProperties(propertiesBackup);
        propertiesBackup = null;
    }


    public void insertInputTableInDb(final Connection con, final String tableName)
          throws SQLException {
        Statement stmt = con.createStatement();
        try {
            insertTableInDb(con, getScenario().getInputTable(tableName));
        }
        finally {
            stmt.close();
        }
    }


    public void insertOutputInDb(Connection con) throws SQLException {
        insertDataSetInDb(con, getScenario().getOutputDataSet());
    }


    public void insertOutputTableInDb(final Connection con, final String tableName)
          throws SQLException {
        Statement stmt = con.createStatement();
        try {
            insertTableInDb(con, getScenario().getOutputTable(tableName));
        }
        finally {
            stmt.close();
        }
    }


    public void spoolInputs(Connection con, String tableName, String orderClause) {
        Table table = getScenario().getInputTable(tableName);
        spoolTable(con, table, orderClause);
    }


    public void spoolOutputs(Connection con, String tableName, String orderClause) {
        Table table = getScenario().getOutputTable(tableName);
        spoolTable(con, table, orderClause);
    }


    public boolean verifyAllOutputs(Connection con)
          throws SQLException {
        for (Iterator i = getScenario().outputTables(); i.hasNext(); ) {
            Table table = (Table)i.next();
            if (!verifyOutputs(con, table.getName(), table.getOrderClause())) {
                TokioLog.error("Erreur de comparaison sur " + table.getName()
                               + " avec comme clause de remise en ordre " + table.getOrderClause()
                               + " si la precdente valeur est egale à 'null' "
                               + "  alors ca veut dire qu'il n'y en a pas."
                               + " Eventuellement il faudrat en ajouter une " + "ou pas ?");
                return false;
            }
        }
        return true;
    }


    /**
     * Permet de vérifier que les tables spécifiées en étalon n'ont pas été modifiées par rapport aux données en input.
     *
     * @param con une copnnection JDBC (et oui)
     *
     * @return <code>true</code> si tout est ok, et <code>false</code> sinon.
     *
     * @throws SQLException Une erreur :)
     */
    public boolean verifyNoChanges(Connection con)
          throws SQLException {
        for (Iterator i = getScenario().outputTables(); i.hasNext(); ) {
            Table outputTable = (Table)i.next();
            Table inputTable = getScenario().getInputTable(outputTable.getName());
            if (inputTable == null) {
                inputTable = new Table(outputTable.getName(), null);
                inputTable.setTemporary(outputTable.isTemporary());
            }

            if (!verifyTable(con, inputTable, outputTable.getOrderClause())) {
                TokioLog.error("Erreur de comparaison sur " + outputTable.getName()
                               + " avec comme clause de remise en ordre "
                               + outputTable.getOrderClause()
                               + " si la precdente valeur est egale à 'null' "
                               + "  alors ca veut dire qu'il n'y en a pas."
                               + " Eventuellement il faudrat en ajouter une ou pas ?");
                return false;
            }
        }
        return true;
    }


    public boolean verifyOutputs(Connection con, String tableName)
          throws SQLException {
        Table table = getScenario().getOutputTable(tableName);
        return verifyOutputs(con, tableName, table.getOrderClause());
    }


    public boolean verifyOutputs(Connection con, String tableName, String orderClause)
          throws SQLException {
        Table table = getScenario().getOutputTable(tableName);

        if (table == null) {
            throw new IllegalArgumentException("table inconnue " + tableName);
        }

        if (getScenario().getInputTable(tableName) != null) {
            table.setTemporary(getScenario().getInputTable(tableName).isTemporary());
        }
        return verifyTable(con, table, orderClause);
    }


    private boolean verifyTable(Connection con, Table table, String orderClause)
          throws SQLException {
        SQLFieldList sqlTypes = newSQLFieldList(con, table.getName(), table.isTemporary());
        List<Row> rows = sortRows(table.getRows(), orderClause, sqlTypes);
        Statement stmt = con.createStatement();
        try {
            String select = databaseQueryHelper.buildSelectQuery(toSqlTable(table), SelectType.ALL);
            if (orderClause != null) {
                select += " order by " + orderClause;
            }
            ResultSet rs = stmt.executeQuery(select);

            int nbOfLinesInDB = 0;
            for (int rowIdx = 0; rs.next(); rowIdx++) {
                nbOfLinesInDB++;

                if (moreRowsInDB(rowIdx, rows.size(), table.getName())) {
                    return false;
                }

                Row expectedRow = rows.get(rowIdx);
                if (!isRowEqual(expectedRow, rs, sqlTypes, rowIdx, table.getName())) {
                    return false;
                }
            }

            if (moreRowsInOutput(nbOfLinesInDB, rows.size(), table.getName())) {
                return false;
            }
        }
        finally {
            stmt.close();
        }
        setLastVerifyOutputsReport("Aucune Erreur");
        return true;
    }


    String buildQuery(Table table, Collection<String> fieldNames) {
        List<SqlField> sqlFields = new ArrayList<SqlField>();
        for (String fieldName : fieldNames) {
            sqlFields.add(SqlField.fieldName(fieldName));
        }
        return databaseQueryHelper.buildInsertQuery(toSqlTable(table),
                                                    sqlFields.toArray(new SqlField[sqlFields.size()]));
    }


    Object convertValue(String value, int sqlType) {
        if (value == null) {
            return null;
        }
        try {
            switch (sqlType) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                    return Integer.valueOf(value);
                case Types.REAL:
                case Types.FLOAT:
                case Types.DOUBLE:
                    return Double.valueOf(value);
                case Types.BIGINT:
                case Types.DECIMAL:
                case Types.NUMERIC:
                    return getBigDecimalValue(value);
                case Types.BIT:
                    return Boolean.valueOf(value);
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    return periodFilter(value);
                case Types.DATE:
                    return getDateFromValue(value);
                case Types.TIME:
                    return java.sql.Time.valueOf(value);
                case Types.TIMESTAMP:
                    try {
                        return getTimestampFromValue(value);
                    }
                    catch (Exception e) {
                        return java.sql.Time.valueOf(value);
                    }
                default:
                    throw new IllegalArgumentException("Type SQL inconnu " + sqlType
                                                       + " pour value = " + value);
            }
        }
        catch (Exception ex) {
            TokioLog.error("Erreur lors de la conversion !", ex);
            throw new IllegalArgumentException("Erreur de conversion "
                                               + " pour la valeur = " + value + " erreur = " + ex);
        }
    }


    Object convertValue(ResultSet resultSet, String columnName, int sqlType) {
        try {
            if (resultSet.getObject(columnName) == null) {
                return null;
            }

            switch (sqlType) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                    return resultSet.getInt(columnName);
                case Types.REAL:
                case Types.FLOAT:
                case Types.DOUBLE:
                    return resultSet.getDouble(columnName);
                case Types.BIGINT:
                case Types.DECIMAL:
                case Types.NUMERIC:
                    return resultSet.getBigDecimal(columnName);
                case Types.BIT:
                    return resultSet.getBoolean(columnName);
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    return resultSet.getString(columnName);
                case Types.DATE:
                    return resultSet.getDate(columnName);
                case Types.TIME:
                    return resultSet.getTime(columnName);
                case Types.TIMESTAMP:
                    return resultSet.getTimestamp(columnName);
                default:
                    return resultSet.getObject(columnName);
            }
        }
        catch (Exception e) {
            TokioLog.error("Erreur lors de la conversion !", e);
            throw new IllegalArgumentException(
                  "Erreur de conversion  pour la colonne = " + columnName + " erreur = " + e);
        }
    }


    private BigDecimal getBigDecimalValue(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return new BigDecimal(1);
        }
        else if ("false".equalsIgnoreCase(value)) {
            return new BigDecimal(0);
        }
        return new BigDecimal(value);
    }


    private String periodFilter(String value) {
        if (value != null && value.startsWith(VariableName.CURRENT_PERIOD)) {
            java.util.Date todayDate = new java.util.Date();
            Period period = new Period(todayDate);
            if (value.startsWith(VariableName.CURRENT_PERIOD + "+")) {
                int added =
                      Integer.parseInt(value.substring(VariableName.CURRENT_PERIOD.length()
                                                       + 1));
                period.addMonth(added);
            }
            else if (value.startsWith(VariableName.CURRENT_PERIOD + "-")) {
                int substracted =
                      Integer.parseInt(value.substring(VariableName.CURRENT_PERIOD.length()
                                                       + 1));
                period.subMonth(substracted);
            }
            return period.toString();
        }
        else {
            return value;
        }
    }


    private Object getDateFromValue(String value) {
        String todayDate = DateUtil.createDateString(new java.util.Date());

        try {
            if (VariableName.TODAY.equalsIgnoreCase(value)) {
                return java.sql.Date.valueOf(todayDate);
            }
            else if (isDateRelative(value)) {
                return new java.sql.Date(computeRelativeDate(value, true));
            }
            else if (isDateRelativeWithoutHourMinuteSecond(value)) {
                return new java.sql.Date(computeRelativeDate(value, false));
            }
            else if (value != null && value.startsWith(VariableName.TODAY + "+")) {
                long added = Long.parseLong(value.substring(6));
                return new java.sql.Date(java.sql.Date.valueOf(todayDate).getTime()
                                         + added);
            }
            else if (value != null && value.startsWith(VariableName.TODAY + "-")) {
                long added = Long.parseLong(value.substring(6));
                return new java.sql.Date(java.sql.Date.valueOf(todayDate).getTime()
                                         - added);
            }
        }
        catch (NumberFormatException nfe) {
            return java.sql.Date.valueOf(todayDate);
        }
        return java.sql.Date.valueOf(value);
    }


    public boolean isDateRelative(String stringValue) {
        Matcher relativeDateMatcher = initRelativeDateMatcher(stringValue, RELATIVE_DATE_REGEXP);
        return relativeDateMatcher.matches();
    }


    public boolean isDateRelativeWithoutHourMinuteSecond(String stringValue) {
        Matcher relativeDateMatcher = initRelativeDateMatcher(stringValue,
                                                              RELATIVE_DATE_WITHOUT_TIME_REGEXP);
        return relativeDateMatcher.matches();
    }


    private Matcher initRelativeDateMatcher(String stringValue, String pattern) {
        Pattern relativeDatePattern = Pattern.compile(pattern);
        return relativeDatePattern.matcher(stringValue);
    }


    public long computeRelativeDate(String stringValue, boolean displayHourMinuteSeconds) {
        if (displayHourMinuteSeconds) {
            Calendar calendar = initCalendar(stringValue, RELATIVE_DATE_REGEXP);
            return calendar.getTimeInMillis();
        }
        else {
            Calendar calendar = initCalendar(stringValue, RELATIVE_DATE_WITHOUT_TIME_REGEXP);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        }
    }


    private Calendar initCalendar(String stringValue, String regexp) {
        Matcher relativeDateMatcher = initRelativeDateMatcher(stringValue, regexp);
        relativeDateMatcher.matches();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(getDateField(relativeDateMatcher), getAmount(relativeDateMatcher));
        return calendar;
    }


    private int getAmount(Matcher relativeDateMatcher) {
        int amount = Integer.parseInt(relativeDateMatcher.group(AMOUNT_GROUP_REGEXP));
        String sign = relativeDateMatcher.group(SIGN_GROUP_REGEXP);
        if ("-".equalsIgnoreCase(sign)) {
            return -amount;
        }
        else {
            return amount;
        }
    }


    private int getDateField(Matcher matcher) {
        String dateField = matcher.group(DATE_FIELD_GROUP_REGEXP);
        if ("D".equals(dateField)) {
            return Calendar.DATE;
        }
        else if ("M".equalsIgnoreCase(dateField)) {
            return Calendar.MONTH;
        }
        else if ("H".equals(dateField)) {
            return Calendar.HOUR_OF_DAY;
        }
        else {
            return Calendar.YEAR;
        }
    }


    private Object getTimestampFromValue(String value) {
        String todayTimestamp = DateUtil.createDateString(new java.util.Date()) + " 00:00:00.0";

        try {
            if (VariableName.TODAY.equalsIgnoreCase(value)) {
                return java.sql.Timestamp.valueOf(todayTimestamp);
            }
            else if (isDateRelative(value)) {
                return new java.sql.Timestamp(computeRelativeDate(value, true));
            }
            else if (isDateRelativeWithoutHourMinuteSecond(value)) {
                return new java.sql.Timestamp(computeRelativeDate(value, false));
            }
            else if (value != null && value.startsWith(VariableName.TODAY + "+")) {
                long added = Long.parseLong(value.substring(6));
                return new java.sql.Timestamp(java.sql.Timestamp.valueOf(todayTimestamp)
                                                    .getTime() + added);
            }
            else if (value != null && value.startsWith(VariableName.TODAY + "-")) {
                long added = Long.parseLong(value.substring(6));
                return new java.sql.Timestamp(java.sql.Timestamp.valueOf(todayTimestamp)
                                                    .getTime() - added);
            }
        }
        catch (NumberFormatException nfe) {
            return java.sql.Timestamp.valueOf(todayTimestamp);
        }
        try {
            return java.sql.Timestamp.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            java.sql.Date date = java.sql.Date.valueOf(value);
            return new java.sql.Timestamp(date.getTime());
        }
    }


    private void setLastVerifyOutputsReport(String lastVerifyOutputsReport) {
        this.lastVerifyOutputsReport = lastVerifyOutputsReport;
    }


    private void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }


    private int getFieldType(final String tableName, final SQLFieldList sqlTypes,
                             final String fieldName) {
        try {
            return sqlTypes.getFieldType(fieldName);
        }
        catch (NoSuchElementException ex) {
            throw new NoSuchElementException("Colonne >" + fieldName + "< "
                                             + "non présente dans la table >" + tableName + "<");
        }
    }


    private boolean isRowEqual(Row expectedRow, ResultSet rs, SQLFieldList sqlTypes,
                               int rowIdx, String tableName) throws SQLException {
        StringBuffer rowErrorReport = null;
        for (Object object : expectedRow.getFields().fieldNameSet()) {
            String field = (String)object;
            int sqlType = sqlTypes.getFieldType(field);
            Object valDb = convertValue(rs, field, sqlType);

            Field expectedField = expectedRow.getFields().get(field);
            if (!expectedField.containsGeneratedValue()) {
                Object expectedVal = convertValue(expectedField.getValue(), sqlType);

                Comparator comp = getScenario().getOutputDataSet().getComparator(field);

                if (!comp.isEqual(expectedVal, valDb, sqlType)) {
                    if (rowErrorReport == null) {
                        rowErrorReport = new StringBuffer();
                    }
                    rowErrorReport.append("\t[").append(field).append("]");
                    rowErrorReport.append("\texpected = (").append(expectedVal).append(")");
                    rowErrorReport.append("\tvaleur   = (").append(valDb).append(")");

                    String reason = comp.getReason();
                    if (reason != null) {
                        rowErrorReport.append(" : ").append(reason);
                    }
                    rowErrorReport.append("\n");
                }
            }
        }

        if (rowErrorReport != null) {
            StringBuilder report = new StringBuilder();
            printErrorMessageRowLocation(expectedRow, report);
            report.append("\n")
                  .append("[Comparator] Ligne ").append(rowIdx + 1).append(" table ")
                  .append(tableName).append(" en erreur !!!").append("\n")
                  .append("   expected :").append(expectedRow.toString(true)).append("\n")
                  .append("   valeur   :")
                  .append(resultSetToString(rs, expectedRow.getFields().fieldNameSet()))
                  .append("\n\n")
                  .append(rowErrorReport.toString());
            setLastVerifyOutputsReport(report.toString());
            TokioLog.info(getLastVerifyOutputsReport());
        }
        return rowErrorReport == null;
    }


    private boolean moreRowsInOutput(int nbOfLinesInDB, int expectedSize, String tableName) {
        if (nbOfLinesInDB != expectedSize) {
            setLastVerifyOutputsReport("[Comparator] Nombre de ligne différent. "
                                       + "il reste encore dans l'étalon de la table " + tableName
                                       + "\n\tNombre de ligne identique = " + nbOfLinesInDB);
            TokioLog.info(getLastVerifyOutputsReport());
            return true;
        }
        return false;
    }


    private boolean moreRowsInDB(int rowIdx, int expectedSize, String tableName) {
        if (rowIdx >= expectedSize) {
            setLastVerifyOutputsReport("[Comparator] Nombre de ligne différent. "
                                       + "il reste encore des lignes dans la table " + tableName
                                       + "\n\tNombre de ligne identique = " + rowIdx);
            TokioLog.info(getLastVerifyOutputsReport());
            return true;
        }
        return false;
    }


    private void insertDataSetInDb(Connection con, DataSet dataSet) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            TableOrder tableOrder = tableOrderBuilder.get(con, dataSet);
            if (isDeleteBeforeInsert()) {
                for (String tableName : tableOrder.buildTablesDeleteOrder()) {
                    SqlTable sqlTable = SqlTable.table(tableName);
                    sqlTable.setTemporary(dataSet.getTable(tableName) != null
                                          && dataSet.getTable(tableName).isTemporary());
                    databaseHelper.truncateTable(con, sqlTable);
                }
            }

            for (String tableName : tableOrder.buildTablesInsertOrder()) {
                insertTableInDb(con, dataSet.getTable(tableName));
            }
        }
        finally {
            stmt.close();
        }
    }


    private void insertTableInDb(Connection connection, Table table) throws SQLException {
        if (table.isIdentityInsert()) {
            setIdentityInsert(connection, table, true);
        }

        SQLFieldList sqlTypes = newSQLFieldList(connection, table.getName(), table.isTemporary());
        for (Row row : table.getRows()) {
            try {
                insertRow(connection, table, row, sqlTypes);
            }
            catch (SQLException e) {
                StringBuilder report = new StringBuilder();
                printErrorMessageRowLocation(row, report);
                TokioLog.error(report.toString());
                TokioLog.error("Insertion en erreur sur la table : " + table.getName());
                TokioLog.error("\t ligne en erreur :" + row);
                throw e;
            }
        }

        if (table.isIdentityInsert()) {
            setIdentityInsert(connection, table, false);
        }
    }


    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    private void insertRow(Connection connection,
                           Table table,
                           Row row,
                           SQLFieldList sqlTypes) throws SQLException {
        FieldMap fields = row.getFields();
        Collection<String> fieldNames = fields.fieldNameSet();
        String query = buildQuery(table, fieldNames);
        PreparedStatement statement = connection.prepareStatement(query);

        int index = 1;
        for (Iterator<String> iter = fieldNames.iterator(); iter.hasNext(); index++) {
            String fieldName = iter.next();
            String value = fields.get(fieldName).getValue();
            int valueType = getFieldType(table.getName(), sqlTypes, fieldName);
            Object convertedValue = convertValue(value, valueType);
            if (convertedValue == null) {
                statement.setNull(index, valueType);
            }
            else {
                statement.setObject(index, convertedValue, valueType);
            }
        }

        statement.executeUpdate();

        if (statement.getWarnings() != null) {
            TokioLog.error("*********** ERREUR WARNING");
            TokioLog.error("Erreur lors de l'insertion d'une ligne !!!");
            StringBuilder stringBuilder = new StringBuilder();
            printErrorMessageRowLocation(row, stringBuilder);
            TokioLog.error(stringBuilder.toString());
            TokioLog.error("\t erreur=" + statement.getWarnings());
            TokioLog.error("\t table=" + table.getName());
            TokioLog.error("\t row=" + row);
            throw statement.getWarnings();
        }
    }


    private void setIdentityInsert(Connection connection, Table table, boolean identityInsert) {
        try {
            databaseHelper
                  .setIdentityInsert(connection, null, table.getName(), table.isTemporary(), identityInsert);
        }
        catch (SQLException ex) {
            // NA
        }
    }


    private String resultSetToString(ResultSet rs, Collection columns)
          throws SQLException {
        StringBuilder buffer = new StringBuilder("{");
        for (Iterator i = columns.iterator(); i.hasNext(); ) {
            String col = i.next().toString();

            buffer.append(col).append("=").append(rs.getObject(col));

            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
        return buffer.append("}").toString();
    }


    private void spoolTable(Connection con, Table table, String orderClause) {
        try {
            List<Row> rows =
                  sortRows(table.getRows(),
                           orderClause,
                           newSQLFieldList(con, table.getName(), table.isTemporary()));
            TokioLog.info("**** SPOOL de la table " + table.getName() + " par "
                          + orderClause);
            for (int i = 0; i < rows.size(); i++) {
                TokioLog.info("[" + (i + 1) + "] \t " + rows.get(i));
            }
        }
        catch (SQLException e) {
            throw new IllegalArgumentException("Erreur lors de la détermination du type"
                                               + "des colonnes ! " + e.getLocalizedMessage());
        }
    }


    private SQLFieldList newSQLFieldList(Connection connection,
                                         String tablename,
                                         boolean temporaryTable) throws SQLException {
        return sqlFieldListBuilder.get(connection, null, tablename, temporaryTable);
    }


    List<Row> sortRows(List<Row> rows,
                       String orderClause,
                       Connection con,
                       String tableName,
                       boolean temporaryTable) {
        try {
            return sortRows(rows, orderClause, newSQLFieldList(con, tableName, temporaryTable));
        }
        catch (SQLException ex) {
            TokioLog.error("Erreur lors du tri", ex);
            throw new IllegalArgumentException("erreur en base, table : " + tableName);
        }
    }


    List<Row> sortRows(List<Row> rows, String orderClause, SQLFieldList sqlTypes) {
        if (orderClause == null) {
            return rows;
        }

        return new RowSorter(rows)
              .orderBy(orderClause)
              .withSqlType(sqlTypes)
              .sort();
    }


    private SqlTable toSqlTable(Table table) {
        SqlTable sqlTable = SqlTable.table(table.getName());
        sqlTable.setTemporary(table.isTemporary());
        return sqlTable;
    }


    private void printErrorMessageRowLocation(Row row, StringBuilder report) {
        LocationPointer locationPointer = row.getLocationPointer();
        if (locationPointer != null) {
            report.append("Erreur à la ligne suivante :\n");
            locationPointer.accept(new LoggerLocationVisitor(report));
        }
    }


    static class RowSorter implements java.util.Comparator<Row> {
        private List<Row> rows;
        private List<String> orderClauseFields;
        private SQLFieldList sqlFields;


        RowSorter(List<Row> rows) {
            this.rows = rows;
        }


        public RowSorter orderBy(String orderClause) {
            StringTokenizer tokenizer = new StringTokenizer(orderClause, ",");
            orderClauseFields = new ArrayList<String>();
            while (tokenizer.hasMoreTokens()) {
                orderClauseFields.add(tokenizer.nextToken().trim());
            }
            if (orderClauseFields.isEmpty()) {
                throw new IllegalArgumentException("Mauvaise clause order by");
            }
            return this;
        }


        public RowSorter withSqlType(SQLFieldList sqlFieldList) {
            this.sqlFields = sqlFieldList;
            return this;
        }


        public List<Row> sort() {
            List<Row> orderedRows = new ArrayList<Row>(rows);
            Collections.sort(orderedRows, this);
            return orderedRows;
        }


        public int compare(Row row1, Row row2) {
            int compare = compareFromOrderClauseFields(row1, row2);
            if (compare != 0) {
                return compare;
            }
            else {
                return rows.indexOf(row1) - rows.indexOf(row2);
            }
        }


        private int compareFromOrderClauseFields(Row row1, Row row2) {
            return compareFromFields(row1, row2, orderClauseFields);
        }


        private int compareFromFields(Row row1, Row row2, Collection<String> fields) {
            FieldMap fields1 = row1.getFields();
            FieldMap fields2 = row2.getFields();
            for (String field : fields) {
                String value1 = getFieldValue(fields1, field);
                String value2 = getFieldValue(fields2, field);

                if (value1 == null && value2 == null) {
                    continue;
                }

                if (value1 == null) {
                    return -1;
                }

                if (value2 == null) {
                    return 1;
                }

                if (isNumberType(sqlFields.getFieldType(field))
                    && isNumber(value1)
                    && isNumber(value2)) {
                    int compare = compareNumbers(value1, value2);
                    if (compare != 0) {
                        return compare;
                    }
                }
                else {
                    int compare = value1.compareTo(value2);
                    if (compare != 0) {
                        return compare;
                    }
                }
            }

            return 0;
        }


        private static String getFieldValue(FieldMap fields, String fieldName) {
            String value = null;
            if (fields.containsField(fieldName)) {
                value = fields.get(fieldName).getValue();
            }
            return value;
        }


        private static Number getNumberValue(String value1) throws ParseException {
            return NumberFormat.getInstance().parse(value1.replace('.', ','));
        }


        private static int compareNumbers(String value1, String value2) {
            try {
                Number number1 = getNumberValue(value1);
                Number number2 = getNumberValue(value2);
                double compare = number1.doubleValue() - number2.doubleValue();
                if (compare != 0) {
                    return (compare > 0 ? 1 : -1);
                }
            }
            catch (ParseException e) {
                // Rien à faire.
            }
            return 0;
        }


        private static boolean isNumberType(int fieldType) {
            switch (fieldType) {
                case Types.BIGINT:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.INTEGER:
                case Types.NUMERIC:
                case Types.REAL:
                case Types.SMALLINT:
                case Types.TINYINT:
                    return true;
                default:
                    return false;
            }
        }


        private static boolean isNumber(String fieldValue) {
            for (int i = 0; i < fieldValue.length(); i++) {
                char currentChar = fieldValue.charAt(i);
                if (!Character.isDigit(currentChar)
                    && currentChar != '.'
                    && currentChar != '-') {
                    return false;
                }
            }
            return true;
        }
    }
}
