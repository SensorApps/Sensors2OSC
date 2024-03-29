package org.sensors2.osc.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

public class AutoCompletePreference extends EditTextPreference {
    private final Set<String> dataSource;
    private final String dataSourceName;

    private AutoCompleteTextView autocompleteText = null;

    private static class OnBindEditTextListener implements EditTextPreference.OnBindEditTextListener {
        private final AutoCompletePreference preference;

        public OnBindEditTextListener(AutoCompletePreference preference){
            this.preference = preference;
        }

        @Override
        public void onBindEditText(@NonNull EditText editText) {
            ViewGroup.LayoutParams params = editText.getLayoutParams();
            ViewGroup viewGroup = (ViewGroup)editText.getParent();
            String currentValue = editText.getText().toString();
            editText.setVisibility(View.GONE);

            AutoCompleteTextView autocompleteText = this.preference.autocompleteText;
            autocompleteText.setLayoutParams(params);
            autocompleteText.setId(android.R.id.edit);
            autocompleteText.setText(currentValue);
            if (autocompleteText.getParent() != null){
                ((ViewGroup)autocompleteText.getParent()).removeView(autocompleteText);
            }
            viewGroup.addView(autocompleteText);
        }
    }

    private static class OnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        private final AutoCompletePreference preference;

        public OnPreferenceChangeListener(AutoCompletePreference preference){
            this.preference = preference;
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            AutoCompletePreference autoCompletePreference = (AutoCompletePreference) preference;
            autoCompletePreference.setText(this.preference.autocompleteText.getText().toString());
            return false;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AutoCompletePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.dataSourceName = getDataSourceName(attrs);
        this.dataSource = this.loadDataSource(this.dataSourceName);
        this.setupAutocomplete(context, attrs);
    }

    public AutoCompletePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.dataSourceName = getDataSourceName(attrs);
        this.dataSource = this.loadDataSource(this.dataSourceName);
        this.setupAutocomplete(context, attrs);
    }

    public AutoCompletePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dataSourceName = getDataSourceName(attrs);
        this.dataSource = this.loadDataSource(this.dataSourceName);
        this.setupAutocomplete(context, attrs);
    }

    public AutoCompletePreference(Context context) {
        super(context);
        this.dataSourceName = null;
        this.dataSource = null;
        this.setupAutocomplete(context, null);
    }

    private void setupAutocomplete(Context context, AttributeSet attrs){
        this.autocompleteText = new AutoCompleteTextView(context, attrs);
        this.autocompleteText.setThreshold(0);
        if (this.dataSource != null){
            this.bindAutocompleteValues(context, this.dataSource);
        }
    }

    private void bindAutocompleteValues(Context context, Set<String> dataSource) {
        List<String> autocompleteValues = new ArrayList<>(dataSource);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, autocompleteValues);
        this.autocompleteText.setAdapter(adapter);
        this.setOnBindEditTextListener(new OnBindEditTextListener(this));
        this.setOnPreferenceChangeListener(new OnPreferenceChangeListener(this));
    }

    private String getDataSourceName(AttributeSet attrs) {
        String namespace = "http://schemas.android.com/apk/res-auto";
        String attribute = "dataSource";
        return attrs.getAttributeValue(namespace, attribute);
    }

    private Set<String> loadDataSource(String dataSourceName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return preferences.getStringSet(dataSourceName, new HashSet<>());
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        this.setSummary(text);
        if (this.dataSourceName == null || this.dataSource == null || this.dataSource.contains(text)){
            return;
        }
        this.dataSource.add(text);
        Context context = this.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(this.dataSourceName, this.dataSource);
        editor.apply();
        this.bindAutocompleteValues(context, this.dataSource);
    }
}