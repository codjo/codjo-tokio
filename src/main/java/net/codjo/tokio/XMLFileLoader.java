/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 * Classe abstraite permettant la gestion de l'URL des fichier XML.
 *
 * @author $Author: cassagc $
 * @version $Revision: 1.1 $
 */
abstract public class XMLFileLoader {
    protected String uri;
    protected ScenarioList scenarii = new ScenarioList();


    protected XMLFileLoader() {
    }


    protected XMLFileLoader(URL url) {
        uri = url.toString();
    }


    protected XMLFileLoader(File file) throws MalformedURLException {
        uri = file.toURL().toString();
    }


    public ScenarioList getScenarii() {
        return scenarii;
    }


    public Scenario getScenario(String name) {
        return scenarii.getScenario(name);
    }


    public abstract void parse(Document doc, String fileUri, Map<String, String> globalProperties) throws
                                                                                                   IOException,
                                                                                                   ParserConfigurationException,
                                                                                                   SAXException;


    protected abstract Scenario doCreateScenario(Node storyNode);


    protected Scenario createScenario(Node node, Map<String, String> globalProperties) {
        Scenario scenario = doCreateScenario(node);
        if (globalProperties != null) {
            if (scenario.getProperties() == null) {
                scenario.setProperties(new Properties());
            }
            scenario.getProperties().putAll(globalProperties);
        }
        return scenario;
    }
}
