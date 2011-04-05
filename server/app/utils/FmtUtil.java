package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * This class handles formatting and validation of input and output values
 */
public class FmtUtil {

    /**
     * Trim useless data from the given transaction text
     *
     * @param text Text to trim
     * @return Trimmed text
     */
    public static String trimTransactionText(String text) {
        final String pattern = "(^\\d{16}\\s)?" +
                "(\\d{2}\\.\\d{2}\\s)?" +
                "([A-Z]{3}\\s\\d+,\\d+\\s)?" +
                "(TIL\\:\\s)?" +
                "(BETNR:\\s*\\d*)?";
        if (text != null) {
            String out = text.trim();
            out = out.replaceAll(pattern, "").trim();
            return out;
        } else {
            return "";
        }
    }

    /**
     * URL encode a map of params
     *
     * @param params Map of params which should be encoded
     * @return Map with values URL-encoded
     */
    public static String encode(Map<String, String> params) {
        final StringBuilder body = new StringBuilder();
        boolean first = true;
        for (String key : params.keySet()) {
            if (!first) {
                body.append("&");
            }
            body.append(key);
            body.append("=");
            try {
                body.append(URLEncoder.encode(params.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                body.append("");
            }
            first = false;
        }
        return body.toString();
    }

    /**
     * Get first word in string split by space
     *
     * @param s String
     * @return First word
     */
    public static String firstWord(String s) {
        if (s != null) {
            final String[] words = s.split(" ");
            if (words.length > 0) {
                return words[0];
            }
        }
        return "";
    }
}
