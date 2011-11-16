/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.EntityDictionary;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.LocationUtil;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.util.XmlUtil;
import net.codjo.variable.basic.BasicVariableReplacer;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XMLStoryUtil {
    public static final String MISSING_ENTITIES_ERROR =
          "Vous devez inclure un fichier de définition d'entités.";
    private static final String NULL_ENTITY_ID_ERROR = "La valeur 'null' pour l'id est interdite.";
    private TokioConfiguration configuration;
    private final CreateEntityManager createEntityManager;
    private final RequiredInstancesManager requiredInstancesManager;
    private final IncludeEntitiesManager includeEntitiesManager;
    private List<File> includeTokioFiles = new ArrayList<File>();


    public XMLStoryUtil(TokioConfiguration configuration,
                        IncludeEntitiesManager includeEntitiesManager,
                        CreateEntityManager createEntityManager,
                        RequiredInstancesManager requiredInstancesManager) {
        this.configuration = configuration;
        this.includeEntitiesManager = includeEntitiesManager;
        this.createEntityManager = createEntityManager;
        this.requiredInstancesManager = requiredInstancesManager;
    }


    public void loadStory(Node storyNode,
                          Scenario sc,
                          File workingDirectory,
                          EntityList entityList,
                          EntityDictionary entityDictionary)
          throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
        NodeList nodes = storyNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (XMLStoriesTags.SCENARIO_PROPERTIES.equals(nodes.item(i).getNodeName())) {
                XMLPropertiesLoader.loadProperties(sc, nodes.item(i), workingDirectory.toString());
            }
            else if (XMLStoriesTags.SCENARIO_PROPERTY.equals(nodes.item(i).getNodeName())) {
                XMLPropertiesLoader.loadProperty(sc, nodes.item(i));
            }

            else if (XMLStoriesTags.INCLUDE_ENTITIES.equals(node.getNodeName())) {
                includeEntitiesManager.process(node, workingDirectory, entityList);
            }
            else if (XMLStoriesTags.INCLUDE_STORY.equals(node.getNodeName())) {
                includeStory(node,
                             workingDirectory,
                             sc,
                             entityList,
                             entityDictionary);
            }
            else if (XMLStoriesTags.STORY_INPUT.equals(node.getNodeName())) {
                loadDataSet(sc.getInputDataSet(),
                            node,
                            entityList,
                            entityDictionary);
            }
            else if (XMLStoriesTags.STORY_OUTPUT.equals(node.getNodeName())) {
                loadDataSet(sc.getOutputDataSet(),
                            node,
                            entityList,
                            entityDictionary);
            }
        }
    }


    public void loadDataSet(DataSet dataset,
                            Node datasetNode,
                            EntityList entityList,
                            EntityDictionary entityDictionary)
          throws TokioLoaderException {
        NodeList nodes = datasetNode.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            if (XMLScenariiTags.COMPARATORS.equals(nodeName)) {
                XMLDatasetUtil.loadComparators(dataset, node);
            }
            else if (XMLScenariiTags.TABLE.equals(nodeName)) {
                XMLTableLoader.loadTable(dataset, node);
            }
            else if (XMLStoriesTags.CREATE_ENTITY.equals(nodeName)) {
                if (entityList.isEmpty()) {
                    throw new RuntimeException(MISSING_ENTITIES_ERROR);
                }
                checkEntityId(node);
                createEntityManager.process(
                      dataset,
                      requiredInstancesManager.getRequiredDataset(dataset),
                      node,
                      entityList,
                      entityDictionary,
                      requiredInstancesManager.getRequiredEntityDictionnary(entityDictionary));
            }
            else if (XMLStoriesTags.COPY_ENTITY.equals(nodeName)) {
                checkEntityId(node);

                String entityRef = XmlUtil.getAttribute(node, XMLStoriesTags.COPY_ENTITY_ENTITY);
                if (!entityDictionary.contains(entityRef)) {
                    throw new RuntimeException(computeBadEntityRefMessage(entityRef));
                }
                copyEntity(dataset, node, entityDictionary);

                copyEntity(requiredInstancesManager.getRequiredDataset(dataset),
                           node,
                           requiredInstancesManager.getRequiredEntityDictionnary(entityDictionary));
            }
            else if (node instanceof Element) {
                XMLTableLoader.loadFlattenTable(dataset, node);
            }
        }
    }


    public void includeStory(Node node,
                             File workingDirectory,
                             Scenario scenario,
                             EntityList entityList,
                             EntityDictionary entityDictionary)
          throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
        String includeAttribute = XmlUtil.getAttribute(node, XMLStoriesTags.INCLUDE_STORY_FILE);
        File includeFile = new File(workingDirectory, includeAttribute);
        if (includeFile.exists()) {
            loadIncludeStory(XmlUtil.loadContent(includeFile),
                             includeFile.getPath(),
                             scenario,
                             workingDirectory,
                             entityList,
                             entityDictionary);
        }
        else {
            URL resource = scenario.getClass().getResource(includeAttribute);

            if (resource == null) {
                throw new TokioLoaderException(computeErrorMessage(includeAttribute));
            }
            else {
                try {
                    if (resource.toURI().isOpaque()) {
                        loadIncludeStory(XmlUtil.loadContent(scenario.getClass().getResource(includeAttribute)),
                                         includeAttribute,
                                         scenario,
                                         workingDirectory,
                                         entityList,
                                         entityDictionary);
                    }
                    else {
                        includeFile = new File(resource.toURI());
                        if (includeFile.exists()) {
                            loadIncludeStory(XmlUtil.loadContent(includeFile),
                                             includeFile.getPath(), scenario,
                                             workingDirectory,
                                             entityList,
                                             entityDictionary);
                        }
                        else {
                            throw new TokioLoaderException(computeErrorMessage(includeAttribute));
                        }
                    }
                }
                catch (URISyntaxException e) {
                    throw new TokioLoaderException(computeErrorMessage(includeAttribute));
                }
            }
        }
        includeTokioFiles.add(includeFile);
    }


    public List<File> getIncludeTokioFiles() {
        return includeTokioFiles;
    }


    private void copyEntity(DataSet scDataset,
                            Node copyEntityNode,
                            EntityDictionary entityDictionary) {
        String entityRef = XmlUtil.getAttribute(copyEntityNode, XMLStoriesTags.COPY_ENTITY_ENTITY);
        String entityId = XmlUtil.getAttribute(copyEntityNode, XMLStoriesTags.COPY_ENTITY_ID);
        for (String tableName : entityDictionary.getTableNames(entityRef)) {
            Table destinationTable = scDataset.buildTable(tableName);
            destinationTable.setOrderClause(entityDictionary.getOrderClause(tableName));
            destinationTable.setIdentityInsert(entityDictionary.isIdentityInsert(tableName));
            copyRows(entityRef, entityId, destinationTable, entityDictionary, copyEntityNode);
        }
    }


    private void copyRows(String entityRef,
                          String entityId,
                          Table destinationTable,
                          EntityDictionary entityDictionary,
                          Node node) {
        Row[] rows = entityDictionary.getRows(entityRef, destinationTable.getName());
        for (Row row : rows) {
            FieldMap fieldsCopy = row.getLocalDefinedFields().duplicate();
            String rowId = computeEntityCopyRowId(row.getId(), entityRef, entityId);
            String refId = computeEntityCopyRowId(row.getRefId(), entityRef, entityId);
            Row newRow = new Row(rowId, refId, row.isAutoComplete(), fieldsCopy);

            LocationUtil.setLocation(newRow, node, row);

            entityDictionary.addRow(entityId, destinationTable.getName(), newRow);
            destinationTable.addRow(newRow);
        }
    }


    private void checkEntityId(Node node) {
        String entityId = XmlUtil.getAttribute(node, XMLStoriesTags.CREATE_ENTITY_ID);
        if ("null".equals(entityId)) {
            throw new TokioLoaderException(String.format(NULL_ENTITY_ID_ERROR), node);
        }
    }


    private String computeEntityCopyRowId(String entityRefRowId, String entityRef, String entityId) {
        if (entityRefRowId == null) {
            return null;
        }
        String initialRowId = entityRefRowId.substring(entityRef.length() + 1, entityRefRowId.length());
        return computeEntityRowId(entityId, initialRowId);
    }


    private String computeEntityRowId(String entityId, String rowId) {
        if (rowId == null) {
            return rowId;
        }
        return entityId + "." + rowId;
    }


    private String computeErrorMessage(String includeAttribute) {
        return includeAttribute + " n'existe pas";
    }


    private String computeBadEntityRefMessage(String entityRef) {
        return "L'entité '" + entityRef + "' n'existe pas.";
    }


    private void loadIncludeStory(String fileContent,
                                  String storyFile,
                                  Scenario scenario,
                                  File workingDirectory,
                                  EntityList entityList,
                                  EntityDictionary entityDictionary)
          throws IOException, ParserConfigurationException, SAXException {
        String fileContentUpdated = fileContent;
        if (scenario.getProperties() != null) {
            fileContentUpdated = BasicVariableReplacer.replaceKeysPerValues(fileContent,
                                                                            scenario.getProperties());
        }
        Document document = XmlUtil.parse(storyFile,
                                          fileContentUpdated,
                                          configuration.isFullLocationFilePath());
        loadStory(document.getDocumentElement(), scenario, workingDirectory, entityList, entityDictionary);
    }
}
