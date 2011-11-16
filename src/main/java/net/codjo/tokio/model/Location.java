package net.codjo.tokio.model;
import net.codjo.tokio.model.LocationPointer.LocationVisitor;
/**
 *
 */
public class Location {
    private LocationPointer locationPointer;
    private String fileName;
    private int lineNumber;
    private String tokioTag;


    public Location() {
    }


    public Location(String fileName, int lineNumber, String tokioTag) {
        this(fileName, lineNumber, tokioTag, null);
    }


    public Location(String fileName, int lineNumber, String tokioTag, LocationPointer locationPointer) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.tokioTag = tokioTag;
        this.locationPointer = locationPointer;
    }


    public LocationPointer getLocationPointer() {
        return locationPointer;
    }


    public void setLocationPointer(LocationPointer locationPointer) {
        this.locationPointer = locationPointer;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public int getLineNumber() {
        return lineNumber;
    }


    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    public String getTokioTag() {
        return tokioTag;
    }


    public void setTokioTag(String tokioTag) {
        this.tokioTag = tokioTag;
    }


    public Location duplicate() {
        Location location = new Location();
        location.locationPointer = (locationPointer == null ? null : locationPointer.duplicate());
        location.fileName = fileName;
        location.lineNumber = lineNumber;
        location.tokioTag = tokioTag;
        return location;
    }


    public void accept(LocationVisitor locationVisitor) {
        locationVisitor.visit(this);
        if (getLocationPointer() != null) {
            getLocationPointer().accept(locationVisitor);
        }
    }


    public boolean isSame(Location location) {
        if (fileName == null && location.fileName != null) {
            return false;
        }
        if (fileName != null && !fileName.equals(location.fileName)) {
            return false;
        }
        if (lineNumber != location.lineNumber) {
            return false;
        }
        if (tokioTag == null && location.tokioTag != null) {
            return false;
        }
        if (tokioTag != null && !tokioTag.equals(location.tokioTag)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return new StringBuilder()
              .append("Location")
              .append("{tokioTag='").append(tokioTag).append('\'')
              .append(", fileName='").append(fileName).append('\'')
              .append(", lineNumber=").append(lineNumber)
              .append('}')
              .toString();
    }
}
