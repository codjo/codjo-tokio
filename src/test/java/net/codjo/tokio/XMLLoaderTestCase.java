/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.util.XmlUtil;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
/**
 *
 */
public abstract class XMLLoaderTestCase extends TestCase {
    protected Document loadDocument(String uri)
          throws ParserConfigurationException, IOException, SAXException {
        return XmlUtil.parse(uri, XmlUtil.loadContent(new URL(uri)));
    }
}
