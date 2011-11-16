package net.codjo.tokio.gui;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import java.awt.Color;
import java.io.File;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
/**
 *
 */
public class TokioViewerTest extends UISpecTestCase {
    private static final String TOKIO_DIR = "/test/gui/";

    private Window window;
    private Table tableList;


    @Override
    protected void setUp() throws Exception {
        TokioViewer viewer = new TokioViewer(
              new File(getClass().getResource(TOKIO_DIR + "tokioViewer_simple.tokio").getPath()));

        window = new Window(viewer);
        tableList = window.getTable("tableList");
    }


    public void test_displayTableList() throws Exception {
        assertTrue(tableList.contentEquals(new Object[][]{
              {"MY_TABLE", false, false},
              {"MY_TABLE_INPUT", false, false},
              {"MY_TABLE_OUTPUT", false, false}
        }));

        assertTrue(tableList.isEditable(new boolean[][]{
              {false, true, true},
              {false, true, false},
              {false, false, true}
        }));
    }


    public void test_displayTableDetail() {
        tableList.click(0, 1);
        window.getButton("Afficher les données").click();
        Table detailTable = window.getTable("input.MY_TABLE");
        assertTrue(detailTable.getHeader().contentEquals(new String[]{
              "FIELD1", "FIELD2"
        }));
        assertTrue(detailTable.contentEquals(new Object[][]{
              {"valueField1Row0", "valueField2Row0"},
              {"valueField1Row1", "valueField2Row1"},
        }));
    }

    //public static void main(String[] args) throws Exception {
    //    configureLookAndFeel();
    //
    //    //TokioViewer viewer = new TokioViewer(new File(TOKIO_DIR + "AffichageArbre.tokio"));
    //    TokioViewer viewer = new TokioViewer(new File(TOKIO_DIR + "tokioViewer_simple.tokio"));
    //    viewer.pack();
    //    viewer.setVisible(true);
    //}


    private static void configureLookAndFeel() throws UnsupportedLookAndFeelException {
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        Plastic3DLookAndFeel.setCurrentTheme(new ExperienceBlue());
        UIManager.setLookAndFeel(new Plastic3DLookAndFeel());

        // Nécessaire lorsque un Editor Combo se trouve sur une table, cf :
        //   net.codjo.gabi.gui.referential.GroupDetailGui
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.focusCellBackground", new JTable().getSelectionBackground());
        UIManager.put("Table.focusCellForeground", Color.WHITE);
    }
}
