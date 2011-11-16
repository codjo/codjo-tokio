package net.codjo.tokio.util;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.File;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
/**
 *
 */
public class TokioDOMParser extends DOMParser {
    private static final String LINE_NUMBER = "lineNumber";
    private static final String TOKIO_FILE = "tokioFile";
    private final String tokioFile;
    private XMLLocator documentLocator;
    private boolean tokioFileSet = false;


    TokioDOMParser(String tokioFile, boolean fullLocationFilePath)
          throws SAXNotSupportedException, SAXNotRecognizedException {
        if (tokioFile == null) {
            this.tokioFile = null;
        }
        else if (!fullLocationFilePath) {
            this.tokioFile = new File(tokioFile).getName();
        }
        else if (tokioFile.startsWith("file:/")) {
            this.tokioFile = tokioFile.substring(6);
        }
        else {
            this.tokioFile = tokioFile;
        }

        setFeature(Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE, false);
        setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, false);
        setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.INCLUDE_IGNORABLE_WHITESPACE, true);
        setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.CREATE_ENTITY_REF_NODES_FEATURE, false);
        setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.INCLUDE_COMMENTS_FEATURE, true);
        setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.CREATE_CDATA_NODES_FEATURE, true);
        setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.DEFER_NODE_EXPANSION_FEATURE, false);
    }


    public static int getLineNumber(Node node) {
        return (Integer)node.getUserData(LINE_NUMBER);
    }


    public static String getTokioFile(Node node) {
        return (String)node.getOwnerDocument().getUserData(TOKIO_FILE);
    }


    public static String getTokioTag(Node node) {
        return node.getNodeName();
    }


    @Override
    public void startDocument(XMLLocator locator,
                              String encoding,
                              NamespaceContext namespaceContext,
                              Augmentations augs) throws XNIException {
        super.startDocument(locator, encoding, namespaceContext, augs);
        documentLocator = locator;
    }


    @Override
    public void startElement(QName elementQName, XMLAttributes attrList, Augmentations augs)
          throws XNIException {
        super.startElement(elementQName, attrList, augs);
        saveNodeInfos();
    }


    private void saveNodeInfos() {
        Node node = null;
        try {
            node = (Node)getProperty(CURRENT_ELEMENT_NODE);
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        if (node != null) {
            node.setUserData(LINE_NUMBER, documentLocator.getLineNumber(), null);
            if (!tokioFileSet) {
                node.getOwnerDocument().setUserData(TOKIO_FILE, tokioFile, null);
                tokioFileSet = true;
            }
        }
    }
}
