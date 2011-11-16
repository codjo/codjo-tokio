package net.codjo.tokio;
import net.codjo.test.common.Directory.NotDeletedException;
import net.codjo.test.common.LogString;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class IncludeEntitiesManagerTest {
    private DirectoryFixture directoryFixture = DirectoryFixture.newTemporaryDirectoryFixture();
    private LogString log = new LogString();
    private IncludeEntitiesManager entityManager = new IncludeEntitiesManager(new XMLEntitiesLoaderMock(log));
    private File entitiesDirectory;


    @Before
    public void setUp() throws NotDeletedException {
        directoryFixture.doSetUp();

        entitiesDirectory = new File(directoryFixture, "_entities");
        entitiesDirectory.mkdir();
    }


    @After
    public void tearDown() throws NotDeletedException {
        directoryFixture.doTearDown();
    }


    @Test
    public void test_nominal() throws Exception {
        new File(entitiesDirectory, "entities.xml").createNewFile();
        String xmlContent = "<include-entities file='_entities/entities.xml'/>";

        entityManager.process(nodeFrom(xmlContent), directoryFixture, new EntityList());

        log.assertContent("loadEntities(entities.xml, " + entitiesDirectory.getPath() + ")");
    }


    @Test
    public void test_fromResources_absolute() throws Exception {
        String xmlContent = "<include-entities file='/net/codjo/tokio/empty.entities'/>";

        entityManager.process(nodeFrom(xmlContent), directoryFixture, new EntityList());

        log.assertContent("loadEntities(empty.entities, null)");
    }


    @Test
    public void test_unknownEntityFile_mustThrowException() throws Exception {
        String xmlContent = "<include-entities file='_entities/none.entities'/>";

        try {
            entityManager.process(nodeFrom(xmlContent), directoryFixture, new EntityList());
            fail();
        }
        catch (TokioLoaderException e) {
            assertEquals("_entities/none.entities n'existe pas", e.getMessage());
        }

        log.assertContent("");
    }


    @Test
    public void test_loadEntitiesFiles() throws Exception {
        String xmlContent1 = "<include-entities file='/net/codjo/tokio/required_withEntities.entities'/>";
        String xmlContent2 = "<include-entities file='/net/codjo/tokio/required_withInheritedCase.entities'/>";

        entityManager.process(nodeFrom(xmlContent1), directoryFixture, new EntityList());
        entityManager.process(nodeFrom(xmlContent2), directoryFixture, new EntityList());

        assertEquals(2, entityManager.getEntitiesFiles().size());
        int pos=0;
        for (File file : entityManager.getEntitiesFiles()) {
            if (pos==0) {
                assertEquals("required_withEntities.entities", file.getName());
            }
            if (pos==1) {
                assertEquals("required_withInheritedCase.entities", file.getName());

            }
            pos++;
        }
    }


    private static Node nodeFrom(String xmlContent)
          throws IOException, ParserConfigurationException, SAXException {
        Document document = XmlUtil.parse(null, xmlContent);
        return document.getFirstChild();
    }


    private static class XMLEntitiesLoaderMock extends XMLEntitiesLoader {
        private LogString log;


        private XMLEntitiesLoaderMock(LogString log) {
            super(new TokioConfiguration());
            this.log = log;
        }


        @Override
        public void loadEntities(String xmlContent, File workingDirectory, File file,
                                 EntityList globalEntityList
        )
              throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
            log.call("loadEntities",
                     file.getName(),
                     workingDirectory == null ? null : workingDirectory.getPath());
        }


        @Override
        public void loadEntities(
              String xmlContent, File workingDirectory, String fileName,
              EntityList globalEntityList)
              throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
            log.call("loadEntities", "InputStream", workingDirectory.getPath());
        }
    }
}
