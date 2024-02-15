package com.tinf.qmobile.service;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.NOTIFY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SplashActivity;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.NotificationUtils;

public class ParserWorker extends Worker {
  private static final String TAG = "ParserWorker";

  public ParserWorker(
      @NonNull
      Context context,
      @NonNull
      WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    Bundle bundle = new Bundle();

    Log.d(TAG, "Starting work...");

    if (!Client.isConnected())
      return Result.failure();

    try {
      if (Tasks.await(checkChanges())) {
        NotificationUtils.debug("Background check finished");
        bundle.putString("Result", "Success");
      } else {
        NotificationUtils.debug("Background failed");
        bundle.putString("Result", "Fail");
      }
    } catch (Exception e) {
      e.printStackTrace();
      NotificationUtils.debug("Background crashed");
      bundle.putString("Result", "Crash");
      bundle.putString("Exception", e.toString());
    }

    FirebaseAnalytics.getInstance(getContext()).logEvent("Parser", bundle);

    Log.d(TAG, "Work stopped");
    Works.scheduleParser();

    return Result.success();
  }

  public Task<Boolean> checkChanges() {
    Log.d(TAG, "Checking...");

    TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    boolean notify = prefs.getBoolean(NOTIFY, true);
    Client.background = true;

    OnResponse onResponse = new OnResponse() {
      private boolean journals = false;
      private boolean report = false;
      private boolean schedule = false;
      private boolean messages = false;
      private boolean materials = false;

      @Override
      public void onStart(int pg) {
        NotificationUtils.debug("Background check starting");
      }

      @Override
      public void onFinish(int pg, int year, int period) {
        if (pg == PG_LOGIN)
          Client.get().load(PG_JOURNALS, notify);

        else if (pg == PG_JOURNALS) {
          journals = true;
          Client.get().load(PG_REPORT, notify);
          Client.get().load(PG_MESSAGES, notify);
          Client.get().load(PG_MATERIALS, notify);

        } else if (pg == PG_REPORT) {
          report = true;
          Client.get().load(PG_SCHEDULE, notify);

        } else if (pg == PG_SCHEDULE)
          schedule = true;

        else if (pg == PG_MESSAGES)
          messages = true;

        else if (pg == PG_MATERIALS)
          materials = true;

        else
          finish(false);

        if (journals && report && materials && schedule && messages)
          finish(true);
      }

      @Override
      public void onError(int pg, String error) {
        finish(false);
      }

      @Override
      public void onAccessDenied(int pg, String message) {
        NotificationUtils.show(
            App.getContext().getResources().getString(R.string.dialog_access_denied),
            App.getContext().getResources().getString(R.string.dialog_check_login),
            10, 0, new Intent(App.getContext(), SplashActivity.class));
        finish(false);
      }

      private void finish(boolean result) {
        Client.background = false;
        Client.get().removeOnResponseListener(this);
        tcs.setResult(result);
        Log.d(TAG, "Checking finished " + (result ? "successfully" : "with fail"));
      }
    };

    Client.get().addOnResponseListener(onResponse);
    Client.get().login();

    return tcs.getTask();
  }

}
