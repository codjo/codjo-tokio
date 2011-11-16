package net.codjo.tokio;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Location;
import net.codjo.tokio.model.LocationPointer;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import static net.codjo.tokio.util.RowUtil.field;
import static net.codjo.tokio.util.RowUtil.row;
import static net.codjo.tokio.util.RowUtil.uniqueKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import org.junit.Test;
/**
 *
 */
public class RequiredInstancesManagerTest {
    private RequiredInstancesManager requiredInstancesManager
          = new RequiredInstancesManager();


    @Test
    public void test_nominal() {
        DataSet dataset1 = new DataSet();
        DataSet inputRequiredDataset1 = requiredInstancesManager.getRequiredDataset(dataset1);
        inputRequiredDataset1.addRow("AP_TABLE", row("row1", field("FIELD", "VALUE1")));

        DataSet dataset2 = new DataSet();
        DataSet inputRequiredDataset2 = requiredInstancesManager.getRequiredDataset(dataset2);
        inputRequiredDataset2.addRow("AP_TABLE", row("row2", field("FIELD", "VALUE2")));

        assertEquals(0, dataset1.getTableCount());
        assertEquals(0, dataset2.getTableCount());

        requiredInstancesManager.finalizeDatasets();

        assertSame(inputRequiredDataset1, requiredInstancesManager.getRequiredDataset(dataset1));

        assertEquals(1, dataset1.getTableCount());
        assertEquals(1, dataset2.getTableCount());
        assertFieldValue("VALUE1", dataset1, "AP_TABLE", "row1", "FIELD");
        assertFieldValue("VALUE2", dataset2, "AP_TABLE", "row2", "FIELD");
    }


    @Test
    public void test_finalizeDatasets() throws Exception {
        DataSet dataset = new DataSet();
        dataset.addRow("AP_TABLE", row("row1", field("FIRSTNAME", "John"), field("LASTNAME", "SMITH")));

        DataSet requiredDataset = requiredInstancesManager.getRequiredDataset(dataset);
        requiredDataset.addRow("AP_TABLE",
                               row("row2", field("FIRSTNAME", "Sarah"), field("LASTNAME", "CONNOR")));
        requiredDataset.addRow("AP_TABLE",
                               row(field("FIRSTNAME", "John"), field("LASTNAME", "SMITH")));
        requiredDataset.addRow("AP_REQUIRED", row("row4", field("ID", "1")));

        requiredInstancesManager.finalizeDatasets();

        assertEquals(2, dataset.getTableCount());

        Table table1 = dataset.getTable("AP_TABLE");
        assertEquals(2, table1.getRowCount());

        assertFieldValue("Sarah", dataset, "AP_TABLE", "row2", "FIRSTNAME");
        assertFieldValue("CONNOR", dataset, "AP_TABLE", "row2", "LASTNAME");

        assertFieldValue("John", dataset, "AP_TABLE", "row1", "FIRSTNAME");
        assertFieldValue("SMITH", dataset, "AP_TABLE", "row1", "LASTNAME");

        Table table2 = dataset.getTable("AP_REQUIRED");
        assertEquals(1, table2.getRowCount());

        assertFieldValue("1", dataset, "AP_REQUIRED", "row4", "ID");
    }


    @Test
    public void test_keepDatasetOrdered() throws Exception {
        DataSet firstInsertedDataset = new DataSet() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        firstInsertedDataset.addRow("AP_TABLE", row("ne doit pas planter.",
                                                    field("FIRSTNAME", "john")));
        DataSet requiredDataset1 = requiredInstancesManager.getRequiredDataset(firstInsertedDataset);
        requiredDataset1.addRow("AP_TABLE", row(field("FIRSTNAME", "john"), field("LASTNAME", "WESTON2")));

        DataSet secondInsertedDataset = new DataSet() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        secondInsertedDataset.addRow("AP_TABLE", row("doit planter !!!",
                                                     field("FIRSTNAME", "john"), field("LASTNAME", "SMITH")));
        DataSet requiredDataset2 = requiredInstancesManager.getRequiredDataset(secondInsertedDataset);
        requiredDataset2.addRow("AP_TABLE", row(uniqueKey("FIRSTNAME"),
                                                field("FIRSTNAME", "john"), field("LASTNAME", "WESTON")));

        try {
            requiredInstancesManager.finalizeDatasets();
            fail();
        }
        catch (Exception e) {
            assertEquals(2, firstInsertedDataset.getTable("AP_TABLE").getRowCount());
        }
    }


