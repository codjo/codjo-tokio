/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.math.BigDecimal;
import java.util.Date;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.10 $
 */
public abstract class AbstractLapsComparator extends AbstractComparator {
    private BigDecimal paramAsNumber;


    protected AbstractLapsComparator(String typeAssert) {
        super(typeAssert);
    }


    public boolean isEqual(Object expected, Object value, int sqlType) {
        if (expected == null || value == null) {
            return expected == value;
        }
        if (expected.getClass() != value.getClass()) {
            throw new IllegalArgumentException("Mauvaise classe! Expected: "
                                               + expected.getClass() + " et Value: " + value.getClass());
        }
        else if (expected instanceof Date) {
//       ok:    expected - param <= value <= expected + param
            long valueDate = ((Date)value).getTime();
            long infDate = getDateInfValue(expected);
            long supDate = getDateSupValue(expected);
            return (infDate <= valueDate) && (valueDate <= supDate);
        }
        else if (expected instanceof Number) {
            long valueNumber = ((Number)value).longValue();
            long infNumber = getNumberInfValue(expected);
            long supNumber = getNumberSupValue(expected);
            return (infNumber <= valueNumber) && (valueNumber <= supNumber);
        }
        else {
            throw new IllegalArgumentException("Mauvaise classe! Expected: "
                                               + expected.getClass() + " et Value: " + value.getClass());
        }
    }


    public void setParam(String param) {
        if (param == null) {
            param = "0";
        }
        super.setParam(param);
        this.paramAsNumber = new BigDecimal(param);
    }


    protected BigDecimal getParamAsNumber() {
        return paramAsNumber;
    }


    protected abstract long getNumberInfValue(Object expected);


    protected abstract long getNumberSupValue(Object expected);


    protected abstract long getDateInfValue(Object expected);


    protected abstract long getDateSupValue(Object expected);
}
