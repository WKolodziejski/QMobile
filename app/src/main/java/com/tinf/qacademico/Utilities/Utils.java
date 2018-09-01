package com.tinf.qacademico.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;
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
    public static final String MATERIAS = ".Materias";
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

            case 1: color = res.getColor(R.color.yellow_A700);
            break;

            case 2: color = res.getColor(R.color.lime_A700);
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

    public static void showChangeDateDialog(Activity activity) {

        SingletonWebView webView = SingletonWebView.getInstance();

        View theView = activity.getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

        final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);

        year.setMinValue(0);
        year.setMaxValue(webView.infos.data_boletim.length - 1);
        year.setValue(webView.data_position_boletim);
        year.setDisplayedValues(webView.infos.data_boletim);
        year.setWrapSelectorWheel(false);

        final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
        periodo.setMinValue(0);
        periodo.setMaxValue(webView.infos.periodo_boletim.length - 1);
        periodo.setValue(webView.periodo_position_boletim);
        periodo.setDisplayedValues(webView.infos.periodo_boletim);
        periodo.setWrapSelectorWheel(false);

        new AlertDialog.Builder(activity.getApplicationContext()).setView(theView)
                .setCustomTitle(Utils.customAlertTitle(Objects.requireNonNull(activity.getApplicationContext()), R.drawable.ic_date_range_black_24dp,
                        R.string.dialog_date_change, R.color.colorPrimary))
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                    webView.data_position_boletim = year.getValue();
                    webView.periodo_position_boletim = periodo.getValue();





                    if (Utils.isConnected(activity.getApplicationContext())) {
                        webView.data_position_boletim = year.getValue();
                        webView.periodo_position_boletim = periodo.getValue();

                        if (webView.data_position_boletim == Integer.parseInt(webView.infos.data_boletim[0])) {

                            webView.loadUrl(URL + PG_BOLETIM);

                        } else {
                            webView.loadUrl(URL + PG_BOLETIM + "&COD_MATRICULA=-1&cmbanos="
                                    + webView.infos.data_boletim[webView.data_position_boletim]
                                    + "&cmbperiodos=1&Exibir+Boletim");
                        }
                    }






                }).setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
}
