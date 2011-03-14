package no.kantega.android.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FmtUtil {

    /**
     * Convert the given date to string using the given format
     *
     * @param format
     * @param date
     * @return The date
     */
    public static String dateToString(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * Format currency according to the default locale
     *
     * @param number
     * @return Formatted currency
     */
    public static String currency(Double number) {
        return currency(number, Locale.getDefault());
    }

    /**
     * Format currency according to the given locale
     *
     * @param number
     * @param locale
     * @return Formatted currency
     */
    public static String currency(Double number, Locale locale) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(number);
    }

    /**
     * Trim useless data from the given transaction text
     *
     * @param text
     * @return Trimmed text
     */
    public static String trimTransactionText(String text) {
        final String pattern = "(\\d+\\*+\\s)?(\\d{2}\\.\\d{2}\\s)?([A-Z]{3}\\s\\d+,\\d+\\s)?(TIL\\:\\s)?";
        return text.replaceAll(pattern, "");
    }

    /**
     * Convert the given string to date using the given format
     *
     * @param format
     * @param date
     * @return The date
     */
    public static Date stringToDate(String format, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isNumber(String s) {
        return s != null && s.matches("^\\d+([,\\.]\\d+)?$");
    }
}
