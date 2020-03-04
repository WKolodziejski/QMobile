package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.AboutActivity;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String CHECK = "key_check";
    public static final String MOBILE = "key_mobile_data";
    public static final String ALERT = "key_alert_mode";
    public static final String NOTIFY = "key_notifications";
    public static final String NIGHT = "key_night_mode";
    public static final String POPUP = "key_popup";
    public static final String SCHEDULE_HOUR = "schedule_first_hour";
    public static final String SCHEDULE_DAYS = "schedule_days";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

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

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), true));
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, newValue) -> {
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
