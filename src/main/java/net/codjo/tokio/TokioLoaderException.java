package net.codjo.tokio;
import net.codjo.tokio.util.TokioDOMParser;
import org.w3c.dom.Node;

public class TokioLoaderException extends RuntimeException {
    private static final String NODE_ERROR_MESSAGE = "%s\n%s(%s:%s)";


    public TokioLoaderException(String message) {
        super(message);
    }


    public TokioLoaderException(String message, Node node) {
        super(node == null ? message : computeMessageWithNode(message, node));
    }


    protected TokioLoaderException(String message, Throwable cause) {
        super(message, cause);
    }


    private static String computeMessageWithNode(String message, Node node) {
        String tokioFile = TokioDOMParser.getTokioFile(node);
        if (tokioFile == null || "".equals(tokioFile)) {
            tokioFile = "UNKNOWN_TOKIO_FILE";
        }
        return String.format(NODE_ERROR_MESSAGE,
                             message,
                             TokioDOMParser.getTokioTag(node),
                             tokioFile,
                             TokioDOMParser.getLineNumber(node));
    }
}
