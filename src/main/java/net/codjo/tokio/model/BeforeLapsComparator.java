/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.Date;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.9 $
 */
class BeforeLapsComparator extends AbstractLapsComparator {
    BeforeLapsComparator() {
        super("before");
    }


    @Override
    protected long getNumberInfValue(Object expected) {
        return ((Number)expected).longValue() - getParamAsNumber().longValue();
    }


    @Override
    protected long getNumberSupValue(Object expected) {
        return ((Number)expected).longValue();
    }


    @Override
    protected long getDateInfValue(Object expected) {
        return ((Date)expected).getTime() - getParamAsNumber().longValue();
    }


    @Override
    protected long getDateSupValue(Object expected) {
        return ((Date)expected).getTime();
    }
}
