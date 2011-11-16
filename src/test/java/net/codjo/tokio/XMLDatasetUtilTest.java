package net.codjo.tokio;
import net.codjo.tokio.model.LoggerLocationVisitor;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.util.RowUtil;
import static net.codjo.tokio.util.RowUtil.nullField;
import net.codjo.tokio.util.Util;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class XMLDatasetUtilTest {
    private StringBuilder stringBuilder = new StringBuilder();


    @Test
    public void test_replaceRowFields() throws Exception {
        String xmlContent = "<replace row='parent'>\n"
                            + "  <field name='COL1' value='VAL1'/>\n"
                            + "</replace>";

        Row replacedRow = RowUtil.row("parent", nullField("COL1"));
        XMLDatasetUtil.replaceRowFields(replacedRow,
                                        Util.nodeFrom(xmlContent, "child.tokio"));

        replacedRow.getLocationPointer().accept(new LoggerLocationVisitor(stringBuilder));

        assertEquals("replace(child.tokio:1)\n"
                     + "\trow(RowUtil:77)\n", stringBuilder.toString());
    }
}
