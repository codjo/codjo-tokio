/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.XMLTableLoader.RowLoader;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Entity;
import net.codjo.tokio.model.EntityDeclaration;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.LocationUtil;
import net.codjo.tokio.model.Parameter;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLEntitiesLoader implements XMLEntitiesTags {
    private static final String CREATE_ENTITY_ID_IN_REQUIRED
          = "Il n'est pas possible de créer des entités dans un required.";
    private static final String ROW_ID_IN_REQUIRED
          = "Aucun id ne doit être spécifié dans une balise row sous un required.";
    private static final String MISSING_FIELD_WITH_UNIQUE_KEY
          = "Un champ spécifié dans une unique-key n'a pas de field associé.";
    private final TokioConfiguration configuration;
    private final IncludeEntitiesManager includeEntitiesManager;


    public XMLEntitiesLoader(TokioConfiguration configuration) {
        this.configuration = configuration;
        this.includeEntitiesManager = new IncludeEntitiesManager(this);
    }


    public void loadEntities(String xmlContent,
                             File workingDirectory,
                             File file,
                             EntityList entityList)
          throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
        loadEntities(xmlContent, workingDirectory, file.getPath(), entityList);
    }


    public void loadEntities(String xmlContent,
                             File workingDirectory,
                             String fileName,
                             EntityList entityList)
          throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
        loadEntities(XmlUtil.parse(fileName, xmlContent, configuration.isFullLocationFilePath()),
                     workingDirectory,
                     entityList);
    }


    private void loadEntities(Document dom, File workingDirectory, EntityList parentEntityList)
          throws IOException, SAXException, ParserConfigurationException {
        EntityList includedEntityList = new EntityList();
        NodeList includeEntitiesNodes = dom.getElementsByTagName(XMLEntitiesTags.INCLUDE_ENTITIES);
        for (int i = 0; i < includeEntitiesNodes.getLength(); i++) {
            includeEntitiesManager.process(includeEntitiesNodes.item(i),
                                           workingDirectory,
                                           includedEntityList);
        }

        EntityList entityList = new EntityList();
        NodeList entityNodes = dom.getElementsByTagName(ENTITY);
        for (int i = 0; i < entityNodes.getLength(); i++) {
            loadEntity(entityNodes.item(i), entityList, includedEntityList);
        }

        parentEntityList.addEntityList(entityList);
    }


    private void loadEntity(Node entityNode, EntityList entityList, EntityList includedEntityList)
          throws TokioLoaderException {
        String entityId = XmlUtil.getAttribute(entityNode, ENTITY_ID);
        Entity entity = new Entity(entityId);
        entity.getEntityModelList().addEntityList(includedEntityList);
        entityList.addEntity(entity);
        NodeList nodes = entityNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            if (ENTITY_PARAMETERS.equals(nodeName)) {
                loadParameters(entity, node);
            }
            else if (ENTITY_BODY.equals(nodeName)) {
                loadDataSet(node, entity.getDataset(), entity.getEntityDeclarationList(), entityList, false);
            }
            else if (ENTITY_REQUIRED.equals(nodeName)) {
                checkRequired(node);
                loadDataSet(node, entity.getRequiredDataSet(), null, entityList, true);
            }
        }
    }


    private void loadParameters(Entity entity, Node parametersNode) {
        NodeList nodes = parametersNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (ENTITY_PARAMETER.equals(node.getNodeName())) {
                GeneratorConfiguration conf = null;
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                    Node generator = node.getChildNodes().item(j);
                    if (generator.getNodeName().startsWith(XMLScenariiTags.GENERATOR_NAME)) {
                        conf = new GeneratorConfiguration(generator.getNodeName(),
                                                          XmlUtil.getAttribute(generator,
                                                                               XMLScenariiTags.GENERATOR_PRECISION));
                    }
                }
                entity.addParameter(XmlUtil.getAttribute(node, PARAMETER_NAME),
                                    XmlUtil.getAttribute(node, PARAMETER_DEFAULT_VALUE), conf);
            }
        }
    }


    private void checkRequired(Node requiredNode) {
        NodeList nodes = requiredNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (XMLEntitiesTags.CREATE_ENTITY.equals(node.getNodeName())) {
                throw new TokioLoaderException(CREATE_ENTITY_ID_IN_REQUIRED, node);
            }
            else {
                checkRowIdInRequired(node);
            }
        }
    }


    private void checkRowIdInRequired(Node tableNode) {
        NodeList nodes = tableNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (XMLEntitiesTags.ROW.equals(node.getNodeName())) {
                String rowId = XmlUtil.getAttribute(node, XMLEntitiesTags.ROW_ID);
                if (rowId != null) {
                    throw new TokioLoaderException(ROW_ID_IN_REQUIRED, node);
                }
            }
        }
    }


    private void loadDataSet(Node datasetNode,
                             DataSet dataset,
                             List<EntityDeclaration> entityDeclarationList,
                             EntityList entityList,
                             boolean inRequired) throws TokioLoaderException {
        NodeList nodes = datasetNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (TABLE.equals(node.getNodeName())) {
                String tableName = XmlUtil.getAttribute(node, XMLScenariiTags.TABLE_NAME);
                XMLTableLoader.loadTable(dataset,
                                         node,
                                         tableName,
                                         new EntityRowLoader(dataset, entityList, inRequired));
            }
            else if (XMLEntitiesTags.CREATE_ENTITY.equals(node.getNodeName())) {
                createEntity(node, entityDeclarationList);
            }
            else if (node instanceof Element) {
                String tableName = node.getNodeName();
                XMLTableLoader.loadTable(dataset,
                                         node,
                                         tableName,
                                         new EntityRowLoader(dataset, entityList, inRequired));
            }
        }
    }


    private void createEntity(Node node, List<EntityDeclaration> entityDeclarationList) {
        String createEntityName = XmlUtil.getAttribute(node, XMLStoriesTags.CREATE_ENTITY_NAME);
        String createEntityId = XmlUtil.getAttribute(node, XMLStoriesTags.CREATE_ENTITY_ID);

        EntityDeclaration entityDeclaration = new EntityDeclaration(createEntityName, createEntityId);
        entityDeclarationList.add(entityDeclaration);
        entityDeclaration.setLocation(LocationUtil.getLocationFromNode(node));

        Map<String, Parameter> entityParameters = new HashMap<String, Parameter>();
        XmlEntityUtil.processCreateEntityParameters(node, entityParameters);
        entityDeclaration.getParameters().putAll(entityParameters);
    }


    private static Row loadRow(Node rowNode,
                               String refId,
                               FieldMap fields,
                               DataSet dataset,
                               EntityList entityList,
                               boolean inRequired) {
        if (refId != null && dataset.getRow(refId) == null) {
            Row row = entityList.getRow(refId);
            if (row == null) {
                throw new RuntimeException(computeBadRefIdMessage(refId));
            }

            fields = row.getLocalDefinedFields().duplicate();
            refId = null;
        }

        Row row = XMLTableLoader.loadRow(rowNode, refId, fields, (inRequired ? "required" : ""));

        Node uniqueKeyNode = findUniqueKeyNode(rowNode);
        if (uniqueKeyNode != null) {
            loadUniqueKey(uniqueKeyNode, row);
        }

        return row;
    }


    private static Node findUniqueKeyNode(Node rowNode) {
        for (int i = 0; i < rowNode.getChildNodes().getLength(); i++) {
            Node node = rowNode.getChildNodes().item(i);
            if (node instanceof Element && XMLEntitiesTags.UNIQUE_KEY.equals(node.getNodeName())) {
                return node;
            }
        }
        return null;
    }


    private static void loadUniqueKey(Node uniqueFields, Row row) {
        NodeList childNodes = uniqueFields.getChildNodes();
        for (int index = 0; index < childNodes.getLength(); index++) {
            Node field = childNodes.item(index);
            if (field instanceof Element) {
                String fieldName = field.getNodeName();
                try {
                    row.addUniqueKey(fieldName);
                }
                catch (IllegalArgumentException e) {
                    throw new TokioLoaderException(MISSING_FIELD_WITH_UNIQUE_KEY, field);
                }
            }
        }
    }


    static String computeBadRefIdMessage(String refId) {
        return "La ligne '" + refId + "' n'existe pas";
    }


    private static class EntityRowLoader implements RowLoader {
        private final DataSet dataset;
        private final EntityList entityList;
        private boolean inRequired;


        private EntityRowLoader(DataSet dataset, EntityList entityList, boolean inRequired) {
            this.dataset = dataset;
            this.entityList = entityList;
            this.inRequired = inRequired;
        }


        public Row loadRow(Node rowNode, String refId, FieldMap fields) {
            return XMLEntitiesLoader.loadRow(rowNode, refId, fields, dataset, entityList, inRequired);
        }
    }
}
