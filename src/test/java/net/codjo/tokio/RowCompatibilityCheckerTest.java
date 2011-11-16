package net.codjo.tokio;
import net.codjo.tokio.model.Row;
import net.codjo.tokio.model.UniqueKey;
import static net.codjo.tokio.util.RowUtil.field;
import static net.codjo.tokio.util.RowUtil.nullField;
import static net.codjo.tokio.util.RowUtil.row;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 *
 */
public class RowCompatibilityCheckerTest {
    private RowCompatibilityChecker checker = new RowCompatibilityChecker();


    @Test
    public void test_matchUniqueKey() throws Exception {
        Row requiredRow = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                              field("STATE", "FRANCE"));
        requiredRow.addUniqueKey("NAME");
        requiredRow.addUniqueKey("LASTNAME");

        Row matchOK1 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                           field("STATE", "FRANCE"));
        Row matchOK2 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                           field("STATE", "CZESKA REPUBLICA"));
        Row matchOK3 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                           field("GENDER", "FEMALE"));
        Row matchOK4 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"));

        Row matchKO1 = row(field("NAME", "sarah"));
        Row matchKO2 = row(field("NAME", "sarah"), field("LASTNAME", "RAMBO"));
        Row matchKO3 = row(field("NAME", "sarah"),
                           field("STATE", "FRANCE"));

        UniqueKey uniqueKey = requiredRow.getUniqueKey();
        assertTrue(checker.matchUniqueKey(uniqueKey, matchOK1));
        assertTrue(checker.matchUniqueKey(uniqueKey, matchOK2));
        assertTrue(checker.matchUniqueKey(uniqueKey, matchOK3));
        assertTrue(checker.matchUniqueKey(uniqueKey, matchOK4));

        assertFalse(checker.matchUniqueKey(uniqueKey, matchKO1));
        assertFalse(checker.matchUniqueKey(uniqueKey, matchKO2));
        assertFalse(checker.matchUniqueKey(uniqueKey, matchKO3));
    }


    @Test
    public void test_matchAllFields() throws Exception {
        Row requiredRow = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"), field("STATE", "FRANCE"));
        requiredRow.addUniqueKey("NAME");

        Row matchOK1 = row(field("NAME", "sarah"),
                           field("LASTNAME", "CONNOR"), field("STATE", "FRANCE"));
        Row matchOK2 = row(field("NAME", "sarah"),
                           field("LASTNAME", "CONNOR"), field("STATE", "FRANCE"), field("GENRE", "female"));

        Row matchKO1 = row(field("NAME", "sarah"),
                           field("LASTNAME", "CONNOR"));
        Row matchKO2 = row(field("NAME", "sarah"),
                           field("LASTNAME", "CALIMERO"), field("STATE", "FRANCE"));
        Row matchKO3 = row(field("NAME", "sarah"),
                           nullField("LASTNAME"), field("STATE", "FRANCE"));

        assertTrue(checker.matchAllFields(requiredRow, matchOK1));
        assertTrue(checker.matchAllFields(requiredRow, matchOK2));

        assertFalse(checker.matchAllFields(requiredRow, matchKO1));
        assertFalse(checker.matchAllFields(requiredRow, matchKO2));
        assertFalse(checker.matchAllFields(requiredRow, matchKO3));
    }


    @Test
    public void test_checkIncompatibility() throws Exception {
        Row requiredRow = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                              field("AGE", "33"), field("STATE", "FRANCE"));
        requiredRow.addUniqueKey("NAME");
        requiredRow.addUniqueKey("LASTNAME");

        Row compatibleRow1 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"));
        Row compatibleRow2 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                 field("AGE", "33"));
        Row compatibleRow3 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                 field("STATE", "FRANCE"));
        Row compatibleRow4 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                 field("AGE", "33"), field("STATE", "FRANCE"));
        Row compatibleRow5 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                 field("GENDER", "female"));
        Row compatibleRow6 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                 field("AGE", "33"),
                                 field("GENDER", "female"));

        Row incompatibleRow1 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                   field("AGE", "33"), field("STATE", "CANADA"));
        Row incompatibleRow2 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                   field("AGE", "33"), nullField("STATE"));
        Row incompatibleRow3 = row(field("NAME", "sarah"), field("LASTNAME", "CONNOR"),
                                   nullField("AGE"));

        assertFalse(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, compatibleRow1));
        assertFalse(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, compatibleRow2));
        assertFalse(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, compatibleRow3));
        assertFalse(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, compatibleRow4));
        assertFalse(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, compatibleRow5));
        assertFalse(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, compatibleRow6));

        assertTrue(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, incompatibleRow1));
        assertTrue(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, incompatibleRow2));
        assertTrue(checker.checkIncompatibilityIgnoringUniqueKey(requiredRow, incompatibleRow3));
    }
}
