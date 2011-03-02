package no.kantega.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class OverviewActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview);
		populateAverageConsumption("100", "700");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "pizza", "Food", "110");
		addTransaction("02.03.11", "BURGER!", "Food", "120");

	}

	private void populateAverageConsumption(String average_day_amount,
			String average_week_amount) {
		TextView average_day = (TextView) findViewById(R.id.average_day);
		TextView average_week = (TextView) findViewById(R.id.average_week);
		average_day.setText(average_day_amount);
		average_week.setText(average_week_amount);
	}

	private void addTransaction(String date, String text, String category,
			String amount) {

		TableLayout transactions = (TableLayout) findViewById(R.id.transactionTableLayout);
		// int children = transactions.getChildCount();
		TableRow tr = new TableRow(this);
		TextView tv = new TextView(this);

		LayoutParams tvParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT,
				1f);

		tv.setText(date);
		tv.setLayoutParams(tvParams);

		tr.addView(tv);

		tv = new TextView(this);
		tv.setText(text);
		tv.setLayoutParams(tvParams);

		tr.addView(tv);

		tv = new TextView(this);
		tv.setText(category);
		tv.setLayoutParams(tvParams);

		tr.addView(tv);

		tv = new TextView(this);
		tv.setText(amount);
		tv.setLayoutParams(tvParams);

		tr.addView(tv);

		transactions.addView(tr, 4);
	}

}
