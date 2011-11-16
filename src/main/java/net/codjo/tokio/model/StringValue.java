package net.codjo.tokio.model;
public class StringValue implements ObjectValue {
    private String value;


    public StringValue(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }


    public boolean isGenerated() {
        return false;
    }


    public ObjectValue duplicate() {
        return new StringValue(value);
    }
}
