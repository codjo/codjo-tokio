package net.codjo.tokio.gui;
import net.codjo.gui.toolkit.swing.CheckBoxEditor;
import net.codjo.gui.toolkit.swing.CheckBoxRenderer;
import net.codjo.gui.toolkit.table.TableRendererSorter;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.gui.toolkit.util.TableUtil;
import net.codjo.tokio.XMLTokioLoader;
import net.codjo.tokio.gui.model.Table;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
/**
 *
 */
public class TokioViewer extends JFrame {
    private JTable tableSelection;
    private JPanel mainPanel;
    private JPanel inputPanel;
    private JButton displayDataButton;
    private JPanel outputPanel;
    private JComboBox scenarioCombo;
    private JButton reloadButton;
    private List<Table> tables;
    private File tokioFile;
    private ScenarioList scenarii;
    private Scenario currentScenario;
    private boolean reloadInProgress = false;
    private Map<String, Table> tableNameToTable;


    public TokioViewer(File tokioFile) {
        this.tokioFile = tokioFile;
        loadFile(tokioFile);
        initScenarioPanel();
        loadSelectedScenario();
        getContentPane().add(mainPanel);
        setPreferredSize(new Dimension(1200, 800));
    }


    private void loadSelectedScenario() {
        String scenario = (String)scenarioCombo.getSelectedItem();
        if (scenario != null) {
            currentScenario = scenarii.getScenario(scenario);
            load();
        }
    }


