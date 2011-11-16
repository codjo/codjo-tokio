/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.junit.Test;
/**
 * Test FieldMap.
 */
public class FieldMapTest extends TestCase {
    public void test_all() throws Exception {
        FieldMap map = new FieldMap();

        try {
            map.put(null);
            fail("le field 'UNKNOWN' n'existe pas !");
        }
        catch (NullPointerException ex) {
        }

        Field oldFieldA = new Field("fieldA", "oldA");
        Field fieldA = new Field(oldFieldA.getName(), "valA");
        Field fieldB = new Field("fieldB");

        map.put(oldFieldA);
        map.put(fieldB);

        assertSame(oldFieldA, map.get(oldFieldA.getName()));
        map.put(fieldA);
        assertSame(fieldA, map.get(fieldA.getName()));
        assertSame(fieldB, map.get(fieldB.getName()));

        assertTrue(map.containsField(fieldA.getName()));
        assertTrue(map.containsField(fieldB.getName()));
        assertFalse(map.containsField("UNKNOWN"));

        try {
            map.get("UNKNOWN");
            fail("le field 'UNKNOWN' n'existe pas !");
        }
        catch (NoSuchElementException ex) {
        }
    }


    public void test_clear() throws Exception {
        FieldMap map = new FieldMap();
        map.put(new Field("fieldA", "valA"));
        map.put(new Field("fieldB"));

        map.clear();

        try {
            map.get("fieldA");
            fail("le field 'fieldA' n'existe plus a cause du clear!");
        }
        catch (NoSuchElementException ex) {
        }
    }


    public void test_toUnmodifiableMap() throws Exception {
        FieldMap map = new FieldMap();
        map.put(new Field("fieldA", "valA"));
        map.put(new Field("fieldB"));

        map = map.toUnmodifiableMap();

        try {
            map.put(new Field("fieldB"));
            fail("la map est non modifiable!");
        }
        catch (UnsupportedOperationException ex) {
        }

        assertEquals(2, map.size());
    }


    public void test_putAll() throws Exception {
        FieldMap map = new FieldMap();
        map.put(new Field("fieldA", "valA"));
        map.put(new Field("fieldB"));

        FieldMap other = new FieldMap();
        other.put(new Field("fieldD", "valD"));

        FieldMap nv = new FieldMap();
        nv.put(new Field("fieldC", "valC"));
        nv.put(new Field("fieldA", "surcharge"));
        nv.putAll(map);
        nv.putAll(other);

        assertEquals("valA", nv.get("fieldA").getValue());
        assertEquals(null, nv.get("fieldB").getValue());
        assertEquals("valC", nv.get("fieldC").getValue());
        assertEquals("valD", nv.get("fieldD").getValue());
    }


    public void test_remove() throws Exception {
        FieldMap map = new FieldMap();
        map.put(new Field("fieldA", "valA"));
        map.put(new Field("fieldB"));

        map.remove("fieldA");
        try {
            map.get("fieldA");
            fail("le field 'fieldA' n'existe plus (removed) !");
        }
        catch (NoSuchElementException ex) {
        }
    }


    public void test_fieldNameSet() throws Exception {
        FieldMap map = new FieldMap();
        map.put(new Field("fieldA", "valA"));
        map.put(new Field("fieldB"));

        Set<String> set = new TreeSet<String>(map.fieldNameSet());
        assertEquals(2, set.size());
        assertEquals("[fieldA, fieldB]", set.toString());
    }


    @Test
    public void test_isSame() throws Exception {
        FieldMap fieldMap1 = new FieldMap();
        FieldMap fieldMap2 = new FieldMap();

        assertTrue(fieldMap1.isSame(fieldMap2));

        fieldMap1.put(new Field("something"));

        assertFalse(fieldMap1.isSame(fieldMap2));

        fieldMap2.put(new Field("something"));

        assertTrue(fieldMap1.isSame(fieldMap2));
    }
}
