package net.codjo.tokio.model;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ParameterTest {

    @Test
    public void test_replace() throws Exception {
        Parameter parentParameter = new Parameter("Parent Parameter");
        Parameter sonParameter = new Parameter("@parameter1@ with suffix");

        sonParameter.replace((VariableValue)sonParameter.getValueObjectList().getValueAsObjects().get(0),
                             parentParameter);

        assertEquals("Parent Parameter with suffix", sonParameter.getValue());
    }
}
