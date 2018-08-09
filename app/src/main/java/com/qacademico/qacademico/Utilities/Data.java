package com.qacademico.qacademico.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.qacademico.qacademico.Class.Infos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Data {
    public static final String YEAR = ".Year";
    public static final String PERIOD = ".Period";
    public static final String DATE = ".Date";

    public static List<?> loadList(Context context, String type, String year, String period) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectInputStream object;
        List<?> list = null;

        try {
            if (type.equals(Utils.DIARIOS)) {
                object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + type + "." + trim(year))));
            } else {
                object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + type + "." + year + "." + period)));
            }
            list = (List<?>) object.readObject();
            Log.i(type, "Lido");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveList(Context context, Object obj, String type, String year, String period) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectOutputStream object;
        try {
            if (type.equals(Utils.DIARIOS)) {
                object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + type + "." + trim(year))));
            } else {
                object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + type + "." + year + "." + period)));
            }
            object.writeObject(obj);
            object.flush();
            object.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveDate(Context context, Object obj) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectOutputStream object;
        try {
                object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + DATE)));

            object.writeObject(obj);
            object.flush();
            object.close();
            Log.i("DATA", "Salvo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Infos loadDate(Context context) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectInputStream object;
        Infos infos = null;

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + DATE)));

            infos = (Infos) object.readObject();
            Log.i("DATA", "Lido");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return infos;
    }

    private static String trim(String string) {
        string = string.replace(" / ", ".");
        Log.i("DATA", string);
        return string;
    }
}
