package net.codjo.tokio.viewer.table;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        try {
            setSelected(Boolean.valueOf(value.toString()));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBackground(isSelected, table);
            setBorderPaintedFlat(true);
            setEnabled(table.isCellEditable(row, column));
        }
        catch (Exception e) {
            setSelected(false);
        }
        return this;
    }


    private void setBackground(boolean isSelected, JTable table) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        }
        else {
            setBackground(table.getBackground());
        }
    }
}
