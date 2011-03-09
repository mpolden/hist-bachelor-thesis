package no.kantega.android;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends TabActivity {
	private boolean customTitleSupported;
	
	private OnClickListener addButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            CharSequence text = "Synchronized";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            
            Intent intent = new Intent().setClass(getApplicationContext(), AddTransactionActivity.class);
            startActivity(intent);
            
        }
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.main);
        
        customTitleBar(getText(R.string.app_name).toString());
        ImageButton addButton = (ImageButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(addButtonListener);
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
    	spec = tabHost.newTabSpec(transactions).setIndicator(transactions, res.getDrawable(R.drawable.tab_transactions)).setContent(intent);
    	tabHost.addTab(spec);
    	
    	intent = new Intent().setClass(this, SynchronizeActivity.class);
    	String synchronize = res.getString(R.string.synchronize);
    	spec = tabHost.newTabSpec(synchronize).setIndicator(synchronize, res.getDrawable(R.drawable.tab_synchronize)).setContent(intent);
    	tabHost.addTab(spec);
    	
    	tabHost.setCurrentTab(0);
    	
    }
    
    private void customTitleBar(String left) {
		// set up custom title
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
			TextView titleTvLeft = (TextView) findViewById(R.id.titleTvLeft);
			titleTvLeft.setText(left);

			ProgressBar titleProgressBar = (ProgressBar) findViewById(R.id.leadProgressBar);
			titleProgressBar.setVisibility(View.GONE);
		}
	}
}