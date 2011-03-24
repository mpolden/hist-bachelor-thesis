package no.kantega.android.afp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.TransactionTag;


public class CategoryActivity extends Activity {

    private Transactions db;
    private EditText category_name;
    private ImageView category_icon;
    private Integer[] iconIds = {
            R.drawable.chicken, R.drawable.tshirt,
            R.drawable.forkknife, R.drawable.fuel,
            R.drawable.winebottle, R.drawable.imac,
            R.drawable.shoebox, R.drawable.user,
            R.drawable.gift, R.drawable.house,
            R.drawable.suitcase};
    private String[] icon_list = {"Chicken", "Shirt", "Fork/knife", "Fuel", "Winebottle", "iMac", "Shoebox", "User"};

    private View.OnClickListener saveCategoryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = category_name.getText().toString();
            if (name != null && !name.trim().equals("")) {
                TransactionTag ttag = new TransactionTag();
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
        category_icon = (ImageView) findViewById(R.id.imageview_newcategory_icon);
        category_icon.setImageResource(iconIds[0]);

        GridView gridView = (GridView) findViewById(R.id.gridview_icons);
        gridView.setAdapter(new IconAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                category_icon.setImageResource(iconIds[position]);
            }
        });
    }


    private class IconAdapter extends BaseAdapter {
        private Context context;

        public IconAdapter(Context c) {
            this.context = c;
        }

        public int getCount() {
            return iconIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, initialize attributes
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(iconIds[position]);
            return imageView;
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
