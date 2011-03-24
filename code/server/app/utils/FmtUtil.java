package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class FmtUtil {

    /**
     * Trim useless data from the given transaction text
     *
     * @param text Text to trim
     * @return Trimmed text
     */
    public static String trimTransactionText(String text) {
        final String pattern = "(\\d+\\*+\\s)?(\\d{2}\\.\\d{2}\\s)?([A-Z]{3}\\s\\d+,\\d+\\s)?(TIL\\:\\s)?";
        return text == null ? "" : text.replaceAll(pattern, "");
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
}
