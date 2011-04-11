package no.kantega.android.afp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import no.kantega.android.afp.utils.Prefs;
import no.kantega.android.afp.utils.Register;

public class SettingsActivity extends Activity {

    private EditText inputUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        this.inputUsername = (EditText) findViewById(R.id.et_username);
        Button save = (Button) findViewById(R.id.button_settings_save);
        final SharedPreferences preferences = Prefs.get(getApplicationContext());
        final String username = preferences.getString("username", null);
        inputUsername.setText(username);
        final SharedPreferences.Editor editor = preferences.edit();
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putString("username", inputUsername.getText().toString());
                        editor.commit();
                        Register.registerWithServer(getApplicationContext());
                        finish();
                    }
                });
    }
}
