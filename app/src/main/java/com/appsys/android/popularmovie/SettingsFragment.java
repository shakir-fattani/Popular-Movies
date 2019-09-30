package com.appsys.android.popularmovie;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Created by shakir on 8/7/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);
    }
}
