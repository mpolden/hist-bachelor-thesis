package no.kantega.android;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

public class MenuActivity extends TabActivity {

    private boolean customTitleSupported;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);

        customTitleBar(getText(R.string.app_name).toString());
        ImageButton addButton = (ImageButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent().setClass(getApplicationContext(), AddTransactionActivity.class);
                startActivity(intent);

            }
        });
        setupTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_categories:
                Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }

    private void setupTabs() {
        Resources res = getResources();
        TabHost tabHost = getTabHost();

        TabHost.TabSpec spec;
        Intent intent;
        intent = new Intent().setClass(this, OverviewActivity.class);
        String overview = res.getString(R.string.overview);
        spec = tabHost.newTabSpec(overview).setIndicator(overview,
                res.getDrawable(R.drawable.tab_overview)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, TransactionsActivity.class);
        String transactions = res.getString(R.string.transactions);
        spec = tabHost.newTabSpec(transactions).setIndicator(transactions,
                res.getDrawable(R.drawable.tab_transactions)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, SynchronizeActivity.class);
        String synchronize = res.getString(R.string.synchronize);
        spec = tabHost.newTabSpec(synchronize).setIndicator(synchronize,
                res.getDrawable(R.drawable.tab_synchronize)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

    }

    private void customTitleBar(String left) {
        // set up custom title
        if (customTitleSupported) { // Check if custom title is supported
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                    R.layout.titlebar);
            TextView titleTvLeft = (TextView) findViewById(R.id.titleTvLeft);
            titleTvLeft.setText(left);

            ProgressBar titleProgressBar = (ProgressBar) findViewById(R.id.leadProgressBar);
            titleProgressBar.setVisibility(View.GONE);
        }
    }
}