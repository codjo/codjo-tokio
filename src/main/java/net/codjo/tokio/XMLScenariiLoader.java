/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.LocationUtil;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.RowDictionary;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Classe responsable du chargement des scenario a partir d'un fichier XML.
 *
 * @author $Author: catteao $
 * @version $Revision: 1.22 $
 */
public class XMLScenariiLoader extends XMLFileLoader implements XMLScenariiTags, XMLScenarioLoader {
    private RowDictionary dictionary = new RowDictionary();


    public XMLScenariiLoader() {
    }


    public XMLScenariiLoader(URL url) throws IOException, SAXException {
        super(url);
        load();
    }


    public XMLScenariiLoader(File file) throws IOException, SAXException {
        super(file);
        if (!file.exists()) {
            throw new IllegalArgumentException("Fichier introuvable " + file);
        }
        load();
    }


    public ScenarioList getScenarioList() {
        return scenarii;
    }


    @Override
    public void parse(Document doc, String fileUri, Map<String, String> globalProperties) {
        this.uri = fileUri;
        NodeList nodes = doc.getElementsByTagName(SCENARIO);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node scenarioNode = nodes.item(i);
            Scenario scenario = createScenario(scenarioNode, globalProperties);
            scenarii.addScenario(scenario);
        }
    }


    @Override
    protected Scenario doCreateScenario(Node scenarioNode) {
        return loadScenario(scenarioNode);
    }


    private void load() throws IOException, SAXException {
        try {
            String xmlContent = XmlUtil.loadContent(new URL(uri));
            Document document = XmlUtil.parse(uri, xmlContent);
            parse(document, uri, null);
        }
        catch (ParserConfigurationException pce) {
            throw new RuntimeException("Erreur de lecture de " + uri + " : " + pce.getLocalizedMessage());
        }
    }


    private void loadDataSet(DataSet dataSet, Node inputNode) {
        NodeList nodes = inputNode.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (COMPARATORS.equals(nodes.item(i).getNodeName())) {
                XMLDatasetUtil.loadComparators(dataSet, nodes.item(i));
            }
            else if (TABLE.equals(nodes.item(i).getNodeName())) {
                loadTable(dataSet, nodes.item(i));
            }
            else if (nodes.item(i) instanceof Element) {
                loadFlattenTable(dataSet, nodes.item(i));
            }
        }
    }


    private Scenario loadScenario(Node scenarioNode) {
        Scenario scenario = new Scenario(XmlUtil.getAttribute(scenarioNode, SCENARIO_ID),
                                         XmlUtil.getAttribute(scenarioNode, SCENARIO_COMMENT),
                                         dictionary);

        NodeList nodes = scenarioNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (SCENARIO_PROPERTIES.equals(nodes.item(i).getNodeName())) {
                XMLPropertiesLoader.loadProperties(scenario, nodes.item(i), this.uri);
            }
            else if (SCENARIO_PROPERTY.equals(nodes.item(i).getNodeName())) {
                XMLPropertiesLoader.loadProperty(scenario, nodes.item(i));
            }
            else if (SCENARIO_INPUT.equals(nodes.item(i).getNodeName())) {
                loadDataSet(scenario.getInputDataSet(), nodes.item(i));
            }
            else if (SCENARIO_OUTPUT.equals(nodes.item(i).getNodeName())) {
                loadDataSet(scenario.getOutputDataSet(), nodes.item(i));
            }
        }

        return scenario;
    }


    private void loadFlattenTable(DataSet dataSet, Node tableNode) {
        loadTable(dataSet, tableNode, tableNode.getNodeName());
    }


    private void loadTable(DataSet dataSet, Node tableNode) {
        String tableName = XmlUtil.getAttribute(tableNode, TABLE_NAME);
        loadTable(dataSet, tableNode, tableName);
    }


    private void loadTable(DataSet dataSet, Node tableNode, String tableName) {
        Boolean identityInsert = XmlUtil.getBooleanAttribute(tableNode, TABLE_IDENTITY);
        String orderClause = XmlUtil.getAttribute(tableNode, TABLE_ORDER);
        Boolean temporary = XmlUtil.getBooleanAttribute(tableNode, TABLE_TEMPORARY);

        dataSet.buildTable(tableName);

        NodeList nodes = tableNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (ROW.equals(childNode.getNodeName())) {
                Row row = loadRow(childNode);
                if (row.getRefId() == null) {
                    LocationUtil.setLocationForNewOrCopyRow(row, childNode);
                }
                else {
                    LocationUtil.setLocationForNewOrCopyRow(row, childNode, "inherited");
                }
                dataSet.addRow(tableName, row);
            }
        }

        dataSet.getTable(tableName).setIdentityInsert(identityInsert);
        dataSet.getTable(tableName).setOrderClause(orderClause);
        dataSet.getTable(tableName).setTemporary(temporary);
    }


    private Row loadRow(Node rowNode) {
        FieldMap fields = new FieldMap();

        NodeList nodes = rowNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String fieldName = null;
            if (FIELD.equals(node.getNodeName())) {
                fieldName = XmlUtil.getAttribute(node, FIELD_NAME);
            }
            else if (node instanceof Element) {
                fieldName = node.getNodeName();
            }
            GeneratorConfiguration generatorConfiguration = null;
            for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                Node generator = node.getChildNodes().item(j);
                if (generator.getNodeName().startsWith(XMLScenariiTags.GENERATOR_NAME)) {
                    String generatorName = generator.getNodeName();
                    String generatorPrecision = XmlUtil.getAttribute(generator,
                                                                     XMLScenariiTags.GENERATOR_PRECISION);

                    generatorConfiguration = new GeneratorConfiguration(generatorName, generatorPrecision);
                }
            }
            if (fieldName != null) {
                String fieldValue = XmlUtil.getAttribute(node, FIELD_VALUE);
                Node fieldNullNode = node.getAttributes().getNamedItem(FIELD_NULL);
                if (fieldNullNode != null) {
                    String fieldNullValue = fieldNullNode.getNodeValue();
                    if ("false".equals(fieldNullValue)) {
                        throw new RuntimeException(
                              "La balise <field> avec l'attribut 'null' doit être à true ou l'attribut 'null' ne doit pas être présent.");
                    }
                    else if (fieldValue != null) {
                        throw new RuntimeException(
                              "La balise <field> avec l'attribut 'null' ne doit pas contenir d'attribut 'value'.");
                    }
                }

                fields.putField(fieldName,
                                fieldValue,
                                XmlUtil.getAttribute(node, XMLScenariiTags.FIELD_NULL),
                                generatorConfiguration);
            }
        }

        return new Row(XmlUtil.getAttribute(rowNode, ROW_ID),
                       XmlUtil.getAttribute(rowNode, ROW_INHERIT_ID),
                       XmlUtil.getAttribute(rowNode, ROW_COMMENT),
                       XmlUtil.getBooleanAttribute(rowNode, ROW_AUTOCOMPLETE), fields);
    }
}
