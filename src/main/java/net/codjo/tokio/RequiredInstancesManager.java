package net.codjo.tokio;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.EntityDictionary;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.LocationUtil;
import net.codjo.tokio.model.LoggerLocationVisitor;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 */
public class RequiredInstancesManager {
    private static final String CONFLICT_ERROR_MESSAGE
          = "Des lignes sont en conflit avec un required.";
    private static final String TOO_MANY_ROWS_ERROR_MESSAGE
          = "Impossible de déterminer une ligne correspondant au required : trop de possibilités.";
    private final RowCompatibilityChecker checker = new RowCompatibilityChecker();
    private final Map<DataSet, DataSet> datasets = new LinkedHashMap<DataSet, DataSet>();
    private final Map<EntityDictionary, EntityDictionary> entityDictionaries
          = new LinkedHashMap<EntityDictionary, EntityDictionary>();


    public DataSet getRequiredDataset(DataSet dataset) {
        DataSet requiredDataset = datasets.get(dataset);
        if (requiredDataset == null) {
            requiredDataset = new DataSet();
            datasets.put(dataset, requiredDataset);
        }
        return requiredDataset;
    }


    public EntityDictionary getRequiredEntityDictionnary(EntityDictionary entityDictionary) {
        EntityDictionary requiredEntityDictionary = entityDictionaries.get(entityDictionary);
        if (requiredEntityDictionary == null) {
            requiredEntityDictionary = new EntityDictionary();
            entityDictionaries.put(entityDictionary, requiredEntityDictionary);
        }
        return requiredEntityDictionary;
    }


    public void finalizeDatasets() {
        for (Entry<DataSet, DataSet> entry : datasets.entrySet()) {
            mergeRequiredDataset(entry.getKey(), entry.getValue());
        }
    }


    private void mergeRequiredDataset(DataSet destinationDataset, DataSet requiredDataset) {
        for (Iterator iterator = requiredDataset.tables(); iterator.hasNext();) {
            Table table = (Table)iterator.next();
            Table destinationTable = destinationDataset.buildTable(table.getName());
            destinationTable.setIdentityInsert(table.isIdentityInsert());
            mergeRequiredRows(destinationTable, table);
        }
    }


    private void mergeRequiredRows(Table destinationTable, Table requiredTable) {
        List<Row> requiredRows = new ArrayList<Row>(requiredTable.getRows());
        Collections.sort(requiredRows, new RowWithUniqueKeyComparator(requiredRows));
        for (Row requiredRow : requiredRows) {
            mergeRequiredRow(destinationTable, requiredRow);
        }
    }


    private void mergeRequiredRow(Table destinationTable, Row requiredRow) {
        boolean thereIsAMerge = false;
        for (Row existingRow : destinationTable.getRows()) {
            if (checker.matchAllFields(requiredRow, existingRow)) {
                LocationUtil.mergeLocationPointers(existingRow.getLocationPointer(),
                                                   requiredRow.getLocationPointer());
                thereIsAMerge = true;
            }
        }
        if (!thereIsAMerge) {
            if (requiredRow.getUniqueKey() == null) {
                destinationTable.addRow(requiredRow);
            }
            else {
                mergeRequiredRowWithUniqueKey(destinationTable, requiredRow);
            }
        }
    }


    private void mergeRequiredRowWithUniqueKey(Table destinationTable, Row requiredRow) {
        List<Row> updatables = new ArrayList<Row>();
        List<Row> incompatiblesRows = new ArrayList<Row>();

        for (Row existingRow : destinationTable.getRows()) {
            if (checker.matchUniqueKey(requiredRow.getUniqueKey(), existingRow)) {
                if (checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, existingRow)) {
                    incompatiblesRows.add(existingRow);
                }
                else {
                    updatables.add(existingRow);
                }
            }
        }

        if (!incompatiblesRows.isEmpty()) {
            throw new TokioLoaderException(computeErrorMessage(requiredRow,
                                                               incompatiblesRows,
                                                               CONFLICT_ERROR_MESSAGE));
        }
        else if (updatables.size() > 1) {
            throw new TokioLoaderException(computeErrorMessage(requiredRow,
                                                               updatables,
                                                               TOO_MANY_ROWS_ERROR_MESSAGE));
        }
        else if (updatables.size() == 1) {
            mergeFields(updatables.get(0), requiredRow);
        }
        else {
            destinationTable.addRow(requiredRow);
        }
    }


    private String computeErrorMessage(Row requiredRow,
                                       List<Row> incompatibleRows,
                                       String initialErrorMessage) {
        StringBuilder stringBuilder = new StringBuilder()
              .append(initialErrorMessage).append("\n")
              .append("\n")
              .append("ligne required :\n");
        requiredRow.getLocationPointer().accept(new LoggerLocationVisitor(stringBuilder));
        stringBuilder.append("\n")
              .append("ligne(s) en conflit :\n");
        for (Row incompatibleRow : incompatibleRows) {
            incompatibleRow.getLocationPointer().accept(new LoggerLocationVisitor(stringBuilder));
        }
        return stringBuilder.toString();
    }


    private void mergeFields(Row row, Row requiredRow) {
        for (Field field : requiredRow.getFields().toFieldList()) {
            if (!row.containsField(field.getName())) {
                row.getLocalDefinedFields().put(new Field(field));
            }
        }
        LocationUtil.mergeLocationPointers(row.getLocationPointer(), requiredRow.getLocationPointer());
    }


    private static class RowWithUniqueKeyComparator implements Comparator<Row> {
        private final List<Row> initialOrder;


        private RowWithUniqueKeyComparator(List<Row> initialOrder) {
            this.initialOrder = initialOrder;
        }


        public int compare(Row row1, Row row2) {
            if (row1.getUniqueKey() == null && row2.getUniqueKey() != null) {
                return -1;
            }
            if (row1.getUniqueKey() != null && row2.getUniqueKey() == null) {
                return 1;
            }
            return initialOrder.indexOf(row1) - initialOrder.indexOf(row2);
        }
    }
}
