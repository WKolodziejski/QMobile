package com.tinf.qmobile.network;

import com.tinf.qmobile.model.matter.Matter;

import java.util.Map;

public class RequestHelper {
    public final int pg;
    public final String url;
    public final int pos;
    public final int method;
    public final Map<String, String> form;
    public final boolean notify;
    public final Matter matter;

    public RequestHelper(int pg, String url, int pos, int method, Map<String, String> form, boolean notify, Matter matter) {
        this.pg = pg;
        this.url = url;
        this.pos = pos;
        this.method = method;
        this.form = form;
        this.notify = notify;
        this.matter = matter;
    }

}
