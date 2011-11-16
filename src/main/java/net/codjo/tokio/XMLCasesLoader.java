/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.EntityDictionary;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.LocationUtil;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.RowDictionary;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 */
public class XMLCasesLoader extends XMLFileLoader implements XMLCasesTags, XMLScenarioLoader {
    private final IncludeEntitiesManager includeEntitiesManager;
    private final RequiredInstancesManager requiredInstancesManager;
    private final XMLStoryUtil xmlStoryUtil;


    public XMLCasesLoader(IncludeEntitiesManager includeEntitiesManager,
                          RequiredInstancesManager requiredInstancesManager,
                          XMLStoryUtil xmlStoryUtil) {
        this.includeEntitiesManager = includeEntitiesManager;
        this.requiredInstancesManager = requiredInstancesManager;
        this.xmlStoryUtil = xmlStoryUtil;
    }


    @Override
    public void parse(Document doc, String fileUri, Map<String, String> globalProperties)
          throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
        this.uri = fileUri;
        File workingDirectory = new File(new URL(fileUri).getPath()).getParentFile();
        EntityList entityList = new EntityList();
        EntityDictionary entityDictionary = new EntityDictionary();
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (CASE.equals(node.getNodeName())) {
                Scenario scenario = createScenario(node, globalProperties);
                loadCase(node,
                         scenario,
                         workingDirectory,
                         entityList,
                         entityDictionary);
                scenarii.addScenario(scenario);
            }
            else if (INCLUDE_ENTITIES.equals(node.getNodeName())) {
                includeEntitiesManager.process(node, workingDirectory, entityList);
            }
        }
    }


    @Override
    protected Scenario doCreateScenario(Node node) {
        return new Scenario(XmlUtil.getAttribute(node, CASE_ID), "", new RowDictionary());
    }


    private void loadCase(Node caseNode,
                          Scenario sc,
                          File workingDirectory,
                          EntityList entityList,
                          EntityDictionary entityDictionary)
          throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
        String inheritId = XmlUtil.getAttribute(caseNode, CASE_INHERIT_ID);
        if (inheritId != null) {
            try {
                Scenario inheritedCase = getScenario(inheritId);
                copyDataSet(inheritedCase.getInputDataSet(), sc.getInputDataSet(), caseNode);
                copyDataSet(inheritedCase.getOutputDataSet(), sc.getOutputDataSet(), caseNode);
                copyDataSet(requiredInstancesManager.getRequiredDataset(inheritedCase.getInputDataSet()),
                            requiredInstancesManager.getRequiredDataset(sc.getInputDataSet()),
                            caseNode);
                copyDataSet(requiredInstancesManager.getRequiredDataset(inheritedCase.getOutputDataSet()),
                            requiredInstancesManager.getRequiredDataset(sc.getOutputDataSet()),
                            caseNode);
            }
            catch (NoSuchElementException e) {
                throw new RuntimeException(computeUnknownInheritIdMessage(sc.getName(), inheritId));
            }
        }

        NodeList nodes = caseNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (INCLUDE_STORY.equals(node.getNodeName())) {
                xmlStoryUtil.includeStory(node,
                                          workingDirectory,
                                          sc,
                                          entityList,
                                          entityDictionary);
            }
            else if (SCENARIO_PROPERTIES.equals(nodes.item(i).getNodeName())) {
                XMLPropertiesLoader.loadProperties(sc, nodes.item(i), uri);
            }
            else if (SCENARIO_PROPERTY.equals(nodes.item(i).getNodeName())) {
                XMLPropertiesLoader.loadProperty(sc, nodes.item(i));
            }
            else if (CASE_INPUT.equals(node.getNodeName())) {
                xmlStoryUtil.loadDataSet(sc.getInputDataSet(),
                                         node,
                                         entityList,
                                         entityDictionary);
            }
            else if (CASE_OUTPUT.equals(node.getNodeName())) {
                xmlStoryUtil.loadDataSet(sc.getOutputDataSet(),
                                         node,
                                         entityList,
                                         entityDictionary);
            }
        }
    }


    private void copyDataSet(DataSet source, DataSet destination, Node node) {
        for (Iterator iterator = source.tables(); iterator.hasNext();) {
            Table table = (Table)iterator.next();
            Table destinationTable = destination.buildTable(table.getName());
            destinationTable.setOrderClause(table.getOrderClause());
            destinationTable.setIdentityInsert(table.isIdentityInsert());
            destinationTable.setTemporary(table.isTemporary());
            copyRows(table, destinationTable, node);
        }
    }


    private void copyRows(Table source, Table destination, Node node) {
        for (Iterator iterator = source.rows(); iterator.hasNext();) {
            Row row = (Row)iterator.next();
            Row newRow = new Row(row.getId(),
                                 row.getRefId(),
                                 row.isAutoComplete(),
                                 row.getLocalDefinedFields().duplicate());

            LocationUtil.setLocationForInheritedRow(newRow, row, node);

            destination.addRow(newRow);
        }
    }


    static String computeUnknownInheritIdMessage(String caseId, String inheritId) {
        return "Le case '" + caseId + "' hérite d'un case inconnu ('" + inheritId + "')";
    }
}
