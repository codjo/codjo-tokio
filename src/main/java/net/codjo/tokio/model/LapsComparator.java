/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.Date;
/**
 * Comparaison de date par plage de validité.
 *
 * @version $Revision: 1.11 $
 */
class LapsComparator extends AbstractLapsComparator {
    LapsComparator() {
        super(ComparatorConverter.LAPS_COMPARATOR);
    }


    @Override
    protected long getNumberInfValue(Object expected) {
        return ((Number)expected).longValue() - getParamAsNumber().longValue();
    }


    @Override
    protected long getNumberSupValue(Object expected) {
        return ((Number)expected).longValue() + getParamAsNumber().longValue();
    }


    @Override
    protected long getDateInfValue(Object expected) {
        return ((Date)expected).getTime() - getParamAsNumber().longValue();
    }


    @Override
    protected long getDateSupValue(Object expected) {
        return ((Date)expected).getTime() + getParamAsNumber().longValue();
    }
}
