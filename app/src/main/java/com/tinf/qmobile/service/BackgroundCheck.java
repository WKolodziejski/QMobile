package com.tinf.qmobile.service;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SplashActivity;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;

public class BackgroundCheck extends JobService {
    private boolean errorOccurred;

    @Override
    public boolean onStartJob(JobParameters job) {

        Client.get().login();

        Client.get().addOnResponseListener(new OnResponse() {
            @Override
            public void onStart(int pg, int pos) {
                /*if (BuildConfig.DEBUG) {
                    Jobs.displayNotification(getApplicationContext(), "Debug", "Verificando notas", "Debug", 0, new Intent(App.getContext(), SplashActivity.class));
                }*/
            }

            @Override
            public void onFinish(int pg, int pos) {

                if (pg == PG_LOGIN) {
                    Client.get().loadYear(0);
                }

                if (pg == PG_SCHEDULE) {
                    errorOccurred = false;
                    Client.get().removeOnResponseListener(this);
                    onStopJob(job);
                }
            }

            @Override
            public void onError(int pg, String error) {
                errorOccurred = true;
                Client.get().removeOnResponseListener(this);
                onStopJob(job);
            }

            @Override
            public void onAccessDenied(int pg, String message) {
                Jobs.displayNotification(getApplicationContext(), getResources().getString(R.string.dialog_access_denied),
                        getResources().getString(R.string.dialog_check_login), getResources().getString(R.string.app_name), 0, new Intent(App.getContext(), SplashActivity.class));
                Client.get().removeOnResponseListener(this);
                onStopJob(job);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Jobs.scheduleJob(errorOccurred);
        return false;
    }

}
