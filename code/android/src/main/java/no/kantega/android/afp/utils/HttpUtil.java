package no.kantega.android.afp.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HttpUtil {

    private static final String TAG = GsonUtil.class.getSimpleName();

    public static String post(final String url, final String s) {
        return post(url, s, "text/plain");
    }

    public static InputStream post(final String url, List<NameValuePair> values) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        try {
            method.setEntity(new UrlEncodedFormEntity(values));
            HttpResponse response = httpClient.execute(method);
            return new BufferedInputStream(response.getEntity().getContent());
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
        return null;
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
