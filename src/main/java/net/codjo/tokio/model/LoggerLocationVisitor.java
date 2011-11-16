package net.codjo.tokio.model;
import net.codjo.tokio.model.LocationPointer.LocationVisitor;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class LoggerLocationVisitor implements LocationVisitor {
    private final StringBuilder stringBuilder;
    private final Map<LocationPointer, String> pointerToPrefix = new HashMap<LocationPointer, String>();
    private final Map<Location, String> locationToPrefix = new HashMap<Location, String>();


    public LoggerLocationVisitor(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }


    public void visit(LocationPointer locationPointer) {
        String prefix = pointerToPrefix.get(locationPointer);
        if (prefix == null) {
            prefix = "";
        }
        for (Location location : locationPointer.getLocations()) {
            locationToPrefix.put(location, prefix);
        }
    }


    public void visit(Location location) {
        String prefix = locationToPrefix.get(location);
        LocationPointer locationPointer = location.getLocationPointer();
        if (locationPointer != null) {
            pointerToPrefix.put(locationPointer, prefix + "\t");
        }

        stringBuilder
              .append(prefix)
              .append(location.getTokioTag())
              .append("(")
              .append(location.getFileName())
              .append(":")
              .append(location.getLineNumber())
              .append(")\n");
    }
}
