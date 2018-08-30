package com.tinf.qacademico.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tinf.qacademico.Class.Infos;
import com.tinf.qacademico.Class.Materia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Data {
    private static final String DATE = ".Date";

    public static List<?> loadList(Context context, String type, String year, String period) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectInputStream object;
        List<?> list = null;

        try {
            if (type.equals(Utils.DIARIOS) || type.equals(Utils.CALENDARIO)) {
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
            Log.e("DATA", "Erro ao salvar: " + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("DATA", "Erro ao salvar: " + e);
        }
        return list;
    }

    public static void saveList(Context context, Object obj, String type, String year, String period) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectOutputStream object;
        try {
            if (type.equals(Utils.DIARIOS) || type.equals(Utils.CALENDARIO)) {
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
            Log.i("DATA", "Salvo");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DATA", "Erro ao salvar: " + e);
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
            Log.e("DATA", "Erro ao salvar: " + e);
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
            Log.e("DATA", "Erro ao ler: " + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("DATA", "Erro ao ler: " + e);
        }
        return infos;
    }

    public static void saveMaterias(Context context, Object obj, String year, String period) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        ObjectOutputStream object;

        try {
            object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                    login_info.getString(Utils.LOGIN_REGISTRATION,
                            "") + "." + year + "." + period)));

            object.writeObject(obj);
            object.flush();
            object.close();
            Log.i("Materias", "Salvo");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Materias", "Erro ao salvar: " + e);
        }
    }

    public static List<Materia> loadMaterias(Context context, String year, String period) {
        SharedPreferences login_info = context.getSharedPreferences(Utils.LOGIN_INFO, 0);

        List<Materia> materias = new ArrayList<>();

        ObjectInputStream object;

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                    login_info.getString(Utils.LOGIN_REGISTRATION,
                            "") + "." + year + "." + period)));

            materias = (List<Materia>) object.readObject();
            Log.i("Materias", "Lido");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Materias", "Erro ao ler: " + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("Materias", "Erro ao ler: " + e);
        }

        return materias;
    }

    private static String trim(String string) {
        string = string.replace(" / ", ".");
        string = string.replace("/", ".");
        Log.i("DATA", string);
        return string;
    }
}
