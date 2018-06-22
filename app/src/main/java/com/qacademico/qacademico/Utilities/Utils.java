package com.qacademico.qacademico.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.qacademico.qacademico.R;

import java.util.Objects;
import java.util.Random;
import java.util.zip.Inflater;

public class Utils {
    public static final String HOME = ".Home";
    public static final String HORARIO = ".Horario";
    public static final String DIARIOS = ".Diarios";
    public static final String BOLETIM = ".Boletim";
    public static final String LOGIN = ".Login";
    public static final String LOGIN_VALID = ".Valido";
    public static final String LOGIN_NAME = ".Nome";
    public static final String LOGIN_INFO = ".Login_Info";
    public static final String LOGIN_REGISTRATION = ".Matricula";
    public static final String LOGIN_PASSWORD = ".Senha";
    public static final String LOGIN_DAY = ".Dia";
    public static final String LOGIN_HOUR = ".Hora";
    public static final String LOGIN_MINUTE = ".Minuto";

    public static String url, pg_login, pg_home, pg_diarios, pg_boletim, pg_horario, pg_materiais, pg_change_password,
            pg_erro, download_update_url, email_to, email_from, email_from_pwd;

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }
        return false;
    }

    /*
     * Método que atualiza os valores das variáveis
     */
    public static void updateDefaultValues(FirebaseRemoteConfig remoteConfig) {
        url = remoteConfig.getString("default_url");
        pg_login = remoteConfig.getString("pg_login");
        pg_home = remoteConfig.getString("pg_home");
        pg_diarios = remoteConfig.getString("pg_diarios");
        pg_boletim = remoteConfig.getString("pg_boletim");
        pg_horario = remoteConfig.getString("pg_horario");
        pg_materiais = remoteConfig.getString("pg_materiais");
        pg_change_password = remoteConfig.getString("pg_change_password");
        pg_erro = remoteConfig.getString("pg_erro");
        download_update_url = remoteConfig.getString("download_update_url");
        email_to = remoteConfig.getString("email_to");
        email_from = remoteConfig.getString("email_from");
        email_from_pwd = remoteConfig.getString("email_from_pass");
    }

    /*
    * Função que fornece um cabeçalho customizado para o AlertDialog
    */
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

    public static int getRandomColorGenerator(Context context) {
        Resources res = context.getResources();
        Random rnd = new Random();
        int color = rnd.nextInt(9);

        switch (color) {
            case 0: color = res.getColor(R.color.deep_orange_400);
            break;

            case 1: color = res.getColor(R.color.yellow_400);
            break;

            case 2: color = res.getColor(R.color.lime_400);
            break;

            case 3: color = res.getColor(R.color.light_green_400);
            break;

            case 4: color = res.getColor(R.color.teal_400);
            break;

            case 5: color = res.getColor(R.color.cyan_400);
            break;

            case 6: color = res.getColor(R.color.light_blue_400);
            break;

            case 7: color = res.getColor(R.color.indigo_400);
            break;

            case 8: color = res.getColor(R.color.dark_purple_400);
            break;
        }
        return color;
    }

    public static void showChangelog(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View theView = Objects.requireNonNull(inflater).inflate(R.layout.dialog_changelog, null);
        TextView changes = (TextView) theView.findViewById(R.id.changelog);
        changes.setText(context.getResources().getString(R.string.changelog_list));
        new AlertDialog.Builder(context).setView(theView)
                .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_history_black_24dp, R.string.action_changes, R.color.changes_dialog))
                .setPositiveButton(R.string.dialog_close, null)
                .show();
    }
}
