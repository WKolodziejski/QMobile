package com.qacademico.qacademico.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.Class.Horario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;

public class Data {
    public static final String YEAR = ".Year";
    public static final String PERIOD = ".Period";

    public static List<?> getList(Context context, String type, String year, String period) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectInputStream object;
        List<?> list = null;

        try {
            if (type.equals(Utils.DIARIOS)) {
                object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + type + "." + year.substring(0, 4) + "." + year.substring(8, 8))));
            } else {
                object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                        login_info.getString(Utils.LOGIN_REGISTRATION,
                                "") + type + "." + year + "." + period)));
            }
            list = (List<?>) object.readObject();
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
                                "") + type + "." + year.substring(0, 4) + "." + year.substring(8, 8))));
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

    public static void saveDate(Context context, String[] array, String wich, String type) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectOutputStream object;
        try {
            object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                    login_info.getString(Utils.LOGIN_REGISTRATION,
                            "") + wich + type)));
            object.writeObject(array);
            object.flush();
            object.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getDate(Context context, String wich, String type) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectInputStream object;
        String[] array = null;

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                    login_info.getString(Utils.LOGIN_REGISTRATION,
                            "") + wich + type)));

            array = (String[]) object.readObject();
            object.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return array;
    }
}
