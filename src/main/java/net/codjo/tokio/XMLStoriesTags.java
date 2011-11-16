/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
/**
 *
 */
public interface XMLStoriesTags extends XMLScenariiTags {
    public static final String STORY = "story";
    public static final String STORY_ID = "id";
    public static final String STORY_INPUT = "input";
    public static final String STORY_OUTPUT = "output";
    public static final String COPY = "copy";
    public static final String COPY_ROW = "row";
    public static final String REPLACE = "replace";
    public static final String REPLACE_ROW = "row";
    public static final String REMOVE = "remove";
    public static final String REMOVE_ROW = "row";
    public static final String INCLUDE_STORY = "include-story";
    public static final String INCLUDE_STORY_FILE = "file";
    public static final String INCLUDE_ENTITIES = "include-entities";
    public static final String INCLUDE_ENTITIES_FILE = "file";
    public static final String CREATE_ENTITY = "create-entity";
    public static final String CREATE_ENTITY_NAME = "name";
    public static final String CREATE_ENTITY_ID = "id";
    public static final String COPY_ENTITY = "copy-entity";
    public static final String COPY_ENTITY_ENTITY = "entity";
    public static final String COPY_ENTITY_ID = "id";
    public static final String CREATE_ENTITY_PARAMETER = "parameter";
    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_VALUE = "value";
    public static final String PARAMETER_NULL = "null";
}
