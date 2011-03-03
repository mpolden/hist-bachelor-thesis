package no.kantega.android;

import java.util.List;

import no.kantega.android.models.Transaction;
import no.kantega.android.utils.GsonUtil;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TransactionsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transactions);
		populateTransactions();
	}

	private void populateTransactions() {
		List<Transaction> transactions = GsonUtil.parseTransactions(GsonUtil
				.getJSON("http://10.10.10.14:9000/t/transactions/10"));
		for (Transaction t : transactions) {
            addTransaction(t);
        }
	}

	private void addTransaction(Transaction t) {
		TableLayout tl = (TableLayout) findViewById(R.id.transaction_table_layout);
		TableRow.LayoutParams tvParams = new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 1f);

		tl.addView(getSeparator());

		TableRow tr = new TableRow(this);
		tr.addView(getTextView("Date", tvParams, true));
		tr.addView(getTextView(t.accountingDate.toString(), tvParams, true));
		tl.addView(tr);

		tl.addView(getSeparator());

		tr = new TableRow(this);
		tr.addView(getTextView("Text", tvParams, false));
		tr.addView(getTextView(t.type.name, tvParams, false));
		tl.addView(tr);

		tr = new TableRow(this);
		tr.addView(getTextView("Category", tvParams, false));
		tr.addView(getTextView(t.tags.get(0).name, tvParams, false));
		tl.addView(tr);

		tr = new TableRow(this);
		tr.addView(getTextView("Amount", tvParams, false));
		tr.addView(getTextView(t.amountOut.toString(), tvParams, false));
		tl.addView(tr);

		tl.addView(getSeparator());

	}

	private TextView getTextView(String s, TableRow.LayoutParams lp,
			boolean bold) {
		TextView tv = new TextView(this);
		if (bold) {
			tv.setText(Html.fromHtml("<b>" + s + "</b>"));
		} else {
			tv.setText(s);
		}
		tv.setLayoutParams(lp);
		return tv;
	}

	private View getSeparator() {
		View v = new View(this);
		v.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.FILL_PARENT, 2));
		v.setBackgroundColor(Color.GRAY);
		return v;
	}

}
