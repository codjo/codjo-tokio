/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import static net.codjo.tokio.XMLEntitiesTags.UNIQUE_KEY;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.LocationUtil;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.util.XmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 *
 */
public final class XMLTableLoader {

    private XMLTableLoader() {
    }


    public static void loadFlattenTable(DataSet dataSet, Node node) {
        loadTable(dataSet, node, node.getNodeName(), new MyRowLoader());
    }


    public static void loadTable(DataSet dataSet, Node tableNode) throws TokioLoaderException {
        String tableName = XmlUtil.getAttribute(tableNode, XMLScenariiTags.TABLE_NAME);
        loadTable(dataSet, tableNode, tableName, new MyRowLoader());
    }


    public static void loadTable(DataSet dataset, Node tableNode, String tableName, RowLoader rowLoader) {
        Boolean identityInsert = XmlUtil.getBooleanAttribute(tableNode, XMLScenariiTags.TABLE_IDENTITY);
        String orderClause = XmlUtil.getAttribute(tableNode, XMLScenariiTags.TABLE_ORDER);
        Boolean nullFirst = XmlUtil.getBooleanAttribute(tableNode, XMLScenariiTags.TABLE_NULL_FIRST);
        Boolean temporary = XmlUtil.getBooleanAttribute(tableNode, XMLScenariiTags.TABLE_TEMPORARY);

        dataset.buildTable(tableName);

        NodeList nodes = tableNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            String nodeName = item.getNodeName();
            if (XMLScenariiTags.ROW.equals(nodeName)) {
                Row row = rowLoader.loadRow(item, null, new FieldMap());
                dataset.addRow(tableName, row);
            }
            else if (XMLStoriesTags.COPY.equals(nodeName)) {
                String rowId = XmlUtil.getAttribute(item, XMLStoriesTags.COPY_ROW);
                Row row = rowLoader.loadRow(item, rowId, new FieldMap());
                dataset.addRow(tableName, row);
            }
            else if (XMLStoriesTags.REPLACE.equals(nodeName)) {
                String rowId = XmlUtil.getAttribute(item, XMLStoriesTags.REPLACE_ROW);
                Row rowRef = XMLDatasetUtil.getRow(dataset.getTable(tableName), rowId);
                Boolean autoComplete = XmlUtil.getBooleanAttribute(item, XMLScenariiTags.ROW_AUTOCOMPLETE);
                if (autoComplete != null) {
                    rowRef.setAutoComplete(autoComplete);
                }
                XMLDatasetUtil.replaceRowFields(rowRef, item);
            }
            else if (XMLStoriesTags.REMOVE.equals(nodeName)) {
                String rowId = XmlUtil.getAttribute(item, XMLStoriesTags.REMOVE_ROW);
                Table table = dataset.getTable(tableName);
                table.removeRow(XMLDatasetUtil.getRow(table, rowId));
            }
        }

        dataset.getTable(tableName).setIdentityInsert(identityInsert);
        dataset.getTable(tableName).setOrderClause(orderClause);
        dataset.getTable(tableName).setNullFirst(nullFirst);
        dataset.getTable(tableName).setTemporary(temporary);
    }


    public static Row loadRow(Node rowNode, String refId, FieldMap fields, String suffix) {
        NodeList nodes = rowNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (XMLScenariiTags.FIELD.equals(nodes.item(i).getNodeName())) {
                String fieldName = XmlUtil.getAttribute(nodes.item(i), XMLScenariiTags.FIELD_NAME);
                putField(fields, nodes.item(i), fieldName);
            }
            else if (nodes.item(i) instanceof Element && !UNIQUE_KEY.equals(nodes.item(i).getNodeName())) {
                putField(fields, nodes.item(i), nodes.item(i).getNodeName());
            }
        }

        Row row = new Row(XmlUtil.getAttribute(rowNode, XMLScenariiTags.ROW_ID),
                          refId,
                          XmlUtil.getAttribute(rowNode, XMLScenariiTags.ROW_COMMENT),
                          XmlUtil.getBooleanAttribute(rowNode, XMLScenariiTags.ROW_AUTOCOMPLETE),
                          fields);

        LocationUtil.setLocationForNewOrCopyRow(row, rowNode, suffix);

        return row;
    }


    public static void putField(FieldMap fields, Node node, String fieldName) {
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

        fields.putField(fieldName,
                        XmlUtil.getAttribute(node, XMLScenariiTags.FIELD_VALUE),
                        XmlUtil.getAttribute(node, XMLScenariiTags.FIELD_NULL),
                        generatorConfiguration);
    }


    interface RowLoader {
        Row loadRow(Node rowNode, String refId, FieldMap fields);
    }

    private static class MyRowLoader implements RowLoader {
        public Row loadRow(Node rowNode, String refId, FieldMap fields) {
            return XMLTableLoader.loadRow(rowNode, refId, fields, "");
        }
    }
}