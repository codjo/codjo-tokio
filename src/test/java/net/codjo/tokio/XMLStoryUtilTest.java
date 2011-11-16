package net.codjo.tokio;
import net.codjo.test.common.LogString;
import net.codjo.test.common.PathUtil;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Entity;
import net.codjo.tokio.model.EntityDictionary;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.util.Util;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.w3c.dom.Node;

public class XMLStoryUtilTest {
    private LogString log = new LogString();
    private TokioConfiguration configuration = new TokioConfiguration();
    private XMLStoryUtil xmlStoryUtil = new XMLStoryUtil(configuration,
                                                         new IncludeEntitiesManager(configuration),
                                                         new CreateEntityManagerMock(log),
                                                         new RequiredInstancesManagerMock(log));
    private EntityDictionary entityDictionary = new EntityDictionary();


    @Test
    public void test_loadStory_createEntity() throws Exception {
        String xmlContent = "<root><input><create-entity name='son'/></input></root>";

        EntityList entityList = new EntityList();
        entityList.addEntity(new Entity("son"));
        xmlStoryUtil.loadStory(Util.nodeFrom(xmlContent, null),
                               new Scenario("Mon scenario", ""),
                               PathUtil.find(getClass(), "TokioWithAntProperties.tokio").getParentFile(),
                               entityList,
                               new EntityDictionary());

        log.assertContent("getRequiredDataset(), "
                          + "process(create-entity(son), "
                          + entityList.toString() + ")");
    }


    @Test
    public void test_includeStory() throws Exception {
        String xmlContent = "<include-story file='TokioWithAntProperties.tokio'/>\n";

        Scenario scenario = new Scenario("Mon scenario", "");

        Properties properties = new Properties();
        properties.setProperty("$key1$", "VALUE");
        scenario.setProperties(properties);

        File file = PathUtil.find(getClass(), "TokioWithAntProperties.tokio");
        final EntityList entityList = new EntityList();
        xmlStoryUtil.includeStory(Util.nodeFrom(xmlContent, null),
                                  file.getParentFile(),
                                  scenario,
                                  entityList,
                                  entityDictionary);

        String resolvedValue = scenario.getInputTable("TABLE").getRow(0).getFields().get("FIELD").getValue();

        assertEquals("VALUE", resolvedValue);
    }


    @Test
    public void test_callToCreateEntityManager() throws Exception {
        String xmlContent = "  <create-entity name='son'/>"
                            + "<create-entity name='father'/>";

        EntityList entityList = new EntityList();
        entityList.addEntity(new Entity("son"));
        entityList.addEntity(new Entity("father"));
        xmlStoryUtil.loadDataSet(new DataSet(),
                                 Util.encapsulatedNodeFrom(xmlContent, null),
                                 entityList,
                                 entityDictionary);

        String expected = "getRequiredDataset(), "
                          + "process(create-entity(%s), "
                          + entityList.toString() + ")";
        log.assertContent(String.format(expected, "son") + ", " + String.format(expected, "father"));
    }


    @Test
    public void test_nullEntityId() throws Exception {
        String xmlContent = "<create-entity name='minimal' id='null'/>";

        try {
            EntityList entityList = new EntityList();
            entityList.addEntity(new Entity("minimal"));
            xmlStoryUtil.loadDataSet(new DataSet(),
                                     Util.encapsulatedNodeFrom(xmlContent, null),
                                     entityList,
                                     entityDictionary);
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals(
                  "La valeur 'null' pour l'id est interdite.\n"
                  + "create-entity(UNKNOWN_TOKIO_FILE:1)",
                  e.getMessage());
        }
    }


    private static class CreateEntityManagerMock extends CreateEntityManager {
        private LogString log;


        private CreateEntityManagerMock(LogString log) {
            this.log = log;
        }


        @Override
        public void process(DataSet dataset,
                            DataSet requiredDataset, Node createEntityNode,
                            EntityList entityList,
                            EntityDictionary entityDictionary,
                            EntityDictionary requiredEntityDictionary)
              throws TokioLoaderException {
            log.call("process",
                     createEntityNode.getNodeName() + "(" + XmlUtil.getAttribute(createEntityNode, "name")
                     + ")",
                     entityList);
        }
    }

    private class RequiredInstancesManagerMock extends RequiredInstancesManager {
        private LogString log;


        private RequiredInstancesManagerMock(LogString log) {
            this.log = log;
        }


        @Override
        public DataSet getRequiredDataset(DataSet dataset) {
            log.call("getRequiredDataset");
            return super.getRequiredDataset(dataset);
        }


        @Override
        public void finalizeDatasets() {
            log.call("finalizeDatasets");
            super.finalizeDatasets();
        }
    }
}
