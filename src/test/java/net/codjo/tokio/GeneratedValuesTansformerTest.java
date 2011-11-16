package net.codjo.tokio;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.datagenerators.GeneratorFactoryMock;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.GeneratedValue;
import net.codjo.tokio.model.NullValue;
import net.codjo.tokio.model.ObjectValueList;
import net.codjo.tokio.model.StringValue;
import net.codjo.tokio.model.Table;
import static net.codjo.tokio.util.RowUtil.field;
import static net.codjo.tokio.util.RowUtil.generatedField;
import static net.codjo.tokio.util.RowUtil.row;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class GeneratedValuesTansformerTest {
    private DataSet dataset = new DataSet();
    private GeneratedValuesTansformer generatedValuesTansformer;
    private GeneratorFactoryMock generatorFactoryMock;


    @Before
    public void setUp() {
        generatorFactoryMock = new GeneratorFactoryMock();
        generatedValuesTansformer = new GeneratedValuesTansformer(generatorFactoryMock);
    }


    @Test
    public void test_transform() throws Exception {
        generatorFactoryMock.mockGenerator("AP_BOOK");
        generatorFactoryMock.mockGenerator("AP_AUTHOR1");
        generatorFactoryMock.mockGenerator("AP_AUTHOR2");
        generatorFactoryMock.mockGenerator("AP_AUTHOR3");

        dataset.addRow("AP_BOOK",
                       row(generatedField("AUTHOR", new GeneratorConfiguration("AP_BOOK")),
                           field("TITLE", "LA PESTE")));
        dataset.addRow("AP_AUTHOR",
                       row(field("FIRSTNAME", "TOTO"),
                           generatedField("LASTNAME", new GeneratorConfiguration("AP_AUTHOR1")),
                           generatedField("COUNTRY", new GeneratorConfiguration("AP_AUTHOR2"))));
        dataset.addRow("AP_AUTHOR",
                       row(generatedField("LASTNAME", new GeneratorConfiguration("AP_AUTHOR3"))));

        generatedValuesTansformer.transform(dataset);

        Table bookTable = dataset.getTable("AP_BOOK");
        assertEquals(1, bookTable.getRowCount());

        FieldMap fields = bookTable.getRow(0).getFields();
        assertEquals(2, fields.size());
        assertEquals("AP_BOOK_1", fields.get("AUTHOR").getValue());
        assertEquals("LA PESTE", fields.get("TITLE").getValue());

        Table authorTable = dataset.getTable("AP_AUTHOR");
        assertEquals(2, authorTable.getRowCount());

        FieldMap firstFields = authorTable.getRow(0).getFields();
        assertEquals(3, firstFields.size());
        assertEquals("TOTO", firstFields.get("FIRSTNAME").getValue());
        assertEquals("AP_AUTHOR1_1", firstFields.get("LASTNAME").getValue());
        assertEquals("AP_AUTHOR2_1", firstFields.get("COUNTRY").getValue());

        FieldMap secondFields = authorTable.getRow(1).getFields();
        assertEquals(1, secondFields.size());
        assertEquals("AP_AUTHOR3_1", secondFields.get("LASTNAME").getValue());
    }


    @Test
    public void test_transformTypes() throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        long time = gregorianCalendar.getTime().getTime();
        generatorFactoryMock.mockGenerator("BOOLEAN", true);
        generatorFactoryMock.mockGenerator("TIMESTAMP", createTimestamp(time));
        generatorFactoryMock.mockGenerator("DATE", new Date(time));
        generatorFactoryMock.mockGenerator("NUMERIC", new BigDecimal(123.5));
        generatorFactoryMock.mockGenerator("STRING", "Stephen King");

        dataset.addRow("AP_AUTHOR",
                       row(generatedField("AUTHOR", new GeneratorConfiguration("STRING")),
                           generatedField("DATE DE NAISSANCE", new GeneratorConfiguration("DATE")),
                           generatedField("HEURE DE NAISSANCE", new GeneratorConfiguration("TIMESTAMP")),
                           generatedField("SEXE", new GeneratorConfiguration("BOOLEAN")),
                           generatedField("POIDS", new GeneratorConfiguration("NUMERIC"))));

        generatedValuesTansformer.transform(dataset);

        FieldMap fields = dataset.getTable("AP_AUTHOR").getRow(0).getFields();
        assertEquals("Stephen King", fields.get("AUTHOR").getValue());
        assertEquals("1970-01-01", fields.get("DATE DE NAISSANCE").getValue());
        assertEquals("1970-01-01 00:00:00.0", fields.get("HEURE DE NAISSANCE").getValue());
        assertEquals("true", fields.get("SEXE").getValue());
        assertEquals("123.5", fields.get("POIDS").getValue());
    }


    @Test
    public void test_transform_fieldConcatenated() throws Exception {
        generatorFactoryMock.mockGenerator("StringGenerator");
        generatorFactoryMock.mockGenerator("IntGenerator");

        dataset.addRow("AP_BOOK",
                       row("row1", new Field("TITLE",
                                             new ObjectValueList(new StringValue("prefix "),
                                                                 new GeneratedValue(new GeneratorConfiguration(
                                                                       "StringGenerator")),
                                                                 new GeneratedValue(new GeneratorConfiguration(
                                                                       "IntGenerator")),
                                                                 new StringValue(" suffix")))));

        generatedValuesTansformer.transform(dataset);

        assertEquals("prefix StringGenerator_1IntGenerator_1 suffix",
                     dataset.getRow("row1").getFields().get("TITLE").getValue());
    }


    @Test
    public void test_transform_nullValue() throws Exception {
        dataset.addRow("AP_BOOK",
                       row("row1", new Field("TITLE", new ObjectValueList(new NullValue()))));

        generatedValuesTansformer.transform(dataset);

        assertNull(dataset.getRow("row1").getFields().get("TITLE").getValue());
    }


    private static Timestamp createTimestamp(long time) {
        Timestamp timestamp;
        timestamp = new Timestamp(time);
        timestamp.setNanos(0);
        return timestamp;
    }
}
