package dltoy.calpoly.edu.movierecs.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import dltoy.calpoly.edu.movierecs.Constants;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    ListPreference themeChoice;
    private String[] curThemes;
    private SharedPreferences pref;
    private int themeNdx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);

        curThemes = getResources().getStringArray(R.array.theme_options);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        themeNdx = pref.getInt(Constants.THEME_KEY, 0);

        themeChoice = (ListPreference) getPreferenceManager().findPreference(getString(R.string.style_key));
        themeChoice.setTitle(getString(R.string.style_title) + getTheme());
        themeChoice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                themeNdx = Integer.parseInt(o.toString());
                SharedPreferences.Editor e = pref.edit();
                e.putInt(Constants.THEME_KEY, themeNdx);
                e.commit();
                getActivity().recreate();
                return true;
            }
        });
    }

    private String getTheme() {

        return " " + curThemes[themeNdx > 0 ? themeNdx - 1 : 0];
    }
}
