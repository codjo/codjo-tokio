/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;

abstract class AbstractComparator implements Comparator {
    private String param = null;
    private String typeAssert = null;


    protected AbstractComparator(String typeAssert) {
        this.typeAssert = typeAssert;
    }


    protected AbstractComparator() {
        this.typeAssert = getClass().getName();
    }


    public void setParam(String param) {
        this.param = param;
    }


    public String getParam() {
        return param;
    }


    public String getTypeAssert() {
        return typeAssert;
    }


    public String getReason() {
        return null;
    }
}
