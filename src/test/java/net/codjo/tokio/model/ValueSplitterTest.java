package net.codjo.tokio.model;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 *
 */
public class ValueSplitterTest {

    @Test
    public void test_nominal() throws Exception {
        String toSplit = "prefix@variable1@@variable2@suffix@mail.com";
        ObjectValueList objectValueList = ValueSplitter.split(toSplit);

        List<ObjectValue> objectValues = objectValueList.getValueAsObjects();
        assertEquals(4, objectValues.size());

        assertValueAsObject(StringValue.class, "prefix", objectValues.get(0));
        assertValueAsObject(VariableValue.class, "@variable1@", objectValues.get(1));
        assertValueAsObject(VariableValue.class, "@variable2@", objectValues.get(2));
        assertValueAsObject(StringValue.class, "suffix@mail.com", objectValues.get(3));
    }


    @Test
    public void test_noValueToSplit() throws Exception {
        assertNotNull(ValueSplitter.split(null));
        assertNotNull(ValueSplitter.split(""));
    }


    private void assertValueAsObject(Class expectedClass, String expectedValue, ObjectValue actual) {
        assertTrue(expectedClass.isInstance(actual));
        assertEquals(expectedValue, actual.getValue());
    }
}
