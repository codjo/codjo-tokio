package net.codjo.tokio;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Entity;
import net.codjo.tokio.model.EntityDeclaration;
import net.codjo.tokio.model.EntityDictionary;
import net.codjo.tokio.model.EntityList;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.FieldMap;
import net.codjo.tokio.model.Location;
import net.codjo.tokio.model.LocationPointer;
import net.codjo.tokio.model.LocationUtil;
import net.codjo.tokio.model.Parameter;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.model.UniqueKey;
import net.codjo.tokio.model.VariableValue;
import net.codjo.tokio.util.XmlUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.w3c.dom.Node;

public class CreateEntityManager {
    private static final String PARAMETER_ERROR_PREFIX
          = "Il n'existe pas de valeur par défaut pour le paramètre '";
    private static final String PARAMETER_ERROR_SUFFIX = "' non spécifié.";
    private static final String PARAMETER_PREFIX = "@";
    private static final String PARAMETER_SUFFIX = "@";


    public void process(DataSet dataset,
                        DataSet requiredDataset,
                        Node createEntityNode,
                        EntityList entityList,
                        EntityDictionary entityDictionary,
                        EntityDictionary requiredEntityDictionnary)
          throws TokioLoaderException {
        String entityName = XmlUtil.getAttribute(createEntityNode, XMLStoriesTags.CREATE_ENTITY_NAME);
        String entityId = XmlUtil.getAttribute(createEntityNode, XMLStoriesTags.CREATE_ENTITY_ID);
        if (entityId == null || "".equals(entityId)) {
            entityId = String.valueOf(System.identityHashCode(entityName));
        }
        Map<String, Parameter> parametersToValues = new HashMap<String, Parameter>();
        XmlEntityUtil.processCreateEntityParameters(createEntityNode, parametersToValues);

        processEntity(dataset,
                      requiredDataset,
                      entityList,
                      entityDictionary,
                      requiredEntityDictionnary,
                      entityName,
                      entityId,
                      parametersToValues,
                      Arrays.asList(LocationUtil.getLocationFromNode(createEntityNode)));
    }


    private void processEntity(DataSet dataset,
                               DataSet requiredDataset,
                               EntityList entityList,
                               EntityDictionary entityDictionary,
                               EntityDictionary requiredEntityDictionnary,
                               String entityName,
                               String entityId,
                               Map<String, Parameter> parametersToValues,
                               List<Location> locations) {
        Entity entity = entityList.getEntity(entityName);
        if (entity == null) {
            throw new RuntimeException(computeUnknownEntityMessage(entityName));
        }

        checkParameterValidities(entity, parametersToValues);
        initParameterValues(entity, parametersToValues);

        for (EntityDeclaration entityDeclaration : entity.getEntityDeclarationList()) {
            Map<String, Parameter> declarationParameters = copyParameters(entityDeclaration.getParameters());

            initParameterValuesFromParent(declarationParameters, parametersToValues);

            List<Location> newLocations = new ArrayList<Location>(locations);
            newLocations.add(0, entityDeclaration.getLocation());

            processEntity(dataset,
                          requiredDataset,
                          entity.getEntityModelList(),
                          entityDictionary,
                          requiredEntityDictionnary,
                          entityDeclaration.getName(),
                          entityId + "." + entityDeclaration.getId(),
                          declarationParameters,
                          newLocations);
        }

        mergeDatasets(entity.getDataset(),
                      entityId,
                      dataset,
                      parametersToValues,
                      entityDictionary,
                      locations);

        mergeDatasets(entity.getRequiredDataSet(),
                      entityId,
                      requiredDataset,
                      parametersToValues,
                      requiredEntityDictionnary,
                      locations);
    }


    private void initParameterValuesFromParent(Map<String, Parameter> declarationParameters,
                                               Map<String, Parameter> parametersToValues) {
        for (Parameter childParameter : declarationParameters.values()) {
            boolean canContinueReplace = true;
            while (canContinueReplace) {
                canContinueReplace = false;

                for (VariableValue variableValue : childParameter.getValueObjectList().findVariableValues()) {
                    Parameter parentParameter = parametersToValues.get(variableValue.getName());
                    if (parentParameter != null) {
                        childParameter.replace(variableValue, parentParameter);
                        canContinueReplace = true;
                    }
                }
            }
        }
    }


    private void checkParameterValidities(Entity entity, Map<String, Parameter> parametersToValues) {
        for (String param : parametersToValues.keySet()) {
            if (!entity.containsParameter(param)) {
                throw new TokioLoaderException(computeBadParameterMessage(param, entity.getName()));
            }
        }
    }


    private void initParameterValues(Entity entity, Map<String, Parameter> parametersToValues)
          throws TokioLoaderException {
        for (String parameter : entity.getParameters()) {
            if (!parametersToValues.containsKey(parameter)) {
                if (entity.hasDefaultValue(parameter)) {
                    parametersToValues.put(parameter, entity.getDefaultValue(parameter));
                }
                else {
                    throw new TokioLoaderException(
                          PARAMETER_ERROR_PREFIX + parameter + PARAMETER_ERROR_SUFFIX);
                }
            }
        }
    }


