package no.kantega.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import no.kantega.android.controllers.Transactions;
import no.kantega.android.models.TransactionTag;


public class CategoryActivity extends Activity {

    private Transactions db;
    private Spinner icon_spinner;
    private EditText category_name;
    private String[] icon_list = {"Chicken", "Shirt", "Fork/knife", "Fuel", "Winebottle", "iMac", "Shoebox", "User"};

    private View.OnClickListener saveCategoryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TransactionTag ttag = new TransactionTag();
            String name = category_name.getText().toString();
            if (name != null) {
                ttag.setName(name);
                db.add(ttag);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid category name", Toast.LENGTH_LONG);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        this.db = new Transactions(getApplicationContext());

        Button saveButton = (Button) findViewById(R.id.button_newcategory_save);
        saveButton.setOnClickListener(saveCategoryButtonListener);
        setupViews();
    }

    private void setupViews() {
        category_name = (EditText) findViewById(R.id.edittext_categoryname);
        icon_spinner = (Spinner) findViewById(R.id.spinner_icon);
        icon_spinner.setAdapter(new CustomIconAdapter(getApplicationContext(), R.layout.iconspinnerrow, icon_list));
    }


    private class CustomIconAdapter extends ArrayAdapter<String> {

        public CustomIconAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.iconspinnerrow, parent, false);
            TextView label = (TextView) row.findViewById(R.id.tv_icon_text);
            label.setText(icon_list[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.row_icon);
            if (icon_list[position] != null) {
                icon.setImageResource(getImageIdByTag(icon_list[position]));
            }

            return row;
        }

    }

    private int getImageIdByTag(String tag) {
        if ("Suitcase".equals(tag)) {
            return R.drawable.suitcase;
        } else if ("Shirt".equals(tag)) {
            return R.drawable.tshirt;
        } else if ("Fork/knife".equals(tag)) {
            return R.drawable.forkknife;
        } else if ("Chicken".equals(tag)) {
            return R.drawable.chicken;
        } else if ("Fuel".equals(tag)) {
            return R.drawable.fuel;
        } else if ("Winebottle".equals(tag)) {
            return R.drawable.winebottle;
        } else if ("iMac".equals(tag)) {
            return R.drawable.imac;
        } else if ("Shoebox".equals(tag)) {
            return R.drawable.shoebox;
        } else if ("User".equals(tag)) {
            return R.drawable.user;
        } else {
            return -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
