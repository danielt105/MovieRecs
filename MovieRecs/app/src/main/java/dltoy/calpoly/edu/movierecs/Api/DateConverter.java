package dltoy.calpoly.edu.movierecs.Api;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public static Date stringToDate(String input) {
        Date date = null;
        try {
            date = DATE_FORMATTER.parse(input);
        }
        catch (ParseException p) {
            Log.e("DateConverter", "Failed to convert string: " + input + " to a date.");
        }
        return date;
    }

    public static String dateToString(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static String getCurrentDateString() {
        return dateToString(new Date(System.currentTimeMillis()));
    }

    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}
