/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
/**
 * Définition des tags XML pour les fichiers TOKIO.
 *
 * @author $Author: catteao $
 * @version $Revision: 1.7 $
 */
public interface XMLScenariiTags {
    public static final String SCENARII = "Scenarii";
    public static final String SCENARII_NAME = "name";
    public static final String SCENARIO = "Scenario";
    public static final String SCENARIO_ID = "id";
    public static final String SCENARIO_INPUT = "input";
    public static final String SCENARIO_PROPERTIES = "properties";
    public static final String SCENARIO_PROPERTY = "property";
    public static final String SCENARIO_OUTPUT = "etalon";
    public static final String SCENARIO_COMMENT = "comment";
    public static final String PROPERTIES_FILENAME = "filename";
    public static final String PROPERTIES_OVERWRITE = "overwrite";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VALUE = "value";
    public static final String TABLE = "table";
    public static final String TABLE_NAME = "name";
    public static final String TABLE_IDENTITY = "identityInsert";
    public static final String TABLE_ORDER = "orderClause";
    public static final String TABLE_NULL_FIRST = "nullFirst";
    public static final String TABLE_TEMPORARY = "temporary";
    public static final String COMPARATORS = "comparators";
    public static final String COMPARATOR = "comparator";
    public static final String COMPARATOR_FIELD = "field";
    public static final String COMPARATOR_ASSERT = "assert";
    public static final String COMPARATOR_PARAM = "param";
    public static final String COMPARATOR_PRECISION = "precision";
    public static final String ROW = "row";
    public static final String ROW_ID = "id";
    public static final String ROW_INHERIT_ID = "inheritId";
    public static final String ROW_COMMENT = "comment";
    public static final String ROW_AUTOCOMPLETE = "autoComplete";
    public static final String FIELD = "field";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_NULL = "null";
    public static final String GENERATOR_PRECISION = "precision";
    public static final String GENERATOR_NAME = "generate";
}
