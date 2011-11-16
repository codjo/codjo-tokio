package net.codjo.tokio.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 */
public class RowIdLocationPointer extends LocationPointer {
    private final Row row;


    public RowIdLocationPointer(Row rowWithRefId) {
        this.row = rowWithRefId;
    }


    @Override
    public List<Location> getLocations() {
        RowDictionary rowDictionnary = row.getRowDictionnary();
        String rowRefId = row.getRefId();
        if (rowDictionnary == null || rowDictionnary.getRowById(rowRefId) == null) {
            return locations;
        }
        else {
            List<Location> newLocations = new ArrayList<Location>(locations);
            newLocations.addAll(rowDictionnary.getRowById(rowRefId).getLocationPointer().getLocations());
            return Collections.unmodifiableList(newLocations);
        }
    }


    @Override
    public LocationPointer duplicate() {
        RowIdLocationPointer rowIdLocationPointer = new RowIdLocationPointer(row);
        for (Location location : locations) {
            rowIdLocationPointer.locations.add(location.duplicate());
        }
        return rowIdLocationPointer;
    }
}
