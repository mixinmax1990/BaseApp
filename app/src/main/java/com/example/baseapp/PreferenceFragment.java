package com.example.baseapp;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class PreferenceFragment extends PreferenceFragmentCompat {

    MainActivity mainActivity;
    PreferenceScreen ps;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mainActivity = (MainActivity) getActivity();
        addPreferencesFromResource(R.xml.main_preferences);
        ps = getPreferenceScreen();
        ps.setTitle("Preferences");
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }




}
