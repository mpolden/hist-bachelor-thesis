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

    private static final String TAG = HttpUtil.class.getSimpleName();

    public static InputStream post(String url, List<NameValuePair> values) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        try {
            method.setEntity(new UrlEncodedFormEntity(values, "UTF-8"));
            HttpResponse response = httpClient.execute(method);
            return new BufferedInputStream(response.getEntity().getContent());
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
        return null;
    }

    public static String post(String url, String s, String contentType) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        try {
            method.setEntity(new StringEntity(s, "UTF-8"));
            method.setHeader("Content-Type", contentType);
            HttpResponse response = httpClient.execute(method);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
        return null;
    }
}
