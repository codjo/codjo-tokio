/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import net.codjo.tokio.datagenerators.GeneratorConfiguration;
/**
 * Classe designant une ligne de donnée.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.11 $
 */
public class Row {
    private RowDictionary rowDictionary = new RowDictionary();
    private FieldMap fields;
    private String comment;
    private String id;
    private String refId;
    private Boolean autoComplete;
    private UniqueKey uniqueKey;
    private LocationPointer locationPointer;
    private static final String UNSPECIFIED_FIELD_VALUE_ERROR
          = "La valeur du field '%s' doit être spécifiée dans la row.";


    public Row(FieldMap fields) {
        this(null, null, fields);
    }


    public Row(Row toCopy) {
        this(toCopy.id, toCopy.refId, toCopy.comment, toCopy.autoComplete, toCopy.fields.duplicate());
    }


    public Row(String id, String refId, FieldMap fields) {
        this(id, refId, null, fields);
    }


    public Row(String id, String refId, Boolean autoComplete, FieldMap fields) {
        this(id, refId, null, autoComplete, fields);
    }


    public Row(String id, String refId, String comment, Boolean autoComplete, FieldMap fields) {
        setId(id);
        setRefId(refId);
        setFields(fields);
        setComment(comment);
        setAutoComplete(autoComplete);
    }


    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public void setFieldValue(String field,
                              String value,
                              String nullValue,
                              GeneratorConfiguration generatorConfiguration) {
        fields.putField(field, value, nullValue, generatorConfiguration);
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        if (this.id != null) {
            throw new IllegalArgumentException(
                  "Impossible pour le moment de changer l'id d'une row");
        }
        this.id = id;
        getRowDictionnary().registerRow(this);
    }


    public String getRefId() {
        return refId;
    }


    public void setRefId(String refId) {
        this.refId = refId;
    }


    public Boolean isAutoComplete() {
        return autoComplete;
    }


    public void setAutoComplete(Boolean autoComplete) {
        this.autoComplete = autoComplete;
    }


    public FieldMap getFields() {
        if (fields == null) {
            return null;
        }
        if (getRefId() == null) {
            return fields.toUnmodifiableMap();
        }

        Row father = getRowDictionnary().getRowById(getRefId());
        if (father == null) {
            throw new IllegalArgumentException(
                  String.format("Identifiant de ligne inconnue : %s", getRefId()));
        }
        FieldMap ret = new FieldMap();
        ret.putAll(father.getFields());
        ret.putAll(fields);
        return ret;
    }


    public long getFieldCount() {
        return getFields().size();
    }


    public boolean containsField(String field) {
        return getFields().containsField(field);
    }


    public void removeField(String fieldName) {
        fields.remove(fieldName);
    }


    public FieldMap getLocalDefinedFields() {
        return fields;
    }


    public boolean containsLocalDefinedField(String field) {
        return getLocalDefinedFields().containsField(field);
    }


    public RowDictionary getRowDictionnary() {
        return rowDictionary;
    }


    public UniqueKey getUniqueKey() {
        return uniqueKey;
    }


    public void addUniqueKey(String fieldName) {
        if (!getLocalDefinedFields().containsField(fieldName)) {
            throw new IllegalArgumentException(String.format(UNSPECIFIED_FIELD_VALUE_ERROR, fieldName));
        }
        Field field = getLocalDefinedFields().get(fieldName);
        if (uniqueKey == null) {
            uniqueKey = new UniqueKey();
        }
        uniqueKey.addField(field);
    }


    public LocationPointer getLocationPointer() {
        return locationPointer;
    }


    public void setLocationPointer(LocationPointer locationPointer) {
        this.locationPointer = locationPointer;
    }


    public void flattenWithInheritedRow() {
        fields = getFields();
        setRefId(null);
    }


    public String toString(boolean expanded) {
        if (expanded) {
            return getFields().toString() + " - " + headerToString();
        }
        else {
            return toString();
        }
    }


    @Override
    public String toString() {
        return "(" + headerToString() + ", content=" + fields + ")";
    }


    void setRowDictionnary(RowDictionary rowDictionary) {
        this.rowDictionary = rowDictionary;
    }


    private void setFields(FieldMap fields) {
        this.fields = null;
        if (fields != null) {
            this.fields = new FieldMap();
            this.fields.putAll(fields);
        }
    }


    private String headerToString() {
        return "id=" + id + ((refId != null) ? (", inheritFrom=" + refId) : "");
    }
}
