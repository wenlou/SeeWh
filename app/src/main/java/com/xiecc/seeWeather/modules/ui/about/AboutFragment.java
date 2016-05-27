package com.xiecc.seeWeather.modules.ui.about;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.xiecc.seeWeather.R;

/**
 * Created by hugo on 2016/2/20 0020.
 */
public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
}
