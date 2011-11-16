package net.codjo.tokio.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 *
 */
public class UniqueKey implements Iterable<Field> {
    private final List<Field> fields = new ArrayList<Field>();


    public int getFieldCount() {
        return fields.size();
    }


    public Field getField(int index) {
        return fields.get(index);
    }


    public Iterator<Field> iterator() {
        return fields.iterator();
    }


    public boolean addField(Field field) {
        return fields.add(field);
    }


    public List<Field> toFieldList() {
        return Collections.unmodifiableList(fields);
    }
}
