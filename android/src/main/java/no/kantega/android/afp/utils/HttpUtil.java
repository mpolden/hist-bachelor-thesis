package no.kantega.android.afp.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This class handles HTTP communication
 */
public class HttpUtil {

    private static final String TAG = HttpUtil.class.getSimpleName();

    /**
     * Post values to the given URL
     *
     * @param url    URL
     * @param values The values to post
     * @return Body of response
     */
    public static InputStream post(String url, List<NameValuePair> values) {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpPost method = new HttpPost(url);
        try {
            method.setEntity(new UrlEncodedFormEntity(values, "UTF-8"));
            HttpResponse response = httpClient.execute(method);
            return new BufferedInputStream(response.getEntity().getContent());
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
        return null;
    }

    /**
     * Post values to the given URL and return the response
     *
     * @param url    URL
     * @param values Values to post
     * @return Http response
     */
    public static HttpResponse postResponse(String url, List<NameValuePair> values) {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpPost method = new HttpPost(url);
        try {
            method.setEntity(new UrlEncodedFormEntity(values, "UTF-8"));
            return httpClient.execute(method);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
        return null;
    }

    /**
     * Post values to the given URL and return the body of the response
     *
     * @param url    URL
     * @param values Values to post
     * @return Body of response
     */
    public static String postString(String url, List<NameValuePair> values) {
        final HttpResponse response = postResponse(url, values);
        try {
            return response != null ? EntityUtils.toString(response.getEntity()) : null;
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
        return null;
    }
}
