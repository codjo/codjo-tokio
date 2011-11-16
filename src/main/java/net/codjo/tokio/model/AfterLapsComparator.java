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
class AfterLapsComparator extends AbstractLapsComparator {
    AfterLapsComparator() {
        super("after");
    }


    @Override
    protected long getNumberInfValue(Object expected) {
        return ((Number)expected).longValue();
    }


    @Override
    protected long getNumberSupValue(Object expected) {
        return ((Number)expected).longValue() + getParamAsNumber().longValue();
    }


    @Override
    protected long getDateInfValue(Object expected) {
        return ((Date)expected).getTime();
    }


    @Override
    protected long getDateSupValue(Object expected) {
        return ((Date)expected).getTime() + getParamAsNumber().longValue();
    }
}
