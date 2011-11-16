/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.util;
import net.codjo.util.file.FileUtil;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 *
 */
public final class XmlUtil {
    private static final String ENTITIES_DTD_URI = "entities.dtd";
    private static final String SCENARII_DTD_URI = "scenarii.dtd";
    private static final String STORY_DTD_URI = "story.dtd";
    private static final String CASES_DTD_URI = "cases.dtd";


    private XmlUtil() {
    }


    public static Document parse(String uri, String xmlContent)
          throws ParserConfigurationException, IOException, SAXException {
        return parse(uri, xmlContent, false);
    }


    public static Document parse(String uri, String xmlContent, boolean fullLocationFilePath)
          throws ParserConfigurationException, IOException, SAXException {
        String contentNoGroup = removeGroup(xmlContent);
        InputSource source = new InputSource(new StringReader(contentNoGroup));
        DOMParser parser = new TokioDOMParser(uri, fullLocationFilePath);
        parser.setErrorHandler(new XMLErrorHandler());
        parser.setEntityResolver(new DtdResolver());
        parser.parse(source);
        return parser.getDocument();
    }


    static String removeGroup(String xmlContent) {
        String step1 = xmlContent.replaceAll("<group>", "");
        String step2 = step1.replaceAll("<group name=.*?>", "");
        String step3 = step2.replaceAll("</group>", "");
        return step3;
    }


    public static String getAttribute(Node node, String attributeName) {
        String value = null;

        NamedNodeMap attributeNodes = node.getAttributes();
        if (attributeNodes != null) {
            Node attNode = attributeNodes.getNamedItem(attributeName);
            if (attNode != null) {
                value = attNode.getNodeValue();
            }
        }

        return value;
    }


    public static Boolean getBooleanAttribute(Node node, String attributeName) {
        String value = getAttribute(node, attributeName);
        if (value == null || "".equals(value)) {
            return null;
        }
        return ("on".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value));
    }


    public static String getXmlFileEncoding(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        try {
            return getXmlFileEncoding(inputStream);
        }
        finally {
            inputStream.close();
        }
    }


    public static String getXmlFileEncoding(URL url) throws IOException {
        InputStream inputStream = url.openStream();
        try {
            return getXmlFileEncoding(inputStream);
        }
        finally {
            inputStream.close();
        }
    }


    public static String loadContent(File file) throws IOException {
        return loadContent(file.toURL());
    }


    public static String loadContent(URL url) throws IOException {
        return FileUtil.loadContent(url, getXmlFileEncoding(url));
    }


    private static String getXmlFileEncoding(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = reader.readLine();
            if (line != null) {
                String str = "<?xml version=\"1.0\" encoding=\"";
                int fromIndex = line.indexOf(str);
                if (fromIndex >= 0) {
                    int toIndex = line.indexOf("\"?>", fromIndex + str.length());
                    return line.substring(fromIndex + str.length(), toIndex);
                }
            }
        }
        finally {
            reader.close();
        }
        return "UTF-8";
    }


    static class DtdResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) throws IOException {
            if (isDtd(systemId, ENTITIES_DTD_URI)) {
                return toInputSource(ENTITIES_DTD_URI);
            }
            if (isDtd(systemId, SCENARII_DTD_URI)) {
                return toInputSource(SCENARII_DTD_URI);
            }
            else if (isDtd(systemId, STORY_DTD_URI)) {
                return toInputSource(STORY_DTD_URI);
            }
            else if (isDtd(systemId, CASES_DTD_URI)) {
                return toInputSource(CASES_DTD_URI);
            }

            TokioLog.info("************************************************************************");
            TokioLog.info("************************************************************************");
            TokioLog.info("L'utilisation d'entite XML est proscrite, merci d'utiliser include-story");
            TokioLog.info("    sytemId = " + systemId);
            TokioLog.info("************************************************************************");
            TokioLog.info("************************************************************************");

            if (systemId.startsWith("file://")) {
                return new InputSource(systemId);
            }
            File file = toFile(systemId, "\\$\\{basedir\\}");
            if (!file.exists()) {
                File file2 = toFile(systemId, "\\$\\{basedir\\}\\\\\\.\\.");
                if (file2.exists()) {
                    return toInputSource(file2);
                }
            }
            return toInputSource(file);
        }


        private InputSource toInputSource(File file) throws IOException {
            return new InputSource(file.getCanonicalFile().toURL().toExternalForm());
        }


        private File toFile(String systemId, String baseDirPattern) {
            String path = systemId.replaceAll(baseDirPattern, ".");
            return new File(path);
        }


        private InputSource toInputSource(String uri) {
            return new InputSource(getClass().getResourceAsStream(uri));
        }


        private boolean isDtd(String systemId, String dtd) {
            return systemId.toLowerCase().endsWith(dtd);
        }
    }
}
