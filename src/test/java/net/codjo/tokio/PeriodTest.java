/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.test.common.DateUtil;
import java.text.ParseException;
import java.util.Date;
import junit.framework.TestCase;

/**
 */
public class PeriodTest extends TestCase {
    public void test_constructorFailed() {
        try {
            new Period(null);
            fail("NullPointerException attempted.");
        }
        catch (NullPointerException exception) {
            ;
        }
    }


    public void test_toString() {
        assertEquals("200402", new Period(createDate("2004-02-01")).toString());
        assertEquals("200512", new Period(createDate("2005-12-21")).toString());
        assertEquals("200106", new Period(createDate("2001-06-07")).toString());
    }


    public void test_addMonth() {
        {
            Period period = new Period(createDate("2004-02-01"));
            period.addMonth(0);
            assertEquals("200402", period.toString());
        }
        {
            Period period = new Period(createDate("2005-01-31"));
            period.addMonth(1);
            assertEquals("200502", period.toString());
        }
        {
            Period period = new Period(createDate("2004-02-01"));
            period.addMonth(11);
            assertEquals("200501", period.toString());
        }
    }


    public void test_addMonth_exception() {
        Period period = new Period(createDate("2005-01-31"));
        try {
            period.addMonth(-1);
            fail("IllegalArgumentException attempted.");
        }
        catch (IllegalArgumentException exception) {
            assertEquals("Month count must be positive", exception.getMessage());
        }
    }


    public void test_getDate() throws Exception {
        Date date = createDate("2004-02-01");
        Period period = new Period(date);
        assertEquals(date, period.getDate());

        period.addMonth(2);
        assertEquals(createDate("2004-04-01"), period.getDate());
        period.subMonth(1);
        assertEquals(createDate("2004-03-01"), period.getDate());
    }


    public void test_getFirstDayDate() throws Exception {
        Period period = new Period(createDate("2004-02-15"));
        assertEquals(createDate("2004-02-01"), period.getFirstDayDate());
    }


    public void test_getLastDayDate() throws Exception {
        {
            Period period = new Period(createDate("2000-12-15"));
            assertEquals(createDate("2000-12-31"), period.getLastDayDate());
        }
        {
            Period period = new Period(createDate("2000-11-15"));
            assertEquals(createDate("2000-11-30"), period.getLastDayDate());
        }
        {
            Period period = new Period(createDate("2000-02-15"));
            assertEquals(createDate("2000-02-29"), period.getLastDayDate());
        }
    }


    public void test_subMonth() {
        {
            Period period = new Period(createDate("2004-02-01"));
            period.subMonth(0);
            assertEquals("200402", period.toString());
        }
        {
            Period period = new Period(createDate("2004-02-01"));
            period.subMonth(1);
            assertEquals("200401", period.toString());
        }
        {
            Period period = new Period(createDate("2005-03-31"));
            period.subMonth(1);
            assertEquals("200502", period.toString());
        }
    }


    public void test_subMonth_exception() {
        Period period = new Period(createDate("2005-01-31"));
        try {
            period.subMonth(-1);
            fail("IllegalArgumentException attempted.");
        }
        catch (IllegalArgumentException exception) {
            assertEquals("Month count must be positive", exception.getMessage());
        }
    }


    public void test_equals() {
        {
            Period period = new Period(createDate("2004-01-01"));
            //noinspection ObjectEqualsNull
            assertFalse(period.equals(null));
        }
        {
            Period period = new Period(createDate("2004-01-01"));
            Period other = new Period(createDate("2005-12-31"));
            assertFalse(period.equals(other));
        }
        {
            final Date date = createDate("2005-01-01");
            Period period = new Period(date);
            Period idem = new Period(date);
            assertTrue(period.equals(idem));
        }
        {
            Period period = new Period(createDate("2005-01-01"));
            Period sameMonthDifferentDay = new Period(createDate("2005-01-31"));
            assertTrue(period.equals(sameMonthDifferentDay));
        }
        {
            final Date date = createDate("2004-01-01");
            Period period = new Period(date);
            assertFalse(period.equals(date));
        }
    }


    public void test_before() {
        {
            Period period = new Period(createDate("2004-01-01"));
            assertFalse(period.before(null));
        }
        {
            Period first = new Period(createDate("2004-07-03"));
            Period sameYearButMonthAfter = new Period(createDate("2004-09-15"));
            assertTrue(first.before(sameYearButMonthAfter));
            assertFalse(sameYearButMonthAfter.before(first));
        }
        {
            Period first = new Period(createDate("2004-01-01"));
            Period yearAfter = new Period(createDate("2005-12-31"));
            assertTrue(first.before(yearAfter));
            assertFalse(yearAfter.before(first));
        }
        {
            Period first = new Period(createDate("2005-09-10"));
            Period yearAfter = new Period(createDate("2006-01-05"));
            assertTrue(first.before(yearAfter));
            assertFalse(yearAfter.before(first));
        }
        {
            final Date date = createDate("2005-01-01");
            assertFalse(new Period(date).before(new Period(date)));
        }
        {
            Period first = new Period(createDate("2005-01-01"));
            Period sameMonthDifferentDay = new Period(createDate("2005-01-31"));
            assertFalse(first.before(sameMonthDifferentDay));
        }
    }


    private Date createDate(String yyyyMMdd) {
        try {
            return DateUtil.createDate(yyyyMMdd);
        }
        catch (ParseException e) {
            ;
        }
        return null;
    }
}
