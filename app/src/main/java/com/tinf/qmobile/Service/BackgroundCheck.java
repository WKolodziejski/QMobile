package com.tinf.qmobile.Service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.VolleyError;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Interfaces.Network.OnResponse;
import com.tinf.qmobile.Network.NetworkSingleton;
import com.tinf.qmobile.Parsers.DiariosParser;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static com.tinf.qmobile.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class BackgroundCheck extends JobService {
    private boolean errorOcurred;

    @Override
    public boolean onStartJob(JobParameters job) {

        NetworkSingleton.getInstance(getApplicationContext()).setOnResponseListener(new OnResponse() {
            @Override
            public void OnFinish(String url, String response1) {

                Document accessPage = Jsoup.parse(response1);
                String msg = accessPage.getElementsByTag("strong").first().text().trim();

                if (msg.contains("Negado")) {
                    errorOcurred = false;
                    Utils.displayNotification(getApplicationContext(), getResources().getString(R.string.dialog_access_denied),
                            getResources().getString(R.string.dialog_check_login), getResources().getString(R.string.app_name), 0, null);
                    onStopJob(job);
                } else {
                    NetworkSingleton.getInstance(getApplicationContext()).createRequest(URL + PG_DIARIOS,
                            response2 -> {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                new DiariosParser(getApplicationContext(), response2, Integer.valueOf(NetworkSingleton.getYears(getApplicationContext())[0]),
                                        prefs.getBoolean("key_notifications", true), ((App) getApplication()).getBoxStore(), new OnResponse() {
                                    @Override
                                    public void OnFinish(String url, String response) {
                                        errorOcurred = false;
                                        onStopJob(job);
                                    }

                                    @Override
                                    public void OnError(String url, VolleyError error) {
                                        errorOcurred = true;
                                        onStopJob(job);
                                    }
                                }).execute();

                            }, error -> {
                                errorOcurred = true;
                                onStopJob(job);
                            });
                }
            }

            @Override
            public void OnError(String url, VolleyError error) {
                errorOcurred = true;
                onStopJob(job);
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Utils.scheduleJob(getApplicationContext(), errorOcurred);
        return false;
    }
}
