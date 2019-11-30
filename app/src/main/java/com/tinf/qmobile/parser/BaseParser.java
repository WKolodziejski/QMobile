package com.tinf.qmobile.parser;

import android.os.AsyncTask;

import com.tinf.qmobile.App;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public abstract class BaseParser extends AsyncTask<String, Void, Boolean> {
    private OnFinish onFinish;
    private OnError onError;
    protected final int pos, page;
    protected final boolean notify;

    public BaseParser(int page, int pos, boolean notify, OnFinish onFinish, OnError onError) {
        this.page = page;
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;
        this.onError = onError;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            return App.getBox().callInTx(() -> {
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

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    public interface OnError {
        void onError(int pg, String error);
    }

    public abstract void parse(Document page);

}
