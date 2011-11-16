package net.codjo.tokio.model;
public class VariableValue implements ObjectValue {
    private String value;


    VariableValue(String value) {
        this.value = value;
    }


    public String getValue() {
        return "@" + value + "@";
    }


    public String getName() {
        return value;
    }


    public boolean isGenerated() {
        return false;
    }


    public ObjectValue duplicate() {
        return new VariableValue(value);
    }
}
