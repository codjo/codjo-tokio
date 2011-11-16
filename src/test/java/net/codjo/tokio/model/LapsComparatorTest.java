/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.math.BigDecimal;
import java.util.Date;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.9 $
 */
public class LapsComparatorTest extends TestCase {
    private LapsComparator comparator = null;


    public void test_isEqual_date() throws Exception {
        comparator.setParam("10000");

        assertTrue(comparator.isEqual(newDate("2004-01-01 10:30:00"),
                                      newDate("2004-01-01 10:30:10"), 0));
        assertTrue(comparator.isEqual(newDate("2004-01-01 10:30:00"),
                                      newDate("2004-01-01 10:29:50"), 0));

        assertFalse(comparator.isEqual(newDate("2004-01-01 10:30:00"),
                                       newDate("2004-01-01 10:29:49"), 0));
        assertFalse(comparator.isEqual(newDate("2004-01-01 10:30:00"),
                                       newDate("2004-01-01 10:30:11"), 0));
    }


    public void test_isEqual_number() throws Exception {
        comparator.setParam("10");

        assertTrue(comparator.isEqual(new BigDecimal("150"), new BigDecimal("160"), 0));
        assertTrue(comparator.isEqual(new BigDecimal("150"), new BigDecimal("140"), 0));

        assertFalse(comparator.isEqual(new BigDecimal("150"), new BigDecimal("165"), 0));
        assertFalse(comparator.isEqual(new BigDecimal("150"), new BigDecimal("139"), 0));
    }


    public void test_isEqual_bad() throws Exception {
        comparator.setParam("10000");

        assertFalse(comparator.isEqual(null, newDate("2004-01-01 10:30:10"), 0));
        assertFalse(comparator.isEqual(newDate("2004-01-01 10:30:00"), null, 0));
        assertTrue(comparator.isEqual(null, null, 0));

        try {
            comparator.isEqual(newDate("2004-01-01 10:30:00"), "dfgdgfd", 0);
            fail("Mauvaise classe ! Expected: java.util.Date et Value: java.lang.String");
        }
        catch (Exception ex) {
        }
        try {
            comparator.isEqual("dfgdgfd", newDate("2004-01-01 10:30:00"), 0);
            fail("Mauvaise classe !");
        }
        catch (Exception ex) {
        }
        try {
            comparator.isEqual("ddd", "ddd", 0);
            fail(
                  "Mauvaise classe ! Expected: java.lang.String et Value: java.lang.String");
        }
        catch (Exception ex) {
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        comparator = new LapsComparator();
    }


    protected void tearDown() throws Exception {
        comparator = null;
        super.tearDown();
    }


    private Date newDate(String date) {
        return java.sql.Timestamp.valueOf(date);
    }
}
