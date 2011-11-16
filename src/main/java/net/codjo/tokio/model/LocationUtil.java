package net.codjo.tokio.model;
import net.codjo.tokio.util.TokioDOMParser;
import org.w3c.dom.Node;
/**
 *
 */
public class LocationUtil {

    private LocationUtil() {
    }


    public static Location getLocationFromNode(Node node) {
        return new Location(TokioDOMParser.getTokioFile(node),
                            TokioDOMParser.getLineNumber(node),
                            node.getNodeName());
    }


    public static void setLocationForNewOrCopyRow(Row rowToUpdate, Node node) {
        setLocation(rowToUpdate, node, null);
    }


    public static void setLocationForNewOrCopyRow(Row rowToUpdate, Node node, String suffix) {
        setLocation(rowToUpdate, node, null, suffix);
    }


    public static void setLocationForReplaceRow(Row rowToUpdate, Node node) {
        setLocation(rowToUpdate, node, rowToUpdate);
    }


    public static void setLocationForInheritedRow(Row rowToUpdate, Row originalRow, Node node) {
        Location newLocation = LocationUtil.getLocationFromNode(node);
        newLocation.setLocationPointer(originalRow.getLocationPointer());
        rowToUpdate.setLocationPointer(new LocationPointer(newLocation));
    }


    public static void setLocation(Row rowToUpdate, Node currentNode, Row originalRow) {
        Location nodeLocation = getLocationFromNode(currentNode);
        if (originalRow != null) {
            nodeLocation.setLocationPointer(originalRow.getLocationPointer());
        }
        else if (rowToUpdate.getRefId() != null) {
            nodeLocation.setLocationPointer(new RowIdLocationPointer(rowToUpdate));
        }
        rowToUpdate.setLocationPointer(new LocationPointer(nodeLocation));
    }


    private static void setLocation(Row rowToUpdate, Node currentNode, Row originalRow, String suffix) {
        Location nodeLocation = getLocationFromNode(currentNode);
        if (suffix != null && !"".equals(suffix)) {
            nodeLocation.setTokioTag(nodeLocation.getTokioTag() + "-" + suffix);
        }
        if (originalRow != null) {
            nodeLocation.setLocationPointer(originalRow.getLocationPointer());
        }
        else if (rowToUpdate.getRefId() != null) {
            nodeLocation.setLocationPointer(new RowIdLocationPointer(rowToUpdate));
        }
        rowToUpdate.setLocationPointer(new LocationPointer(nodeLocation));
    }


    public static void mergeLocationPointers(LocationPointer toUpdate, LocationPointer toMerge) {
        for (Location locationToMerge : toMerge.getLocations()) {
            if (!mergeLocations(toUpdate, locationToMerge)) {
                toUpdate.addLocation(locationToMerge);
            }
        }
    }


    private static boolean mergeLocations(LocationPointer toUpdate, Location toMerge) {
        for (Location locationToUpdate : toUpdate.getLocations()) {
            if (locationToUpdate.isSame(toMerge)) {
                LocationPointer locationPointerToMerge = toMerge.getLocationPointer();
                if (locationPointerToMerge != null) {
                    LocationPointer locationPointerToUpdate = locationToUpdate.getLocationPointer();
                    if (locationPointerToUpdate == null) {
                        locationToUpdate.setLocationPointer(new LocationPointer());
                    }
                    mergeLocationPointers(locationPointerToUpdate, locationPointerToMerge);
                }
                return true;
            }
        }
        return false;
    }
}
