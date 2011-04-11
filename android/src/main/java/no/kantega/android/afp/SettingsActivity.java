package no.kantega.android.afp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {
    private EditText inputUsername;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        this.inputUsername = (EditText) findViewById(R.id.et_username);
        this.save = (Button) findViewById(R.id.button_settings_save);

        SharedPreferences app_preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String username = app_preferences.getString("username", "");
        inputUsername.setText(username);

        final SharedPreferences.Editor editor = app_preferences.edit();
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putString("username", inputUsername.getText().toString());
                        editor.commit();
                        finish();
                    }
                });
    }


}
