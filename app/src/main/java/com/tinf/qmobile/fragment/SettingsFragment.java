package com.tinf.qmobile.fragment;

import static com.tinf.qmobile.network.Client.pos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.AboutActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.network.Client;

public class SettingsFragment extends PreferenceFragmentCompat {
  public static final String CHECK = "key_check";
  public static final String MOBILE = "key_mobile_data";
  public static final String ALERT = "key_alert_mode";
  public static final String NOTIFY = "key_notifications";
  public static final String NIGHT = "key_night_mode";
  public static final String POPUP = "key_popup";
  public static final String DATA = "key_share_data";
  public static final String DB = "key_reset_db";
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

    bindPreferenceSummaryToValue(findPreference(POPUP));

    Preference reset = findPreference(DB);
    reset.setOnPreferenceClickListener(preference -> {
      new MaterialAlertDialogBuilder(requireActivity())
          .setTitle(getResources().getString(R.string.dialog_clear_data_title))
          .setMessage(getResources().getString(R.string.dialog_clear_data_text))
          .setCancelable(true)
          .setPositiveButton(getResources().getString(R.string.dialog_clear_data_delete),
                             (dialogInterface, i) -> {
                               Toast.makeText(getContext(), getResources().getString(
                                   R.string.toast_reset_db_success), Toast.LENGTH_SHORT).show();

                               DataBase.get().getBoxStore().removeAllObjects();
                               Client.get().changeDate(pos);
                             })
          .setNeutralButton(getResources().getString(R.string.dialog_clear_data_cancel), null)
          .create()
          .show();

      return true;
    });

    Preference about = findPreference("key_about");
    about.setOnPreferenceClickListener(preference -> {
      startActivity(new Intent(requireActivity(), AboutActivity.class));
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
      AppCompatDelegate.setDefaultNightMode(
          getPreferenceManager().getSharedPreferences().getBoolean(NIGHT, false) ?
          AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
      return true;
    });

        /*Preference db = findPreference(DB);
        db.setOnPreferenceClickListener(preference -> {
            DataBase.get().close();
            return BoxStore.deleteAllFiles(getContext(), User.getCredential(REGISTRATION));
        });*/
  }

  private void bindPreferenceSummaryToValue(Preference preference) {
    preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                                                             PreferenceManager
                                                                 .getDefaultSharedPreferences(
                                                                     preference.getContext())
                                                                 .getBoolean(preference.getKey(),
                                                                             true));
  }

  private final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
      (preference, newValue) -> {
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
