/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class EntityList {
    private Map<String, Entity> entities = new HashMap<String, Entity>();


    public Map<String, Entity> getEntities() {
        return entities;
    }


    public boolean isEmpty() {
        return entities.isEmpty();
    }


    public Entity getEntity(String entityName) {
        return entities.get(entityName);
    }


    public void addEntity(Entity entity) {
        entities.put(entity.getName(), entity);
    }


    public void addEntityList(EntityList entityList) {
        entities.putAll(entityList.getEntities());
    }


    public Row getRow(String rowId) {
        for (Entity entity : entities.values()) {
            Row row = entity.getRow(rowId);
            if (row != null) {
                return row;
            }
        }
        return null;
    }
}
