/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
/**
 * Classe responsable de la comparaison de 2 chaines.
 */
class ContainsComparator extends AbstractComparator {
    ContainsComparator() {
        super("contains");
    }


    public boolean isEqual(Object expected, Object value, int sqlType) {
        return value == expected
               || value != null
                  && expected != null
                  && value.toString().contains(expected.toString());
    }
}