    private void initScenarioPanel() {
        initCombo();

        scenarioCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (!reloadInProgress) {
                    clearData();
                    refreshData();
                }
            }
        });

        reloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadInProgress = true;
                String selectedScenario = (String)scenarioCombo.getSelectedItem();
                scenarioCombo.removeAllItems();

                clearData();
                loadFile(tokioFile);
                initCombo();
                scenarioCombo.setSelectedItem(selectedScenario);
                refreshData();
                reloadInProgress = false;
            }
        });
    }


    private void refreshData() {
        Map<String, Table> currentTableSelection = getCurrentTableSelection();
        loadSelectedScenario();
        applyCurrentSelection(currentTableSelection);
        displayTables();
    }


    private void applyCurrentSelection(Map<String, Table> currentTableSelection) {
        for (Table table : tables) {
            Table oldTable = currentTableSelection.get(table.getName());
            if (oldTable != null) {
                if (oldTable.isDisplayInput() && table.isInput()) {
                    table.setDisplayInput(true);
                }
                if (oldTable.isDisplayOutput() && table.isOutput()) {
                    table.setDisplayOutput(true);
                }
            }
        }
    }


    private Map<String, Table> getCurrentTableSelection() {
        return new HashMap<String, Table>(tableNameToTable);
    }


    private void initCombo() {
        Scenario[] scenarios = scenarii.toArray();
        for (Scenario scenario : scenarios) {
            scenarioCombo.addItem(scenario.getName());
        }
    }


    private void clearData() {
        mainPanel.invalidate();
        inputPanel.removeAll();
        outputPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private void loadFile(File tokioFile) {
        try {
            scenarii = new XMLTokioLoader(tokioFile).getScenarii();
        }
        catch (Exception e) {
            ErrorDialog.show(null,
                             "Erreur de chargement du fichier '" + tokioFile.getAbsolutePath() + "'",
                             e);
        }
    }


    private void load() {
        tableNameToTable = new HashMap<String, Table>();
        loadDataSet(currentScenario.getInputDataSet(), tableNameToTable, true);
        loadDataSet(currentScenario.getOutputDataSet(), tableNameToTable, false);
        initTableData(tableNameToTable);
        initDataViewer();
    }


    private void initDataViewer() {
        displayDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTables();
            }
        });
    }


    private void displayTables() {
        mainPanel.invalidate();
        inputPanel.removeAll();
        outputPanel.removeAll();

        JSplitPane inputTopSplit = createVerticalSplit();
        inputPanel.add(inputTopSplit);
        JSplitPane outputTopSplit = createVerticalSplit();
        outputPanel.add(outputTopSplit);

        JSplitPane lastInputSplit = null;
        JSplitPane lastOutputSplit = null;

        for (Table table : tables) {
            if (table.isDisplayInput()) {
                lastInputSplit = inputTopSplit;
                inputTopSplit = addDetailTable(table.getName(),
                                               "input",
                                               currentScenario.getInputDataSet(),
                                               inputTopSplit);
            }
            if (table.isDisplayOutput()) {
                lastOutputSplit = outputTopSplit;
                outputTopSplit = addDetailTable(table.getName(),
                                                "output",
                                                currentScenario.getOutputDataSet(),
                                                outputTopSplit);
            }
        }

        if (lastInputSplit != null) {
            lastInputSplit.remove(inputTopSplit);
        }

        if (lastOutputSplit != null) {
            lastOutputSplit.remove(outputTopSplit);
        }

        mainPanel.revalidate();
    }


    private JSplitPane addDetailTable(String tableName, String prefix, DataSet dataset, JSplitPane split) {
        net.codjo.tokio.model.Table table = dataset.getTable(tableName);
        TableDetailModel model = new TableDetailModel(table);

        JTable detailTable = new JTable(model);
        TableRendererSorter rendererSorter = new TableRendererSorter(detailTable);
        rendererSorter.addMouseListenerToHeaderInTable(detailTable);
        detailTable.setModel(rendererSorter);
        rendererSorter.changeHeaderRenderer(detailTable);

        detailTable.setPreferredScrollableViewportSize(new Dimension(800, 100));
        detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        detailTable.setName(prefix + "." + tableName);
        detailTable.setDefaultRenderer(Field.class, new FieldRenderer());
        autosizeAll(detailTable);

        split.setLeftComponent(createDetailTablePanel(tableName, detailTable));
        JSplitPane newSplit = createVerticalSplit();
        split.setRightComponent(newSplit);
        return newSplit;
    }


    private static JSplitPane createVerticalSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setOneTouchExpandable(true);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setResizeWeight(1);
        return split;
    }


    private void autosizeAll(JTable table) {
        for (int index = 0; index < table.getColumnCount(); index++) {
            int largestWidth = computeLargestWidth(table, index);
            TableColumn column = TableUtil.getTableColumn(table, index);
            column.setPreferredWidth(largestWidth);
            column.setWidth(column.getPreferredWidth());
        }
    }


    private int computeLargestWidth(JTable table, int columnIndex) {
        int largestWidth = TableUtil.MIN_COLUMN_WIDTH;
        for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
            Component component = TableUtil.getRenderedComponentAt(table, rowIndex, columnIndex);
            int componentWidth = computeWidthForComponent(component) + TableUtil.CELL_MARGIN;
            if (largestWidth < componentWidth) {
                largestWidth = componentWidth;
            }
        }
        return largestWidth;
    }


    private static int computeWidthForComponent(Component component) {
        return (int)(component.getPreferredSize().getWidth() + 1);
    }


    private JPanel createDetailTablePanel(String tableName, JTable detailTable) {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BorderLayout());

        JPanel tablePanel = createTablePanel(detailTable);
        detailPanel.add(createTableLabel(tableName), BorderLayout.NORTH);
        detailPanel.add(tablePanel, BorderLayout.CENTER);
        return detailPanel;
    }


    private JLabel createTableLabel(String tableName) {
        JLabel label = new JLabel("  " + tableName);
        label.setFont(GuiUtil.DEFAULT_FONT.deriveFont(Font.BOLD));
        return label;
    }


    private JPanel createTablePanel(JTable detailTable) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        return panel;
    }


    private void initTableData(Map<String, Table> tableList) {
        tables = new ArrayList<Table>(tableList.values());
        TableListModel model = new TableListModel(tables);
        tableSelection.setModel(model);
        model.fireTableDataChanged();
        model.fireTableStructureChanged();
        tableSelection.setName("tableList");
        tableSelection.getColumnModel().getColumn(1).setCellRenderer(new CheckBoxRenderer());
        tableSelection.getColumnModel().getColumn(2).setCellRenderer(new CheckBoxRenderer());
        tableSelection.getColumnModel().getColumn(1).setCellEditor(new CheckBoxEditor());
        tableSelection.getColumnModel().getColumn(2).setCellEditor(new CheckBoxEditor());
        setColumnWidth(tableSelection, 1, 40);
        setColumnWidth(tableSelection, 2, 40);
    }


    private void setColumnWidth(JTable jTable, int graphicalColumnIndex, int width) {
        TableColumn input = TableUtil.getTableColumn(jTable, graphicalColumnIndex);
        input.setPreferredWidth(width);
        input.setWidth(width);
        input.setMaxWidth(width);
        input.setMinWidth(width);
    }


    private void loadDataSet(DataSet set, Map<String, Table> tableList, boolean isInput) {
        Iterator iterator = set.tables();
        while (iterator.hasNext()) {
            net.codjo.tokio.model.Table tokioTable = (net.codjo.tokio.model.Table)iterator.next();
            String tableName = tokioTable.getName();
            Table table = tableList.get(tableName);
            if (table == null) {
                table = new Table(tableName, isInput, !isInput);
                tableList.put(tableName, table);
            }
            if (isInput) {
                table.setInput(true);
            }
            else {
                table.setOutput(true);
            }
        }
    }


    private void createUIComponents() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
    }


    private class FieldRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            return super.getTableCellRendererComponent(table,
                                                       ((Field)value).getValue(),
                                                       isSelected,
                                                       hasFocus,
                                                       row,
                                                       column);
        }
    }
}
