package net.codjo.tokio.datagenerators;
import java.sql.Date;
import java.util.Calendar;
/**
 *
 */
public class DateGenerator implements Generator<Date> {
    private int current = 0;


    public Date generateDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.add(Calendar.DAY_OF_YEAR, current);
        current++;

        return new Date(calendar.getTimeInMillis());
    }


    public Date generateValue() {
        return generateDate();
    }
}
