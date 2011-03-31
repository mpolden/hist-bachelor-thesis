package no.kantega.android.afp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int DATE_DIALOG_ID = 0;
    private Transactions db;
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
    private AlertDialog alertDialog;
    List<Transaction> matchingTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittransaction);
        this.t = (Transaction) getIntent().getExtras().getSerializable("transaction");
        this.db = new Transactions(getApplicationContext());
        this.suggestUrl = Prefs.getProperties(getApplication()).get("suggestTag").toString();
        this.untagged = new TransactionTag(getResources().getString(R.string.not_tagged));
        this.selectedTag = t.getTag();
        this.alertDialog = getAlertDialog();
        findViewById(R.id.edittransaction_button_edittransaction).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        matchingTransactions = db.getByText(t.getText(), t.get_id());
                        if (!selectedTag.equals(untagged) && !matchingTransactions.isEmpty()) {
                            alertDialog.setMessage(String.format(getResources().getString(R.string.auto_tag),
                                    matchingTransactions.size()));
                            alertDialog.show();
                        } else {
                            saveTransaction(false);
                        }
                    }
                });
        setupViews();
        updateSpinnerPosition(selectedTag);
        updateDisplay();
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
        if (!t.isInternal()) {
            text.setEnabled(false);
            date.setEnabled(false);
            amount.setEnabled(false);
        }
        category = (Spinner) findViewById(R.id.edittransaction_spinner_category);
        suggestedTag = (TextView) findViewById(R.id.suggested_tag);
        text.setText(FmtUtil.trimTransactionText(t.getText()));
        date.setText(FmtUtil.dateToString(DATE_FORMAT, t.getDate()));
        amount.setText(String.valueOf(t.getAmount()));
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(t.getDate());
        pickYear = calendar.get(Calendar.YEAR);
        pickMonth = calendar.get(Calendar.MONTH) + 1; // Starts at 0, wtf!
        pickDay = calendar.get(Calendar.DAY_OF_MONTH);
        adapter = new ArrayAdapter<TransactionTag>(this, android.R.layout.simple_spinner_item, db.getTags());
        adapter.add(untagged);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTag = (TransactionTag) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void saveTransaction(final boolean autoTag) {
        TransactionTag tag = null;
        if (!selectedTag.equals(untagged)) {
            tag = selectedTag;
            if (autoTag) {
                for (Transaction matching : matchingTransactions) {
                    matching.setTag(tag);
                    db.update(matching);
                }
            }
        }
        if (t.isInternal()) {
            boolean editTransactionOk = true;
            Date d = FmtUtil.stringToDate(DATE_FORMAT, String.format("%s-%s-%s",
                    pickYear, pickMonth, pickDay));
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
            Toast.makeText(getApplicationContext(), R.string.transaction_updated,
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                saveTransaction(true);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        saveTransaction(false);
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, pickYear, pickMonth,
                        pickDay);
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pickYear = year;
            pickMonth = monthOfYear + 1;
            pickDay = dayOfMonth;
            updateDisplay();
        }
    };

    private void updateDisplay() {
        date.setText(FmtUtil.dateToString(DATE_FORMAT, FmtUtil.stringToDate(DATE_FORMAT, String.format("%s-%s-%s",
                pickYear, pickMonth, pickDay))));
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
                final List<Map<String, String>> result = GsonUtil.parseMap(s);
                if (result != null && !result.isEmpty()) {
                    final String tag = result.get(0).get("tag");
                    suggestedTag.setText(tag);
                    updateSpinnerPosition(new TransactionTag(tag));
                } else {
                    updateSpinnerPosition(null);
                }
            }
        }
    }
}
