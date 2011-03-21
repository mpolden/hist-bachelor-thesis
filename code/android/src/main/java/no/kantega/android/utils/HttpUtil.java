package no.kantega.android.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

    private static final String TAG = GsonUtil.class.getSimpleName();

    /**
     * Retrieve body of the given URL
     *
     * @param url
     * @return URL body
     */
    public static String getBody(final String url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet method = new HttpGet(url);
        String body = null;
        try {
            HttpResponse response = httpClient.execute(method);
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        }
        return body;
    }

    /**
     * Retrieve body of the given URL as an InputStream (for large requests)
     *
     * @param url
     * @return Body as InputStream
     */
    public static InputStream getBodyAsStream(final String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.
                    openConnection();
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            InputStream out = new BufferedInputStream(connection.getInputStream());
            return out;
        } catch (MalformedURLException e) {
            Log.d(TAG, "IOException", e);
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        }
        return null;
    }
}
