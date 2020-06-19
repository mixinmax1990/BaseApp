package com.example.baseapp;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

public class PreferenceFragment extends PreferenceFragmentCompat {

    MainActivity mainActivity;
    PreferenceScreen ps;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mainActivity = (MainActivity) getActivity();
        addPreferencesFromResource(R.xml.main_preferences);
        context = this.getContext();

        final SwitchPreference dark_mode = (SwitchPreference) findPreference("dark_mode");

        dark_mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (dark_mode.isChecked()) {
                    context.setTheme(R.style.AppThemeDark);
                    Toast.makeText(context, "Unchecked", Toast.LENGTH_SHORT).show();
                } else {
                    context.setTheme(R.style.AppThemeLight);
                    Toast.makeText(context, "Checked", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


    }


}
