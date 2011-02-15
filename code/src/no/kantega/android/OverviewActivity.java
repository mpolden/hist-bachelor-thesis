package no.kantega.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class OverviewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        TextView textView = (TextView) findViewById(R.id.placeholder);
        textView.setText(R.string.temp);
    }

}