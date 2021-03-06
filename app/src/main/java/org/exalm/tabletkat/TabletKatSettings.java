package org.exalm.tabletkat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.*;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.List;

public class TabletKatSettings extends PreferenceActivity {
    public static final String ACTION_PREFERENCE_CHANGED = "org.exalm.tabletkat.PREFERENCE_CHANGED";

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (!onIsMultiPane()) {
            setupSimplePreferencesScreen();
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    private void setupSimplePreferencesScreen() {
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_recents);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_recents);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_launcher);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_launcher);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_settings);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_settings);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_about);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_about);

        setUpPreferenceChangeListener(findPreference("enable_tablet_ui"));
        setUpPreferenceChangeListener(findPreference("extended_settings"));
        setUpPreferenceChangeListener(findPreference("ics_clock_font"));
        setUpPreferenceChangeListener(findPreference("battery_percents"));

        setUpPreferenceChangeListener(findPreference("enable_mod_recents"));
        setUpPreferenceChangeListener(findPreference("overlay_recents"));

        if (getString(R.string.pref_summary_translation).isEmpty()) {
            getPreferenceScreen().removePreference(findPreference("translation"));
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        if (!onIsMultiPane()) {
            return;
        }

        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Intent i = new Intent(ACTION_PREFERENCE_CHANGED);
            i.putExtra("key", preference.getKey());
            if (newValue instanceof Boolean){
                i.putExtra("boolValue", (Boolean) newValue);
            }
            if (newValue instanceof Integer){
                i.putExtra("intValue", (Integer) newValue);
            }
            i.putExtra("stringValue", "" + newValue);
            preference.getContext().sendBroadcast(i);
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static void setUpPreferenceChangeListener(Preference preference) {
        preference.setOnPreferenceChangeListener(sPreferenceChangeListener);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.pref_general);

            setUpPreferenceChangeListener(findPreference("enable_tablet_ui"));
            setUpPreferenceChangeListener(findPreference("extended_settings"));
            setUpPreferenceChangeListener(findPreference("ics_clock_font"));
            setUpPreferenceChangeListener(findPreference("battery_percents"));
        }
    }

    public static class RecentsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.pref_recents);

            setUpPreferenceChangeListener(findPreference("enable_mod_recents"));
            setUpPreferenceChangeListener(findPreference("overlay_recents"));
        }
    }

    public static class LauncherPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.pref_launcher);
        }
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.pref_settings);
        }
    }

    public static class AboutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.pref_about);

            if (getString(R.string.pref_summary_translation).isEmpty()) {
                getPreferenceScreen().removePreference(findPreference("translation"));
            }
        }
    }
}
