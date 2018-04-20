package com.qacademico.qacademico.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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

public class Utils {
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
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
        View theTitle = inflater.inflate(R.layout.dialog_title, null);
        ImageView title_img = (ImageView) theTitle.findViewById(R.id.dialog_img);
        TextView title_txt = (TextView) theTitle.findViewById(R.id.dialog_txt);
        LinearLayout title_bckg = (LinearLayout) theTitle.findViewById(R.id.dialog_bckg);
        title_img.setImageResource(img);
        title_bckg.setBackgroundColor(context.getResources().getColor(color));
        title_txt.setText(txt);
        return theTitle;
    }
}
