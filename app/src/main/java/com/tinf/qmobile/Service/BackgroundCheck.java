package com.tinf.qmobile.Service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Jobs;

import static com.tinf.qmobile.Network.Client.PG_DIARIOS;
import static com.tinf.qmobile.Network.Client.PG_LOGIN;

public class BackgroundCheck extends JobService {
    private boolean errorOcurred;

    @Override
    public boolean onStartJob(JobParameters job) {

        Client.get().login();

        Client.get().setOnResponseListener(new OnResponse() {
            @Override
            public void onStart(String url, int year) {
                //TODO adicionar Log ou notificação pra DeBug
            }

            @Override
            public void onFinish(int pg, int year) {

                if (pg == PG_LOGIN) {
                    Client.get().checkChanges(PG_DIARIOS);
                }

                if (pg == PG_DIARIOS) {
                    errorOcurred = false;
                    onStopJob(job);
                }
            }

            @Override
            public void onError(int pg, String error) {
                errorOcurred = true;
                onStopJob(job);
            }

            @Override
            public void onAccessDenied(int pg, String message) {
                Jobs.displayNotification(getResources().getString(R.string.dialog_access_denied),
                        getResources().getString(R.string.dialog_check_login), getResources().getString(R.string.app_name), 0, null);
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
