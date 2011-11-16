/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import net.codjo.tokio.util.XmlUtil;
import net.codjo.variable.basic.BasicVariableReplacer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
/**
 *
 */
public class XMLTokioLoader {
    private final Map<String, String> antProperties;
    private IncludeEntitiesManager includeEntitiesManager;
    private final XMLScenarioLoader xmlScenarioLoader;
    private XMLStoryUtil storyUtil;


    public XMLTokioLoader(URL url)
          throws IOException, SAXException, ParserConfigurationException, TokioLoaderException {
        this(new File(url.getPath()));
    }


    public XMLTokioLoader(File file)
          throws IOException, SAXException, ParserConfigurationException, TokioLoaderException {
        this(file, Collections.<String, String>emptyMap());
    }


    public XMLTokioLoader(File file, Map<String, String> antProperties)
          throws IOException, SAXException, ParserConfigurationException, TokioLoaderException {
        this(file, antProperties, false);
    }


    public XMLTokioLoader(File file, Map<String, String> antProperties, boolean showFullLocationFilePath)
          throws IOException, SAXException, ParserConfigurationException, TokioLoaderException {
        if (!file.exists()) {
            throw new IllegalArgumentException("Fichier introuvable '" + file + "'");
        }

        this.antProperties = antProperties;
        this.xmlScenarioLoader = load(file.toURL().toString(),
                                      replaceVariablesInFile(file),
                                      showFullLocationFilePath);
    }


    public Scenario getScenario(String name) {
        return xmlScenarioLoader.getScenario(name);
    }


    public ScenarioList getScenarii() {
        return xmlScenarioLoader.getScenarii();
    }


    public IncludeEntitiesManager getIncludeEntitiesManager() {
        return includeEntitiesManager;
    }


    public XMLStoryUtil getStoryUtil() {
        return storyUtil;
    }


    private XMLScenarioLoader load(String uri, String fileContent, boolean showFullLocationFilePath)
          throws IOException, SAXException, ParserConfigurationException, TokioLoaderException {
        TokioConfiguration configuration = new TokioConfiguration();
        configuration.setFullLocationFilePath(showFullLocationFilePath);

        includeEntitiesManager = new IncludeEntitiesManager(configuration);
        CreateEntityManager createEntityManager = new CreateEntityManager();
        RequiredInstancesManager requiredInstancesManager = new RequiredInstancesManager();
        storyUtil = new XMLStoryUtil(configuration,
                                     includeEntitiesManager,
                                     createEntityManager,
                                     requiredInstancesManager);

        Document document = XmlUtil.parse(uri, fileContent, showFullLocationFilePath);

        XMLFileLoader xmlFileLoader;
        String rootName = document.getDocumentElement().getNodeName();
        if (rootName.equals(XMLScenariiLoader.SCENARII)) {
            xmlFileLoader = new XMLScenariiLoader();
        }
        else if (rootName.equals(XMLStoriesLoader.STORY)) {
            xmlFileLoader = new XMLStoriesLoader(storyUtil);
        }
        else if (rootName.equals(XMLCasesLoader.CASES)) {
            xmlFileLoader = new XMLCasesLoader(includeEntitiesManager,
                                               requiredInstancesManager,
                                               storyUtil);
        }
        else {
            throw new RuntimeException(
                  "Votre fichier tokio doit contenir une balise <Scenarii>, une balise <story> ou une balise <cases>.");
        }
        xmlFileLoader.parse(document, uri, antProperties);

        requiredInstancesManager.finalizeDatasets();

        return (XMLScenarioLoader)xmlFileLoader;
    }


    private String replaceVariablesInFile(File file) throws IOException {
        return BasicVariableReplacer.replaceKeysPerValues(XmlUtil.loadContent(file), antProperties);
    }
}
