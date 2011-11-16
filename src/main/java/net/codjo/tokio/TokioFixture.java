/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.test.common.fixture.Fixture;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.SQLException;
import org.junit.Assert;
/**
 * Fixture permettant d'interagir facilement avec la librairie Tokio. Ce fixture utilise un {@link
 * JdbcFixture} pour acceder à la base.<p><u>Utilisation Simple</u> : Le fichier <tt>tokio.properties</tt>
 * doit se trouver à la racine de l'arborescence des tests, un fichier tokio <tt>MyClassDeTestTest.xml</tt>
 * doit se trouver dans le même repertoire que la classe de test.</p>
 * <pre>public class MyClassDeTestTest extends TestCase {
 *     private TokioFixture tokioFixture = new TokioFixture(MyClassDeTestTest.class);
 *      protected void setUp() throws Exception {        tokioFixture.doSetUp();    }
 *     public void test_myTest() throws SQLException {        tokioFixture.insertInputInDb("myStory");
 *         // Execute les traitements        tokioFixture.assertAllOutputs("myStory");    }
 *     protected void tearDown() throws Exception {        tokioFixture.doTearDown();
 * }}</pre><p><u>Utilisation
 * Etendu</u>.</p>
 * <pre>public class MyClassDeTestTest extends TestCase {
 *     private TokioFixture tokioFixture = new TokioFixture(MyClassDeTestTest.class, "MyTokioFile.tokio");
 *       protected void setUp() throws Exception {
 *         tokioFixture.doSetUp(JdbcFixture.newTokioFixture());    }
 *       public void test_myTest() throws SQLException {        tokioFixture.insertInputInDb("myStory");
 *          // Execute les traitements         tokioFixture.assertAllOutputs("myStory");    }
 *       protected void tearDown() throws Exception {        tokioFixture.doTearDown();    }}</pre>
 */
public class TokioFixture extends CompositeFixture {
    @SuppressWarnings({"StaticNonFinalField"})
    private static TokioFile lastLoadedFile = null;
    private TokioFile tokioFile;
    private JDBCScenario loadedScenario;


    public TokioFixture(Class testClass, String tokioFileName) {
        tokioFile = new TokioFile(testClass, tokioFileName);
    }


    public TokioFixture(Class testClass) {
        tokioFile = new TokioFile(testClass);
    }


    public void insertInputInDb(String storyName) throws SQLException {
        getJDBCScenario(storyName).insertInputInDb(getConnection());
    }


    public void insertInputInDb(String storyName, boolean deleteBeforeInsert) throws SQLException {
        JDBCScenario jdbcScenario = getJDBCScenario(storyName);
        jdbcScenario.setDeleteBeforeInsert(deleteBeforeInsert);
        jdbcScenario.insertInputInDb(getConnection());
    }


    public void assertAllOutputs(String storyName) throws SQLException {
        Assert.assertTrue("Assert Etalon", getJDBCScenario(storyName).verifyAllOutputs(getConnection()));
    }


    /**
     * @Deprecated Utiliser executeUpdate
     */
    public void executeQuery(String query) throws SQLException {
        executeUpdate(query);
    }


    public void executeUpdate(String query) throws SQLException {
        getJdbcFixture().executeUpdate(query);
    }


    public void assertQueryResult(String query, String[][] expected) throws SQLException {
        getJdbcFixture().assertQueryResult(query, expected);
    }


    public JDBCScenario getJDBCScenario(String storyName) {
        if (loadedScenario == null || !storyName.equals(loadedScenario.getScenario().getName())) {
            if (!tokioFile.isLoaded()) {
                fail("Le fichier Tokio n'est pas chargé ! (Appeler la méthode doSetup(...) )");
            }
            loadedScenario = tokioFile.getScenario(storyName);
        }
        return loadedScenario;
    }


