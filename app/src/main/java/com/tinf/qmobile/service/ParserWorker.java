package com.tinf.qmobile.service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.tinf.qmobile.App;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SplashActivity;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;

public class ParserWorker extends Worker {
    private static final String TAG = "ParserWorker";
    private boolean messagesLoaded;
    private boolean scheduleLoaded;
    private boolean materialsLoaded;

    public ParserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d(TAG, "Work created");
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting work...");

        if (!Client.isConnected())
            return Result.failure();

        Client.background = true;

        Client.get().addOnResponseListener(new OnResponse() {

            @Override
            public void onStart(int pg) {
                //if (BuildConfig.DEBUG)
                    //Works.displayNotification("Debug", "Background check starting", -1, 0, new Intent(App.getContext(), SplashActivity.class));
            }

            @Override
            public void onFinish(int pg) {

                if (pg == PG_LOGIN)
                    Client.get().checkChanges();

                if (pg == PG_SCHEDULE)
                    scheduleLoaded = true;

                if (pg == PG_MESSAGES)
                    messagesLoaded = true;

                if (pg == PG_MATERIALS)
                    materialsLoaded = true;

                if (messagesLoaded && scheduleLoaded && materialsLoaded)
                    Client.get().removeOnResponseListener(this);
            }

            @Override
            public void onError(int pg, String error) {
                Client.get().removeOnResponseListener(this);
            }

            @Override
            public void onAccessDenied(int pg, String message) {
                Works.displayNotification(getApplicationContext().getResources().getString(R.string.dialog_access_denied),
                        getApplicationContext().getResources().getString(R.string.dialog_check_login),
                        10,
                        0, new Intent(App.getContext(), SplashActivity.class));
                Client.get().removeOnResponseListener(this);
            }
        });

        Client.get().login();

        Log.d(TAG, "Work stopped");
        Works.schedule();

        return Result.success();
    }

}
