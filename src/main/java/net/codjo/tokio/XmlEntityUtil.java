package net.codjo.tokio;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import net.codjo.tokio.model.Parameter;
import net.codjo.tokio.util.XmlUtil;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlEntityUtil {
    private XmlEntityUtil() {
    }


    public static void processCreateEntityParameters(Node createEntityNode,
                                                     Map<String, Parameter> parametersToValues) {
        NodeList nodes = createEntityNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            processEntityParameter(nodes.item(i), parametersToValues);
        }
    }


    public static void processEntityParameter(Node node, Map<String, Parameter> parametersToValues) {
        String nodeName = node.getNodeName();
        if (!XMLStoriesTags.CREATE_ENTITY_PARAMETER.equals(nodeName)) {
            return;
        }

        GeneratorConfiguration generatorConfiguration = null;
        for (int j = 0; j < node.getChildNodes().getLength() && generatorConfiguration == null; j++) {
            Node generator = node.getChildNodes().item(j);

            if (generator.getNodeName().startsWith(XMLScenariiTags.GENERATOR_NAME)) {
                String generatorName = generator.getNodeName();
                String generatorPrecision = XmlUtil.getAttribute(generator,
                                                                 XMLScenariiTags.GENERATOR_PRECISION);
                generatorConfiguration = new GeneratorConfiguration(generatorName,
                                                                    generatorPrecision);
                parametersToValues.put(XmlUtil.getAttribute(node, XMLStoriesTags.PARAMETER_NAME),
                                       new Parameter(generatorConfiguration));
            }
        }

        if (generatorConfiguration == null) {
            String fieldValue = XmlUtil.getAttribute(node, XMLStoriesTags.PARAMETER_VALUE);
            Node fieldNullNode = node.getAttributes().getNamedItem(XMLStoriesTags.PARAMETER_NULL);
            if (fieldNullNode != null) {
                String fieldNullValue = fieldNullNode.getNodeValue();
                if ("false".equals(fieldNullValue)) {
                    throw new RuntimeException(
                          "La balise <parameter> avec l'attribut 'null' doit être à true ou l'attribut 'null' ne doit pas être présent.");
                }
                else if (fieldValue != null) {
                    throw new RuntimeException(
                          "La balise <parameter> avec l'attribut 'null' ne doit pas contenir d'attribut 'value'.");
                }
            }
            parametersToValues.put(XmlUtil.getAttribute(node, XMLStoriesTags.PARAMETER_NAME),
                                   new Parameter(fieldValue));
        }
    }
}
