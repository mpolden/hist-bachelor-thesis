package no.kantega.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.kantega.android.controllers.Transactions;
import no.kantega.android.models.Transaction;
import no.kantega.android.models.TransactionTag;
import no.kantega.android.utils.FmtUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditTransactionActivity extends Activity {

    private Transactions db;
    private List<String> categories;
    private Bundle extras;
    private Transaction t;
    private String selectedTransactionTag;
    private int pickYear;
    private int pickMonth;
    private int pickDay;
    private static final int DATE_DIALOG_ID = 0;
    private EditText text;
    private Button date;
    private EditText amount;
    private Spinner category;
    private View.OnClickListener editTransactionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean editTransactionOk = true;
            TransactionTag ttag = new TransactionTag();
            ttag.setName(selectedTransactionTag);
            Date d = FmtUtil.stringToDate("yyyy-MM-dd", String.format("%s-%s-%s", pickYear, pickMonth, pickDay));
            if (amount.getText().toString().trim() != "" && FmtUtil.isNumber(amount.getText().toString())) {
                t.setAmountOut(Double.parseDouble(amount.getText().toString()));
            } else {
                Toast.makeText(getApplicationContext(), R.string.invalid_amount,
                        Toast.LENGTH_LONG).show();
                editTransactionOk = false;
            }
            if (editTransactionOk) {
                t.setText(text.getText().toString());
                t.setTag(ttag);
                t.setAccountingDate(d);
                t.setDirty(true);
                t.setChanged(true);
                db.update(t);
                Toast.makeText(getApplicationContext(), R.string.transaction_updated,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittransaction);
        extras = getIntent().getExtras();
        t = (Transaction) extras.getSerializable("transaction");
        this.db = new Transactions(getApplicationContext());
        Button editButton = (Button) findViewById(R.id.edittransaction_button_edittransaction);
        editButton.setOnClickListener(editTransactionButtonListener);
        setupViews();
    }

    private void setupViews() {
        text = (EditText) findViewById(R.id.edittransaction_edittext_text);
        date = (Button) findViewById(R.id.edittransaction_button_pickDate);
        amount = (EditText) findViewById(R.id.edittransaction_edittext_amount);
        category = (Spinner) findViewById(R.id.edittransaction_spinner_category);
        text.setEnabled(false);
        selectedTransactionTag = t.getTag().getName();
        text.setText(FmtUtil.trimTransactionText(t.getText()));
        date.setText(FmtUtil.dateToString("yyyy-MM-dd", t.getAccountingDate()));
        amount.setText(String.valueOf(t.getAmountOut()));
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        final Calendar c = Calendar.getInstance();
        pickYear = c.get(Calendar.YEAR);
        pickMonth = c.get(Calendar.MONTH);
        pickDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
        fillCategoryList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new MyOnItemSelectedListener());
        int spinnerPosition = adapter.getPosition(selectedTransactionTag);
        category.setSelection(spinnerPosition);
    }

    // updates the date we display in the TextView
    private void updateDisplay() {
        date.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(pickMonth + 1).append("-").append(pickDay).append("-")
                .append(pickYear).append(" "));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, pickYear, pickMonth,
                        pickDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pickYear = year;
            pickMonth = monthOfYear;
            pickDay = dayOfMonth;
            updateDisplay();
        }
    };

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
