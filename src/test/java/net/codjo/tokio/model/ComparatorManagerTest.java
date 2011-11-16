/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import net.codjo.test.common.DateUtil;
import java.sql.Timestamp;
import java.util.Date;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ComparatorManager}.
 *
 * @version $Revision: 1.11 $
 */
public class ComparatorManagerTest extends TestCase {
    private ComparatorManager manager = null;


    public void test_list() throws Exception {
        ContainsComparator comparator = new ContainsComparator();

        assertEquals(0, manager.size());
        manager.addComparator("MA_COL", comparator);
        assertEquals(1, manager.size());
        assertEquals(comparator, manager.getComparator("MA_COL"));

        // On ecrase le comparateur
        manager.addComparator("MA_COL", new LapsComparator());
        assertEquals(1, manager.size());
        assertEquals(LapsComparator.class, manager.getComparator("MA_COL").getClass());

        // On ajout un nouveau comparateur
        manager.addComparator("MA_COL_BIS", comparator);
        assertEquals(2, manager.size());
        assertEquals(comparator, manager.getComparator("MA_COL_BIS"));
    }


    public void test_list_default() throws Exception {
        ContainsComparator comparator = new ContainsComparator();
        manager.addComparator("MA_COL", comparator);

        Comparator comp = manager.getComparator("UNE_AUTRE_COLONNE");
        assertNotNull(comp);
        assertEquals(ComparatorManager.DefaultComparator.class, comp.getClass());

        assertEquals(comp, manager.getComparator("BOBO"));
    }


    public void test_defaultComparator() throws Exception {
        ComparatorManager.DefaultComparator comparator =
              new ComparatorManager.DefaultComparator();

        Object sameObject = new Object();
        assertTrue(comparator.isEqual(sameObject, sameObject, java.sql.Types.JAVA_OBJECT));

        assertTrue(comparator.isEqual(newDate("2005-02-07 10:30:00"),
                                      newDate("2005-02-07 10:30:00"), java.sql.Types.TIMESTAMP));

        assertFalse(comparator.isEqual(newDate("2005-02-07 10:30:00"),
                                       newDate("2005-02-06 10:30:00"), java.sql.Types.TIMESTAMP));
    }


    public void test_defaultComparator_today() throws Exception {
        ComparatorManager.DefaultComparator comparator =
              new ComparatorManager.DefaultComparator();

        Date today = new Date();
        assertFalse(comparator.isEqual(newDate("2005-02-07 10:30:00"),
                                       new Timestamp(today.getTime()), java.sql.Types.TIMESTAMP));

        assertTrue(comparator.isEqual(newDate(DateUtil.createDateString(today) + " 10:00:00"),
                                      newDate(DateUtil.createDateString(today) + " 10:01:00"),
                                      java.sql.Types.TIMESTAMP));
    }


    public void test_defaultComparator_expectedNull()
          throws Exception {
        ComparatorManager.DefaultComparator comparator =
              new ComparatorManager.DefaultComparator();

        Date today = new Date();
        assertFalse(comparator.isEqual(null, new Timestamp(today.getTime()),
                                       java.sql.Types.TIMESTAMP));

        assertTrue(comparator.isEqual(newDate(DateUtil.createDateString(today) + " 10:00:00"),
                                      newDate(DateUtil.createDateString(today) + " 10:01:00"),
                                      java.sql.Types.TIMESTAMP));
    }


    private Date newDate(String date) {
        return java.sql.Timestamp.valueOf(date);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        manager = new ComparatorManager();
    }


    @Override
    protected void tearDown() throws Exception {
        manager = null;
        super.tearDown();
    }
}
