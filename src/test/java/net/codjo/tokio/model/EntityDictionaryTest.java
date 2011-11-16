package net.codjo.tokio.model;
import static net.codjo.tokio.util.RowUtil.field;
import static net.codjo.tokio.util.RowUtil.row;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class EntityDictionaryTest {
    private EntityDictionary entityDictionary;


    @Before
    public void setUp() {
        entityDictionary = new EntityDictionary();
        entityDictionary.addRow("father", "table", row(field("field", "value")));
        entityDictionary.addRow("father.son", "table", row(field("field2", "value2")));
        entityDictionary.addRow("father.son", "table1", row(field("aField", "aValue")));
        entityDictionary.addRow("mother.son", "table2", row(field("aField", "aValue")));
    }


    @Test
    public void test_addRow() throws Exception {
        Assert.assertTrue(entityDictionary.contains("father"));
        Assert.assertTrue(entityDictionary.contains("father.son"));
        Assert.assertTrue(entityDictionary.contains("mother"));
    }


    @Test
    public void test_getTableNames() throws Exception {
        String[] tableNames = entityDictionary.getTableNames("father");
        Assert.assertEquals(2, tableNames.length);
        Assert.assertEquals("table", tableNames[0]);
        Assert.assertEquals("table1", tableNames[1]);

        tableNames = entityDictionary.getTableNames("noOne");
        Assert.assertEquals(0, tableNames.length);
    }


    @Test
    public void test_getRows() throws Exception {
        Row[] fatherRows = entityDictionary.getRows("father", "table");
        Assert.assertEquals(2, fatherRows.length);

        Row[] sonRows = entityDictionary.getRows("father", "table1");
        Assert.assertEquals(1, sonRows.length);

        Row[] noRows = entityDictionary.getRows("father", "nonono");
        Assert.assertEquals(0, noRows.length);

        noRows = entityDictionary.getRows("noOne", "table");
        Assert.assertEquals(0, noRows.length);
    }
}
