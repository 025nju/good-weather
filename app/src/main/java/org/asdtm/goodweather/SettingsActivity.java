package org.asdtm.goodweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.asdtm.goodweather.service.NotificationService;
import org.asdtm.goodweather.utils.Constants;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    public boolean hasHeaders() {
        return super.hasHeaders();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || WidgetPreferenceFragment.class.getName().equals(fragmentName)
                || AboutPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            final SwitchPreference notificationSwitch = (SwitchPreference) findPreference(
                    Constants.KEY_PREF_IS_NOTIFICATION_ENABLED);
            notificationSwitch.setOnPreferenceChangeListener(notificationListener);

        }

        Preference.OnPreferenceChangeListener notificationListener =
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        boolean isEnabled = (boolean) o;
                        NotificationService.setNotificationServiceAlarm(getActivity(),
                                                                        isEnabled);
                        return true;
                    }
                };


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case Constants.KEY_PREF_TEMPERATURE:
                    setSummary();
                    getActivity().sendBroadcast(
                            new Intent(Constants.ACTION_FORCED_APPWIDGET_UPDATE));
                    break;
                case Constants.KEY_PREF_INTERVAL_NOTIFICATION:
                    Preference pref = findPreference(key);
                    NotificationService.setNotificationServiceAlarm(getActivity(),
                                                                    pref.isEnabled());
                    setSummary();
                    break;
            }
        }

        private void setSummary() {
            Preference temperaturePref = findPreference(Constants.KEY_PREF_TEMPERATURE);
            ListPreference temperatureListPref = (ListPreference) temperaturePref;
            temperaturePref.setSummary(temperatureListPref.getEntry());

            Preference notificationIntervalPref = findPreference(
                    Constants.KEY_PREF_INTERVAL_NOTIFICATION);
            ListPreference notificationIntervalListPref = (ListPreference) notificationIntervalPref;
            notificationIntervalPref.setSummary(notificationIntervalListPref.getEntry());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                                 .registerOnSharedPreferenceChangeListener(this);
            setSummary();
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                                 .unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    public static class WidgetPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_widget);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case Constants.KEY_PREF_WIDGET_LIGHT_THEME:
                    Intent intent = new Intent(Constants.ACTION_APPWIDGET_THEME_CHANGED);
                    getActivity().sendBroadcast(intent);
                    break;
                case Constants.KEY_PREF_WIDGET_UPDATE_PERIOD:
                    Intent intent1 = new Intent(Constants.ACTION_APPWIDGET_UPDATE_PERIOD_CHANGED);
                    getActivity().sendBroadcast(intent1);
                    setSummary();
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                                 .registerOnSharedPreferenceChangeListener(this);
            setSummary();
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                                 .unregisterOnSharedPreferenceChangeListener(this);
        }

        private void setSummary() {
            Preference updatePeriodPref = findPreference(Constants.KEY_PREF_WIDGET_UPDATE_PERIOD);
            ListPreference updatePeriodListPref = (ListPreference) updatePeriodPref;
            updatePeriodPref.setSummary(updatePeriodListPref.getEntry());
        }
    }

    public static class AboutPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                                 .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                                 .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}