    @Test
    public void test_includeRow() throws Exception {
        DataSet dataset = new DataSet();

        Row row1 = row(field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("AGE", "69"));
        dataset.addRow("AP_TABLE", row1);

        Row row2 = row(field("FIRSTNAME", "sarah"), field("LASTNAME", "SMITH"));
        dataset.addRow("AP_TABLE", row2);

        DataSet requiredDataset = requiredInstancesManager.getRequiredDataset(dataset);

        Row row3 = row(uniqueKey("FIRSTNAME"),
                       field("FIRSTNAME", "sophie"), field("LASTNAME", "SMITH"));
        requiredDataset.addRow("AP_TABLE", row3);

        Row row4 = row(uniqueKey("FIRSTNAME", "LASTNAME"),
                       field("FIRSTNAME", "sophie"), field("LASTNAME", "SMITH"));
        requiredDataset.addRow("AP_TABLE", row4);

        Row row5 = row(field("FIRSTNAME", "sarah"), field("LASTNAME", "CONNOR"));
        requiredDataset.addRow("AP_TABLE", row5);

        requiredInstancesManager.finalizeDatasets();

        assertEquals(4, dataset.getTable("AP_TABLE").getRowCount());
        assertThat(dataset.getTable("AP_TABLE").getRows(),
                   allOf(hasItems(row1, row2, row5), anyOf(hasItem(row3), hasItem(row4))));
    }


