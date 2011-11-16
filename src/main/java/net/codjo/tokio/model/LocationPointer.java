package net.codjo.tokio.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 */
public class LocationPointer {
    protected final List<Location> locations = new ArrayList<Location>();


    public LocationPointer() {
    }


    public LocationPointer(Location location) {
        locations.add(location);
    }


    public List<Location> getLocations() {
        return Collections.unmodifiableList(locations);
    }


    public void addLocation(Location location) {
        locations.add(location);
    }


    public LocationPointer duplicate() {
        LocationPointer locationPointer = new LocationPointer();
        for (Location location : locations) {
            locationPointer.locations.add(location.duplicate());
        }
        return locationPointer;
    }


    public void accept(LocationVisitor locationVisitor) {
        locationVisitor.visit(this);
        for (Location location : getLocations()) {
            location.accept(locationVisitor);
        }
    }


    public static interface LocationVisitor {

        void visit(LocationPointer locationPointer);


        void visit(Location location);
    }
}
