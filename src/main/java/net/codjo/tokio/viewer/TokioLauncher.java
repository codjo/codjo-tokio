package net.codjo.tokio.viewer;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import org.apache.log4j.Logger;
/**
 *
 */
public class TokioLauncher {

    private static final Logger LOG = Logger.getLogger(TokioLauncher.class);
    private static TokioViewer viewer;


    private TokioLauncher() {
    }


    public static void main(String[] args) throws Exception {
        try {
            configureLookAndFeel();
            viewer = new TokioViewer(new File(args[0]));
            viewer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            viewer.pack();
            viewer.setVisible(true);
        }
        catch (Exception e) {
            TokioViewer.showErrorDialog(e.getMessage(), e);
            System.exit(-1);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        while (str != null) {
            str = in.readLine();
            process(str);
        }
    }


    private static void process(String str) {
        if ("reload".equals(str)) {
            viewer.toFront();
            viewer.requestFocus();
        }
    }


    private static void configureLookAndFeel() {
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        Plastic3DLookAndFeel.setCurrentTheme(new ExperienceBlue());
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        }
        catch (UnsupportedLookAndFeelException e) {
            LOG.error("erreur interne avec le look and feel", e);
            System.exit(-1);
        }

//         Nécessaire lorsque un Editor Combo se trouve sur une table, cf :
//           net.codjo.gabi.gui.referential.GroupDetailGui
        UIManager.put("TableInfo.selectionForeground", Color.WHITE);
        UIManager.put("TableInfo.focusCellBackground", new JTable().getSelectionBackground());
        UIManager.put("TableInfo.focusCellForeground", Color.WHITE);
    }
}
