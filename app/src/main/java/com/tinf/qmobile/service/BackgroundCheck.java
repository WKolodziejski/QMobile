package com.tinf.qmobile.service;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tinf.qmobile.App;
import com.tinf.qmobile.activity.settings.SplashActivity;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.Jobs;

public class BackgroundCheck extends JobService {
    private boolean errorOcurred;

    @Override
    public boolean onStartJob(JobParameters job) {

        Client.get().login();

        Client.get().addOnResponseListener(new OnResponse() {
            @Override
            public void onStart(int pg, int pos) {
                //TODO adicionar Log ou notificação pra DeBug
            }

            @Override
            public void onFinish(int pg, int pos) {

                if (pg == PG_LOGIN) {
                    Client.get().checkChanges(PG_DIARIOS);
                }

                if (pg == PG_DIARIOS) {
                    errorOcurred = false;
                    Client.get().removeOnResponseListener(this);
                    onStopJob(job);
                }
            }

            @Override
            public void onError(int pg, String error) {
                errorOcurred = true;
                Client.get().removeOnResponseListener(this);
                onStopJob(job);
            }

            @Override
            public void onAccessDenied(int pg, String message) {
                Jobs.displayNotification(getResources().getString(R.string.dialog_access_denied),
                        getResources().getString(R.string.dialog_check_login), getResources().getString(R.string.app_name), 0, new Intent(App.getContext(), SplashActivity.class));
                Client.get().removeOnResponseListener(this);
                onStopJob(job);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Jobs.scheduleJob(errorOcurred);
        return false;
    }
}
