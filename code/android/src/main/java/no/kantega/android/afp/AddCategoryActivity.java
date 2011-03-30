package no.kantega.android.afp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.TransactionTag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AddCategoryActivity extends Activity {

    private Transactions db;
    private EditText category_name;
    private ImageView category_icon;
    private List<Integer> iconIds;

    private int currentIconId;

    private final View.OnClickListener saveCategoryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = category_name.getText().toString();
            if (name != null && !name.trim().equals("")) {
                TransactionTag ttag = new TransactionTag();
                ttag.setName(name);
                ttag.setImageId(currentIconId);
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
        setupIconList();
        setupViews();
    }

    private void setupIconList() {
        Field[] drawables = R.drawable.class.getFields();
        iconIds = new ArrayList<Integer>();
        for (Field f : drawables) {
            String name = f.getName();
            if (name.startsWith("tag_")) {
                int resID = getResources().getIdentifier(name, "drawable", getPackageName());
                if (resID > 0) {
                    iconIds.add(resID);
                }
            }
        }
    }

    private void setupViews() {
        category_name = (EditText) findViewById(R.id.edittext_categoryname);
        category_icon = (ImageView) findViewById(R.id.imageview_newcategory_icon);
        category_icon.setImageResource(iconIds.get(0));
        currentIconId = iconIds.get(0);
        GridView gridView = (GridView) findViewById(R.id.gridview_icons);
        gridView.setAdapter(new IconAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                category_icon.setImageResource(iconIds.get(position));
                currentIconId = iconIds.get(position);
            }
        });
    }

    private class IconAdapter extends BaseAdapter {

        private final Context context;

        public IconAdapter(Context c) {
            this.context = c;
        }

        public int getCount() {
            return iconIds.size();
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
            imageView.setImageResource(iconIds.get(position));
            return imageView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
