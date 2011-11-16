package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FieldTest {
    private static final String STRING_CONSTANT = "this is a string value";
    private static final GeneratorConfiguration GENERATOR_CONFIGURATION =
          new GeneratorConfiguration(GeneratorConfiguration.GENERATE_STRING, "20");


    @Test
    public void test_stringValue() throws Exception {
        Field field = new Field("title", STRING_CONSTANT);

        assertEquals(STRING_CONSTANT, field.getValue());
        Iterator<ObjectValue> objectIterator = field.getValueObjectList().iterator();
        assertTrue(objectIterator.next() instanceof StringValue);
        assertFalse(objectIterator.hasNext());
    }


    @Test
    public void test_generatedValue() throws Exception {
        Field field = new Field("title", GENERATOR_CONFIGURATION);

        assertEquals(GeneratorConfiguration.GENERATE_STRING + "(20)", field.getValue());
        Iterator<ObjectValue> objectIterator = field.getValueObjectList().iterator();
        assertTrue(objectIterator.next() instanceof GeneratedValue);
        assertFalse(objectIterator.hasNext());
    }


    @Test
    public void test_compositeValue() throws Exception {
        Field field = new Field("title");
        field.setValueObjectList(new ObjectValueList(new GeneratedValue(GENERATOR_CONFIGURATION),
                                                     new StringValue(STRING_CONSTANT)));

        assertEquals(GeneratorConfiguration.GENERATE_STRING + "(20)" + STRING_CONSTANT, field.getValue());
    }


    @Test
    public void test_variableValue() throws Exception {
        Field field = new Field("title", "@variable@");
        assertEquals("@variable@", field.getValue());
        Iterator<ObjectValue> objectIterator = field.getValueObjectList().iterator();
        assertTrue(objectIterator.next() instanceof VariableValue);
        assertFalse(objectIterator.hasNext());

        field = new Field("title", "@variable1@@variable2@toto@gmail.com");
        assertEquals("@variable1@@variable2@toto@gmail.com", field.getValue());

        objectIterator = field.getValueObjectList().iterator();
        assertValueAsObject(VariableValue.class, "@variable1@", objectIterator.next());
        assertValueAsObject(VariableValue.class, "@variable2@", objectIterator.next());
        assertValueAsObject(StringValue.class, "toto@gmail.com", objectIterator.next());
        assertFalse(objectIterator.hasNext());

        field = new Field("title", "variable1@variable2@toto@gmail.com");
        assertEquals("variable1@variable2@toto@gmail.com", field.getValue());

        objectIterator = field.getValueObjectList().iterator();
        assertValueAsObject(StringValue.class, "variable1", objectIterator.next());
        assertValueAsObject(VariableValue.class, "@variable2@", objectIterator.next());
        assertValueAsObject(StringValue.class, "toto@gmail.com", objectIterator.next());
        assertFalse(objectIterator.hasNext());
    }


    @Test
    public void test_constructor() throws Exception {
        Field field = new Field(new Field("field string", STRING_CONSTANT));
        assertEquals("field string", field.getName());
        assertEquals(STRING_CONSTANT, field.getValue());

        field = new Field(new Field("field generator", GENERATOR_CONFIGURATION));
        assertEquals("field generator", field.getName());
        assertEquals(GeneratorConfiguration.GENERATE_STRING + "(20)", field.getValue());

        Field sourceField = new Field("field composite");
        sourceField.setValueObjectList(new ObjectValueList(new StringValue(STRING_CONSTANT),
                                                           new GeneratedValue(GENERATOR_CONFIGURATION)));
        field = new Field(sourceField);
        assertEquals("field composite", field.getName());
        assertEquals(STRING_CONSTANT + GeneratorConfiguration.GENERATE_STRING + "(20)", field.getValue());
    }


    @Test
    public void test_isSame() throws Exception {
        Field field1 = new Field("something");
        Field field2 = new Field("something");
        Field field3 = new Field("something different");

        assertTrue(field1.isSame(field2));
        assertFalse(field1.isSame(field3));

        field1.setValue("some value");

        assertFalse(field1.isSame(field2));
        assertFalse(field1.isSame(field3));

        field2.setValue("some value");
        field3.setValue("some value");

        assertTrue(field1.isSame(field2));
        assertFalse(field1.isSame(field3));
    }


    private void assertValueAsObject(Class expectedClass, String expectedValue, ObjectValue actual) {
        assertTrue(expectedClass.isInstance(actual));
        assertEquals(expectedValue, actual.getValue());
    }
}
