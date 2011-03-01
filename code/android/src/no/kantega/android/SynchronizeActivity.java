package no.kantega.android;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class SynchronizeActivity extends Activity {	
	
		
	private OnClickListener syncButtonListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			CharSequence text = "Synchronized";			
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
			//TextView top3_category_1 = (TextView)findViewById(R.id.top3_category_1);
			//top3_category_1.setText("Øl");
			changeTab();
			
		}
	};
	
	private void changeTab() {
		TabActivity test = (TabActivity)this.getParent();
		test.getTabHost().setCurrentTab(0);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.synchronize);
	    ImageButton syncButton = (ImageButton)findViewById(R.id.syncButton);
	    syncButton.setImageResource(R.drawable.syncbutton);
	    syncButton.setOnClickListener(syncButtonListener);
	    
	
	}
}