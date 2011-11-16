/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.util;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * Handler d'erreur.
 */
class XMLErrorHandler implements ErrorHandler {
    public void error(SAXParseException sAXParseException)
          throws SAXException {
        TokioLog.error("ERROR : ");
        print(sAXParseException);
        throw sAXParseException;
    }


    public void fatalError(SAXParseException sAXParseException)
          throws SAXException {
        TokioLog.error("FATAL : ");
        print(sAXParseException);
        throw sAXParseException;
    }


    public void warning(SAXParseException sAXParseException)
          throws SAXException {
        TokioLog.error("WARNING : ");
        print(sAXParseException);
    }


    private void print(SAXParseException ex) {
        TokioLog.error("[L=" + ex.getLineNumber() + " C=" + ex.getColumnNumber() + "] "
                       + ex.toString());
    }
}
