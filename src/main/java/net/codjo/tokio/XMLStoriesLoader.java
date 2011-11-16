/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.EntityDictionary;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.RowDictionary;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 *
 */
public class XMLStoriesLoader extends XMLFileLoader implements XMLStoriesTags, XMLScenarioLoader {
    private final RowDictionary rowDictionary = new RowDictionary();
    private XMLStoryUtil xmlStoryUtil;


    public XMLStoriesLoader(XMLStoryUtil xmlStoryUtil) {
        this.xmlStoryUtil = xmlStoryUtil;
    }


    @Override
    public void parse(Document doc, String fileUri, Map<String, String> globalProperties)
          throws IOException, ParserConfigurationException, SAXException, TokioLoaderException {
        this.uri = fileUri;
        File workingDirectory = new File(new URL(fileUri).getPath()).getParentFile();
        Element storyNode = doc.getDocumentElement();
        Scenario scenario = createScenario(storyNode, globalProperties);
        EntityList entityList = new EntityList();
        EntityDictionary entityDictionary = new EntityDictionary();
        xmlStoryUtil.loadStory(storyNode,
                               scenario,
                               workingDirectory,
                               entityList,
                               entityDictionary);
        scenarii.addScenario(scenario);
    }


    @Override
    protected Scenario doCreateScenario(Node storyNode) {
        return new Scenario(XmlUtil.getAttribute(storyNode, STORY_ID), "", rowDictionary);
    }
}
