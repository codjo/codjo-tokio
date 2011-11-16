/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.util.XmlUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
/**
 * Classe responsable du chargement des properties a partir d'un fichier XML.
 *
 * @author $Author: cassagc $
 * @version $Revision: 1.1 $
 */
public class XMLPropertiesLoader implements XMLScenariiTags {
    private static final Logger LOG = Logger.getLogger(XMLScenariiLoader.class);


    public static void loadProperty(Scenario sc, Node node) {
        String name = XmlUtil.getAttribute(node, PROPERTY_NAME);
        String value = XmlUtil.getAttribute(node, PROPERTY_VALUE);
        if (sc.getProperties() == null) {
            Properties scProperties = (Properties)System.getProperties().clone();
            scProperties.setProperty(name, value);
            sc.setProperties(scProperties);
        }
        else {
            sc.getProperties().setProperty(name, value);
        }
    }


    public static void loadProperties(Scenario sc, Node propertiesNode, String uri) {
        String filename = XmlUtil.getAttribute(propertiesNode, PROPERTIES_FILENAME);
        boolean overwrite = "true".equals(XmlUtil.getAttribute(propertiesNode, PROPERTIES_OVERWRITE));
        Properties prop = System.getProperties();

        if (filename != null) {
            try {
                File fileObject = new File(getWorkingPath(uri), filename);
                if (fileObject.exists() && checkFileFormat(fileObject)) {
                    prop = createProperties(fileObject, overwrite);
                }
            }
            catch (Exception fileReadingException) {
                LOG.error(fileReadingException.getMessage(), fileReadingException);
            }
        }

        sc.setProperties(prop);
    }


    private static String getWorkingPath(String uri) {
        try {
            return (new File((new URL(uri)).getFile())).getParent();
        }
        catch (MalformedURLException e) {
            return uri;
        }
    }


    private static Properties createProperties(File fileObject, boolean overwrite)
          throws IOException {
        Properties currentProperties = (Properties)System.getProperties().clone();
        Properties properties = new Properties();
        properties.load(new FileInputStream(fileObject));
        if (overwrite) {
            return properties;
        }
        else {
            Set<Map.Entry<Object, Object>> propSet = properties.entrySet();
            for (Map.Entry<Object, Object> entry : propSet) {
                currentProperties.setProperty((String)entry.getKey(), (String)entry.getValue());
            }
            return currentProperties;
        }
    }


    public static boolean checkFileFormat(File fileToCheck)
          throws IOException {
        String regexToMatch = "[^\\s]+=.*";

        FileInputStream fileInputStream = new FileInputStream(fileToCheck);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        String currentLine = bufferedReader.readLine();
        while (currentLine != null) {
            if (!currentLine.matches(regexToMatch)) {
                bufferedReader.close();
                fileInputStream.close();
                return false;
            }
            currentLine = bufferedReader.readLine();
        }
        bufferedReader.close();
        fileInputStream.close();
        return true;
    }
}