    private Map<String, Parameter> copyParameters(Map<String, Parameter> parameters) {
        Map<String, Parameter> declarationParameters = new HashMap<String, Parameter>();
        for (Entry<String, Parameter> entry : parameters.entrySet()) {
            declarationParameters.put(entry.getKey(), new Parameter(entry.getValue()));
        }
        return declarationParameters;
    }


    private void mergeDatasets(DataSet entityDataset,
                               String entityId,
                               DataSet destination,
                               Map<String, Parameter> parametersToValues,
                               EntityDictionary entityDictionary,
                               List<Location> locations) {
        for (Iterator<Table> iterator = entityDataset.tables(); iterator.hasNext();) {
            Table table = iterator.next();
            Table destinationTable = mergeTable(destination, parametersToValues, entityDictionary, table);
            mergeRows(entityId,
                      table,
                      destinationTable,
                      parametersToValues,
                      entityDictionary,
                      locations);
        }
    }


    private Table mergeTable(DataSet destination,
                             Map<String, Parameter> parametersToValues,
                             EntityDictionary entityDictionary, Table table) {
        String tableName = convertName(table.getName(), parametersToValues);
        Table destinationTable = destination.buildTable(tableName);
        destinationTable.setOrderClause(table.getOrderClause());
        destinationTable.setNullFirst(table.isNullFirst());
        destinationTable.setIdentityInsert(table.isIdentityInsert());
        entityDictionary.setOrderClause(tableName, table.getOrderClause());
        entityDictionary.setNullFirst(tableName, table.isNullFirst());
        entityDictionary.setIdentityInsert(tableName, table.isIdentityInsert());
        return destinationTable;
    }


    private void mergeRows(String entityId,
                           Table sourceTable,
                           Table destinationTable,
                           Map<String, Parameter> parametersToValues,
                           EntityDictionary entityDictionary,
                           List<Location> locations) {
        for (Iterator<Row> iterator = sourceTable.rows(); iterator.hasNext();) {
            Row row = iterator.next();
            Row newRow = convertRow(entityId, parametersToValues, row);

            LocationPointer locationPointer = row.getLocationPointer().duplicate();
            for (Location location : locations) {
                Location duplicate = location.duplicate();
                duplicate.setLocationPointer(locationPointer);
                locationPointer = new LocationPointer(duplicate);
            }
            newRow.setLocationPointer(locationPointer);

            entityDictionary.addRow(entityId, sourceTable.getName(), newRow);
            destinationTable.addRow(newRow);
        }
    }


    private Row convertRow(String entityId, Map<String, Parameter> parametersToValues, Row row) {
        String rowId = convertName(row.getId(), parametersToValues);
        FieldMap fieldMap = convertFields(row, parametersToValues);
        rowId = computeEntityRowId(entityId, rowId);
        String refId = computeEntityRowId(entityId, row.getRefId());
        Row newRow = new Row(rowId, refId, row.isAutoComplete(), fieldMap);
        UniqueKey uniqueKey = row.getUniqueKey();
        if (uniqueKey != null) {
            for (Field field : uniqueKey) {
                newRow.addUniqueKey(field.getName());
            }
        }
        return newRow;
    }


    private FieldMap convertFields(Row row, Map<String, Parameter> parametersToValues) {
        FieldMap destinationFields = new FieldMap();
        FieldMap localDefinedFields = row.getLocalDefinedFields();
        for (Iterator<Field> iterator = localDefinedFields.iterator(); iterator.hasNext();) {
            Field field = iterator.next();
            String fieldName = convertName(field.getName(), parametersToValues);

            Field newField = convertField(field, parametersToValues);
            newField.setName(fieldName);
            destinationFields.put(newField);
        }
        return destinationFields;
    }


    private Field convertField(Field value, Map<String, Parameter> parametersToValues) {
        Field duplicatedField = new Field(value);

        boolean replaceDone = true;
        while (replaceDone) {
            replaceDone = false;

            for (VariableValue variableValue : duplicatedField.getValueObjectList().findVariableValues()) {
                Parameter parentParameter = parametersToValues.get(variableValue.getName());
                if (parentParameter != null) {
                    duplicatedField.replace(variableValue, parentParameter);
                    replaceDone = true;
                }
            }
        }

        return duplicatedField;
    }


    private String convertName(String value, Map<String, Parameter> parametersToValues) {
        if (value == null) {
            return value;
        }
        for (Entry<String, Parameter> mapEntry : parametersToValues.entrySet()) {
            String pattern = PARAMETER_PREFIX + mapEntry.getKey() + PARAMETER_SUFFIX;
            Object parameterValue = mapEntry.getValue();

            if (value.contains(pattern) && ((Parameter)parameterValue).getValue() == null) {
                return null;
            }
            else {
                value = value.replaceAll(pattern, ((Parameter)parameterValue).getValue());
            }
        }
        return value;
    }


    private String computeEntityRowId(String entityId, String rowId) {
        if (rowId == null || entityId == null || entityId.contains(".null.") || entityId.endsWith(".null")) {
            return null;
        }
        return entityId + "." + rowId;
    }


    private String computeUnknownEntityMessage(String entityName) {
        return "L'entité '" + entityName + "' n'a pas été définie.";
    }


    private String computeBadParameterMessage(String parameterName, String entityName) {
        return "Le paramètre " + parameterName + " n'est pas défini dans l'entité " + entityName;
    }
}
