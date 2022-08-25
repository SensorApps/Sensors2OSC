package org.sensors2.osc.preferences;

import android.content.Context;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

public class ExtendedSwitchPreference extends SwitchPreference {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ExtendedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ExtendedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ExtendedSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedSwitchPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView textView = view.findViewById(android.R.id.title);
        if (textView != null) {
            textView.setSingleLine(false);
        }
    }
}