/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import net.codjo.tokio.XMLScenariiTags;
import net.codjo.tokio.util.XmlUtil;
import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/**
 *
 */
public class DatasetChecker {
    private DatasetChecker() {
    }


    public static void check(String expected, DataSet dataSet) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader reader = new StringReader(expected.replace('\'', '\"'));
            Document document = builder.parse(new InputSource(reader));
            checkDocument(document, dataSet);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    private static void checkDocument(Document doc, DataSet dataSet) {
        NodeList nodes = doc.getElementsByTagName(XMLScenariiTags.TABLE);
        assertEquals("nombre de tables attendues", nodes.getLength(), dataSet.getTableCount());
        for (int i = 0; i < nodes.getLength(); i++) {
            Node tableNode = nodes.item(i);
            String tableName = XmlUtil.getAttribute(tableNode, XMLScenariiTags.TABLE_NAME);
            Table table = dataSet.getTable(tableName);
            if (table == null) {
                fail("La table '" + tableName + "' n'existe pas.");
            }
            checkIdentityInsert(tableNode, table);
            checkRows(tableNode, table);
        }
    }


    private static void checkIdentityInsert(Node tableNode, Table table) {
        String identityInsert = XmlUtil.getAttribute(tableNode, "identityInsert");
        assertEquals(Boolean.parseBoolean(identityInsert), table.isIdentityInsert());
    }


    private static void checkRows(Node tableNode, Table table) {
        NodeList rowNodes =
              ((Element)tableNode).getElementsByTagName(XMLScenariiTags.ROW);
        assertEquals("nombre de lignes attendues dans la table " + table.getName(),
                     rowNodes.getLength(),
                     table.getRowCount());
        List rows = table.getRows();
        for (int i = 0; i < rowNodes.getLength(); i++) {
            checkFields(rowNodes.item(i), table.getName(), (Row)rows.get(i));
        }
    }


    private static void checkFields(Node rowNode, String tableName, Row row) {
        NodeList fieldNodes = ((Element)rowNode).getElementsByTagName(XMLScenariiTags.FIELD);
        assertEquals("nombre de champs attendus ", fieldNodes.getLength(), row.getFieldCount());
        FieldMap fields = row.getFields();
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Node fieldNode = fieldNodes.item(i);
            String fieldName = XmlUtil.getAttribute(fieldNode, XMLScenariiTags.FIELD_NAME);
            Field field = fields.get(fieldName);

            if (fieldNode.getChildNodes().getLength() != 0) {
                checkGenerator(fieldNode, fieldName, field);
            }
            else {
                String fieldValue = XmlUtil.getAttribute(fieldNode, XMLScenariiTags.FIELD_VALUE);
                try {
                    assertEquals("champ : valeur incorrect " + fieldName, fieldValue, field.getValue());
                }
                catch (NoSuchElementException e) {
                    fail("Le champ '" + fieldName + "' de la table '" + tableName + "' n'existe pas.");
                }
            }
        }
    }


    private static void checkGenerator(Node fieldNode, String fieldName, Field field) {
        for (int j = 0; j < fieldNode.getChildNodes().getLength(); j++) {
            Node generator = fieldNode.getChildNodes().item(j);
            if (generator.getNodeName().startsWith(XMLScenariiTags.GENERATOR_NAME)) {
                String generatorName = generator.getNodeName();
                String generatorPrecision = XmlUtil.getAttribute(generator,
                                                                 XMLScenariiTags.GENERATOR_PRECISION);
                if (!field.containsGeneratedValue()) {
                    fail("Le generateur '" + generatorName + "' du champ '" + fieldName + "' n'existe pas.");
                }

                assertEquals("champ : generateur incorrect" + fieldName,
                             generatorName + "(" + generatorPrecision + ")",
                             field.getValue());
            }
        }
    }
}
