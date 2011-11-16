package net.codjo.tokio;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseQueryHelper;
import net.codjo.database.common.api.DatabaseQueryHelper.SelectType;
import net.codjo.database.common.api.structure.SqlField;
import static net.codjo.database.common.api.structure.SqlTable.temporaryTable;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
public class TokioFixtureTest {
    private static final String STORY_NAME = "myStory";
    private TokioFixture fixture;
    private final DatabaseFactory databaseFactory = new DatabaseFactory();
    private final DatabaseQueryHelper databaseQueryHelper = databaseFactory.getDatabaseQueryHelper();


    @Before
    public void setUp() throws Exception {
        fixture = new TokioFixture(TokioFixtureTest.class, "/net/codjo/tokio/TokioFixtureTest.xml");
    }


    @After
    public void tearDown() throws Exception {
        fixture.doTearDown();
    }


    @Test
    public void test_constructor() throws Exception {
        TokioFixture anotherFixture = new TokioFixture(TokioFixtureTest.class);
        anotherFixture.loadTokioFile();

        assertEquals("myStory", anotherFixture.getJDBCScenario("myStory").getScenario().getName());
    }


    @Test
    public void test_constructor_extension_tokio() throws Exception {
        TokioFixture anotherFixture = new TokioFixture(TokioFixtureDummy.class);
        anotherFixture.loadTokioFile();

        assertEquals("myStory", anotherFixture.getJDBCScenario("myStory").getScenario().getName());
    }


    @Test
    public void test_constructor_error() throws Exception {
        TokioFixture anotherFixture = new TokioFixture(TokioFixtureTest.class, "Not a file name");
        try {
            anotherFixture.loadTokioFile();
            fail();
        }
        catch (Exception ex) {
            assertEquals("Le fichier tokio 'Not a file name' est introuvable ou invalide", ex.getMessage());
            assertNotNull(ex.getCause());
        }
    }


    @Test
    public void test_constructorByClass_error() throws Exception {
        TokioFixture anotherFixture = new TokioFixture(TokioFixture.class);
        try {
            anotherFixture.loadTokioFile();
            fail();
        }
        catch (FileNotFoundException ex) {
            assertEquals(
                  "Le fichier tokio 'TokioFixture.tokio' (ou 'TokioFixture.xml') est introuvable.",
                  ex.getMessage());
        }
    }


    @Test
    public void test_doSetUp() throws Exception {
        TokioFixture anotherFixture = new TokioFixture(TokioFixtureTest.class);

        anotherFixture.doSetUp();

//        assertEquals("LIB_INT_dbo", anotherFixture.getJdbcFixture().getUsername());
    }


    @Test
    public void test_getJDBCScenario() throws Exception {
        fixture.doSetUp();
        JDBCScenario scenario = fixture.getJDBCScenario(STORY_NAME);
        assertEquals("myStory", scenario.getScenario().getName());
    }


    @Test
    public void test_getJDBCScenario_badStory() throws Exception {
        fixture.doSetUp();
        try {
            fixture.getJDBCScenario("xxxx");
            fail();
        }
        catch (NoSuchElementException ex) {
        }
    }


    @Test
    public void test_insertInputInDb_noConnection() throws SQLException {
        try {
            fixture.insertInputInDb(STORY_NAME);
            fail();
        }
        catch (IllegalStateException ex) {
            assertEquals("Le fichier Tokio n'est pas chargé ! (Appeler la méthode doSetup(...) )",
                         ex.getMessage());
        }
    }


    @Test
    public void test_insertInputInDb() throws Exception {
        fixture.doSetUp(databaseFactory.createJdbcFixture());
        createTableTest();

        fixture.insertInputInDb(STORY_NAME);

        fixture.getJdbcFixture().assertContent(temporaryTable("TABLE_TEST"), new String[][]{{"ANCIEN"}});
    }


    @Test
    public void test_insertInputInDb_doNotDeleteBeforeInsert() throws Exception {
        fixture.doSetUp(databaseFactory.createJdbcFixture());

        createTableTest();
        fixture.getJdbcFixture().create(temporaryTable("TABLE_TEST_BIS"), "COL_BIS varchar(8)");

        fixture.insertInputInDb(STORY_NAME);
        fixture.insertInputInDb("doNotDeleteBeforeInsert", false);

        fixture.getJdbcFixture().assertContent(temporaryTable("TABLE_TEST"), new String[][]{{"ANCIEN"}});
        fixture.getJdbcFixture().assertContent(temporaryTable("TABLE_TEST_BIS"), new String[][]{{"VAL_BIS"}});
    }


    @Test
    public void test_verifyAllOutputs() throws Exception {
        fixture.doSetUp(databaseFactory.createJdbcFixture());
        createTableTest();

        String insert = databaseQueryHelper
              .buildInsertQuery(temporaryTable("TABLE_TEST"), createValuatedField("NEW"));
        fixture.getJdbcFixture().executeUpdate(insert);

        fixture.assertAllOutputs(STORY_NAME);
    }


    @Test(expected = AssertionError.class)
    public void test_verifyAllOutputs_failure() throws Exception {
        fixture.doSetUp(databaseFactory.createJdbcFixture());
        createTableTest();

        fixture.assertAllOutputs(STORY_NAME);
    }


    @Test
    public void test_executeUpdate() throws Exception {
        fixture.doSetUp(databaseFactory.createJdbcFixture());

        createTableTest();

        String insert = databaseQueryHelper
              .buildInsertQuery(temporaryTable("TABLE_TEST"), createValuatedField("TEMPO"));
        fixture.executeUpdate(insert);

        fixture.getJdbcFixture().assertContent(temporaryTable("TABLE_TEST"), new String[][]{{"TEMPO"}});
    }


    @Test
    public void test_assertQueryResult() throws Exception {
        fixture.doSetUp(databaseFactory.createJdbcFixture());

        createTableTest();

        String insert = databaseQueryHelper
              .buildInsertQuery(temporaryTable("TABLE_TEST"), createValuatedField("TEMPO"));
        fixture.executeUpdate(insert);

        String select = databaseQueryHelper
              .buildSelectQuery(temporaryTable("TABLE_TEST"), SelectType.ALL);
        fixture.assertQueryResult(select, new String[][]{{"TEMPO"}});
    }


    @Test
    public void test_buffer() throws Exception {
        TokioFixture firstFixture = new TokioFixture(TokioFixtureTest.class);
        firstFixture.loadTokioFile();

        TokioFixture secondFixture = new TokioFixture(TokioFixtureTest.class);
        secondFixture.loadTokioFile();

        assertSame(firstFixture.getJDBCScenario("myStory").getScenario(),
                   secondFixture.getJDBCScenario("myStory").getScenario());
    }


    @Test
    public void test_buffer_new() throws Exception {
        TokioFixture firstFixture = new TokioFixture(TokioFixtureTest.class);
        firstFixture.loadTokioFile();

        TokioFixture secondFixture = new TokioFixture(TokioFixtureDummy.class);
        secondFixture.loadTokioFile();

        assertNotSame(firstFixture.getJDBCScenario("myStory").getScenario(),
                      secondFixture.getJDBCScenario("myStory").getScenario());
    }


    private void createTableTest() throws SQLException {
        fixture.getJdbcFixture().create(temporaryTable("TABLE_TEST"), "COL_A varchar(6)");
    }


    private SqlField createValuatedField(String value) {
        SqlField field = SqlField.fieldName(null);
        field.setValue(value);
        return field;
    }
}
