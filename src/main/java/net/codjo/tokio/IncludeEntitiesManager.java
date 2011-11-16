package net.codjo.tokio;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.util.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class IncludeEntitiesManager {
    private final XMLEntitiesLoader entitiesLoader;
    private List<File> entitiesFiles = new ArrayList<File>();


    public IncludeEntitiesManager(TokioConfiguration configuration) {
        this(new XMLEntitiesLoader(configuration));
    }


    public IncludeEntitiesManager(XMLEntitiesLoader entitiesLoader) {
        this.entitiesLoader = entitiesLoader;
    }


    public void process(Node node, File workingDirectory, EntityList entityList)
          throws IOException, SAXException, ParserConfigurationException {
        String includeAttribute = XmlUtil.getAttribute(node, XMLStoriesTags.INCLUDE_ENTITIES_FILE);
        File includeFile = new File(workingDirectory, includeAttribute);
        if (includeFile.exists()) {
            entitiesLoader.loadEntities(XmlUtil.loadContent(includeFile),
                                        includeFile.getParentFile(),
                                        includeFile,
                                        entityList);
        }
        else {
            URL resource = getClass().getResource(includeAttribute);
            if (resource == null) {
                throw new TokioLoaderException(computeErrorMessage(includeAttribute));
            }
            else {
                try {
                    if (resource.toURI().isOpaque()) {
                        String xmlContent = XmlUtil.loadContent(resource);
                        entitiesLoader.loadEntities(xmlContent, null, includeAttribute, entityList);
                    }
                    else {
                        includeFile = new File(resource.toURI());
                        if (includeFile.exists()) {
                            entitiesLoader.loadEntities(XmlUtil.loadContent(includeFile),
                                                        null,
                                                        includeFile,
                                                        entityList);
                        }
                        else {
                            throw new TokioLoaderException(computeErrorMessage(includeAttribute));
                        }
                    }
                }
                catch (URISyntaxException e) {
                    throw new TokioLoaderException(computeErrorMessage(includeAttribute));
                }
            }
        }
        entitiesFiles.add(includeFile);
    }


    private static String computeErrorMessage(String includeAttribute) {
        return includeAttribute + " n'existe pas";
    }


    public List<File> getEntitiesFiles() {
        return entitiesFiles;
    }
}
