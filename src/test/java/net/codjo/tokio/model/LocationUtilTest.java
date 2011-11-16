package net.codjo.tokio.model;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class LocationUtilTest {

    @Test
    public void test_mergeLocationPointers() throws Exception {
        LocationPointer toUpdate = new LocationPointer();
        toUpdate.addLocation(
              new Location("parent.entities", 18, "row"));
        toUpdate = new LocationPointer(
              new Location("myFile.tokio", 7, "create-entity", toUpdate));

        LocationPointer toMerge = new LocationPointer();
        toMerge.addLocation(
              new Location("child.entities", 56, "row"));
        toMerge = new LocationPointer(
              new Location("myFile.tokio", 7, "create-entity", toMerge));

        LocationUtil.mergeLocationPointers(toUpdate, toMerge);

        StringBuilder stringBuilder = new StringBuilder();
        toUpdate.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("create-entity(myFile.tokio:7)\n"
                     + "\trow(parent.entities:18)\n"
                     + "\trow(child.entities:56)\n",
                     stringBuilder.toString());
    }
}
