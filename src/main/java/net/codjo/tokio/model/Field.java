/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;

public class Field {
    private String name;
    private ObjectValueList objectValueList;


    public Field(Field field) {
        this.name = field.name;
        this.objectValueList = field.objectValueList.duplicate();
    }


    public Field(String name) {
        this(name, new ObjectValueList(new NullValue()));
    }


    public Field(String name, String value) {
        this(name, ValueSplitter.split(value));
    }


    public Field(String name, GeneratorConfiguration generatorConfiguration) {
        this(name, new ObjectValueList(new GeneratedValue(generatorConfiguration)));
    }


    public Field(String name, ObjectValueList objectValueList) {
        this.name = name;
        this.objectValueList = objectValueList;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getValue() {
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


    public boolean containsGeneratedValue() {
        return objectValueList.containsGeneratedValue();
    }


    public void replace(VariableValue variableValue, Parameter parentParameter) {
        objectValueList.replace(variableValue, parentParameter.getValueObjectList());
    }


    public boolean isSame(Field field) {
        if (this == field) {
            return true;
        }
        if (field == null) {
            return false;
        }

        if (name != null ? !name.equals(field.name) : field.name != null) {
            return false;
        }

        if (getValue() != null ? !getValue().equals(field.getValue()) : field.getValue() != null) {
            return false;
        }

        return true;
    }


    @Override
    public String toString() {
        return getName() + "=" + getValue();
    }
}
