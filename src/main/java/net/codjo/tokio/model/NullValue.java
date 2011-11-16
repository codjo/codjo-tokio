package net.codjo.tokio.model;
public class NullValue implements ObjectValue {

    public String getValue() {
        return null;
    }


    public boolean isGenerated() {
        return false;
    }


    public ObjectValue duplicate() {
        return new NullValue();
    }
}
