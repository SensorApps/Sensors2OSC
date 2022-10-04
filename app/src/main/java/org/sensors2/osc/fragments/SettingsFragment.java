package org.sensors2.osc.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import android.view.View;

import org.sensors2.osc.R;

import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.preference.Preference;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(org.sensors2.osc.R.xml.preferences);
        addPreferencesFromResource(org.sensors2.common.R.xml.common_preferences);
    }
    @SuppressLint("RestrictedApi")
    @Override
    protected Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new PreferenceGroupAdapter(preferenceScreen) {
            @Override
            public void onBindViewHolder(PreferenceViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                Preference preference = getItem(position);
                View iconFrame = holder.itemView.findViewById(R.id.icon_frame);
                if (iconFrame != null) {
                    iconFrame.setVisibility(View.GONE);
                }
            }
        };
    }
}
