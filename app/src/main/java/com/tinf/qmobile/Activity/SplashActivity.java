package com.tinf.qmobile.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.Utilities.Jobs;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;

import androidx.appcompat.app.AppCompatActivity;
import io.fabric.sdk.android.Fabric;
import io.objectbox.BoxStore;

import static com.tinf.qmobile.Utilities.User.REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.VERSION_INFO;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    Log.d(TAG, task.getResult().getToken());
                });

        Fabric.with(this, new Crashlytics());

        Fabric.with(new Fabric.Builder(this)
                .kits(new CrashlyticsCore.Builder().build())
                .debuggable(true)
                .build());

        Crashlytics.setUserIdentifier(User.getCredential(REGISTRATION));

        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(Utils.VERSION, true)) {
            if (BoxStore.deleteAllFiles(getApplicationContext(), User.getCredential(REGISTRATION))) {
                getSharedPreferences(VERSION_INFO, MODE_PRIVATE).edit().putBoolean(Utils.VERSION, false).apply();
                User.clearInfos();
            }
        }

        Intent intent;

        if (User.isValid()) {
            Jobs.scheduleJob(false);
            if (!Client.get().isValid()) {
                Client.get().login();
            }
            ((App) getApplication()).setLogged(true);
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
