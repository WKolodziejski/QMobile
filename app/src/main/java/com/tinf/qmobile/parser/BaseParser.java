package com.tinf.qmobile.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.objectbox.Box;


public abstract class BaseParser extends AsyncTask<String, Void, Boolean> {
    private OnFinish onFinish;
    private OnError onError;
    protected final int pos, page;
    protected final boolean notify;

    protected Box<Matter> matterBox = DataBase.get().getBoxStore().boxFor(Matter.class);
    protected Box<Period> periodBox = DataBase.get().getBoxStore().boxFor(Period.class);
    protected Box<Journal> journalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
    protected Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
    protected Box<Material> materialsBox = DataBase.get().getBoxStore().boxFor(Material.class);
    protected Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);

    public BaseParser(int page, int pos, boolean notify, OnFinish onFinish, OnError onError) {
        this.page = page;
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;
        this.onError = onError;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        Log.i(String.valueOf(page), String.valueOf(User.getYear(pos)) + String.valueOf(User.getPeriod(pos)));
        try {
            return DataBase.get().getBoxStore().callInTx(() -> {
                parse(Jsoup.parse(strings[0]));
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            onFinish.onFinish(page, pos);
        else
            onError.onError(page, "Problema ao processar página");
    }

    public interface OnError {
        void onError(int pg, String error);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    public abstract void parse(Document document);

    protected String formatNumber(String s) {
        return s.substring(s.indexOf(":") + 1).trim();
    }

    protected String formatGrade(String s) {
        return s.startsWith(",") ? "" : s.replaceAll(",", ".");
    }

}
