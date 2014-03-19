package net.codjo.tokio.viewer;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideSplitPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.tokio.DefaultSQLFieldListBuilder;
import net.codjo.tokio.GeneratedValuesTansformer;
import net.codjo.tokio.ImplicitDataTransformer;
import net.codjo.tokio.XMLTokioLoader;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Field;
import net.codjo.tokio.model.LoggerLocationVisitor;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
import net.codjo.tokio.model.Table;
import net.codjo.tokio.viewer.model.TableInfo;
import net.codjo.tokio.viewer.table.TableRendererSorter;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import static java.awt.FlowLayout.LEFT;

public class TokioViewer extends JFrame {
    private static final Color VERY_LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color LIGHT_BLUE = new Color(180, 180, 255);

    private static final int INPUT_COLUMN_INDEX = 1;
    private static final int OUPUT_COLUMN_INDEX = 2;

    private JXTable tableSelection;
    private JPanel mainPanel;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private JComboBox scenarioCombo;
    private JCheckBox autoCompleteBox;
    private JTextArea linePosition;
    private JLabel linePositionTableName;
    private JSplitPane leftSplitPane;
    private JScrollPane inputScroller;
    private JScrollPane outputScroller;

    private JideSplitPane inputTopSplit;
    private JideSplitPane outputTopSplit;
    private JPopupMenu popupMenu = new JPopupMenu();

    private Map<String, JLabel> tableNameToLabels = new HashMap<String, JLabel>();
    private Map<String, JXTable> tableNameToInputTables = new HashMap<String, JXTable>();
    private Map<String, JXTable> tableNameToOutputTables = new HashMap<String, JXTable>();

    private List<TableInfo> tableInfos;
    private File tokioFile;
    private ScenarioList scenarii;
    private Scenario currentScenario;
    private boolean reloadInProgress = false;

    private Map<String, TableInfo> tableNameToTable;

    private ImplicitDataTransformer implicitDataTransformer =
          new ImplicitDataTransformer(new DefaultSQLFieldListBuilder());

    private GeneratedValuesTansformer generatedValuesTransformer = new GeneratedValuesTansformer();
    private Map<File, Long> filesListWithLastUpdateDate = new TreeMap<File, Long>();
    private Connection connection;


    public TokioViewer(File tokioFile) throws TokioViewerException {
        this.tokioFile = tokioFile;
        initPopupMenu();
        loadFile(tokioFile);
        initScenarioPanel();
        initInputOutputPanel();
        loadSelectedScenario();
        initAutomaticDataRefresh();
        initLinePosition();
        add(mainPanel);
        setPreferredSize(new Dimension(1200, 800));
        setTitle("Visualisation du fichier :" + tokioFile.getName());
    }


    private void initPopupMenu() {
        popupMenu.add(new AbstractAction("Voir dans l'INPUT") {
            public void actionPerformed(ActionEvent e) {
                scrollToTable(TokioViewer.this.inputPanel, TokioViewer.this.inputScroller);
            }
        });
        popupMenu.add(new AbstractAction("Voir dans l'OUTPUT") {
            public void actionPerformed(ActionEvent e) {
                scrollToTable(TokioViewer.this.outputPanel, TokioViewer.this.outputScroller);
            }
        });
    }


    private void initLinePosition() {
        linePosition.setTabSize(2);
        linePositionTableName.setText("");
        leftSplitPane.setDividerLocation(550);
    }


    private void initDatabaseConnection() {
        if (connection == null) {
            JdbcFixture jdbcFixture = JdbcFixture.newFixture();
            jdbcFixture.doSetUp();
            connection = jdbcFixture.getConnection();
        }
    }


    private void initAutomaticDataRefresh() {
        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) {
                refreshDataIfTokioHasChanged();
            }