    @Test
    public void test_ignoreRow() throws Exception {
        DataSet dataset = new DataSet();

        Row row1 = row(field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("AGE", "69"));
        dataset.addRow("AP_TABLE", row1);

        Row row2 = row(field("FIRSTNAME", "sarah"), field("LASTNAME", "SMITH"));
        dataset.addRow("AP_TABLE", row2);

        DataSet requiredDataset = requiredInstancesManager.getRequiredDataset(dataset);

        Row row3 = row(field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("AGE", "69"));
        requiredDataset.addRow("AP_TABLE", row3);

        Row row4 = row(uniqueKey("FIRSTNAME"),
                       field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("AGE", "69"));
        requiredDataset.addRow("AP_TABLE", row4);

        Row row5 = row(uniqueKey("FIRSTNAME"), field("FIRSTNAME", "john"));
        requiredDataset.addRow("AP_TABLE", row5);

        Row row6 = row(uniqueKey("LASTNAME"), field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"));
        requiredDataset.addRow("AP_TABLE", row6);

        requiredInstancesManager.finalizeDatasets();

        assertEquals(2, dataset.getTable("AP_TABLE").getRowCount());
        assertThat(dataset.getTable("AP_TABLE").getRows(),
                   hasItems(row1, row2));
    }


    @Test
    public void test_rejectRow_conflictWithUniqueKey() throws Exception {
        DataSet dataset = new DataSet();

        Row row1 = row(field("FIRSTNAME", "john"),
                       field("LASTNAME", "SMITH"),
                       field("AGE", "69"),
                       field("GENDER", "female"));
        row1.setLocationPointer(new LocationPointer(new Location("file1.tokio", 26, "row")));
        dataset.addRow("AP_TABLE", row1);

        Row row2 = row(field("FIRSTNAME", "sarah"),
                       field("LASTNAME", "SMITH"),
                       field("AGE", "7"),
                       field("GENDER", "female"),
                       field("STATE", "france"));
        row2.setLocationPointer(new LocationPointer(new Location("file2.tokio", 54, "row")));
        dataset.addRow("AP_TABLE", row2);

        DataSet requiredDataset = requiredInstancesManager.getRequiredDataset(dataset);

        Row row3 = row(uniqueKey("LASTNAME", "GENDER"),
                       field("FIRSTNAME", "sarah"),
                       field("LASTNAME", "SMITH"),
                       field("AGE", "69"),
                       field("GENDER", "female"));
        row3.setLocationPointer(new LocationPointer(new Location("family.entities", 65, "row")));
        requiredDataset.addRow("AP_TABLE", row3);

        try {
            requiredInstancesManager.finalizeDatasets();
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals("Des lignes sont en conflit avec un required.\n"
                         + "\n"
                         + "ligne required :\n"
                         + "row(family.entities:65)\n"
                         + "\n"
                         + "ligne(s) en conflit :\n"
                         + "row(file1.tokio:26)\n"
                         + "row(file2.tokio:54)\n",
                         e.getMessage());
        }
    }


    @Test(expected = TokioLoaderException.class)
    public void test_rejectRow_conflictWithUniqueKey_insertOrder() throws Exception {
        DataSet requiredDataset = requiredInstancesManager.getRequiredDataset(new DataSet());

        Row row1 = row(uniqueKey("LASTNAME"),
                       field("FIRSTNAME", "sarah"), field("LASTNAME", "SMITH"));
        requiredDataset.addRow("AP_TABLE", row1);

        Row row2 = row(field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"));
        requiredDataset.addRow("AP_TABLE", row2);

        Row row3 = row(uniqueKey("LASTNAME"),
                       field("FIRSTNAME", "sarah"), field("LASTNAME", "SMITH"));
        requiredDataset.addRow("AP_TABLE", row3);

        Row row4 = row(field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"));
        requiredDataset.addRow("AP_TABLE", row4);

        requiredInstancesManager.finalizeDatasets();
    }


    @Test
    public void test_rejectRow_tooManyPossibilities() throws Exception {
        DataSet dataset = new DataSet();

        Row row1 = row(field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("AGE", "69"));
        row1.setLocationPointer(new LocationPointer(new Location("file1.tokio", 26, "row")));
        dataset.addRow("AP_TABLE", row1);

        Row row2 = row(field("FIRSTNAME", "sarah"), field("LASTNAME", "SMITH"));
        row2.setLocationPointer(new LocationPointer(new Location("file2.tokio", 54, "row")));
        dataset.addRow("AP_TABLE", row2);

        DataSet requiredDataset = requiredInstancesManager.getRequiredDataset(dataset);

        Row row3 = row(uniqueKey("LASTNAME"),
                       field("LASTNAME", "SMITH"), field("GENDER", "female"));
        row3.setLocationPointer(new LocationPointer(new Location("family.entities", 65, "row")));
        requiredDataset.addRow("AP_TABLE", row3);

        try {
            requiredInstancesManager.finalizeDatasets();
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals(
                  "Impossible de déterminer une ligne correspondant au required : trop de possibilités.\n"
                  + "\n"
                  + "ligne required :\n"
                  + "row(family.entities:65)\n"
                  + "\n"
                  + "ligne(s) en conflit :\n"
                  + "row(file1.tokio:26)\n"
                  + "row(file2.tokio:54)\n",
                  e.getMessage());
        }
    }


    @Test
    public void test_replaceRow() throws Exception {
        DataSet dataset = new DataSet();

        Row row1 = row(field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("AGE", "69"));
        dataset.addRow("AP_TABLE", row1);

        DataSet requiredDataset = requiredInstancesManager.getRequiredDataset(dataset);

        Row row3 = row(uniqueKey("FIRSTNAME", "LASTNAME"),
                       field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("GENDER", "male"));
        requiredDataset.addRow("AP_TABLE", row3);

        Row row4 = row(uniqueKey("FIRSTNAME", "LASTNAME"),
                       field("FIRSTNAME", "john"), field("LASTNAME", "SMITH"), field("WEIGHT", "125"));
        requiredDataset.addRow("AP_TABLE", row4);

        requiredInstancesManager.finalizeDatasets();

        assertEquals(1, dataset.getTable("AP_TABLE").getRowCount());
        assertSame(row1, dataset.getTable("AP_TABLE").getRow(0));
        assertEquals(5, row1.getFieldCount());
        assertEquals("john", row1.getFields().get("FIRSTNAME").getValue());
        assertEquals("SMITH", row1.getFields().get("LASTNAME").getValue());
        assertEquals("69", row1.getFields().get("AGE").getValue());
        assertEquals("male", row1.getFields().get("GENDER").getValue());
        assertEquals("125", row1.getFields().get("WEIGHT").getValue());
    }


    private void assertFieldValue(String expectedFieldValue,
                                  DataSet inputDataset,
                                  String tableName,
                                  String rowId,
                                  String fieldName) {
        assertEquals(expectedFieldValue,
                     inputDataset.getTable(tableName)
                           .getRowById(rowId)
                           .getFields()
                           .get(fieldName).getValue());
    }
}
