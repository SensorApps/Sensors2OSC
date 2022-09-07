package org.sensors2.osc.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(org.sensors2.osc.R.xml.preferences);
        addPreferencesFromResource(org.sensors2.common.R.xml.common_preferences);
    }
}
