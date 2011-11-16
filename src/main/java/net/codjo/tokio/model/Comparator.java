package net.codjo.tokio.model;

public interface Comparator {
    void setParam(String param);


    String getParam();


    String getTypeAssert();


    boolean isEqual(Object expected, Object actual, int sqlType);


    String getReason();
}
