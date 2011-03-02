package no.kantega.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

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

		fetchData();
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

	private void fetchData() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(
				"http://10.10.10.77:9000/transactions/topTags/3");
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			String body = EntityUtils.toString(response.getEntity());
			Map<String, String> map = new HashMap<String, String>();
			int i = 1;
			for (String line : body.split("\n")) {
				String[] parts = line.split(" ");
				if (parts.length >= 2) {
					map.put("category" + i, parts[0]);
					map.put("amount" + i, parts[1]);
					i++;
				}
			}
			populateCategories(map);
		} catch (IOException e) {
			Log.d("Exception", "IOException", e);
		}
	}

	private void populateCategories(Map<String, String> values) {
		TextView category1 = (TextView) findViewById(R.id.top3_category_1);
		TextView category2 = (TextView) findViewById(R.id.top3_category_2);
		TextView category3 = (TextView) findViewById(R.id.top3_category_3);
		TextView amount1 = (TextView) findViewById(R.id.top3_amount_1);
		TextView amount2 = (TextView) findViewById(R.id.top3_amount_2);
		TextView amount3 = (TextView) findViewById(R.id.top3_amount_3);
		category1.setText(values.get("category1"));
		category2.setText(values.get("category2"));
		category3.setText(values.get("category3"));
		amount1.setText(values.get("amount1"));
		amount2.setText(values.get("amount2"));
		amount3.setText(values.get("amount3"));
	}

}
