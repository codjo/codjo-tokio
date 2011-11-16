/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.model.ComparatorConverter;
import net.codjo.tokio.model.DataSet;
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
public final class XMLDatasetUtil {
    private XMLDatasetUtil() {
    }


    public static void loadComparators(DataSet dataSet, Node comparatorsNode) {
        NodeList nodes = comparatorsNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (XMLScenariiTags.COMPARATOR.equals(nodes.item(i).getNodeName())) {
                loadComparator(dataSet, nodes.item(i));
            }
        }
    }


    public static Row getRow(Table table, String rowId) {
        Row row = table.getRowById(rowId);
        if (row == null) {
            throw new RuntimeException(computeBadRowIdMessage(rowId, table.getName()));
        }
        return row;
    }


    public static String computeBadRowIdMessage(String rowId, String tableName) {
        return "La ligne '" + rowId + "' n'existe pas dans la table '" + tableName + "'.";
    }


    public static void replaceRowFields(Row row, Node rowNode) throws TokioLoaderException {
        NodeList fields = rowNode.getChildNodes();
        for (int i = 0; i < fields.getLength(); i++) {
            Node field = fields.item(i);
            String fieldName = null;
            if (XMLScenariiTags.FIELD.equals(field.getNodeName())) {
                fieldName = XmlUtil.getAttribute(field, XMLScenariiTags.FIELD_NAME);
            }
            else if (field instanceof Element) {
                fieldName = field.getNodeName();
            }
            if (fieldName != null) {
                if (!row.containsField(fieldName)) {
                    throw new TokioLoaderException(computeBadFieldNameMessage(fieldName, row.getId()), field);
                }
                GeneratorConfiguration generatorConfiguration = null;
                for (int j = 0; j < field.getChildNodes().getLength(); j++) {
                    Node generator = field.getChildNodes().item(j);
                    if (generator.getNodeName().startsWith(XMLScenariiTags.GENERATOR_NAME)) {
                        String generatorName = generator.getNodeName();
                        String generatorPrecision = XmlUtil.getAttribute(generator,
                                                                         XMLScenariiTags.GENERATOR_PRECISION);

                        generatorConfiguration = new GeneratorConfiguration(generatorName,
                                                                            generatorPrecision);
                    }
                }

                row.setFieldValue(fieldName,
                                  XmlUtil.getAttribute(field, XMLScenariiTags.FIELD_VALUE),
                                  XmlUtil.getAttribute(field, XMLScenariiTags.FIELD_NULL),
                                  generatorConfiguration);
            }
        }

        LocationUtil.setLocationForReplaceRow(row, rowNode);
    }


    public static String computeBadFieldNameMessage(String fieldName, String rowId) {
        return "Le champ '" + fieldName + "' associé à la ligne '" + rowId + "' n'existe pas.";
    }


    private static void loadComparator(DataSet dataSet, Node comparatorsNode) {
        Node fieldNode =
              comparatorsNode.getAttributes().getNamedItem(XMLScenariiTags.COMPARATOR_FIELD);
        Node assertNode =
              comparatorsNode.getAttributes().getNamedItem(XMLScenariiTags.COMPARATOR_ASSERT);
        Node paramNode =
              comparatorsNode.getAttributes().getNamedItem(XMLScenariiTags.COMPARATOR_PARAM);
        Node precisionNode =
              comparatorsNode.getAttributes().getNamedItem(XMLScenariiTags.COMPARATOR_PRECISION);

        if (assertNode != null) {
            String value = null;
            if (paramNode != null) {
                value = paramNode.getNodeValue();
            }
            try {
                dataSet.addComparator(fieldNode.getNodeValue(),
                                      ComparatorConverter.newComparator(assertNode.getNodeValue(), value));
            }
            catch (Exception ex) {
                dataSet.addComparator(fieldNode.getNodeValue(),
                                      ComparatorConverter.newComparator(value));
            }
        }
        else if (precisionNode != null) {
            String value = precisionNode.getNodeValue();
            dataSet.addComparator(fieldNode.getNodeValue(),
                                  ComparatorConverter.newComparator(value));
        }
    }
}
