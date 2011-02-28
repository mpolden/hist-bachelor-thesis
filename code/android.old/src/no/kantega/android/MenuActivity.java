package no.kantega.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.menu);
        TextView textView = (TextView) findViewById(R.id.placeholder);
        textView.setText(R.string.description);
        Button overview = (Button) findViewById(R.id.overview);
        overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        OverviewActivity.class));
            }
        });
    }
}
