package no.kantega.android;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MenuActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        setupTabs();
        
    }
    
    private void setupTabs() {
    	Resources res = getResources();
    	TabHost tabHost = getTabHost();
    	
    	TabHost.TabSpec spec;
    	Intent intent;
    	intent = new Intent().setClass(this, OverviewActivity.class);
    	String overview = res.getString(R.string.overview);    	
    	spec = tabHost.newTabSpec(overview).setIndicator(overview, res.getDrawable(R.drawable.tab_overview)).setContent(intent);
    	tabHost.addTab(spec);
    	
    	intent = new Intent().setClass(this, TransactionsActivity.class);
    	String transactions = res.getString(R.string.transactions);    	
    	spec = tabHost.newTabSpec(transactions).setIndicator(transactions, res.getDrawable(R.drawable.tab_synchronize)).setContent(intent);
    	tabHost.addTab(spec);
    	
    	intent = new Intent().setClass(this, SynchronizeActivity.class);
    	String synchronize = res.getString(R.string.synchronize);
    	spec = tabHost.newTabSpec(synchronize).setIndicator(synchronize, res.getDrawable(R.drawable.tab_synchronize)).setContent(intent);
    	tabHost.addTab(spec);
    	
    	tabHost.setCurrentTab(0);
    	
    }
}