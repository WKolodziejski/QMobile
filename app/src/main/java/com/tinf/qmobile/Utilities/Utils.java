package com.tinf.qmobile.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Activity.MateriaActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Service.BackgroundCheck;

import java.util.Objects;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import io.objectbox.BoxStore;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Utils {
    public static final String CALENDARIO = ".Calendario";
    public static final String MATERIAS = ".Materias";
    public static final String YEARS = ".Years";
    public static final String ETAPAS = ".Etapas";
    public static final String HORARIO = ".Horario";
    public static final String URL = "http://qacademico.ifsul.edu.br//qacademico/index.asp?t=";
    public static final String UPDATE_REQUEST = "UPD";
    public static final String VERSION = ".v1.0.0-r7";
    public static final int PG_LOGIN = 1001;
    public static final int PG_HOME = 2000;
    public static final int PG_DIARIOS = 2071;
    public static final int PG_BOLETIM = 2032;
    public static final int PG_HORARIO = 2010;
    public static final int PG_MATERIAIS = 2061;
    public static final int PG_CALENDARIO = 2020;
    public static final int PG_ERRO = 1;
    public static final int PG_ACESSO_NEGADO = 3;

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) App.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }
        return false;
    }

    public static View customAlertTitle(Context context, int img, int txt, int color) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View theTitle = Objects.requireNonNull(inflater).inflate(R.layout.dialog_title, null);
        ImageView title_img = (ImageView) theTitle.findViewById(R.id.dialog_img);
        TextView title_txt = (TextView) theTitle.findViewById(R.id.dialog_txt);
        LinearLayout title_bckg = (LinearLayout) theTitle.findViewById(R.id.dialog_bckg);
        title_img.setImageResource(img);
        title_bckg.setBackgroundColor(context.getResources().getColor(color));
        title_txt.setText(txt);
        return theTitle;
    }

    public static int getRandomColorGenerator() {
        Random rnd = new Random();
        int color = rnd.nextInt(9);

        switch (color) {
            case 0: color = R.color.deep_orange_400;
            break;

            case 1: color = R.color.yellow_A700;
            break;

            case 2: color = R.color.lime_A700;
            break;

            case 3: color = R.color.light_green_400;
            break;

            case 4: color = R.color.teal_400;
            break;

            case 5: color = R.color.cyan_400;
            break;

            case 6: color = R.color.light_blue_400;
            break;

            case 7: color = R.color.indigo_400;
            break;

            case 8: color = R.color.dark_purple_400;
            break;
        }
        return color;
    }

    public static int pickColor(String string, BoxStore boxStore){
        int color = 0;

        if (string.contains("Biologia")) {
            color = R.color.biologia;
        } else if (string.contains("Educação Física")) {
            color = R.color.edFisica;
        } else if (string.contains("Filosofia")) {
            color = R.color.filosofia;
        } else if (string.contains("Física")) {
            color = R.color.fisica;
        } else if (string.contains("Geografia")) {
            color = R.color.geografia;
        } else if (string.contains("História")) {
            color = R.color.historia;
        } else if (string.contains("Portugu")) {
            color = R.color.portugues;
        } else if (string.contains("Matemática")) {
            color = R.color.matematica;
        } else if (string.contains("Química")) {
            color = R.color.quimica;
        } else if (string.contains("Sociologia")) {
            color = R.color.sociologia;
        } else {
            Materia materia = boxStore.boxFor(Materia.class).query().equal(Materia_.name, string).build().findFirst();

            if (materia != null) {
                color = materia.getColor();
            }

            if (color == 0) {
                color = Utils.getRandomColorGenerator();
            }
        }
        return color;
    }

    public static String trimp(String string) {
        string = string.substring(string.indexOf(":"));
        string = string.replace(":", "");
        return string;
    }

    public static String trim1(String string) {
        string = string.substring(string.indexOf(", ") + 2);
        return string;
    }

    public static String trimb(String string) {
        string = string.substring(0, 4);
        return string;
    }

    public static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getBoolean("key_check_diarios", true)) {
            Job.Builder diarios = dispatcher.newJobBuilder()
                    .setService(BackgroundCheck.class)
                    .setTag("Diarios")
                    .setRecurring(false)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setConstraints(Constraint.ON_UNMETERED_NETWORK);

            if (prefs.getBoolean("key_mobile_data", false)) {
                diarios.addConstraint(Constraint.ON_ANY_NETWORK);
            }

            if (prefs.getBoolean("key_alert_mode", false)) {
                diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(3), (int) HOURS.toSeconds(5)));
                Log.i("JobScheduler", "Alert mode");
            } else {
                diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(20), (int) HOURS.toSeconds(24)));
                Log.i("JobScheduler", "Normal mode");
            }
            dispatcher.cancelAll();
            dispatcher.schedule(diarios.build());
            Log.i("JobScheduler", "Job scheduled");

        } else {
            dispatcher.cancelAll();
            Log.i("JobScheduler", "All jobs cancelled");
        }
    }

    public static void cancellAllJobs(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancelAll();
    }

    public static void displayNotification(Context context, String title, String txt, String channelID, int id, Bundle extras) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(txt);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(channelID);

        mBuilder.setSmallIcon(R.drawable.icon_launcher_grey);
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(txt);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(new NotificationChannel(channelID, channelID,
                    NotificationManager.IMPORTANCE_DEFAULT));
            mBuilder.setChannelId(channelID);
        }

        mBuilder.setAutoCancel(true);

        Intent resultIntent = extras == null ? new Intent(context, MainActivity.class) : new Intent(context, MateriaActivity.class).putExtras(extras);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MateriaActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mBuilder.build());
        }
    }
}
