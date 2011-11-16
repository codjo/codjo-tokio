package net.codjo.tokio.model;
import net.codjo.tokio.util.RowUtil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class RowIdLocationPointerTest {
    private Row parentRow = RowUtil.row("parent");
    private Row childRow = RowUtil.row();
    private RowIdLocationPointer locationPointer = new RowIdLocationPointer(childRow);


    @Before
    public void setUp() {
        childRow.setRefId("parent");
        childRow.getLocationPointer().getLocations().get(0).setLocationPointer(locationPointer);
    }


    @Test
    public void test_getLocations() throws Exception {
        DataSet dataset = new DataSet();
        dataset.addRow("AP_TABLE", childRow);

        assertTrue(locationPointer.getLocations().isEmpty());

        dataset.addRow("AP_TABLE", parentRow);

        assertEquals(1, locationPointer.getLocations().size());
        assertEquals(parentRow.getLocationPointer().getLocations(), locationPointer.getLocations());
    }
}
