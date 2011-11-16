package net.codjo.tokio;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.LocationPointer;
import net.codjo.tokio.model.LoggerLocationVisitor;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.util.RowUtil;
import net.codjo.tokio.util.Util;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 *
 */
public class XMLTableLoaderTest {
    private StringBuilder stringBuilder = new StringBuilder();
    private DataSet dataset = new DataSet();


    @Test
    public void test_row_location() throws Exception {
        XMLTableLoader.loadTable(dataset, Util.nodeFrom("<table name='AP_TABLE'>\n"
                                                        + " <row/>\n"
                                                        + "</table>", "child.tokio"));

        Table table = dataset.getTable("AP_TABLE");
        assertEquals(1, table.getRowCount());

        table.getRow(0).getLocationPointer().accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("row(child.tokio:2)\n", stringBuilder.toString());
    }


    @Test
    public void test_copy_location() throws Exception {
        XMLTableLoader.loadTable(dataset, Util.nodeFrom("<table name='AP_TABLE'>\n"
                                                        + "  <copy row='parent'/>"
                                                        + "</table>", "child.tokio"));

        LocationPointer locationPointer = dataset.getTable("AP_TABLE").getRow(0).getLocationPointer();

        locationPointer.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy(child.tokio:2)\n", stringBuilder.toString());

        XMLTableLoader.loadTable(dataset, Util.nodeFrom("<table name='AP_TABLE'>\n"
                                                        + "  <row/>\n"
                                                        + "  <row id='parent'/>\n"
                                                        + "</table>", "parent.tokio"));

        stringBuilder = new StringBuilder();
        locationPointer.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy(child.tokio:2)\n"
                     + "\trow(parent.tokio:3)\n", stringBuilder.toString());
    }


    @Test
    public void test_replace_location() throws Exception {
        XMLTableLoader.loadTable(dataset, Util.nodeFrom("<table name='AP_TABLE'>\n"
                                                        + "  <row id='parent'/>\n"
                                                        + "  <replace row='parent'/>\n"
                                                        + "</table>", "child.tokio"));

        LocationPointer locationPointer = dataset.getTable("AP_TABLE").getRow(0).getLocationPointer();

        locationPointer.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("replace(child.tokio:3)\n"
                     + "\trow(child.tokio:2)\n", stringBuilder.toString());
    }


    @Test
    public void test_loadRow_location() throws Exception {
        String xmlContent = "<case>\n"
                            + "  <AP_EMPTY/>\n"
                            + "  <AP_TABLE>\n"
                            + "    <row/>\n"
                            + "  </AP_TABLE>\n"
                            + "</case>\n";

        Row row = XMLTableLoader.loadRow(getRowNode("myTest", xmlContent, 1, 0), null, new FieldMap(), "");

        LocationPointer locationPointer = row.getLocationPointer();
        assertNotNull(locationPointer);
        assertEquals(1, locationPointer.getLocations().size());

        locationPointer.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("row(myTest:4)\n", stringBuilder.toString());
    }


    @Test
    public void test_loadRow_location_refId() throws Exception {
        String xmlContent = "<case>\n"
                            + "  <AP_EMPTY/>\n"
                            + "  <AP_TABLE>\n"
                            + "    <copy/>\n"
                            + "  </AP_TABLE>\n"
                            + "</case>\n";

        Row row = XMLTableLoader.loadRow(getRowNode("myTest", xmlContent, 1, 0),
                                         "parent",
                                         new FieldMap(),
                                         "");

        row.getLocationPointer().accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy(myTest:4)\n", stringBuilder.toString());

        dataset.addRow("AP_TABLE", row);
        dataset.addRow("AP_TABLE", RowUtil.row("parent"));

        stringBuilder = new StringBuilder();
        row.getLocationPointer().accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("copy(myTest:4)\n"
                     + "\trow(RowUtil:77)\n", stringBuilder.toString());
    }


    @Test
    public void test_loadRow_suffix() throws Exception {
        String xmlContent = "<case>\n"
                            + "  <AP_EMPTY/>\n"
                            + "  <AP_TABLE>\n"
                            + "    <row/>\n"
                            + "  </AP_TABLE>\n"
                            + "</case>\n";

        Row row = XMLTableLoader.loadRow(getRowNode("myTest", xmlContent, 1, 0),
                                         null,
                                         new FieldMap(),
                                         "suffix");

        LocationPointer locationPointer = row.getLocationPointer();
        assertNotNull(locationPointer);
        assertEquals(1, locationPointer.getLocations().size());

        locationPointer.accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("row-suffix(myTest:4)\n", stringBuilder.toString());
    }


    private Node getRowNode(String uri, String xmlContent, int tableIndex, int rowIndex)
          throws IOException, ParserConfigurationException, SAXException {
        return Util.nodeFrom(xmlContent, uri)
              .getChildNodes().item(tableIndex * 2 + 1)
              .getChildNodes().item(rowIndex * 2 + 1);
    }
}
