/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.Scenario;
import java.io.File;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;
/**
 * Classe de test.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.17 $
 */
public class XMLPropertiesLoaderTest extends XMLLoaderTestCase {
    private Properties propertiesBackup;


    public void test_pasDeChargementProperties() throws Exception {
        Properties properties = backupSystemProperties();
        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        Scenario sc = loader.getScenario("PasDeChargementProperties");
        setProperties(sc);

        assertEquals(properties, System.getProperties());
    }


    public void test_chargementProperties() throws Exception {
        Properties properties = backupSystemProperties();
        assertFalse("toto".equals(properties.getProperty("user.name")));

        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        Scenario sc = loader.getScenario("ChargementProperties");
        setProperties(sc);

        properties.setProperty("user.name", "toto");
        assertEquals(properties, System.getProperties());
    }


    public void test_chargementPropertiesNotFound()
          throws Exception {
        Properties properties = backupSystemProperties();
        assertFalse((new File(System.getProperty("user.dir")
                              + "src/test/resources/test/properties/file.unexisting")).exists());

        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        Scenario sc = loader.getScenario("ChargementPropertiesNotFound");
        setProperties(sc);

        assertEquals(properties, System.getProperties());
    }


    public void test_chargementPropertiesBadProperty()
          throws Exception {
        Properties properties = backupSystemProperties();
        assertNull(System.getProperty("pouet"));

        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        Scenario sc = loader.getScenario("ChargementPropertiesBadProperty");
        setProperties(sc);

        properties.setProperty("pouet", "toto");
        assertEquals(properties, System.getProperties());
    }


    public void test_chargementPropertiesCorrupted()
          throws Exception {
        Properties properties = backupSystemProperties();

        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        Scenario sc = loader.getScenario("ChargementPropertiesCorrupted");
        setProperties(sc);

        assertEquals(properties, System.getProperties());
    }


    public void test_chargementPropertiesOverwrite()
          throws Exception {
        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        Scenario sc = loader.getScenario("ChargementPropertiesOverwrite");
        setProperties(sc);

        Properties properties = new Properties();
        properties.setProperty("pouet", "youpi");
        assertEquals(properties, System.getProperties());
    }


    public void test_chargementPropertySimple() throws Exception {
        Properties properties = backupSystemProperties();
        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        Scenario sc = loader.getScenario("ChargementPropertySimple");
        setProperties(sc);

        properties.setProperty("user.name", "toto");
        assertEquals(properties, System.getProperties());
    }


    public void test_tearPropertiesDown() throws Exception {
        Properties properties = backupSystemProperties();
        XMLScenariiLoader loader =
              load(XMLPropertiesLoaderTest.class
                    .getResource("/test/scenarii/ScenariiForTestProperties.xml").toString());
        JDBCScenario jdbcsc = new JDBCScenario(loader.getScenario("ChargementPropertySimple"));
        jdbcsc.setPropertiesUp();

        Properties modifiedProperties = (Properties)properties.clone();
        modifiedProperties.setProperty("user.name", "toto");
        assertEquals(modifiedProperties, System.getProperties());

        jdbcsc.tearPropertiesDown();

        assertEquals(properties, System.getProperties());
    }


    private void setProperties(Scenario sc) throws ClassNotFoundException, SQLException {
        JDBCScenario jdbcsc = new JDBCScenario(sc);
        jdbcsc.setPropertiesUp();
    }


    private XMLScenariiLoader load(String uri) throws Exception {
        XMLScenariiLoader loader = new XMLScenariiLoader();
        loader.parse(loadDocument(uri), uri, null);
        return loader;
    }


    private Properties backupSystemProperties() {
        return (Properties)System.getProperties().clone();
    }


    @Override
    protected void tearDown() throws Exception {
        System.setProperties(propertiesBackup);
    }


    @Override
    protected void setUp() throws Exception {
        // La propriété user.timezone est initialisée à null mais modifiée lors de l'exécution :
        // on force donc l'initialisation pour permettre la comparaison avant / après.
        // @quote : Except when user.timezone is set, the JVM gets the timezone like any
        // other program: it checks the value of TZ (environment variable) and
        // failing that, uses the system default timezone.
        System.setProperty("user.timezone", TimeZone.getDefault().getID());
        propertiesBackup = backupSystemProperties();
    }
}
