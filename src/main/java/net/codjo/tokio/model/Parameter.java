package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;

public class Parameter {
    private ObjectValueList objectValueList;


    public Parameter(Parameter parameter) {
        this.objectValueList = parameter.objectValueList.duplicate();
    }


    public Parameter(String value) {
        this(ValueSplitter.split(value));
        if (Entity.NULL_VALUE.equals(value)) {
            this.objectValueList = new ObjectValueList(new NullValue());
        }
    }


    public Parameter(GeneratorConfiguration generator) {
        this(new ObjectValueList(new GeneratedValue(generator)));
    }


    public Parameter(ObjectValueList objectValueList) {
        this.objectValueList = objectValueList;
    }


    public String getValue() {
        if (Entity.NULL_VALUE.equals(objectValueList.getValue())) {
            return null;
        }
        return objectValueList.getValue();
    }


    public void setValue(String value) {
        objectValueList = ValueSplitter.split(value);
    }


    public ObjectValueList getValueObjectList() {
        return objectValueList;
    }


    public void setValueObjectList(ObjectValueList objectValueList) {
        this.objectValueList = objectValueList;
    }


    public void replace(VariableValue variableValue, Parameter parentParameter) {
        objectValueList.replace(variableValue, parentParameter.getValueObjectList());
    }
}