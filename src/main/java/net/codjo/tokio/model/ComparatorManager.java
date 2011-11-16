/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import net.codjo.test.common.DateUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Manager de comparateur de colonne.
 *
 * @author $Author: crego $
 */
class ComparatorManager {
    private Map<String, Object> comparators;
    private DefaultComparator defaultComparator = new DefaultComparator();


    ComparatorManager() {
        comparators = new HashMap<String, Object>();
    }


    public int size() {
        return comparators.size();
    }


    public void addComparator(String field, Comparator comparator) {
        comparators.put(field, comparator);
    }


    public Comparator getComparator(String field) {
        Object comparator = comparators.get(field);
        if (comparator != null) {
            return (Comparator)comparator;
        }
        else {
            return defaultComparator;
        }
    }


    public Iterator<Entry<String, Object>> iterator() {
        return Collections.unmodifiableMap(comparators).entrySet().iterator();
    }


    static class DefaultComparator extends AbstractComparator {
        private final String today;


        DefaultComparator() {
            today = DateUtil.createDateString(new java.util.Date());
        }


        public boolean isEqual(Object expected, Object actual, int sqlType) {
            if (expected == actual) {
                return true;
            }

            if (expected == null || actual == null) {
                return false;
            }

            if (sqlType == java.sql.Types.TIMESTAMP
                && actual.toString().startsWith(today)
                && expected.toString().startsWith(today)) {
                return true;
            }

            return expected.equals(actual);
        }
    }
}
