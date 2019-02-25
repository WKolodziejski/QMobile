package com.tinf.qmobile.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tinf.qmobile.App;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.Utilities.Jobs;
import com.tinf.qmobile.Utilities.User;
import androidx.appcompat.app.AppCompatActivity;
import io.fabric.sdk.android.Fabric;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BuildConfig.DEBUG) {

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

            Log.d(TAG, "Release version");

        } else {
            Log.d(TAG, "Debug version");
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
