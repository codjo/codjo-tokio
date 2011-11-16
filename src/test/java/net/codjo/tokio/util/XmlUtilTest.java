package net.codjo.tokio.util;
import net.codjo.test.common.Directory;
import net.codjo.test.common.PathUtil;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import net.codjo.tokio.util.XmlUtil.DtdResolver;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/**
 *
 */
public class XmlUtilTest {
    private DtdResolver dtdResolver = new DtdResolver();


    @Test
    public void test_resolveEntity() throws IOException {
        InputSource source = dtdResolver.resolveEntity(null, "file:///C:/Dev/projects/monTokio.tokio");
        assertThat(source.getSystemId(),
                   is("file:///C:/Dev/projects/monTokio.tokio"));
    }


    @Test
    public void test_resolveEntity_fileExist() throws IOException {
        assertResolvedEntity("${basedir}\\pom.xml", "./pom.xml");
    }


    @Test
    public void test_resolveEntity_intellij_found() throws IOException {
        assertResolvedEntity("${basedir}\\..\\pom.xml", "./pom.xml");
    }


    @Test
    public void test_resolveEntity_intellij_notFound() throws IOException {
        assertResolvedEntity("${basedir}\\..\\alacon.xml", "../alacon.xml");
    }


    @Test
    public void test_parse_nodeInfos() throws Exception {
        Document document = XmlUtil.parse("myTest", "<root>\n"
                                                    + "  <family>\n"
                                                    + "    <husband/>\n"
                                                    + "    <wife/>\n"
                                                    + "\n"
                                                    + "    <children/>\n"
                                                    + "  </family>\n"
                                                    + "</root>");

        NodeList childNodes = document.getChildNodes();
        Node rootNode = childNodes.item(0);
        assertNodeInfos("myTest", "root", 1, rootNode);

        Node familyNode = rootNode.getChildNodes().item(1);
        assertNodeInfos("myTest", "family", 2, familyNode);

        NodeList familyChildNodes = familyNode.getChildNodes();
        assertNodeInfos("myTest", "husband", 3, familyChildNodes.item(1));
        assertNodeInfos("myTest", "wife", 4, familyChildNodes.item(3));
        assertNodeInfos("myTest", "children", 6, familyChildNodes.item(5));
    }


    @Test
    public void test_removeGroup() {

        String filterString = XmlUtil.removeGroup("<root>\n"
                                                  + "  <group>\n"
                                                  + "    <group name=\"subGroup\">\n"
                                                  + "         <groupToKeep>\n"
                                                  + "             <children/>\n"
                                                  + "         </groupToKeep>\n"
                                                  + "         <anotherGroupToKeep/>\n"
                                                  + "   </group>\n"
                                                  + "  </group>\n"
                                                  + "</root>");

        assertEquals("<root>\n  \n    \n"
                     + "         <groupToKeep>\n"
                     + "             <children/>\n"
                     + "         </groupToKeep>\n"
                     + "         <anotherGroupToKeep/>\n   \n  \n"
                     + "</root>",
                     filterString);
    }


    @Test
    public void test_oldFormalism() throws Exception {
        Document document =
              XmlUtil.parse("myTest", XmlUtil.loadContent(getClass().getResource("oldFormalism.tokio")));

        Node scenariiNode = document.getChildNodes().item(1);
        Node scenarioNode = scenariiNode.getChildNodes().item(1);
        Node inputNode = scenarioNode.getChildNodes().item(1);
        NodeList inputChildNodes = inputNode.getChildNodes();

        Node currencyTableNode = inputChildNodes.item(1);
        assertFirstRowFields(currencyTableNode, "REF_CURRENCY", "CODE=EUR", "LABEL=EURO");

        Node countryTableNode = inputChildNodes.item(3);
        assertFirstRowFields(countryTableNode, "REF_COUNTRY", "CODE=FR", "LABEL=FRANCE");

        Node localTableNode = inputChildNodes.item(5);
        assertFirstRowFields(localTableNode, "AP_FAMILY", "FIRSTNAME=John", "LASTNAME=SMITH");
    }


    @Test
    public void test_getXmlFileEncoding() throws Exception {
        assertEquals("ISO-8859-1",
                     XmlUtil.getXmlFileEncoding(new File(getClass().getResource("ISO-8859-1_etalon.tokio").toURI())));

        assertEquals("UTF-8",
                     XmlUtil.getXmlFileEncoding(new File(getClass().getResource("UTF-8_etalon.tokio").toURI())));

        assertEquals("ISO-8859-1",
                     XmlUtil.getXmlFileEncoding(getClass().getResource("ISO-8859-1_etalon.tokio")));

        assertEquals("UTF-8",
                     XmlUtil.getXmlFileEncoding(getClass().getResource("UTF-8_etalon.tokio")));

        assertEquals("UTF-8",
                     XmlUtil.getXmlFileEncoding(getClass().getResource("noEncoding_etalon.tokio")));
    }


    private void assertFirstRowFields(Node currencyTableNode,
                                      String tableName,
                                      String... fields) {
        assertEquals("table", currencyTableNode.getNodeName());
        assertEquals(tableName, XmlUtil.getAttribute(currencyTableNode, "name"));
        assertEquals("field",
                     currencyTableNode.getChildNodes().item(1).getChildNodes().item(1).getNodeName());
        for (int i = 0; i < fields.length; i++) {
            String[] split = fields[i].split("=");
            String fieldName = split[0];
            String fieldValue = split[1];
            assertEquals(fieldName,
                         XmlUtil.getAttribute(currencyTableNode.getChildNodes().item(1).getChildNodes().item(
                               i * 2 + 1), "name"));
            assertEquals(fieldValue,
                         XmlUtil.getAttribute(currencyTableNode.getChildNodes().item(1).getChildNodes().item(
                               i * 2 + 1), "value"));
        }
    }


    private void assertNodeInfos(String expectedUri,
                                 String expectedNodeName,
                                 int expectedLineNumber,
                                 Node node) {
        assertEquals(expectedNodeName, node.getNodeName());
        assertEquals(expectedLineNumber, TokioDOMParser.getLineNumber(node));
        assertEquals(expectedUri, TokioDOMParser.getTokioFile(node));
    }


    private void assertResolvedEntity(String systemIdInXml, String expectedInputSource) throws IOException {
        Directory baseDirectory = PathUtil.findBaseDirectory(XmlUtilTest.class);
        File expectedRealFile = new File(baseDirectory, expectedInputSource).getCanonicalFile();

        InputSource source = dtdResolver.resolveEntity(null, systemIdInXml);
        assertThat(source.getSystemId(), is(expectedRealFile.toURL().toExternalForm()));
    }
}
