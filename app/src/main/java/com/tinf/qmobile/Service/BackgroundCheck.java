package com.tinf.qmobile.Service;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Interfaces.Network.OnResponse;
import com.tinf.qmobile.Network.NetworkSingleton;
import com.tinf.qmobile.Parsers.DiariosParser;
import com.tinf.qmobile.Utilities.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.objectbox.BoxStore;

import static com.tinf.qmobile.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class BackgroundCheck extends JobService {
    private boolean shoulRetry = true;

    @Override
    public boolean onStartJob(JobParameters job) {

        NetworkSingleton.getInstance(getApplicationContext()).setOnResponseListener(new OnResponse() {
            @Override
            public void OnFinish(String url, String response1) {

                Document accessPage = Jsoup.parse(response1);
                String msg = accessPage.getElementsByTag("strong").first().text().trim();

                if (msg.contains("Negado")) {
                    Utils.displayNotification(getApplicationContext(), "Finished", "Acesso Negado", "Test", 0, null);
                    onStopJob(job);
                } else {
                    NetworkSingleton.getInstance(getApplicationContext()).createRequest(URL + PG_DIARIOS,
                            response2 -> {
                                new DiariosParser(getApplicationContext(), response2, Integer.valueOf(NetworkSingleton.getYears(getApplicationContext())[0]),
                                        ((App) getApplication()).getBoxStore(), new OnResponse() {
                                    @Override
                                    public void OnFinish(String url, String response) {
                                        Utils.displayNotification(getApplicationContext(), "Finished", "No grades available", "Test", 4, null);
                                        shoulRetry = false;
                                        onStopJob(job);
                                    }

                                    @Override
                                    public void OnError(String url, VolleyError error) {
                                        Utils.displayNotification(getApplicationContext(), "Volley Error", error.getCause().getMessage(), "Test", 1, null);
                                        onStopJob(job);
                                    }
                                }).execute();

                            }, error -> {
                                Utils.displayNotification(getApplicationContext(), "Volley Error", error.getCause().getMessage(), "Test", 2, null);
                                onStopJob(job);
                            });
                }
            }

            @Override
            public void OnError(String url, VolleyError error) {
                Utils.displayNotification(getApplicationContext(), "Volley Error", error.getMessage(), "Test", 3, null);
                onStopJob(job);
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Utils.scheduleJob(getApplicationContext());
        return shoulRetry;
    }
}
