/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
/**
 * Définition des tags XML pour les fichiers TOKIO.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.1.1.1 $
 */
public interface XMLTag {
    // Scenarii
    public static final String SCENARII_TAG = "Scenarii";
    public static final String SCENARII_NAME_TAG = "name";

    // Scenario
    public static final String SCENARIO_TAG = "Scenario";
    public static final String SCENARIO_ID_TAG = "id";
    public static final String INPUT_TAG = "input";
    public static final String OUTPUT_TAG = "etalon";
    public static final String SCENARIO_COMMENT_TAG = "comment";

    // TableInfo
    public static final String TABLE_TAG = "table";
    public static final String TABLE_NAME_TAG = "name";
    public static final String TABLE_IDENTITY_TAG = "identityInsert";
    public static final String TABLE_ORDER_TAG = "orderClause";
    public static final String TABLE_TEMPORARY_TAG = "temporary";

    // Row
    public static final String ROW_TAG = "row";
    public static final String ROW_ID_TAG = "id";
    public static final String ROW_INHERIT_ID_TAG = "inheritId";
    public static final String ROW_COMMENT_TAG = "comment";

    // Field
    public static final String FIELD_TAG = "field";
    public static final String FIELD_NAME_TAG = "name";
    public static final String FIELD_VALUE_TAG = "value";
}
