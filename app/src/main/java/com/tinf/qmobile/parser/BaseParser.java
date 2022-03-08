package com.tinf.qmobile.parser;

import static com.tinf.qmobile.App.getContext;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Sender;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.Executors;

import io.objectbox.Box;

public abstract class BaseParser {
    protected OnFinish onFinish;
    protected OnError onError;
    protected final int page;
    protected final int year;
    protected final int period;
    protected final boolean notify;
    private boolean success;

    protected Box<Matter> matterBox = DataBase.get().getBoxStore().boxFor(Matter.class);
    protected Box<Period> periodBox = DataBase.get().getBoxStore().boxFor(Period.class);
    protected Box<Journal> journalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
    protected Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
    protected Box<Material> materialsBox = DataBase.get().getBoxStore().boxFor(Material.class);
    protected Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);
    protected Box<Message> messageBox = DataBase.get().getBoxStore().boxFor(Message.class);
    protected Box<Attachment> attachmentBox = DataBase.get().getBoxStore().boxFor(Attachment.class);
    protected Box<Sender> senderBox = DataBase.get().getBoxStore().boxFor(Sender.class);
    protected Box<Clazz> classBox = DataBase.get().getBoxStore().boxFor(Clazz.class);
    protected FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    public BaseParser(int page, int year, int period, boolean notify, OnFinish onFinish, OnError onError) {
        this.page = page;
        this.year = year;
        this.period = period;
        this.notify = notify;
        this.onFinish = onFinish;
        this.onError = onError;
    }

    public void execute(String string) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Log.i(String.valueOf(page), year + "/" + period);

            try {
                DataBase.get().getBoxStore().callInTx(() -> {
                    parse(Jsoup.parse(string));
                    success = true;
                    return true;
                });
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
                success = false;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (success)
                    onFinish.onFinish(page);
                else
                    onError.onError(page, getContext().getString(R.string.client_error));
            });
        });
    }

    public interface OnError {
        void onError(int pg, String error);
    }

    public interface OnFinish {
        void onFinish(int pg);
    }

    public abstract void parse(final Document document);

    protected String formatNumber(String s) {
        return s.substring(s.indexOf(":") + 1).trim();
    }

    protected String formatGrade(String s) {
        return s.startsWith(",") ? "" : s.replaceAll(",", ".");
    }

}
