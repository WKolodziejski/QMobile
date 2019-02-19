package com.tinf.qmobile.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.R;
import java.util.Objects;
import java.util.Random;

public class Utils {
    public static final int UPDATE_REQUEST = 0;
    public static final String VERSION = ".v1.0.0-r10";
    public static final String VERSION_INFO = ".Version";

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
        int color = new Random().nextInt(9);

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
        return color;//App.getContext().getResources().getColor(color);
    }

    public static int pickColor(String string) {
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
            Materia materia = App.getBox().boxFor(Materia.class).query().equal(Materia_.name, string).build().findFirst();

            if (materia != null) {
                color = materia.getColor();
            }

            if (color == 0) {
                color = Utils.getRandomColorGenerator();
            }
        }
        return color;
    }
}
