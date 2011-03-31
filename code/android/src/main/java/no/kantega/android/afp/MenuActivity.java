package no.kantega.android.afp;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.TabHost;

public class MenuActivity extends TabActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

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
                Intent i = new Intent(getApplicationContext(), AddCategoryActivity.class);
                startActivity(i);
                break;
            case R.id.menu_pie_chart:
                Intent i2 = new Intent(getApplicationContext(), PieChartActivity.class);
                startActivity(i2);
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


}