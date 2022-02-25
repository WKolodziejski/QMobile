package com.tinf.qmobile.network;

import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.parser.BaseParser;

import java.util.Map;

public class RequestHelper {
    public final int pg;
    public final String url;
    public final int year;
    public final int period;
    public final int method;
    public final Map<String, String> form;
    public final boolean notify;
    public final Matter matter;
    public final BaseParser.OnFinish onFinish;

    public RequestHelper(int pg, String url, int year, int period, int method, Map<String, String> form, boolean notify, Matter matter, BaseParser.OnFinish onFinish) {
        this.pg = pg;
        this.url = url;
        this.year = year;
        this.period = period;
        this.method = method;
        this.form = form;
        this.notify = notify;
        this.matter = matter;
        this.onFinish = onFinish;
    }

}
