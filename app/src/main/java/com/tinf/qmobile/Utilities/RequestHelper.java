package com.tinf.qmobile.Utilities;

import java.util.Map;

public class RequestHelper {
    public final int pg;
    public final String url;
    public final int pos;
    public final int method;
    public final Map<String, String> form;
    public final boolean notify;

    public RequestHelper(int pg, String url, int pos, int method, Map<String, String> form, boolean notify) {
        this.pg = pg;
        this.url = url;
        this.pos = pos;
        this.method = method;
        this.form = form;
        this.notify = notify;
    }
}