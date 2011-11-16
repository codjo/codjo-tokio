package net.codjo.tokio.model;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class LocationPointerTest {
    private StringBuilder stringBuilder = new StringBuilder();


    @Test
    public void test_nominal() throws Exception {
        Location location1 = new Location("cases.tokio", 45, "row");
        LocationPointer locationPointer1 = new LocationPointer(location1);

        Location location2 = new Location("cases.tokio", 78, "copy", locationPointer1);
        LocationPointer locationPointer2 = new LocationPointer(location2);

        locationPointer1.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("row(cases.tokio:45)\n", stringBuilder.toString());

        stringBuilder = new StringBuilder();
        locationPointer2.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy(cases.tokio:78)\n"
                     + "\trow(cases.tokio:45)\n", stringBuilder.toString());
    }
}
