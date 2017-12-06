package bank.fioclient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Project: fio-client
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class DateUtils {

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Vrátí string ve formátu: yyyy-MM-dd
     * 
     * @param date
     * @return
     */
    public static String toString(Calendar date) {
        if (date == null) {
            return null;
        }
        return SDF.format(date.getTime());
    }

    /**
     * 
     * @param date
     * @return
     */
    public static Calendar toCalendar(String date) {
        if (date == null) {
            return null;
        }
        try {
            String s = date.split("\\+")[0];
            Date d = SDF.parse(s);
            Calendar c = GregorianCalendar.getInstance();
            c.setTime(d);
            return c;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse date: " + date, e);
        }
    }
}
