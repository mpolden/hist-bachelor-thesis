package no.kantega.android;

import android.os.Bundle;
import android.widget.TextView;

public class TransactionActivity extends MenuActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        TextView textView = (TextView) findViewById(R.id.placeholder);
        textView.setText(R.string.tempTransaction);
    }
}