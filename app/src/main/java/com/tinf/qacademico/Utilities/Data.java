package com.tinf.qacademico.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tinf.qacademico.Class.Calendario.Meses;
import com.tinf.qacademico.Class.Infos;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.tinf.qacademico.Utilities.Utils.CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qacademico.Utilities.Utils.MATERIAS;

public class Data {

    public static List<Meses> loadCalendar(Context context) {
        SharedPreferences login_info = context.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE);
        SingletonWebView webView = SingletonWebView.getInstance();

        ObjectInputStream object;
        List<Meses> calendar = new ArrayList<>();

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                    login_info.getString(LOGIN_REGISTRATION,
                            "") + CALENDARIO + "." + webView.data_year[webView.year_position])));

            calendar = (List<Meses>) object.readObject();
            Log.i("Calendario", "Lido");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Calendario", "Erro ao salvar: " + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("Calendario", "Erro ao salvar: " + e);
        }
        return calendar;
    }

    public static void saveCalendar(Context context, Object obj) {
        SharedPreferences login_info = context.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE);
        SingletonWebView webView = SingletonWebView.getInstance();

        ObjectOutputStream object;
        try {
            object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                    login_info.getString(LOGIN_REGISTRATION,
                            "") + CALENDARIO + "." + webView.data_year[webView.year_position])));
            object.writeObject(obj);
            object.flush();
            object.close();
            Log.i("DATA", "Salvo");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DATA", "Erro ao salvar: " + e);
        }
    }

    public static void saveMaterias(Context context, Object obj) {
        SharedPreferences login_info = context.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE);
        SingletonWebView webView = SingletonWebView.getInstance();

        ObjectOutputStream object;

        try {
            object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                    login_info.getString(LOGIN_REGISTRATION, "") + MATERIAS
                            + "." + webView.data_year[webView.year_position])));

            object.writeObject(obj);
            object.flush();
            object.close();
            Log.i("Materias", "Salvo");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Materias", "Erro ao salvar: " + e);
        }
    }

    public static List<Materia> loadMaterias(Context context) {
        SharedPreferences login_info = context.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE);
        SingletonWebView webView = SingletonWebView.getInstance();

        List<Materia> materias = new ArrayList<>();

        ObjectInputStream object;

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(
                    login_info.getString(LOGIN_REGISTRATION, "") + MATERIAS
                            + "." + webView.data_year[webView.year_position])));

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

}
