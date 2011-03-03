package no.kantega.android.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormattingUtil {

    public static String date(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String currency(Double number) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(number);
    }

    public static String trimTransactionText(String text) {
        final String pattern = "(\\d+\\*+\\s)?(\\d{2}\\.\\d{2}\\s)?([A-Z]{3}\\s\\d+,\\d+\\s)?";
        return text.replaceAll(pattern, "");
    }
}
