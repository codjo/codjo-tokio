package net.codjo.tokio.foreignkeys;
import java.util.ArrayList;
import java.util.List;
public class ForeignKey {
    private String fromTableName;
    private final List<String> fromColumnNames = new ArrayList<String>();

    private String toTableName;
    private final List<String> toColumnNames = new ArrayList<String>();


    public String getFromTableName() {
        return fromTableName;
    }


    public void setFromTableName(String fromTableName) {
        this.fromTableName = fromTableName;
    }


    public List<String> getFromColumnNames() {
        return fromColumnNames;
    }


    public void addFromColumnName(String fromColumnName) {
        fromColumnNames.add(fromColumnName);
    }


    public String getToTableName() {
        return toTableName;
    }


    public void setToTableName(String toTableName) {
        this.toTableName = toTableName;
    }


    public List<String> getToColumnNames() {
        return toColumnNames;
    }


    public void addToColumnName(String toColumnName) {
        toColumnNames.add(toColumnName);
    }
}