            public void windowLostFocus(WindowEvent e) {
            }
        });
    }


    protected void refreshDataIfTokioHasChanged() {
        boolean entityModified = false;
        for (File file : filesListWithLastUpdateDate.keySet()) {
            if (file.lastModified() > filesListWithLastUpdateDate.get(file)) {
                filesListWithLastUpdateDate.put(file, file.lastModified());
                entityModified = true;
            }
        }

        if (entityModified) {
            reloadTokio();
        }
    }


    private void reloadTokio() {
        reloadInProgress = true;
        String selectedScenario = (String)scenarioCombo.getSelectedItem();
        scenarioCombo.removeAllItems();

        clearData();
        try {
            loadFile(tokioFile);

            initCombo();
            scenarioCombo.setSelectedItem(selectedScenario);
            refreshData();
        }
        catch (TokioViewerException e) {
            showErrorDialog(e.getMessage(), e);
        }
        finally {
            reloadInProgress = false;
        }
    }


    private void initInputOutputPanel() {
        inputTopSplit = createVerticalSplit();
        inputPanel.add(inputTopSplit);
        outputTopSplit = createVerticalSplit();
        outputPanel.add(outputTopSplit);
    }


    private void loadSelectedScenario() throws TokioViewerException {
        String scenario = (String)scenarioCombo.getSelectedItem();
        if (scenario == null) {
            return;
        }

        currentScenario = scenarii.getScenario(scenario);

        if (autoCompleteBox.isSelected()) {
            try {
                applyTransformers(currentScenario.getInputDataSet());
            }
            catch (Exception e) {
                throw new TokioViewerException("Impossible de générer les données implicites.", e);
            }
        }

        tableNameToTable = new HashMap<String, TableInfo>();
        loadDataSet(currentScenario.getInputDataSet(), tableNameToTable, DetailTableMode.INPUT);
        loadDataSet(currentScenario.getOutputDataSet(), tableNameToTable, DetailTableMode.OUTPUT);
        initTableData(tableNameToTable);
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

        autoCompleteBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoCompleteBox.isSelected()) {
                    initDatabaseConnection();
                    refreshData();
                }
                else {
                    reloadTokio();
                }
            }
        });
    }


    private void refreshData() {
        Map<String, TableInfo> currentTableSelection = getCurrentTableSelection();
        try {
            loadSelectedScenario();
            applyCurrentSelection(currentTableSelection);
            displayTables(true);
        }
        catch (TokioViewerException e) {
            showErrorDialog(e.getMessage(), e);
        }
    }


    private void applyCurrentSelection(Map<String, TableInfo> currentTableSelection) {
        for (TableInfo tableInfo : tableInfos) {
            TableInfo oldTableInfo = currentTableSelection.get(tableInfo.getName());
            if (oldTableInfo != null) {
                if (oldTableInfo.isDisplayInput() && tableInfo.isInput()) {
                    tableInfo.setDisplayInput(true);
                }
                if (oldTableInfo.isDisplayOutput() && tableInfo.isOutput()) {
                    tableInfo.setDisplayOutput(true);
                }
            }
        }
    }


    private Map<String, TableInfo> getCurrentTableSelection() {
        return new HashMap<String, TableInfo>(tableNameToTable);
    }


    private void initCombo() {
        Scenario[] scenarios = scenarii.toArray();
        for (Scenario scenario : scenarios) {
            scenarioCombo.addItem(scenario.getName());
        }
    }


    private void clearData() {
        mainPanel.invalidate();
        inputTopSplit.removeAll();
        outputTopSplit.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    }


    protected void loadFile(File tokioFileToLoad) throws TokioViewerException {
        try {
            XMLTokioLoader loader = new XMLTokioLoader(tokioFileToLoad);

            filesListWithLastUpdateDate.put(tokioFileToLoad, tokioFileToLoad.lastModified());
            addFiles(loader.getIncludeEntitiesManager().getEntitiesFiles(), filesListWithLastUpdateDate);
            addFiles(loader.getStoryUtil().getIncludeTokioFiles(), filesListWithLastUpdateDate);

            scenarii = loader.getScenarii();
        }
        catch (Exception e) {
            throw new TokioViewerException(
                  "Erreur de chargement du fichier '" + tokioFileToLoad.getAbsolutePath() + "'", e);
        }
    }


    private void addFiles(List<File> filesToAdd, Map<File, Long> filesList) {
        for (final File file : filesToAdd) {
            filesList.put(file, file.lastModified());
        }
    }


    public static void showErrorDialog(String message, Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        JTextArea textArea = buildTextArea(stackTrace.toString());
        String exceptionDescription = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "";
        JTextArea detailArea = buildTextArea(exceptionDescription);
        detailArea.setLineWrap(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, 0, 20));
        JTabbedPane tabPane = new JTabbedPane(JTabbedPane.BOTTOM);
        tabPane.addTab("Message", new JScrollPane(textArea));
        tabPane.addTab("Détail", new JScrollPane(detailArea));
        panel.add(tabPane, BorderLayout.CENTER);
        Object[] array = {message, panel};

        Object[] options = {"OK"};

        JOptionPane optionPane =
              new JOptionPane(array, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION,
                              null, options, options[0]);

        JDialog dialog = optionPane.createDialog(null, "Erreur");
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }


    private static JTextArea buildTextArea(String exceptionMsg) {
        JTextArea textArea = new JTextArea(exceptionMsg, 20, 60);
        textArea.setName("errorMessage");
        textArea.setEnabled(true);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(205, 205, 205));
        textArea.setDisabledTextColor(Color.black);
        textArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        return textArea;
    }


    private void applyTransformers(DataSet dataSet) throws Exception {
        generatedValuesTransformer.transform(dataSet);
        implicitDataTransformer.transform(connection, dataSet);
    }


    private void displayTables(boolean reload) {
        mainPanel.invalidate();
        inputTopSplit.removeAll();
        outputTopSplit.removeAll();

        for (TableInfo tableInfo : tableInfos) {
            if (tableInfo.isDisplayInput()) {
                addDetailTable(tableInfo.getName(),
                               DetailTableMode.INPUT,
                               currentScenario.getInputDataSet(),
                               inputTopSplit,
                               tableNameToInputTables,
                               reload);
            }
            if (tableInfo.isDisplayOutput()) {
                addDetailTable(tableInfo.getName(),
                               DetailTableMode.OUTPUT,
                               currentScenario.getOutputDataSet(),
                               outputTopSplit,
                               tableNameToOutputTables,
                               reload);
            }
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private void addDetailTable(String tableName,
                                DetailTableMode detailTableMode,
                                DataSet dataset,
                                JideSplitPane split,
                                Map<String, JXTable> tableNameToTables,
                                boolean reload) {
        Table table = dataset.getTable(tableName);
        TableDetailModel model = new TableDetailModel(table);

        JXTable detailTable = tableNameToTables.get(tableName);
        if (detailTable == null) {
            detailTable = createDetailTable(tableName, detailTableMode, model);
            tableNameToTables.put(tableName, detailTable);
        }
        else {
            TableRendererSorter sorter = (TableRendererSorter)detailTable.getModel();
            sorter.setModel(model);
            sorter.fireTableDataChanged();
            if (reload) {
                sorter.fireTableStructureChanged();
                detailTable.packAll();
            }
        }

        MyListSelectionListener selectionListener = new MyListSelectionListener(detailTable, table);
        detailTable.addMouseListener(selectionListener);
        detailTable.getSelectionModel().addListSelectionListener(selectionListener);

        split.add(createDetailTablePanel(tableName, detailTable, detailTableMode), JideBoxLayout.VARY);
    }


    private JXTable createDetailTable(String tableName,
                                      DetailTableMode detailTableMode,
                                      TableDetailModel model) {
        JXTable detailTable = new JXTable(model);
        detailTable.addHighlighter(HighlighterFactory.createSimpleStriping(VERY_LIGHT_GRAY));
        detailTable.setSortable(false);
        detailTable.setColumnControlVisible(true);
        TableRendererSorter rendererSorter = new TableRendererSorter(detailTable);
        rendererSorter.addMouseListenerToHeaderInTable(detailTable);
        detailTable.setModel(rendererSorter);
        rendererSorter.changeHeaderRenderer(detailTable);

        detailTable.setPreferredScrollableViewportSize(new Dimension(800, 100));
        detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        detailTable.setName(detailTableMode.getPrefix() + "." + tableName);
        detailTable.setDefaultRenderer(Field.class, new FieldRenderer());
        detailTable.packAll();

        return detailTable;
    }


    private static JideSplitPane createVerticalSplit() {
        JideSplitPane splitPane = new JideSplitPane(JideSplitPane.VERTICAL_SPLIT);
        splitPane.setProportionalLayout(true);
        splitPane.setShowGripper(true);
        return splitPane;
    }


    private JPanel createDetailTablePanel(String tableName,
                                          JTable detailTable,
                                          DetailTableMode detailTableMode) {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BorderLayout());

        JPanel tablePanel = createTablePanel(detailTable);
        detailPanel.add(createTableHeaderPanel(tableName, detailTableMode), BorderLayout.NORTH);
        detailPanel.add(tablePanel, BorderLayout.CENTER);
        return detailPanel;
    }


    private JPanel createTableHeaderPanel(final String tableName,
                                          final DetailTableMode detailTableMode) {
        JPanel tableHeaderPanel = new JPanel(new FlowLayout(LEFT));

        tableHeaderPanel.add(createTableLabel(tableName));
        JButton closeDetailTableButton = new JButton(new ImageIcon(getClass().getResource("close_red.png")));
        closeDetailTableButton.setName(String.format("%s.%s.closeButton",
                                                     detailTableMode.getPrefix(),
                                                     tableName));
        closeDetailTableButton.setToolTipText("Fermer la vue");
        closeDetailTableButton.setBorder(BorderFactory.createEmptyBorder());
        tableHeaderPanel.add(closeDetailTableButton);

        closeDetailTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (detailTableMode == DetailTableMode.INPUT) {
                    getCurrentTableSelection().get(tableName).setDisplayInput(false);
                }
                else {
                    getCurrentTableSelection().get(tableName).setDisplayOutput(false);
                }
                displayTables(false);
            }
        });

        return tableHeaderPanel;
    }


    private JLabel createTableLabel(String tableName) {
        JLabel label = new JLabel("  " + tableName);
        label.setFont(new JLabel().getFont().deriveFont(Font.BOLD));
        tableNameToLabels.put(tableName, label);
        return label;
    }


    private JPanel createTablePanel(JTable detailTable) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        return panel;
    }


    private void initTableData(Map<String, TableInfo> tableList) {
        tableInfos = new ArrayList<TableInfo>(tableList.values());
        TableListModel model = new TableListModel(tableInfos);
        tableSelection.setModel(model);
        model.fireTableDataChanged();
        model.fireTableStructureChanged();
        tableSelection.setName("tableList");
        tableSelection.getColumnModel().getColumn(INPUT_COLUMN_INDEX).setCellRenderer(new CheckBoxRenderer());
        tableSelection.getColumnModel().getColumn(OUPUT_COLUMN_INDEX).setCellRenderer(new CheckBoxRenderer());
        tableSelection.getColumnModel().getColumn(INPUT_COLUMN_INDEX).setCellEditor(new CheckBoxEditor());
        tableSelection.getColumnModel().getColumn(OUPUT_COLUMN_INDEX).setCellEditor(new CheckBoxEditor());
        tableSelection.setSelectionBackground(LIGHT_BLUE);
        tableSelection.setSortable(false);

        tableSelection.addMouseListener(new PopupHelper());

        model.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    if (e.getColumn() > 0) {
                        displayTables(false);
                    }
                }
            }
        });

        setColumnWidth(tableSelection, INPUT_COLUMN_INDEX, 40);
        setColumnWidth(tableSelection, OUPUT_COLUMN_INDEX, 40);
    }


    private void scrollToTable(JPanel panel, JScrollPane scroller) {
        int selectedRow = tableSelection.convertRowIndexToModel(tableSelection.getSelectedRow());
        if (selectedRow == -1) {
            return;
        }
        String tableName = (String)tableSelection.getValueAt(selectedRow, 0);
        JLabel label = tableNameToLabels.get(tableName);

        if (label == null) {
            return;
        }
        Rectangle rectangle = SwingUtilities.convertRectangle(label,
                                                              label.getBounds(null),
                                                              panel);

        panel.scrollRectToVisible(rectangle);

        int height = scroller.getViewport().getHeight();
        panel.scrollRectToVisible(new Rectangle(new Double(rectangle.getX()).intValue(),
                                                new Double(rectangle.getY()).intValue()
                                                + height - label.getHeight(),
                                                new Double(rectangle.getWidth()).intValue(),
                                                new Double(rectangle.getHeight()).intValue()));
        repaint();
    }


    private void setColumnWidth(JTable jTable, int graphicalColumnIndex, int width) {
        TableColumn input = jTable.getColumnModel().getColumn(graphicalColumnIndex);
        input.setPreferredWidth(width);
        input.setWidth(width);
        input.setMaxWidth(width);
        input.setMinWidth(width);
    }


    private void loadDataSet(DataSet set,
                             Map<String, TableInfo> tableInfoByName,
                             DetailTableMode detailTableMode) {
        Iterator iterator = set.tables();
        while (iterator.hasNext()) {
            Table tokioTable = (Table)iterator.next();
            String tableName = tokioTable.getName();
            TableInfo tableInfo = tableInfoByName.get(tableName);

            boolean isInput = detailTableMode == DetailTableMode.INPUT;

            if (tableInfo == null) {
                tableInfo = new TableInfo(tableName,
                                          isInput,
                                          detailTableMode == DetailTableMode.OUTPUT);
                tableInfoByName.put(tableName, tableInfo);
            }

            if (isInput) {
                tableInfo.setInput(true);
            }
            else {
                tableInfo.setOutput(true);
            }
        }
    }


    protected Map<File, Long> getFilesListWithLastUpdateDate() {
        return filesListWithLastUpdateDate;
    }


    private void createUIComponents() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
    }


    private class FieldRenderer extends DefaultTableCellRenderer {
        private CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();


        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            if (table.convertColumnIndexToModel(column) == TableDetailModel.ID_COLUMN_INDEX) {
                String id = ((Field)value).getValue();
                boolean hasId = !"".equals(id);
                JCheckBox checkBox =
                      (JCheckBox)checkBoxRenderer.getTableCellRendererComponent(table, hasId, isSelected,
                                                                                hasFocus, row, column);
                checkBox.setToolTipText(hasId ? id : null);
                return checkBox;
            }
            return super.getTableCellRendererComponent(table,
                                                       ((Field)value).getValue(),
                                                       isSelected,
                                                       hasFocus,
                                                       row,
                                                       column);
        }
    }

    private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        private CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setBorderPainted(true);
        }


        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            setBackground(isSelected, table);
            setSelected((value != null && (Boolean)value));
            setEnabled(table.isCellEditable(row, column));

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

    private class CheckBoxEditor extends DefaultCellEditor {
        private CheckBoxEditor() {
            super(new JCheckBox());
            JCheckBox checkBox = (JCheckBox)editorComponent;
            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        }
    }

    private class MyListSelectionListener extends MouseAdapter implements ListSelectionListener {
        private JTable table;
        private Table tokioTable;
        private int lastSelectedRow;


        private MyListSelectionListener(JTable table, Table tokioTable) {
            this.table = table;
            this.tokioTable = tokioTable;
        }


        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int selectedRow = table.rowAtPoint(e.getPoint());
                if (table.getSelectedRowCount() == 1) {
                    doIt(selectedRow);
                }
            }
        }


        public void valueChanged(ListSelectionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && lastSelectedRow != selectedRow) {
                doIt(selectedRow);
            }
        }


        private void doIt(int selectedRow) {
            lastSelectedRow = selectedRow;
            if (selectedRow >= 0) {
                TableRendererSorter rendererSorter = (TableRendererSorter)table.getModel();
                final Row row = tokioTable.getRow(rendererSorter.getConvertedIndex(selectedRow));
                final StringBuilder textContent = new StringBuilder();
                row.getLocationPointer().accept(new LoggerLocationVisitor(textContent));
                linePosition.setText(textContent.toString());
                linePositionTableName.setText(tokioTable.getName());
            }
            else {
                linePosition.setText("");
                linePositionTableName.setText("");
            }
        }
    }

    private final class PopupHelper extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            tableMousePressed(event);
        }


        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }


        void tableMousePressed(MouseEvent event) {
            if (javax.swing.SwingUtilities.isRightMouseButton(event)) {
                int row = tableSelection.rowAtPoint(event.getPoint());
                if (row != -1) {
                    tableSelection.setRowSelectionInterval(row, row);
                }
            }
            maybeShowPopup(event);
        }


        /**
         * Affiche le popupMenu si necessaire
         *
         * @param event L'événement souris
         */
        private void maybeShowPopup(MouseEvent event) {
            if (event.isPopupTrigger()) {
                popupMenu.show(event.getComponent(), event.getX(), event.getY());
            }
        }
    }

    public static enum DetailTableMode {
        INPUT,
        OUTPUT;


        String getPrefix() {
            if (this == INPUT) {
                return "input";
            }
            return "output";
        }
    }
}
