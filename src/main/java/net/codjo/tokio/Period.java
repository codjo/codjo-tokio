/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Classe utilitaire permettant de manipuler les periodes (yyyyMM).
 */
class Period {
    private final SimpleDateFormat simpleDateFormat;
    private final Calendar calendar;


    Period(Date date) {
        if (date == null) {
            throw new NullPointerException("Date parameter is required.");
        }
        simpleDateFormat = new SimpleDateFormat("yyyyMM");
        calendar = Calendar.getInstance();
        calendar.setTime(date);
    }


    /**
     * Periode sous la forme de chaine de caractere.
     *
     * @return la periode sous la forme 'yyyyMM'.
     */
    @Override
    public String toString() {
        return simpleDateFormat.format(calendar.getTime());
    }


    public void addMonth(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Month count must be positive");
        }
        setMonthDelta(count);
    }


    public void subMonth(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Month count must be positive");
        }
        setMonthDelta(-count);
    }


    private void setMonthDelta(int count) {
        calendar.add(Calendar.MONTH, count);
    }


    public Date getDate() {
        return calendar.getTime();
    }


    public Date getFirstDayDate() {
        return dayOfMonthToDate(1);
    }


    public Date getLastDayDate() {
        return dayOfMonthToDate(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    }


    private Date dayOfMonthToDate(final int dayOfMonth) {
        try {
            return new SimpleDateFormat("yyyyMMdd").parse(toString() + dayOfMonth);
        }
        catch (ParseException e) {
            ;
        }
        return null;
    }


    /**
     * Ne compare que le mois et l'année.
     */
    public boolean before(Period other) {
        if (other == null) {
            return false;
        }

        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTime(other.getDate());

        return calendar.get(Calendar.YEAR) <= otherCalendar.get(Calendar.YEAR) && (
              calendar.get(Calendar.YEAR) < otherCalendar.get(Calendar.YEAR)
              || calendar.get(Calendar.MONTH) < otherCalendar.get(Calendar.MONTH));
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Period)) {
            return false;
        }

        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTime(((Period)other).getDate());

        return calendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR)
               && calendar.get(Calendar.MONTH) == otherCalendar.get(Calendar.MONTH);
    }
}
