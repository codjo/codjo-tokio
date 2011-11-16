package net.codjo.tokio.gui;
import net.codjo.tokio.gui.model.Table;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
/**
 *
 */
public class TableListModel extends AbstractTableModel {
    private static final String[] COLUMNS = new String[]{"Nom de la table", "Input", "Output"};
    private List<Table> tableList;


    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }


    public TableListModel(List<Table> tableList) {
        this.tableList = tableList;
        Collections.sort(tableList, new Comparator<Table>() {
            public int compare(Table o1, Table o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }


    public int getRowCount() {
        return tableList.size();
    }


    public int getColumnCount() {
        return 3;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        Table row = tableList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.getName();
            case 1:
                return row.isDisplayInput();
            case 2:
                return row.isDisplayOutput();
            default:
                throw new IllegalStateException("Erreur dans le modèle interne du TableListModel.");
        }
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Table row = tableList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                throw new IllegalStateException("Erreur dans le modèle interne du TableListModel.");
            case 1:
                row.setDisplayInput((Boolean)aValue);
                break;
            case 2:
                row.setDisplayOutput((Boolean)aValue);
                break;
            default:
                throw new IllegalStateException("Erreur dans le modèle interne du TableListModel.");
        }
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Table row = tableList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
                return row.isInput();
            case 2:
                return row.isOutput();
            default:
                throw new IllegalStateException("Erreur dans le modèle interne du TableListModel.");
        }
    }
}
