package no.kantega.android.afp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.models.TransactionTag;
import no.kantega.android.afp.utils.FmtUtil;
import no.kantega.android.afp.utils.GsonUtil;
import no.kantega.android.afp.utils.HttpUtil;
import no.kantega.android.afp.utils.Prefs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EditTransactionActivity extends Activity {

    private static final String TAG = EditTransactionActivity.class.
            getSimpleName();
    private static final int DATE_DIALOG_ID = 0;
    private Transactions db;
    //private List<String> categories;
    private ArrayAdapter<TransactionTag> adapter;
    private Transaction t;
    private TransactionTag selectedTag;
    private int pickYear;
    private int pickMonth;
    private int pickDay;
    private EditText text;
    private Button date;
    private EditText amount;
    private Spinner category;
    private TextView suggestedTag;
    private String suggestUrl;
    private TransactionTag untagged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittransaction);
        Bundle extras = getIntent().getExtras();
        t = (Transaction) extras.getSerializable("transaction");
        this.db = new Transactions(getApplicationContext());
        Button editButton = (Button) findViewById(R.id.edittransaction_button_edittransaction);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionTag tag = null;
                if (!selectedTag.equals(untagged)) {
                    tag = selectedTag;
                }
                if (t.isInternal()) {
                    boolean editTransactionOk = true;
                    Date d = FmtUtil.stringToDate("yyyy-MM-dd", String.format("%s-%s-%s", pickYear, pickMonth + 1, pickDay));
                    if (FmtUtil.isNumber(amount.getText().toString())) {
                        t.setAmount(Double.parseDouble(amount.getText().toString()));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.invalid_amount,
                                Toast.LENGTH_LONG).show();
                        editTransactionOk = false;
                    }
                    if (editTransactionOk) {
                        t.setText(text.getText().toString());
                        t.setTag(tag);
                        t.setDate(d);
                        t.setDirty(true);
                        db.update(t);
                        Toast.makeText(getApplicationContext(), R.string.transaction_updated,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    t.setTag(tag);
                    t.setDirty(true);
                    db.update(t);
                    Toast.makeText(getApplicationContext(), R.string.transaction_updated, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        setupViews();
        checkInternal();
        this.suggestUrl = Prefs.getProperties(getApplication()).get("suggestTag").toString();
        this.untagged = new TransactionTag(getResources().getString(R.string.not_tagged));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new SuggestionsTask().execute(suggestUrl,
                FmtUtil.trimTransactionText(t.getText()));
    }

    private void updateSpinnerPosition(TransactionTag t) {
        if (t == null) {
            category.setSelection(adapter.getPosition(untagged));
        } else {
            category.setSelection(adapter.getPosition(t));
        }
    }

    private void setupViews() {
        text = (EditText) findViewById(R.id.edittransaction_edittext_text);
        date = (Button) findViewById(R.id.edittransaction_button_pickDate);
        amount = (EditText) findViewById(R.id.edittransaction_edittext_amount);
        category = (Spinner) findViewById(R.id.edittransaction_spinner_category);
        suggestedTag = (TextView) findViewById(R.id.suggested_tag);
        //currentTag = (TextView) findViewById(R.id)
        text.setText(FmtUtil.trimTransactionText(t.getText()));
        date.setText(FmtUtil.dateToString("yyyy-MM-dd", t.getDate()));
        amount.setText(String.valueOf(t.getAmount()));
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
        adapter = new ArrayAdapter<TransactionTag>(this, android.R.layout.simple_spinner_item, db.getTags());
        adapter.add(untagged);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new MyOnItemSelectedListener());
        selectedTag = t.getTag();
        updateSpinnerPosition(selectedTag);
    }

    private void checkInternal() {
        if (!t.isInternal()) {
            text.setEnabled(false);
            date.setEnabled(false);
            amount.setEnabled(false);
        }
    }

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

    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pickYear = year;
            pickMonth = monthOfYear;
            pickDay = dayOfMonth;
            updateDisplay();
        }
    };

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            /*if (selectedTag != null && !selectedTag.equals("Not tagged")) {
                selectedTag = parent.getItemAtPosition(pos).toString();
            } else {
                selectedTag = null;
            }*/
            selectedTag = (TransactionTag) parent.getItemAtPosition(pos);
        }

        public void onNothingSelected(AdapterView parent) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private class SuggestionsTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.post(params[0], params[1], "text/plain");
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                List<Map<String, String>> result = GsonUtil.parseMap(s);
                if (!result.isEmpty()) {
                    final String tag = result.get(0).get("tag");
                    suggestedTag.setText(tag);
                    updateSpinnerPosition(new TransactionTag(tag));
                }
                updateSpinnerPosition(null);
            }
        }
    }
}
