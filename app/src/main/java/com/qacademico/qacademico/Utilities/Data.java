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

public class Data {

    public static List<Diarios> getDiarios(Context context) {
        SharedPreferences login_info = context.getSharedPreferences("login_info", 0);

        ObjectInputStream object;
        List<Diarios> diarios = null;

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(login_info.getString("matricula",
                    "") + ".diarios")));
            diarios = (List<Diarios>) object.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return diarios;
    }

    public static List<Boletim> getBoletim(Context context) {
        SharedPreferences login_info = context.getSharedPreferences("login_info", 0);

        ObjectInputStream object;
        List<Boletim> boletim = null;

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(login_info.getString("matricula", "") + ".boletim")));
            boletim = (List<Boletim>) object.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return boletim;
    }

    public static List<Horario> getHorario(Context context) {
        SharedPreferences login_info = context.getSharedPreferences("login_info", 0);

        ObjectInputStream object;
        List<Horario> horario = null;

        try {
            object = new ObjectInputStream(new FileInputStream(context.getFileStreamPath(login_info.getString("matricula", "") + ".horario")));
            horario = (List<Horario>) object.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return horario;
    }

    public static void saveObject(Context context, Object obj, String name) {
        SharedPreferences login_info = context.getSharedPreferences("login_info", 0);

        ObjectOutputStream object;
        try {
            object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                    login_info.getString("matricula", "") + name )));
            object.writeObject(obj);
            object.flush();
            object.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImage(Context context) {
        SharedPreferences login_info = context.getSharedPreferences("login_info", 0);

        Bitmap image = null;

        try {
            File dir = new File(String.valueOf(context.getDir(login_info.getString("matricula", ""), Context.MODE_PRIVATE)));

            File file = new File(dir, "profile.jpg");
            image = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }

    public static void setImage(Context context, Bitmap image) {
        SharedPreferences login_info = context.getSharedPreferences("login_info", 0);

        File dir = new File(String.valueOf(context.getDir(login_info.getString("matricula", ""), Context.MODE_PRIVATE)));

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "profile.jpg");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
