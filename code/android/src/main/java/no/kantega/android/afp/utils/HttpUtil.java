package no.kantega.android.afp.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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

    /**
     * Post JSON to URL
     *
     * @param url
     * @param json
     */
    public static String postJSON(final String url, final String json) {
        return post(url, json, "application/json");
    }

    /**
     * Post plain text to URL
     *
     * @param url
     * @param s
     * @return Body
     */
    public static String post(final String url, final String s) {
        return post(url, s, "text/plain");
    }

    private static String post(final String url, List<NameValuePair> values) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        String body = null;
        try {
            method.setEntity(new UrlEncodedFormEntity(values));
            HttpResponse response = httpClient.execute(method);
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        }
        return body;
    }

    private static String post(final String url, final String s,
                               final String contentType) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        String body = null;
        try {
            method.setEntity(new StringEntity(s, "UTF-8"));
            method.setHeader("Content-Type", contentType);
            HttpResponse response = httpClient.execute(method);
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        }
        return body;
    }
}