    public JdbcFixture getJdbcFixture() {
        JdbcFixture jdbcFixture = (JdbcFixture)get(JdbcFixture.class);
        if (jdbcFixture == null) {
            fail("Pas de connection disponible ! (Appeler la méthode doSetup(...) )");
        }
        return jdbcFixture;
    }


    public java.sql.Connection getConnection() {
        return getJdbcFixture().getConnection();
    }


    public void doSetUp(JdbcFixture jdbcFixture) throws Exception {
        addFixture(jdbcFixture);
        addFixture(new Fixture() {
            public void doSetUp() throws Exception {
                loadTokioFile();
            }


            public void doTearDown() throws Exception {
                tokioFile = null;
                loadedScenario = null;
            }
        });
        super.doSetUp();
    }


    void loadTokioFile() throws FileNotFoundException {
        // Pour les tests (evite de faire un setup)
        if (tokioFile.isSameThan(lastLoadedFile)) {
            tokioFile.loadFrom(lastLoadedFile);
        }
        else {
            tokioFile.load();
        }
        lastLoadedFile = tokioFile;
    }


    @Override
    public void doSetUp() throws Exception {
        doSetUp(Tokio.newJdbcFixture());
    }


    private void fail(String message) {
        throw new IllegalStateException(message);
    }


    private static class TokioFile {
        private XMLTokioLoader loader;
        private Class testClass;
        private String tokioFileName;


        TokioFile(Class testClass) {
            this.testClass = testClass;
        }


        TokioFile(Class testClass, String tokioFileName) {
            this.testClass = testClass;
            this.tokioFileName = tokioFileName;
        }


        JDBCScenario getScenario(String storyName) {
            return new JDBCScenario(loader.getScenario(storyName));
        }


        public boolean isLoaded() {
            return loader != null;
        }


        void load() throws FileNotFoundException {
            if (tokioFileName != null) {
                loadFromNamedFile();
            }
            else {
                loadFromClassName();
            }
        }


        public boolean isSameThan(TokioFile lastLoadedFile) {
            return lastLoadedFile != null
                   && lastLoadedFile.testClass == testClass
                   && isSame(lastLoadedFile.tokioFileName, tokioFileName);
        }


        public void loadFrom(TokioFile lastLoadedFile) {
            loader = lastLoadedFile.loader;
        }


        private void loadFromNamedFile() {
            try {
                loader = new XMLTokioLoader(testClass.getResource(tokioFileName));
            }
            catch (Exception exception) {
                throw new TokioLoaderException(
                      "Le fichier tokio '" + tokioFileName + "' est introuvable ou invalide", exception);
            }
        }


        private void loadFromClassName() throws FileNotFoundException {
            try {
                String fileName = toTokioFileName("tokio");
                URL resource = testClass.getResource(fileName);
                if (resource != null) {
                    loader = new XMLTokioLoader(resource);
                }
                else {
                    fileName = toTokioFileName("xml");
                    resource = testClass.getResource(fileName);
                    if (resource != null) {
                        loader = new XMLTokioLoader(resource);
                    }
                    else {
                        throw new FileNotFoundException("Le fichier tokio '" + toTokioFileName("tokio")
                                                        + "' (ou '" + toTokioFileName("xml") + "')"
                                                        + " est introuvable.");
                    }
                }
            }
            catch (FileNotFoundException ex) {
                throw ex;
            }
            catch (Exception exception) {
                throw new TokioLoaderException("Le fichier tokio '" + toTokioFileName("tokio")
                                               + "' (ou '" + toTokioFileName("xml") + "')"
                                               + " est invalide.", exception);
            }
        }


        private String toTokioFileName(String extension) {
            String name = testClass.getName();
            String shortClassName = name.substring(name.lastIndexOf('.') + 1, name.length());
            return shortClassName + "." + extension;
        }


        private boolean isSame(String first, String second) {
            return (first == null && second == null)
                   || (first != null && first.equals(second));
        }
    }
}
