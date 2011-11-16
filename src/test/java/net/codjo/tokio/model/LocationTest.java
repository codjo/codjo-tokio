package net.codjo.tokio.model;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 *
 */
public class LocationTest {

    @Test
    public void test_isSame() throws Exception {
        Location location = new Location("myFile.tokio", 7, "row");

        assertFalse(location.isSame(new Location()));
        assertFalse(location.isSame(new Location("myFile.entities", 7, "row")));
        assertFalse(location.isSame(new Location("myFile.tokio", 77, "row")));
        assertFalse(location.isSame(new Location("myFile.tokio", 7, "create-entity")));

        assertTrue(location.isSame(new Location("myFile.tokio", 7, "row")));
        assertTrue(location.isSame(new Location("myFile.tokio", 7, "row", new LocationPointer())));
    }
}
