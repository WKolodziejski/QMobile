package com.qacademico.qacademico.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qacademico.qacademico.R;
import java.util.Objects;
import java.util.Random;

public class Utils {
    public static final String HOME = ".Home";
    public static final String HORARIO = ".Horario";
    public static final String BOLETIM = ".Boletim";
    public static final String DIARIOS = ".Diarios";
    public static final String CALENDARIO = ".Calendario";
    public static final String ORGANIZACAO = ".Organizacao";
    public static final String MATERIAIS = ".Materiais";
    public static final String NOTAS = ".Notas";
    public static final String LOGIN = ".Login";
    public static final String LOGIN_VALID = ".Valido";
    public static final String LOGIN_NAME = ".Nome";
    public static final String LOGIN_INFO = ".Login_Info";
    public static final String LOGIN_REGISTRATION = ".Matricula";
    public static final String LOGIN_PASSWORD = ".Senha";
    public static final String LOGIN_DAY = ".Dia";
    public static final String LOGIN_HOUR = ".Hora";
    public static final String LOGIN_MINUTE = ".Minuto";
    public static final String FIRST_LOGIN = ".FirstLogin";
    public static final String EXPANDABLE_LIST = ".ExpandableList";
    public static final String URL = "http://qacademico.ifsul.edu.br/qacademico/index.asp?t=";
    public static final String PG_LOGIN = "1001";
    public static final String PG_HOME = "2000";
    public static final String PG_DIARIOS = "2071";
    public static final String PG_BOLETIM = "2032";
    public static final String PG_HORARIO = "2010";
    public static final String PG_MATERIAIS = "2061";
    public static final String PG_CALENDARIO = "2020";
    public static final String pg_change_password = "1011";
    public static final String PG_ERRO = "1";
    public static final String download_update_url = "http://www.geocities.ws/tinfqacadmob/Qacademico/";
    public static final String email_to = "tinf703@gmail.com";
    public static final String email_from = "qacadmobapp@gmail.com";
    public static final String email_from_pwd = "3N7D66GP8";

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }
        return false;
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
