package net.codjo.tokio.model;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class ObjectValueListTest {
    @Test
    public void test_findVariableValues() throws Exception {
        VariableValue variableValue1 = new VariableValue("variable1");
        VariableValue variableValue2 = new VariableValue("variable2");
        VariableValue variableValue3 = new VariableValue("another variable to replace");
        ObjectValueList objectValueList = new ObjectValueList(new StringValue("This is the begin !!!"),
                                                              variableValue1,
                                                              variableValue2,
                                                              new StringValue("This is the end !!!"),
                                                              variableValue3);

        List<VariableValue> actual = objectValueList.findVariableValues();
        assertEquals(Arrays.asList(variableValue1, variableValue2, variableValue3), actual);
    }


    @Test
    public void test_replace() throws Exception {
        VariableValue variableToReplace = new VariableValue("variable to replace");
        ObjectValueList objectValueList = new ObjectValueList(new StringValue("This is the begin !!!"),
                                                              variableToReplace,
                                                              new StringValue("This is the end !!!"));

        StringValue replacement = new StringValue("replaced !!!");
        objectValueList.replace(variableToReplace, new ObjectValueList(replacement));

        assertEquals("This is the begin !!!replaced !!!This is the end !!!", objectValueList.getValue());
        assertSame(replacement, objectValueList.getValueAsObjects().get(1));
    }
}
