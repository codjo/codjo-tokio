/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.util;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.AssertionFailedError;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 * Classe utilitaire pour la génération.
 *
 * @author $Author: crego $
 * @version $Revision: 1.8 $
 */
public final class Util {
    private Util() {
    }


    public static void compare(String expected, String actual) {
        if (expected.equals(actual)) {
            return;
        }
        for (int i = 0; i < expected.length(); i++) {
            if (!actual.startsWith(expected.substring(0, i))) {
                int min = Math.max(0, i - 30);
                String a =
                      "..." + expected.substring(min, Math.min(i + 30, expected.length()))
                      + "...";
                String b =
                      "..." + actual.substring(min, Math.min(i + 30, actual.length()))
                      + "...";
                throw new AssertionFailedError("Comparaison\n\texpected = " + a + "\n"
                                               + "\tactual   = " + b);
            }
        }
    }


    /**
     * Mise à plat de la chaîne de charactère (sans saut de ligne).
     */
    public static String flatten(String str) {
        StringBuilder buffer = new StringBuilder();
        boolean previousWhite = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\r' || ch == '\n') {
            }
            else if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                if (!previousWhite) {
                    buffer.append(" ");
                }
                previousWhite = true;
            }
            else {
                buffer.append(ch);
                previousWhite = false;
            }
        }

        return buffer.toString();
    }


    public static Node encapsulatedNodeFrom(String xmlContent)
          throws IOException, ParserConfigurationException, SAXException {
        return encapsulatedNodeFrom(xmlContent, null);
    }


    public static Node encapsulatedNodeFrom(String xmlContent, String xmlFileUri)
          throws IOException, ParserConfigurationException, SAXException {
        return nodeFrom(xmlContent, xmlFileUri, true);
    }


    public static Node nodeFrom(String xmlContent)
          throws IOException, ParserConfigurationException, SAXException {
        return nodeFrom(xmlContent, null);
    }


    public static Node nodeFrom(String xmlContent, String xmlFileUri)
          throws IOException, ParserConfigurationException, SAXException {
        return nodeFrom(xmlContent, xmlFileUri, false);
    }


    private static Node nodeFrom(String xmlContent, String xmlFileUri, boolean encapsulate)
          throws IOException, ParserConfigurationException, SAXException {
        if (encapsulate) {
            xmlContent = "<input>" + xmlContent + "</input>";
        }
        Document document = XmlUtil.parse(xmlFileUri, xmlContent);
        return document.getFirstChild();
    }
}
