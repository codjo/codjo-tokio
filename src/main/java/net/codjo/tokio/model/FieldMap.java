/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
/**
 * Une map de field.
 */
public class FieldMap {
    private final Map<String, Field> content;


    public FieldMap() {
        this(new LinkedHashMap<String, Field>());
    }


    protected FieldMap(Map<String, Field> content) {
        this.content = content;
    }


    public void put(Field newField) {
        content.put(newField.getName(), newField);
    }


    public Field get(String fieldName) {
        Field field = content.get(fieldName);
        if (field == null) {
            throw new NoSuchElementException("Le champ '" + fieldName + "' est inconnu");
        }
        return field;
    }


    public void clear() {
        content.clear();
    }


    public FieldMap toUnmodifiableMap() {
        return new UnmodifiableFieldMap(this);
    }


    public List<Field> toFieldList() {
        return Collections.unmodifiableList(new ArrayList<Field>(content.values()));
    }


    public void putAll(FieldMap fields) {
        content.putAll(fields.content);
    }


    public boolean containsField(String fieldName) {
        return content.containsKey(fieldName);
    }


    public void remove(String name) {
        content.remove(name);
    }


    public Collection<String> fieldNameSet() {
        return content.keySet();
    }


    public void putField(String name,
                         String value,
                         GeneratorConfiguration generatorConfiguration) {
        putField(name, value, "false", generatorConfiguration);
    }


    public void putField(String name,
                         String value,
                         String nullValue,
                         GeneratorConfiguration generatorConfiguration) {
        Field newField;
        if ("true".equals(nullValue)) {
            newField = new Field(name);
        }
        else if (generatorConfiguration != null) {
            newField = new Field(name, generatorConfiguration);
        }
        else {
            newField = new Field(name, value);
        }
        put(newField);
    }


    public Iterator<Field> iterator() {
        return content.values().iterator();
    }


    public long size() {
        return content.size();
    }


    public FieldMap duplicate() {
        FieldMap copy = new FieldMap();
        for (Iterator<Field> iterator = iterator(); iterator.hasNext();) {
            Field field = iterator.next();
            Field fieldCopy = new Field(field);
            copy.put(fieldCopy);
        }
        return copy;
    }


    public boolean isSame(FieldMap fieldMap) {
        if (this == fieldMap) {
            return true;
        }
        if (fieldMap == null) {
            return false;
        }

        if (content.size() != fieldMap.content.size()) {
            return false;
        }

        for (Entry<String, Field> entry : content.entrySet()) {
            String key = entry.getKey();

            if (!fieldMap.content.containsKey(key)) {
                return false;
            }

            Field value = entry.getValue();
            Field comparedValue = fieldMap.get(key);

            if (value != null ? !value.isSame(comparedValue) : comparedValue != null) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder("[");
        for (Iterator<Field> iter = content.values().iterator(); iter.hasNext();) {
            Field field = iter.next();
            toString.append(field.toString());
            if (iter.hasNext()) {
                toString.append(", ");
            }
        }
        return toString.append("]").toString();
    }


    private static class UnmodifiableFieldMap extends FieldMap {
        UnmodifiableFieldMap(FieldMap map) {
            super(Collections.unmodifiableMap(map.content));
        }
    }
}
