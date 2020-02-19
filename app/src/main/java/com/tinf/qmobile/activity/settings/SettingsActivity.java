package com.tinf.qmobile.activity.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;

import com.tinf.qmobile.R;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;

public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String CHECK = "key_check";
    public static final String MOBILE = "key_mobile_data";
    public static final String ALERT = "key_alert_mode";
    public static final String NOTIFY = "key_notifications";
    public static final String NIGHT = "key_night_mode";
    public static final String SCHEDULE_HOUR = "schedule_first_hour";
    public static final String SCHEDULE_DAYS = "schedule_days";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.action_bar_default, (ViewGroup) findViewById(android.R.id.content));
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    @Override
    public void finish() {
        super.finish();
        Jobs.scheduleJob(false);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_main);

            bindPreferenceSummaryToValue(findPreference(CHECK));

            bindPreferenceSummaryToValue(findPreference(MOBILE));

            bindPreferenceSummaryToValue(findPreference(ALERT));

            bindPreferenceSummaryToValue(findPreference(NOTIFY));

            bindPreferenceSummaryToValue(findPreference(NIGHT));

            Preference about = findPreference("key_about");
            about.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return true;
            });

            Preference privacy = findPreference("key_privacy");
            privacy.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://sites.google.com/view/qmobileapp"));
                startActivity(intent);
                return true;
            });

            Preference night_mode = findPreference(NIGHT);
            night_mode.setOnPreferenceClickListener(preference -> {
                AppCompatDelegate.setDefaultNightMode(getPreferenceManager().getSharedPreferences().getBoolean(NIGHT, false) ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                return true;
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), true));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, newValue) -> {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
        }
        return true;
    };

}