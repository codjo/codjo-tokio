package net.codjo.tokio;
import net.codjo.tokio.foreignkeys.ForeignKey;
import net.codjo.tokio.foreignkeys.ForeignKeyMetadata;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import static net.codjo.tokio.util.RowUtil.field;
import static net.codjo.tokio.util.RowUtil.nullField;
import static net.codjo.tokio.util.RowUtil.row;
import java.sql.Connection;
import java.sql.Types;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class ImplicitDataTransformerTest {
    private ForeignKeyMetadataMock foreignKeyMetadataMock = new ForeignKeyMetadataMock();
    private SQLFieldListBuilderMock sqlFieldListBuilderMock = new SQLFieldListBuilderMock();
    private ImplicitDataTransformer implicitDataTransformer =
          new ImplicitDataTransformer(foreignKeyMetadataMock, sqlFieldListBuilderMock);
    private DataSet dataset = new DataSet();
    private Connection connectionMock = Mockito.mock(Connection.class);


    @Before
    public void setUp() throws Exception {
        when(connectionMock.getCatalog()).thenReturn(null);

        sqlFieldListBuilderMock.mockField("BOOK", "TITLE", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("BOOK", "AUTHOR_LASTNAME", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("BOOK", "AUTHOR_FIRSTNAME", Types.VARCHAR);

        sqlFieldListBuilderMock.mockField("AUTHOR", "LASTNAME", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("AUTHOR", "FIRSTNAME", Types.VARCHAR);

        sqlFieldListBuilderMock.mockField("NOVELL", "TITLE", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("NOVELL", "AUTHOR_LASTNAME", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("NOVELL", "AUTHOR_FIRSTNAME", Types.VARCHAR);

        foreignKeyMetadataMock.mockForeignKey("BOOK", foreignKey("BOOK", asList("AUTHOR_LASTNAME",
                                                                                "AUTHOR_FIRSTNAME"),
                                                                 "AUTHOR", asList("LASTNAME",
                                                                                  "FIRSTNAME")));
    }


    @Test
    public void test_transform() throws Exception {
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Pug l'apprenti"),
                                   field("AUTHOR_LASTNAME", "Feist"),
                                   field("AUTHOR_FIRSTNAME", "Raymond E.")));

        implicitDataTransformer.transform(connectionMock, dataset);

        Table authorTable = dataset.getTable("AUTHOR");
        assertNotNull(authorTable);
        assertEquals(1, authorTable.getRowCount());

        FieldMap fieldMap = authorTable.getRow(0).getFields();
        assertEquals("Feist", fieldMap.get("LASTNAME").getValue());
        assertEquals("Raymond E.", fieldMap.get("FIRSTNAME").getValue());
    }


    @Test
    public void test_transform_shouldNotAddRow() throws Exception {
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Pug l'apprenti"),
                                   field("AUTHOR_LASTNAME", null),
                                   field("AUTHOR_FIRSTNAME", null)));

        implicitDataTransformer.transform(connectionMock, dataset);

        Table authorTable = dataset.getTable("AUTHOR");
        assertNull(authorTable);
    }


    @Test
    public void test_transform_shouldAddTwoRow() throws Exception {
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Pug l'apprenti"),
                                   field("AUTHOR_LASTNAME", "Feist"),
                                   field("AUTHOR_FIRSTNAME", "Raymond E.")));
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Le silmarillion"),
                                   field("AUTHOR_LASTNAME", "Tolkien"),
                                   field("AUTHOR_FIRSTNAME", "J. R. R.")));
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Dune")));
        dataset.addRow("AUTHOR", row(field("LASTNAME", "Tolkien"),
                                     field("FIRSTNAME", "J. R. R.")));

        implicitDataTransformer.transform(connectionMock, dataset);

        Table authorTable = dataset.getTable("AUTHOR");
        assertNotNull(authorTable);
        assertEquals(3, authorTable.getRowCount());

        FieldMap firstRow = authorTable.getRow(0).getFields();
        assertEquals("Tolkien", firstRow.get("LASTNAME").getValue());
        assertEquals("J. R. R.", firstRow.get("FIRSTNAME").getValue());
        assertNull(authorTable.getRow(0).isAutoComplete());

        FieldMap secondRow = authorTable.getRow(1).getFields();
        assertEquals("Feist", secondRow.get("LASTNAME").getValue());
        assertEquals("Raymond E.", secondRow.get("FIRSTNAME").getValue());
        assertTrue(authorTable.getRow(1).isAutoComplete());

        assertTrue(authorTable.getRow(2).isAutoComplete());
    }


    @Test
    public void test_transform_twoFromTables_shouldAddOneRow() throws Exception {
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Le seigneur des anneaux"),
                                   field("AUTHOR_LASTNAME", "Tolkien"),
                                   field("AUTHOR_FIRSTNAME", "J. R. R.")));
        dataset.addRow("NOVELL", row(true,
                                     field("TITLE", "Le silmarillion"),
                                     field("AUTHOR_LASTNAME", "Tolkien"),
                                     field("AUTHOR_FIRSTNAME", "J. R. R.")));

        implicitDataTransformer.transform(connectionMock, dataset);

        Table authorTable = dataset.getTable("AUTHOR");
        assertNotNull(authorTable);
        assertEquals(1, authorTable.getRowCount());

        FieldMap firstRow = authorTable.getRow(0).getFields();
        assertEquals("Tolkien", firstRow.get("LASTNAME").getValue());
        assertEquals("J. R. R.", firstRow.get("FIRSTNAME").getValue());
        assertTrue(authorTable.getRow(0).isAutoComplete());
    }


    @Test
    public void test_transform_nullValue() throws Exception {
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Pug l'apprenti"),
                                   nullField("AUTHOR_LASTNAME"),
                                   field("AUTHOR_FIRSTNAME", "Raymond E.")));

        dataset.addRow("AUTHOR", row(field("LASTNAME", "Tolkien"),
                                     field("FIRSTNAME", "J. R. R.")));

        implicitDataTransformer.transform(connectionMock, dataset);

        Table authorTable = dataset.getTable("AUTHOR");
        assertNotNull(authorTable);
        assertEquals(2, authorTable.getRowCount());

        FieldMap fieldMap = authorTable.getRow(1).getFields();
        assertNull(fieldMap.get("LASTNAME").getValue());
        assertEquals("Raymond E.", fieldMap.get("FIRSTNAME").getValue());
    }


    @Test
    public void test_transform_twoLevels_shouldAddTwoRows() throws Exception {
        sqlFieldListBuilderMock.mockField("AUTHOR", "AUTHOR_COUNTRY", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("COUNTRY", "REF", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("COUNTRY", "LABEL", Types.VARCHAR);

        foreignKeyMetadataMock.mockForeignKey("AUTHOR", foreignKey("AUTHOR", asList("AUTHOR_COUNTRY"),
                                                                   "COUNTRY", asList("REF")));

        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Le seigneur des anneaux"),
                                   field("AUTHOR_LASTNAME", "Tolkien"),
                                   field("AUTHOR_FIRSTNAME", "J. R. R.")));
        dataset.addRow("BOOK", row(true,
                                   field("TITLE", "Le silmarillion"),
                                   field("AUTHOR_LASTNAME", "Tolkien"),
                                   field("AUTHOR_FIRSTNAME", "J. R. R.")));

        implicitDataTransformer.transform(connectionMock, dataset);

        Table authorTable = dataset.getTable("AUTHOR");
        assertNotNull(authorTable);
        assertEquals(1, authorTable.getRowCount());

        FieldMap authorRow = authorTable.getRow(0).getFields();
        assertEquals("Tolkien", authorRow.get("LASTNAME").getValue());
        assertEquals("J. R. R.", authorRow.get("FIRSTNAME").getValue());
        assertTrue(authorTable.getRow(0).isAutoComplete());

        Table countryTable = dataset.getTable("COUNTRY");
        assertNotNull(countryTable);
        assertEquals(1, countryTable.getRowCount());

        assertTrue(countryTable.getRow(0).isAutoComplete());
    }


    @Test
    public void test_autoComplete() throws Exception {
        dataset.clear();
        dataset.addRow("AP_AUTOCOMPLETE", row(true, "myRow",
                                              field("01_FIELD1", "VALUE1"),
                                              field("02_FIELD2", null)));

        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "01_FIELD1", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "02_FIELD2", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "03_TINYINT", Types.TINYINT);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "04_SMALLINT", Types.SMALLINT);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "05_INTEGER", Types.INTEGER);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "06_REAL", Types.REAL);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "07_FLOAT", Types.FLOAT);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "08_DOUBLE", Types.DOUBLE);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "09_BIGINT", Types.BIGINT);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "10_DECIMAL", Types.DECIMAL);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "11_NUMERIC", Types.NUMERIC);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "12_BIT", Types.BIT);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "13_CHAR", Types.CHAR);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "14_VARCHAR", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "15_LONGVARCHAR", Types.LONGVARCHAR);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "16_DATE", Types.DATE);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "17_TIME", Types.TIME);
        sqlFieldListBuilderMock.mockField("AP_AUTOCOMPLETE", "18_TIMESTAMP", Types.TIMESTAMP);

        implicitDataTransformer.transform(connectionMock, dataset);

        Row row = dataset.getRow("myRow");
        assertEquals("VALUE1", row.getFields().get("01_FIELD1").getValue());
        assertNull(row.getFields().get("02_FIELD2").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("03_TINYINT").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("04_SMALLINT").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("05_INTEGER").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("06_REAL").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("07_FLOAT").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("08_DOUBLE").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("09_BIGINT").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("10_DECIMAL").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("11_NUMERIC").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_NUMBER, row.getFields().get("12_BIT").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_STRING, row.getFields().get("13_CHAR").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_STRING, row.getFields().get("14_VARCHAR").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_STRING, row.getFields().get("15_LONGVARCHAR").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_DATE, row.getFields().get("16_DATE").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_TIME, row.getFields().get("17_TIME").getValue());
        assertEquals(AutoCompleteConstants.DEFAULT_TIMESTAMP, row.getFields().get("18_TIMESTAMP").getValue());
    }


    @Test
    public void test_assertNoCycle() throws Exception {
        sqlFieldListBuilderMock.mockField("CYCLE", "CODE1", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("CYCLE", "CODE2", Types.VARCHAR);
        sqlFieldListBuilderMock.mockField("CYCLE", "LABEL", Types.VARCHAR);

        foreignKeyMetadataMock.mockForeignKey("CYCLE",
                                              foreignKey("CYCLE", asList("CODE1"),
                                                         "CYCLE", asList("CODE2")),
                                              foreignKey("CYCLE", asList("CODE2"),
                                                         "CYCLE", asList("CODE1")));

        dataset.addRow("CYCLE", row(true,
                                    field("CODE1", "VALUE1"),
                                    field("CODE2", "VALUE2"),
                                    field("LABEL", "LABEL_VALUE")));

        implicitDataTransformer.transform(connectionMock, dataset);

        Table table = dataset.getTable("CYCLE");
        assertEquals(4, table.getRowCount());

        FieldMap row1 = table.getRow(0).getFields();
        assertEquals("VALUE1", row1.get("CODE1").getValue());
        assertEquals("VALUE2", row1.get("CODE2").getValue());
        assertEquals("LABEL_VALUE", row1.get("LABEL").getValue());

        FieldMap row2 = table.getRow(1).getFields();
        assertEquals("Z", row2.get("CODE1").getValue());
        assertEquals("VALUE1", row2.get("CODE2").getValue());
        assertEquals("Z", row2.get("LABEL").getValue());

        FieldMap row3 = table.getRow(2).getFields();
        assertEquals("Z", row3.get("CODE1").getValue());
        assertEquals("Z", row3.get("CODE2").getValue());
        assertEquals("Z", row3.get("LABEL").getValue());

        FieldMap row4 = table.getRow(3).getFields();
        assertEquals("VALUE2", row4.get("CODE1").getValue());
        assertEquals("Z", row4.get("CODE2").getValue());
        assertEquals("Z", row4.get("LABEL").getValue());
    }


    private static ForeignKey foreignKey(String fromTableName, List<String> fromColNames,
                                         String toTableName, List<String> toColNames) {
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setFromTableName(fromTableName);
        for (String fromColName : fromColNames) {
            foreignKey.addFromColumnName(fromColName);
        }
        foreignKey.setToTableName(toTableName);
        for (String toColName : toColNames) {
            foreignKey.addToColumnName(toColName);
        }
        return foreignKey;
    }


    private static class ForeignKeyMetadataMock extends ForeignKeyMetadata {
        private final Map<String, List<ForeignKey>> tableToForeignKeys
              = new HashMap<String, List<ForeignKey>>();


        @Override
        public List<ForeignKey> findForeignKeys(Connection connection, String tableName) {
            if (tableToForeignKeys.containsKey(tableName)) {
                return tableToForeignKeys.get(tableName);
            }
            return Collections.emptyList();
        }


        public void mockForeignKey(String tableName, ForeignKey... foreignKey) {
            tableToForeignKeys.put(tableName, asList(foreignKey));
        }
    }
}
