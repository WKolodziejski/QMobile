package com.tinf.qmobile.Utilities;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.R;
import java.util.Objects;
import java.util.Random;

public class Utils {
    public static final String CALENDARIO = ".Calendario";
    public static final String MATERIAS = ".Materias";
    public static final String LOGIN_VALID = ".Valido";
    public static final String LOGIN_NAME = ".Nome";
    public static final String LOGIN_INFO = ".Login_Info";
    public static final String YEARS = ".Years";
    public static final String ETAPAS = ".Etapas";
    public static final String HORARIO = ".Horario";
    public static final String LAST_LOGIN = ".Last_Login";
    public static final String LOGIN_REGISTRATION = ".Matricula";
    public static final String LOGIN_PASSWORD = ".Senha";
    public static final String URL = "http://qacademico.ifsul.edu.br//qacademico/index.asp?t=";
    public static final String UPDATE_REQUEST = "UPD";
    public static final String PG_LOGIN = "1001";
    public static final String PG_HOME = "2000";
    public static final String PG_DIARIOS = "2071";
    public static final String PG_BOLETIM = "2032";
    public static final String PG_HORARIO = "2010";
    public static final String PG_MATERIAIS = "2061";
    public static final String PG_CALENDARIO = "2020";
    public static final String PG_ERRO = "1";

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
}
