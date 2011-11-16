/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.Iterator;
import java.util.Properties;
/**
 * Un scenario de test. Un scenario est constitué d'un ensemble de donnée en entrée et étalon.
 *
 * @author $Author: crego $
 * @version $Revision: 1.8 $
 */
public class Scenario {
    private RowDictionary dictionary = new RowDictionary();
    private DataSet inputDataSet = new DataSet();
    private DataSet outputDataSet = new DataSet();
    private Properties properties;
    private String comment;
    private String name;


    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public Properties getProperties() {
        return properties;
    }


    public Scenario(String name, String comment) {
        setName(name);
        setComment(comment);
        getInputDataSet().setRowDictionnary(dictionary);
        getOutputDataSet().setRowDictionnary(dictionary);
    }


    public Scenario(String name, String comment, RowDictionary dictionary) {
        setName(name);
        setComment(comment);
        this.dictionary = dictionary;
        getInputDataSet().setRowDictionnary(dictionary);
        getOutputDataSet().setRowDictionnary(dictionary);
    }


    public String getComment() {
        return comment;
    }


    public DataSet getInputDataSet() {
        return inputDataSet;
    }


    public Table getInputTable(String tableName) {
        return getInputDataSet().getTable(tableName);
    }


    public String getName() {
        return name;
    }


    public DataSet getOutputDataSet() {
        return outputDataSet;
    }


    public Table getOutputTable(String tableName) {
        return getOutputDataSet().getTable(tableName);
    }


    public void addInputRow(String tableName, Row row) {
        getInputDataSet().addRow(tableName, row);
    }


    public void addOutputRow(String tableName, Row row) {
        getOutputDataSet().addRow(tableName, row);
    }


    public void clear() {
        getOutputDataSet().clear();
        getInputDataSet().clear();
    }


    public Iterator inputTables() {
        return getInputDataSet().tables();
    }


    public Iterator outputTables() {
        return getOutputDataSet().tables();
    }


    @Override
    public String toString() {
        return name;
    }


    private void setComment(String comment) {
        this.comment = comment;
    }


    private void setName(String name) {
        this.name = name;
    }
}
