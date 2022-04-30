package org.sensors2.osc.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import androidx.core.app.NavUtils;

/**
 * Created by thomas on 03.11.14.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(org.sensors2.osc.R.xml.preferences);
        addPreferencesFromResource(org.sensors2.common.R.xml.common_preferences);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
