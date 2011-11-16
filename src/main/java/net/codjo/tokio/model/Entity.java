/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class Entity {
    public static final String NULL_VALUE = "null";
    private final Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    private final DataSet dataSet = new DataSet();
    private final DataSet requiredDataSet = new DataSet();
    private final EntityList entityModelList = new EntityList();
    private final List<EntityDeclaration> entityDeclarationList = new ArrayList<EntityDeclaration>();
    private String name;


    public Entity(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public String[] getParameters() {
        return parameters.keySet().toArray(new String[parameters.size()]);
    }


    public DataSet getDataset() {
        return dataSet;
    }


    public DataSet getRequiredDataSet() {
        return requiredDataSet;
    }


    public boolean hasDefaultValue(String parameter) {
        return parameters.get(parameter) != null;
    }


    public Parameter getDefaultValue(String parameter) {
        return parameters.get(parameter);
    }


    public EntityList getEntityModelList() {
        return entityModelList;
    }


    public List<EntityDeclaration> getEntityDeclarationList() {
        return entityDeclarationList;
    }


    public void addParameter(String parameter) {
        parameters.put(parameter, null);
    }


    public void addParameter(String parameter, Parameter value) {
        parameters.put(parameter, value);
    }


    public void addParameter(String parameter, GeneratorConfiguration generator) {
        if (generator != null) {
            parameters.put(parameter, new Parameter(generator));
        }
        else {
            parameters.put(parameter, null);
        }
    }


    public void addParameter(String parameter, String defaultValue, GeneratorConfiguration generator) {
        if (generator != null) {
            parameters.put(parameter, new Parameter(generator));
        }
        else if (defaultValue != null) {
            parameters.put(parameter, new Parameter(defaultValue));
        }
        else {
            parameters.put(parameter, null);
        }
    }


    public Parameter getParameter(String parameter) {
        return parameters.get(parameter);
    }


    public boolean containsParameter(String parameterName) {
        return parameters.keySet().contains(parameterName);
    }


    @Deprecated
    //utilisation parallèle du requiredDataset
    public Row getRow(String rowId) {
        return dataSet.getRow(rowId);
    }
}
