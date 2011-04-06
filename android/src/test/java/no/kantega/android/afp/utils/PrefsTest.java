package no.kantega.android.afp.utils;

import android.content.SharedPreferences;
import no.kantega.android.afp.OverviewActivity;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;

@RunWith(MavenizedTestRunner.class)
public class PrefsTest {

    @Test
    public void testGet() {
        SharedPreferences preferences = Prefs.get(new OverviewActivity().getApplicationContext());
        assertNotNull(preferences);
    }

    @Test
    public void testGetProperties() {
        Properties properties = Prefs.getProperties(new OverviewActivity().getApplicationContext());
        assertNotNull(properties);
        assertNotNull(properties.getProperty("suggestTag"));
        assertNotNull(properties.getProperty("newTransactions"));
        assertNotNull(properties.getProperty("allTransactions"));
        assertNotNull(properties.getProperty("saveTransactions"));
    }
}
