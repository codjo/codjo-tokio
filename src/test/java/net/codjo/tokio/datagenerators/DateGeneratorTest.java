package net.codjo.tokio.datagenerators;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 */
public class DateGeneratorTest {

    @Test
    public void test_generateDate() throws Exception {
        DateGenerator generator = new DateGenerator();

        Date date1 = generator.generateDate();
        Date date2 = generator.generateDate();

        Assert.assertNotNull(date1);
        Assert.assertNotNull(date2);

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date1);
        int day1 = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(date2);
        int day2 = cal.get(Calendar.DAY_OF_YEAR);
        cal.set(1970, Calendar.JANUARY, 1);

        Assert.assertEquals(cal.getTimeInMillis(), date1.getTime());
        Assert.assertEquals(1, day2 - day1);
    }
}
