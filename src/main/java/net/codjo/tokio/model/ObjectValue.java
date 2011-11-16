package net.codjo.tokio.model;

public interface ObjectValue {
    String getValue();


    boolean isGenerated();


    ObjectValue duplicate();
}
