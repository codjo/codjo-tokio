/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.math.BigDecimal;
import junit.framework.TestCase;
/**
 * Classe de test {@link ComparatorConverter}.
 *
 * @version $Revision: 1.6 $
 */
public class ComparatorConverterTest extends TestCase {
    /*
        public void test_unserialize() throws Exception {
            ComparatorConverter.SerializeData data =
                new ComparatorConverter.SerializeData(ComparatorConverter.Types.CONTAINS);

            Comparator contains = ComparatorConverter.unserialize(data);

            assertTrue(contains.isEqual("bobo", "je suis bobo !", java.sql.Types.VARCHAR));
            assertFalse(contains.isEqual("bobo", "je suis JC !", java.sql.Types.VARCHAR));
        }

    */
    public void test_std_precision() throws Exception {
        Comparator comp = ComparatorConverter.newComparator("10");
        assertEquals(LapsComparator.class, comp.getClass());
        assertTrue("Le param doit etre positionné",
                   comp.isEqual(new BigDecimal("150"), new BigDecimal("160"), 0));
    }


    public void test_std() throws Exception {
        Comparator comp = ComparatorConverter.newComparator("contains", null);
        assertEquals(ContainsComparator.class, comp.getClass());

        comp =
              ComparatorConverter.newComparator(ComparatorConverter.LAPS_COMPARATOR, "100");
        assertEquals(LapsComparator.class, comp.getClass());
        assertTrue("Le param doit etre positionné",
                   comp.isEqual(new BigDecimal("150"), new BigDecimal("160"), 0));

        comp = ComparatorConverter.newComparator(CustomComparator.class.getName(), "0");
        assertEquals(CustomComparator.class, comp.getClass());
    }


    public void test_before() throws Exception {
        Comparator comp = ComparatorConverter.newComparator("before", "10000");
        assertEquals(BeforeLapsComparator.class, comp.getClass());
        assertFalse(comp.isEqual(new BigDecimal("150"), new BigDecimal("160"), 0));
        assertTrue(comp.isEqual(new BigDecimal("160"), new BigDecimal("150"), 0));
    }


    public void test_after() throws Exception {
        Comparator comp = ComparatorConverter.newComparator("after", "10000");
        assertEquals(AfterLapsComparator.class, comp.getClass());
        assertTrue(comp.isEqual(new BigDecimal("150"), new BigDecimal("160"), 0));
        assertFalse(comp.isEqual(new BigDecimal("160"), new BigDecimal("150"), 0));
    }


    public static class CustomComparator extends AbstractComparator {
        public boolean isEqual(Object expected, Object value, int sqlType) {
            return false;
        }
    }
}
