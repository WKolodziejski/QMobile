package com.tinf.qmobile.Utilities;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.R;
import java.util.Calendar;
import java.util.Random;

public class Utils {
    public static final int UPDATE_REQUEST = 0;
    public static final String VERSION = ".v1.0.2-r12";
    public static final String VERSION_INFO = ".Version";

    private static int getRandomColorGenerator() {
        int color = new Random().nextInt(9);

        switch (color) {
            case 0: color = R.color.deep_orange_500;
                break;

            case 1: color = R.color.yellow_a700;
                break;

            case 2: color = R.color.lime_a700;
                break;

            case 3: color = R.color.light_green_500;
                break;

            case 4: color = R.color.teal_500;
                break;

            case 5: color = R.color.cyan_500;
                break;

            case 6: color = R.color.light_blue_500;
                break;

            case 7: color = R.color.indigo_500;
                break;

            case 8: color = R.color.deep_purple_500;
                break;
        }
        return App.getContext().getResources().getColor(color);
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
            Matter matter = App.getBox().boxFor(Matter.class).query().equal(Matter_.title, string).build().findFirst();

            if (matter != null) {
                color = matter.getColor();
            }

            if (color == 0) {
                color = getRandomColorGenerator();
            }

            return color;
        }
        return App.getContext().getResources().getColor(color);
    }

    public static long getDate(String date, boolean isMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, isMonth ? 0 : 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.MONTH, getMonth(date) - 1);
        cal.set(Calendar.DAY_OF_MONTH, getDay(date));
        return cal.getTimeInMillis();
    }

    private static int getDay(String date) {
        return Integer.parseInt(date.substring(0, date.indexOf("/")));
    }

    private static int getMonth(String date) {
        return Integer.parseInt(date.substring(date.indexOf("/") + 1, date.lastIndexOf("/")));
    }

    private static int getYear(String date) {
        return Integer.parseInt(date.substring(date.lastIndexOf("/") + 1));
    }

}
