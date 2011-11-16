/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.8 $
 */
public class ContainsComparatorTest extends TestCase {
    private ContainsComparator comparator = null;


    public void test_contains() throws Exception {
        assertTrue(comparator.isEqual("toto", "sklsklsdj toto qxcjksdlfqslmk", 0));
        assertTrue(comparator.isEqual("toto", "toto qxcjksdlfqslmk", 0));
        assertTrue(comparator.isEqual("toto", "sklsklsdj toto", 0));
        assertFalse(comparator.isEqual("toto", "sklsklsdj toti", 0));

        assertTrue(comparator.isEqual("", "sklsklsdj toto", 0));
    }


    public void test_contains_null() throws Exception {
        assertFalse(comparator.isEqual(null, "sklsklsdj toto qxcjksdlfqslmk", 0));
        assertFalse(comparator.isEqual("toto", null, 0));
        assertTrue(comparator.isEqual(null, null, 0));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comparator = new ContainsComparator();
    }


    @Override
    protected void tearDown() throws Exception {
        comparator = null;
        super.tearDown();
    }
}
