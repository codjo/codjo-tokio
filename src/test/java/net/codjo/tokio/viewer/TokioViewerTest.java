package net.codjo.tokio.viewer;
import net.codjo.test.common.LogString;
import net.codjo.test.common.PathUtil;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.uispec4j.ComboBox;
import org.uispec4j.ItemNotFoundException;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

public class TokioViewerTest extends UISpecTestCase {
    LogString log = new LogString();
    private static final int INPUT_COLUMN = 1;
    private static final int OUTPUT_COLUMN = 2;

    private Window window;
    private Table tableList;


    private TokioViewer initViewer(String fileName) throws TokioViewerException {
        TokioViewer viewer = new TokioViewerMock(PathUtil.find(TokioViewerTest.class, fileName));

        window = new Window(viewer);
        tableList = window.getTable("tableList");
        return viewer;
    }


    public void test_displayTableList() throws Exception {
        initViewer("tokioViewer_simple.tokio");

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


    public void test_reloadTokioWhenUpdatedDate() throws Exception {
        TokioViewer viewer = initViewer("tokioViewer_withEntities.tokio");
        log.assertContent("loadFile()");
        log.clear();

        Map<File, Long> fileLongMap = viewer.getFilesListWithLastUpdateDate();

        assertEquals(3, fileLongMap.keySet().size());
        Iterator<Entry<File, Long>> iterator = fileLongMap.entrySet().iterator();
        File fileEntity = iterator.next().getKey();
        File fileInclude = iterator.next().getKey();
        File fileTokio = iterator.next().getKey();
        assertEquals("tokioViewer_withEntities.tokio", fileTokio.getName());
        assertEquals("required_withEntities.entities", fileEntity.getName());
        assertEquals("RefCodification.tokio", fileInclude.getName());

        viewer.refreshDataIfTokioHasChanged();
        log.assertContent("");

        fileTokio.setLastModified(fileTokio.lastModified() + 1);
        viewer.refreshDataIfTokioHasChanged();
        log.assertContent("loadFile()");
        log.clear();

        fileEntity.setLastModified(fileEntity.lastModified() + 1);
        viewer.refreshDataIfTokioHasChanged();
        log.assertContent("loadFile()");
        log.clear();

        fileInclude.setLastModified(fileInclude.lastModified() + 1);
        viewer.refreshDataIfTokioHasChanged();
        log.assertContent("loadFile()");
    }


    public void test_displayTableDetailForStory() throws TokioViewerException {
        initViewer("tokioViewer_simple.tokio");

        tableList.click(0, INPUT_COLUMN);
        checkDetailTable("input.MY_TABLE",
                         new String[]{
                               "Id", "MY_TABLE_FIELD1", "MY_TABLE_FIELD2", "MY_TABLE_FIELD3"
                         },
                         new Object[][]{
                               {Boolean.FALSE, "myTableField1Row0", "myTableField2Row0", ""},
                               {Boolean.FALSE, "myTableField1Row1", "", "myTableField3Row1"},
                         });

        tableList.click(1, INPUT_COLUMN);
        checkDetailTable("input.MY_TABLE_INPUT",
                         new String[]{
                               "Id", "MY_TABLE_INPUT_FIELD1", "MY_TABLE_INPUT_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.TRUE, "myTableInputField1Row0", "myTableInputField2Row0"},
                         });

        tableList.click(2, OUTPUT_COLUMN);
        checkDetailTable("output.MY_TABLE_OUTPUT",
                         new String[]{
                               "Id", "MY_TABLE_OUTPUT_FIELD1", "MY_TABLE_OUTPUT_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.FALSE, "myTableOutputField1Row0", "myTableOutputField2Row0"},
                         });
    }


    public void test_displayTableDetailForCases() throws TokioViewerException {
        initViewer("tokioViewer_cases.tokio");
        ComboBox casesCombo = window.getComboBox();
        assertTrue(casesCombo.selectionEquals("Simple"));

        tableList.click(0, INPUT_COLUMN);
        checkDetailTable("input.MY_TABLE",
                         new String[]{
                               "Id", "MY_TABLE_FIELD1", "MY_TABLE_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.TRUE, "myTableField1Row0", "myTableField2Row0"},
                         });

        tableList.click(1, INPUT_COLUMN);
        checkDetailTable("input.MY_TABLE_INPUT",
                         new String[]{
                               "Id", "MY_TABLE_INPUT_FIELD1", "MY_TABLE_INPUT_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.FALSE, "myTableInputField1Row0", "myTableInputField2Row0"},
                         });

        tableList.click(2, OUTPUT_COLUMN);
        checkDetailTable("output.MY_TABLE",
                         new String[]{
                               "Id", "MY_TABLE_FIELD1", "MY_TABLE_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.TRUE, "myTableField1Row0", "myTableField2Row0"},
                         });

        casesCombo.select("HeritageSansModificationStructure");
        checkDetailTable("input.MY_TABLE",
                         new String[]{
                               "Id", "MY_TABLE_FIELD1", "MY_TABLE_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.TRUE, "myTableNewValue", "myTableField2Row0"},
                         });

        checkDetailTable("input.MY_TABLE_INPUT",
                         new String[]{
                               "Id", "MY_TABLE_INPUT_FIELD1", "MY_TABLE_INPUT_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.FALSE, "myTableInputField1Row0", "myTableInputField2Row0"},
                         });

        checkDetailTable("output.MY_TABLE",
                         new String[]{
                               "Id", "MY_TABLE_FIELD1", "MY_TABLE_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.TRUE, "myTableNewValueOutput", "myTableField2Row0"},
                         });

        casesCombo.select("HeritageAvecModificationStructure");

        checkDetailTable("input.MY_TABLE",
                         new String[]{
                               "Id", "MY_TABLE_FIELD1", "MY_TABLE_FIELD2", "MY_TABLE_FIELD3"
                         },
                         new Object[][]{
                               {Boolean.TRUE, "myTableField1Row0", "myTableField2Row0", ""},
                               {Boolean.FALSE, "newValueMyTableField1", "", "newValueMyTableField3"},
                         });

        checkDetailTable("input.MY_TABLE_INPUT",
                         new String[]{
                               "Id", "MY_TABLE_INPUT_FIELD1", "MY_TABLE_INPUT_FIELD2"
                         },
                         new Object[][]{
                               {Boolean.FALSE, "myTableInputField1Row0", "myTableInputField2Row0"},
                         });

        checkDetailTable("output.MY_TABLE",
                         new String[]{
                               "Id", "MY_TABLE_FIELD1", "MY_TABLE_FIELD2", "MY_TABLE_FIELD3"
                         },
                         new Object[][]{
                               {Boolean.TRUE, "myTableField1Row0", "myTableField2Row0", ""},
                               {Boolean.FALSE, "newValueMyTableField1Output", "",
                                "newValueMyTableField3Output"},
                         });
    }


    public void test_closeInputDetailTableWithButton() throws Exception {
        initViewer("tokioViewer_simple.tokio");

        tableList.click(0, INPUT_COLUMN);

        assertTrue(tableList.contentEquals(new Object[][]{
              {"MY_TABLE", true, false},
              {"MY_TABLE_INPUT", false, false},
              {"MY_TABLE_OUTPUT", false, false}
        }));

        assertDetailTableIsVisible("input.MY_TABLE", true);

        window.getButton("input.MY_TABLE.closeButton").click();

        assertDetailTableIsVisible("input.MY_TABLE", false);

        assertTrue(tableList.contentEquals(new Object[][]{
              {"MY_TABLE", false, false},
              {"MY_TABLE_INPUT", false, false},
              {"MY_TABLE_OUTPUT", false, false}
        }));
    }

       public void test_closeOutputDetailTableWithButton() throws Exception {
        initViewer("tokioViewer_simple.tokio");

        tableList.click(0, OUTPUT_COLUMN);
        tableList.click(2, OUTPUT_COLUMN);

        assertTrue(tableList.contentEquals(new Object[][]{
              {"MY_TABLE", false, true},
              {"MY_TABLE_INPUT", false, false},
              {"MY_TABLE_OUTPUT", false, true}
        }));

        assertDetailTableIsVisible("output.MY_TABLE", true);
        assertDetailTableIsVisible("output.MY_TABLE_OUTPUT", true);

        window.getButton("output.MY_TABLE_OUTPUT.closeButton").click();

        assertDetailTableIsVisible("output.MY_TABLE", true);
        assertDetailTableIsVisible("output.MY_TABLE_OUTPUT", false);

        assertTrue(tableList.contentEquals(new Object[][]{
              {"MY_TABLE", false, true},
              {"MY_TABLE_INPUT", false, false},
              {"MY_TABLE_OUTPUT", false, false}
        }));

        window.getButton("output.MY_TABLE.closeButton").click();

        assertDetailTableIsVisible("output.MY_TABLE", false);
        assertDetailTableIsVisible("output.MY_TABLE_OUTPUT", false);
           
        assertTrue(tableList.contentEquals(new Object[][]{
              {"MY_TABLE", false, false},
              {"MY_TABLE_INPUT", false, false},
              {"MY_TABLE_OUTPUT", false, false}
        }));
    }

    private void checkDetailTable(String tableName, String[] expectedHeaders, Object[][] expectedContent) {
        Table detailTable = window.getTable(tableName);
        assertTrue(detailTable.getHeader().contentEquals(expectedHeaders));
        assertTrue(detailTable.contentEquals(expectedContent));
    }


    private void assertDetailTableIsVisible(String tableName, boolean visible) {
        try {
            window.getTable(tableName);
            if (!visible) {
                fail("La table " + tableName + " ne devrait pas être affichée");
            }
        }
        catch (ItemNotFoundException e) {
            if (visible) {
                fail("La table " + tableName + " devrait être affichée");
            }
        }
    }


    private class TokioViewerMock extends TokioViewer {
        private TokioViewerMock(File tokioFile) throws TokioViewerException {
            super(tokioFile);
        }


        @Override
        protected void loadFile(File tokioFile) throws TokioViewerException {
            super.loadFile(tokioFile);
            log.call("loadFile");
        }
    }
}
