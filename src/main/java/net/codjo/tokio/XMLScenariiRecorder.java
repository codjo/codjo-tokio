/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.Comparator;
import net.codjo.tokio.model.ComparatorConverter;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.util.TokioLog;
import java.io.StringReader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * Classe responsable de l'enregistrement d'un scenario dans un fichier XML.
 *
 * @author $Author: crego $
 * @version $Revision: 1.15 $
 */
public class XMLScenariiRecorder implements XMLScenariiTags {
    private static final String XSL =
          "<?xml version=\"1.0\"?>" + "<xsl:stylesheet"
          + "        xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\""
          + "        version=\"1.0\">" + "    <xsl:output method=\"xml\" indent=\"yes\" "
          + "        encoding=\"ISO-8859-1\"" + "        doctype-system=\"scenarii.dtd\""
          + "        />" + "    <xsl:template match=\"/\" >"
          + "        <xsl:copy-of select=\".\"/>" + "    </xsl:template>"
          + "</xsl:stylesheet>";
    private Document rootDocument;
    private ScenarioList scenarii;
    private String scenariiName;


    public XMLScenariiRecorder(String scenariiName, ScenarioList sc) {
        this.scenarii = sc;
        this.scenariiName = scenariiName;
    }


    public void printXml(Writer writer)
          throws ParserConfigurationException, TransformerException {
        if (writer == null) {
            throw new NullPointerException();
        }

        buildDom();

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer =
              tFactory.newTransformer(new StreamSource(new StringReader(XSL)));
        transformer.transform(new DOMSource(rootDocument), new StreamResult(writer));
    }


    private void setAttribute(Element elt, String attributeName, String value) {
        if (value == null) {
            return;
        }
        elt.setAttribute(attributeName, value);
    }


    private void setAttribute(Element elt, String attributeName, boolean value) {
        if (!value) {
            return;
        }
        elt.setAttribute(attributeName, "on");
    }


    private Node buildDatasetNode(DataSet dataSet, String nodeName) {
        Element node = rootDocument.createElement(nodeName);

        if (dataSet.getComparatorsNumber() > 0) {
            Element comparatorsNode = rootDocument.createElement(COMPARATORS);
            node.appendChild(comparatorsNode);

            for (Iterator i = dataSet.comparators(); i.hasNext();) {
                Map.Entry entry = (Map.Entry)i.next();
                comparatorsNode.appendChild(buildComparatorNode((String)entry.getKey(),
                                                                (Comparator)entry.getValue()));
            }
        }

        for (Iterator i = dataSet.tables(); i.hasNext();) {
            Table table = (Table)i.next();
            node.appendChild(buildTableNode(table));
        }

        return node;
    }


    private void buildDom() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new MyErrorHandler());

        DocumentType type =
              builder.getDOMImplementation().createDocumentType(SCENARII,
                                                                "-//AGF, Inc.//DTD Scenarii 2.0//EN",
                                                                "Scenarii.dtd");

//                    "D:/pims/LibrairieTokio/src/net/codjo/tokio/Scenarii.dtd");
        rootDocument =
              builder.getDOMImplementation().createDocument(null, SCENARII, type);
        setAttribute(rootDocument.getDocumentElement(), SCENARII_NAME, scenariiName);

        for (Iterator i = scenarii.scenarii(); i.hasNext();) {
            Scenario item = (Scenario)i.next();
            rootDocument.getDocumentElement().appendChild(buildScenarioNode(item));
        }
    }


    private Node buildFieldNode(Field entry) {
        Element node = rootDocument.createElement(FIELD);
        node.setAttribute(FIELD_NAME, entry.getName());
        node.setAttribute(FIELD_VALUE, entry.getValue());
        return node;
    }


    private Node buildRowNode(Row row) {
        Element node = rootDocument.createElement(ROW);
        setAttribute(node, ROW_ID, row.getId());
        setAttribute(node, ROW_INHERIT_ID, row.getRefId());
        setAttribute(node, ROW_COMMENT, row.getComment());

        for (Iterator i = row.getLocalDefinedFields().iterator(); i.hasNext();) {
            node.appendChild(buildFieldNode((Field)i.next()));
        }

        return node;
    }


    private Node buildScenarioNode(Scenario sc) {
        Element node = rootDocument.createElement(SCENARIO);
        setAttribute(node, SCENARIO_ID, sc.getName());

        if (sc.getComment() != null) {
            Element comment = rootDocument.createElement(SCENARIO_COMMENT);
            comment.appendChild(rootDocument.createTextNode(sc.getComment()));
            node.appendChild(comment);
        }

        node.appendChild(buildDatasetNode(sc.getInputDataSet(), SCENARIO_INPUT));
        node.appendChild(buildDatasetNode(sc.getOutputDataSet(), SCENARIO_OUTPUT));

        return node;
    }


    private Node buildTableNode(Table table) {
        Element node = rootDocument.createElement(TABLE);
        setAttribute(node, TABLE_NAME, table.getName());
        setAttribute(node, TABLE_ORDER, table.getOrderClause());
        setAttribute(node, TABLE_NULL_FIRST, table.isNullFirst());
        setAttribute(node, TABLE_IDENTITY, table.isIdentityInsert());

        for (Row row : table.getRows()) {
            node.appendChild(buildRowNode(row));
        }

        return node;
    }


    private Node buildComparatorNode(String field, Comparator comparator) {
        Element node = rootDocument.createElement(COMPARATOR);
        setAttribute(node, COMPARATOR_FIELD, field);
        String type = comparator.getTypeAssert();
        if (ComparatorConverter.LAPS_COMPARATOR.equals(type)) {
            setAttribute(node, COMPARATOR_PRECISION, comparator.getParam());
        }
        else {
            setAttribute(node, COMPARATOR_ASSERT, type);
            setAttribute(node, COMPARATOR_PARAM, comparator.getParam());
        }

        return node;
    }


    private class MyErrorHandler implements ErrorHandler {
        public void error(SAXParseException sAXParseException)
              throws SAXException {
            TokioLog.error("ERROR : ");
            print(sAXParseException);
        }


        public void fatalError(SAXParseException sAXParseException)
              throws SAXException {
            TokioLog.error("FATAL : ");
            print(sAXParseException);
        }


        public void warning(SAXParseException sAXParseException)
              throws SAXException {
            TokioLog.error("WARNING : ");
            print(sAXParseException);
        }


        private void print(SAXParseException ex) {
            TokioLog.error("[L=" + ex.getLineNumber() + " C=" + ex.getColumnNumber()
                           + "] " + ex.toString());
        }
    }
}
