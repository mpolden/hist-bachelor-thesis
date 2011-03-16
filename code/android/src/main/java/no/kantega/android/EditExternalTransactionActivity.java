package no.kantega.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.kantega.android.controllers.Transactions;
import no.kantega.android.models.Transaction;
import no.kantega.android.models.TransactionTag;
import no.kantega.android.utils.FmtUtil;

import java.util.ArrayList;
import java.util.List;

public class EditExternalTransactionActivity extends Activity {
    private Transaction t;
    private Bundle extras;
    private Transactions db;
    private List<String> categories;
    private String selectedTransactionTag;

    private TextView text;
    private TextView date;
    private TextView amount;
    private Spinner category;

    private View.OnClickListener editTransactionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean editTransactionOk = true;

            TransactionTag ttag = new TransactionTag();
            ttag.setName(selectedTransactionTag);
            t.setTag(ttag);
            db.update(t);
            Toast.makeText(getApplicationContext(), "Transaction updated", Toast.LENGTH_LONG).show();
            finish();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editexternaltransaction);

        extras = getIntent().getExtras();
        t = (Transaction)extras.getSerializable("transaction");

        this.db = new Transactions(getApplicationContext());

        Button editButton = (Button)findViewById(R.id.editexternaltransaction_button_edittransaction);
        editButton.setOnClickListener(editTransactionButtonListener);
        setupViews();

    }

    private void setupViews() {
        text = (TextView)findViewById(R.id.editexternaltransaction_textview_text);
        date = (TextView)findViewById(R.id.editexternaltransaction_textview_date);
        amount = (TextView)findViewById(R.id.editexternaltransaction_textview_amount);
        category = (Spinner)findViewById(R.id.editexternaltransaction_spinner_category);

        selectedTransactionTag = t.getTag().getName();
        text.setText(FmtUtil.trimTransactionText(t.getText()));
        date.setText(FmtUtil.dateToString("yyyy-MM-dd", t.getAccountingDate()));
        amount.setText(t.getAmountOut().toString());

        fillCategoryList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new MyOnItemSelectedListener());

        int spinnerPosition = adapter.getPosition(selectedTransactionTag);
        category.setSelection(spinnerPosition);
    }

    private void fillCategoryList() {
        ArrayList<TransactionTag> transactionTagList = new ArrayList<TransactionTag>(db.getTags());
        categories = new ArrayList<String>();
        for (int i = 0; i < transactionTagList.size(); i++) {
            categories.add(transactionTagList.get(i).getName());
        }
    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            selectedTransactionTag = parent.getItemAtPosition(pos).toString();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
