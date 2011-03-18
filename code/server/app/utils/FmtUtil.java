package utils;

public class FmtUtil {

    /**
     * Trim useless data from the given transaction text
     *
     * @param text
     * @return Trimmed text
     */
    public static String trimTransactionText(String text) {
        final String pattern = "(\\d+\\*+\\s)?(\\d{2}\\.\\d{2}\\s)?([A-Z]{3}\\s\\d+,\\d+\\s)?(TIL\\:\\s)?";
        return text == null ? "" : text.replaceAll(pattern, "");
    }
}
