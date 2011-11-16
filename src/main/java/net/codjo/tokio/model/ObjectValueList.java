package net.codjo.tokio.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObjectValueList implements Iterable<ObjectValue> {
    private final List<ObjectValue> objectValues = new ArrayList<ObjectValue>();


    public ObjectValueList(ObjectValue... values) {
        objectValues.addAll(Arrays.asList(values));
    }


    public ObjectValueList(ObjectValue prefix, ObjectValueList splittedValue) {
        objectValues.add(prefix);
        objectValues.addAll(splittedValue.objectValues);
    }


    public Iterator<ObjectValue> iterator() {
        return objectValues.iterator();
    }


    public String getValue() {
        if (objectValues.isEmpty()) {
            return "";
        }
        if (objectValues.size() == 1 && objectValues.get(0) instanceof NullValue) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (ObjectValue objectValue : objectValues) {
            String value = objectValue.getValue();
            if (value != null) {
                builder.append(value);
            }
        }
        return builder.toString();
    }


    public boolean containsGeneratedValue() {
        for (ObjectValue objectValue : objectValues) {
            if (objectValue.isGenerated()) {
                return true;
            }
        }
        return false;
    }


    public ObjectValueList duplicate() {
        List<ObjectValue> duplicateValues = new ArrayList<ObjectValue>();
        for (ObjectValue objectValue : objectValues) {
            duplicateValues.add(objectValue.duplicate());
        }
        return new ObjectValueList(duplicateValues.toArray(new ObjectValue[duplicateValues.size()]));
    }


    public List<ObjectValue> getValueAsObjects() {
        return objectValues;
    }


    public List<VariableValue> findVariableValues() {
        List<VariableValue> result = new ArrayList<VariableValue>();
        for (ObjectValue objectValue : this) {
            if (VariableValue.class.isInstance(objectValue)) {
                result.add((VariableValue)objectValue);
            }
        }
        return result;
    }


    public void replace(VariableValue variableValue, ObjectValueList objectValueList) {
        int index = objectValues.indexOf(variableValue);
        objectValues.remove(index);
        objectValues.addAll(index, objectValueList.getValueAsObjects());
    }
}
