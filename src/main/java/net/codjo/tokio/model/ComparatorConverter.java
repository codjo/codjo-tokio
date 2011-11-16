/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
/**
 * Convertit un SerializeData depuis ou vers {@link Comparator} .
 *
 * @version $Revision: 1.7 $
 */
public final class ComparatorConverter {
    public static final String LAPS_COMPARATOR = "closeTo";


    private ComparatorConverter() {
    }


    public static Comparator newComparator(String type, String param)
          throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if ("contains".equals(type)) {
            return new ContainsComparator();
        }
        else if (LAPS_COMPARATOR.equals(type)) {
            LapsComparator comparator = new LapsComparator();
            comparator.setParam(param);
            return comparator;
        }
        else if ("before".equals(type)) {
            BeforeLapsComparator comparator = new BeforeLapsComparator();
            comparator.setParam(param);
            return comparator;
        }
        else if ("after".equals(type)) {
            AfterLapsComparator comparator = new AfterLapsComparator();
            comparator.setParam(param);
            return comparator;
        }

        else {
            Comparator comparator = (Comparator)Class.forName(type).newInstance();
            comparator.setParam(param);
            return comparator;
        }
    }


    public static Comparator newComparator(String precision) {
        LapsComparator comparator = new LapsComparator();
        comparator.setParam(precision);
        return comparator;
    }

/*
    public static Comparator unserialize(SerializeData data) {
        return new ContainsComparator();
    }

    public static class Types {
        public static final Types CONTAINS = new Types("contains");
        private final String name;

        private Types(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }


    public static class SerializeData {
        private final Types type;

        public SerializeData(Types type) {
            this.type = type;
        }

        public Types getType() {
            return type;
        }
    }
*/
}
