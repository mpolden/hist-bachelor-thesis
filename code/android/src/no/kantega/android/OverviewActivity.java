package no.kantega.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class OverviewActivity extends Activity {
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.overview);
	        
	    }

	 
	 private void populateAverageConsumption(String average_day_amount, String average_week_amount) {
		 TextView average_day = (TextView)findViewById(R.id.average_day);		 
		 TextView average_week = (TextView)findViewById(R.id.average_week);
		 average_day.setText(average_day_amount);
		 average_week.setText(average_week_amount);		 
	 }
	 
}
