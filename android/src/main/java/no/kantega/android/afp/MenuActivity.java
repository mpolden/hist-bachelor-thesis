package no.kantega.android.afp;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * This activity handles the bottom navigation panel (tabs)
 */
public class MenuActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupTabs();
    }

    /**
     * Configure the tab menu
     */
    private void setupTabs() {
        Resources res = getResources();
        final TabHost tabHost = getTabHost();
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
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setTabColors(tabHost);
            }
        });
    }

    public static void setTabColors(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {

            //tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.LTGRAY);
        }
        //tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.RED);
    }
}