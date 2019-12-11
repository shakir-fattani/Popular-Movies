package com.appsys.android.popularmovie

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.shakirfattani.course.movielisting.R

/**
 * Created by shakir on 8/7/2017.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        addPreferencesFromResource(R.xml.pref_visualizer)
    }
}