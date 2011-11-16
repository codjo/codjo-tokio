package net.codjo.tokio.foreignkeys;
import net.codjo.database.common.api.JdbcFixture;
import static net.codjo.database.common.api.structure.SqlConstraint.foreignKey;
import static net.codjo.database.common.api.structure.SqlField.fields;
import static net.codjo.database.common.api.structure.SqlIndex.primaryKeyIndex;
import static net.codjo.database.common.api.structure.SqlTable.table;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.test.common.fixture.DirectoryFixture;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class ForeignKeyMetadataTest {
    private JdbcFixture jdbc = JdbcFixture.newFixture();
    private DirectoryFixture directoryFixture = DirectoryFixture.newTemporaryDirectoryFixture();
    private ForeignKeyMetadata foreignKeyMetadata;
    private CompositeFixture fixture = new CompositeFixture(directoryFixture, jdbc);


    @Before
    public void setUp() throws Exception {
        fixture.doSetUp();

        foreignKeyMetadata = new ForeignKeyMetadata();

        jdbc.create(table("AUTHOR"), "NAME varchar(50) null,"
                                     + "FIRST_NAME varchar(50) null");
        jdbc.advanced().create(primaryKeyIndex("PK_AUTHOR", table("AUTHOR"), fields("NAME", "FIRST_NAME")));

        jdbc.create(table("BOOK"), "  TITLE varchar(50) null,"
                                   + "AUTHOR_NAME varchar(50) null,"
                                   + "AUTHOR_FIRST_NAME varchar(50) null");
        jdbc.advanced().create(foreignKey("FK_BOOK_AUTHOR",
                                          table("BOOK"), fields("AUTHOR_NAME", "AUTHOR_FIRST_NAME"),
                                          table("AUTHOR"), fields("NAME", "FIRST_NAME")));
    }


    @After
    public void tearDown() throws Exception {
        fixture.doTearDown();
    }


    @Test
    public void test_findForeignKey() throws Exception {
        List<ForeignKey> result = foreignKeyMetadata.findForeignKeys(jdbc.getConnection(), "BOOK");

        assertEquals(1, result.size());
        ForeignKey foreignKey = result.get(0);

        assertEquals("BOOK", foreignKey.getFromTableName());
        assertEquals("AUTHOR", foreignKey.getToTableName());

        assertEquals(2, foreignKey.getFromColumnNames().size());
        assertThat(foreignKey.getFromColumnNames(), hasItems("AUTHOR_NAME", "AUTHOR_FIRST_NAME"));

        assertEquals(2, foreignKey.getToColumnNames().size());
        assertThat(foreignKey.getToColumnNames(), hasItems("NAME", "FIRST_NAME"));
    }


    @Test
    public void test_findForeignKeys_noFK() throws Exception {
        assertTrue(foreignKeyMetadata.findForeignKeys(jdbc.getConnection(), "AUTHOR").isEmpty());
    }


    @Test
    public void test_findForeignKeys_unknownTable() throws Exception {
        try {
            foreignKeyMetadata.findForeignKeys(jdbc.getConnection(), "UNKNOWN");
            fail();
        }
        catch (Exception e) {
            assertEquals("Problème d'accès à la base et/ou la table UNKNOWN n'existe pas !!!",
                         e.getMessage());
        }
    }


    @Test
    public void test_findForeignKeys_twoForeignKeys() throws Exception {
        jdbc.create(table("COUNTRY"), "COUNTRY_CODE varchar(50) null");
        jdbc.advanced().create(primaryKeyIndex("PK_COUNTRY", table("COUNTRY"), fields("COUNTRY_CODE")));

        jdbc.create(table("NOVELL"), "  TITLE varchar(50) null,"
                                     + "AUTHOR_NAME varchar(50) null,"
                                     + "AUTHOR_FIRST_NAME varchar(50) null,"
                                     + "COUNTRY_CODE varchar(50) null");
        jdbc.advanced().create(foreignKey("FK_NOVELL_AUTHOR",
                                          table("NOVELL"), fields("AUTHOR_NAME", "AUTHOR_FIRST_NAME"),
                                          table("AUTHOR"), fields("NAME", "FIRST_NAME")));
        jdbc.advanced().create(foreignKey("FK_NOVELL_COUNTRY",
                                          table("NOVELL"), fields("COUNTRY_CODE"),
                                          table("COUNTRY"), fields("COUNTRY_CODE")));

        List<ForeignKey> result = foreignKeyMetadata.findForeignKeys(jdbc.getConnection(), "NOVELL");

        assertEquals(2, result.size());

        ForeignKey firstForeignKey = result.get(0);
        assertEquals("NOVELL", firstForeignKey.getFromTableName());
        assertEquals("AUTHOR", firstForeignKey.getToTableName());
        assertEquals(2, firstForeignKey.getFromColumnNames().size());
        assertThat(firstForeignKey.getFromColumnNames(), hasItems("AUTHOR_NAME", "AUTHOR_FIRST_NAME"));
        assertEquals(2, firstForeignKey.getToColumnNames().size());
        assertThat(firstForeignKey.getToColumnNames(), hasItems("NAME", "FIRST_NAME"));

        ForeignKey secondForeignKey = result.get(1);
        assertEquals("NOVELL", secondForeignKey.getFromTableName());
        assertEquals("COUNTRY", secondForeignKey.getToTableName());
        assertEquals(1, secondForeignKey.getFromColumnNames().size());
        assertThat(secondForeignKey.getFromColumnNames(), hasItems("COUNTRY_CODE"));
        assertEquals(1, secondForeignKey.getToColumnNames().size());
        assertThat(secondForeignKey.getToColumnNames(), hasItems("COUNTRY_CODE"));
    }
}
