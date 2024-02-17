package com.tinf.qmobile.network;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.MESSAGES;
import static com.tinf.qmobile.network.OnResponse.PG_CALENDAR;
import static com.tinf.qmobile.network.OnResponse.PG_CLASSES;
import static com.tinf.qmobile.network.OnResponse.PG_FETCH_YEARS;
import static com.tinf.qmobile.network.OnResponse.PG_GENERATOR;
import static com.tinf.qmobile.network.OnResponse.PG_JOURNALS;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIALS;
import static com.tinf.qmobile.network.OnResponse.PG_MESSAGE;
import static com.tinf.qmobile.network.OnResponse.PG_MESSAGES;
import static com.tinf.qmobile.network.OnResponse.PG_MESSAGES_FORM;
import static com.tinf.qmobile.network.OnResponse.PG_MESSAGE_FIND;
import static com.tinf.qmobile.network.OnResponse.PG_REPORT;
import static com.tinf.qmobile.network.OnResponse.PG_SCHEDULE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.parser.BaseParser;
import com.tinf.qmobile.parser.CalendarParser;
import com.tinf.qmobile.parser.ClassParser;
import com.tinf.qmobile.parser.JournalParser;
import com.tinf.qmobile.parser.LoginParser;
import com.tinf.qmobile.parser.MaterialsParser;
import com.tinf.qmobile.parser.ReportParser;
import com.tinf.qmobile.parser.ResponseParser;
import com.tinf.qmobile.parser.ScheduleParser;
import com.tinf.qmobile.parser.messages.FindMessageParser;
import com.tinf.qmobile.parser.messages.FormMessagesParser;
import com.tinf.qmobile.parser.messages.OnMessagesLoad;
import com.tinf.qmobile.parser.messages.PageMessagesParser;
import com.tinf.qmobile.parser.messages.SingleMessageParser;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.UserUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
  private static final String TAG = "Network Client";
  private static final String GERADOR = "/qacademico/lib/rsa/gerador_chaves_rsa.asp";
  private static final String VALIDA = "/qacademico/lib/validalogin.asp";
  private final FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
  private static Client instance;
  private final List<RequestHelper> requestsHelper;
  private final List<RequestRunning> requestsRunning;
  private final List<OnResponse> onResponses;
  private final List<OnUpdate> onUpdates;
  private final List<OnEvent> onEvents;
  private final RequestQueue requestsQueue;
  private final Map<String, String> params;
  private final ExecutorService executors;
  private final Handler handler;
  private String qCookie;
  private String aspCookie;
  private String URL;
  private String keyA;
  private String keyB;
  private boolean isValid;
  private boolean isLogging;

  public static int pos;
  public static boolean background;

  public enum Resp {
    OK,
    HOST,
    DENIED,
    EGRESS,
    UPDATE,
    QUEST,
    REG,
    UNKNOWN
  }

  private Client() {
    this.requestsQueue = Volley.newRequestQueue(getContext(), new HurlStack());
    this.requestsHelper = new LinkedList<>();
    this.requestsRunning = new LinkedList<>();
    this.params = new HashMap<>();
    this.onUpdates = new LinkedList<>();
    this.onResponses = new LinkedList<>();
    this.onEvents = new LinkedList<>();
    this.handler = new Handler(Looper.getMainLooper());
    this.executors = Executors.newFixedThreadPool(2);
    this.URL = UserUtils.getURL();

    CookieManager.getInstance()
                 .setAcceptCookie(true);
  }

  public static synchronized Client get() {
    if (instance == null) {
      instance = new Client();
    }
    return instance;
  }

  private void createRequest(int pg,
                             String url,
                             int year,
                             int period,
                             int method,
                             Map<String, String> form,
                             boolean notify,
                             Object[] payload,
                             BaseParser.OnFinish onFinish) {
    if (!isConnected()) {
      return;
    }

    Log.d(TAG, "Creating request: " + pg + " in " + year + "/" + period);

    if (!isValid) {
      if (!isLogging) {
        login();
      }
      addToQueue(pg, url, year, period, method, form, notify, payload, onFinish);
    } else {
      Log.i(TAG, "Request for: " + pg + " in " + year + "/" + period);

      boolean isNew = true;

      for (RequestHelper h : requestsHelper)
        if (h.pg == pg && h.year == year && h.period == period) {
          Log.d(TAG, "Duplicate request: " + pg + " in " + year + "/" + period);
          isNew = false;
          break;
        }

      synchronized (requestsRunning) {
        for (RequestRunning r : requestsRunning)
          if (r.pg == pg && r.year == year && r.period == period && r.pg != PG_CLASSES) {
            Log.d(TAG, "Duplicate request: " + pg + " in " + year + "/" + period);
            isNew = false;
            break;
          }
      }

      if (!isNew) {
        checkQueue(pg, year, period);
        return;
      }

      addRequest(new StringRequest(method, URL + url, responseASCII -> {
        String response =
            new String(responseASCII.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        if (response.contains("�")) response = responseASCII;

        Resp r =
            ResponseParser.parseResponse(response, pg, year, period, this::callOnError, this::callOnAccessDenied);

        if (r == Resp.DENIED) {
          addToQueue(pg, url, year, period, method, form, notify, payload, onFinish);
          login();
          callOnAccessDenied(pg, year, period, getContext().getResources()
                                             .getString(R.string.login_expired));

        } else if (r == Resp.OK) {
          if (pg == PG_JOURNALS) {
            new JournalParser(pg, year, period, notify, onFinish, this::callOnError).execute(
                response);

          } else if (pg == PG_REPORT) {
            new ReportParser(pg, year, period, notify, onFinish, this::callOnError).execute(
                response);

          } else if (pg == PG_SCHEDULE) {
            new ScheduleParser(pg, year, period, notify, onFinish, this::callOnError).execute(
                response);

          } else if (pg == PG_MATERIALS) {
            new MaterialsParser(pg, year, period, notify, onFinish, this::callOnError).execute(
                response);

          } else if (pg == PG_CALENDAR) {
            new CalendarParser(pg, year, period, notify, onFinish, this::callOnError).execute(
                response);

          } else if (pg == PG_MESSAGES) {
            new PageMessagesParser(pg, year, period, notify, onFinish, this::callOnError).execute(
                response);

          } else if (pg == PG_CLASSES) {
            new ClassParser((Matter) payload[0], pg, year, period, notify, onFinish,
                            this::callOnError).execute(response);

          } else if (pg == PG_MESSAGES_FORM) {
            new FormMessagesParser((OnMessagesLoad) payload[0], pg, year, period, notify, onFinish,
                                   this::callOnError).execute(response);

          } else if (pg == PG_MESSAGE_FIND) {
            new FindMessageParser((OnMessagesLoad) payload[0], (Message) payload[1], pg, year,
                                  period, notify, onFinish, this::callOnError).execute(response);

          } else if (pg == PG_MESSAGE) {
            new SingleMessageParser((Message) payload[0], pg, year, period, notify, onFinish,
                                    this::callOnError).execute(response);

          } else if (pg == PG_FETCH_YEARS) {
            Document document = Jsoup.parse(response);

            Element frm = document.getElementById("ANO_PERIODO2");

            if (frm != null) {
              Elements dates = frm.getElementsByTag("option");

              String[] years = new String[dates.size() - 1];

              for (int i = 0; i < dates.size() - 1; i++)
                years[i] = dates.get(i + 1)
                                .text();

              UserUtils.setYears(years);
            }

            callOnFinish(PG_FETCH_YEARS, year, period);

            loadYear(0);
          }
        }
      }, error -> onError(pg, year, period, error)) {

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
          setCookie(response);
          return super.parseNetworkResponse(response);
        }

        @Override
        public Map<String, String> getHeaders() {
          return params;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
          return !form.isEmpty() ? form : super.getParams();
        }

      }, pg, year, period);
    }
  }

  public void load(int pg) {
    load(pg, UserUtils.getYear(pos), UserUtils.getPeriod(pos), this::callOnFinish);
  }

  public void load(long id) {
    Matter matter = DataBase.get()
                            .getBoxStore()
                            .boxFor(Matter.class)
                            .get(id);

    load(matter);
  }

  public void load(Matter matter) {
    Log.d("Client", "loading " + matter.getTitle());

    executors.execute(() -> {
      int qid = matter.getQID();

      if (qid != -1) createRequest(PG_CLASSES,
                                   INDEX + PG_JOURNALS + "&ACAO=VER_FREQUENCIA&COD_PAUTA=" + qid +
                                   "&ANO_PERIODO=" + matter.getYear_() + "_" + matter.getPeriod_(),
                                   matter.getYear_(), matter.getPeriod_(), POST, new HashMap<>(),
                                   false, new Object[] { matter }, this::callOnFinish);
    });
  }

  private void load(int pg,
                    int year,
                    int period,
                    BaseParser.OnFinish onFinish) {
    load(pg, year, period, onFinish, false);
  }

  private void load(int pg,
                    int year,
                    int period) {
    load(pg, year, period, this::callOnFinish, false);
  }

  public void load(int pg,
                   boolean notify) {
    load(pg, UserUtils.getYear(pos), UserUtils.getPeriod(pos), this::callOnFinish, notify);
  }

  // Carrega as infos do form ASP
  public void loadMessagesForm(OnMessagesLoad onMessagesLoad) {
    createRequest(PG_MESSAGES_FORM, MESSAGES, UserUtils.getYear(pos),
                  UserUtils.getPeriod(pos), GET, new HashMap<>(), false,
                  new Object[] { onMessagesLoad },
                  this::callOnFinish);
  }

  // Carrega uma página de mensagens
  public void loadMessagesPage(Map<String, String> form,
                               int pg) {
    Log.d("Messages", form.toString());
    createRequest(PG_MESSAGES, MESSAGES, pg, pg, POST, form, false, null,
                  this::callOnFinish);
  }

  // Busca a página que contém a mensagem
  public void findMessagesPage(OnMessagesLoad onMessagesLoad,
                               Map<String, String> form,
                               Message message,
                               int pg) {
    if (pg == 1) {
      createRequest(PG_MESSAGE_FIND, MESSAGES, pg, pg, GET, new HashMap<>(), false,
                    new Object[] { onMessagesLoad, message }, this::callOnFinish);
    } else {
      createRequest(PG_MESSAGE_FIND, MESSAGES, pg, pg, POST, form, false,
                    new Object[] { onMessagesLoad, message }, this::callOnFinish);
    }
  }

  // Carrega a mensagem
  public void load(Message message,
                   Map<String, String> form) {
    createRequest(PG_MESSAGE, MESSAGES, UserUtils.getYear(pos),
                  UserUtils.getPeriod(pos), POST, form, false,
                  new Object[] { message },
                  this::callOnFinish);
  }

  private void load(int pg,
                    int year,
                    int period,
                    BaseParser.OnFinish onFinish,
                    boolean notify) {
    executors.execute(() -> {
      int method = GET;
      String url = INDEX + pg;
      Map<String, String> form = new HashMap<>();

      switch (pg) {
        case PG_FETCH_YEARS:
          url = INDEX + PG_JOURNALS;
          break;

        case PG_MESSAGES:
          url = MESSAGES;
          break;

        case PG_JOURNALS:
          method = POST;
          form.put("ANO_PERIODO2", year + "_" + period);
          break;

        case PG_REPORT:
        case PG_SCHEDULE:
          url = url.concat("&cmbanos=" + year + "&cmbperiodos=" + period);
          break;

        case PG_MATERIALS:
          method = POST;
          form.put("ANO_PERIODO", year + "_" + period);
          break;
      }

      createRequest(pg, url, year, period, method, form, notify, null, onFinish);
    });
  }

  public <T> void addRequest(Request<T> request,
                             int pg,
                             int year,
                             int period) {
    if (isConnected()) {
      callOnStart(pg);
      request.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 1.5f));
      synchronized (requestsRunning) {
        requestsRunning.add(new RequestRunning(pg, year, period));
      }
      synchronized (requestsQueue) {
        requestsQueue.add(request);
      }
      Log.v(TAG, "Loading: " + request);
    } else {
      onError(pg, year, period, new VolleyError(getContext().getResources()
                                              .getString(R.string.client_no_connection)));
    }
  }

  public void login() {
    Log.d(TAG, "Logging in");

    if (isLogging) {
      Log.d(TAG, "Already logging in");
      return;
    }

    isLogging = true;
    executors.execute(() -> fetchParams(
        success -> addRequest(new StringRequest(POST, URL + VALIDA, responseASCII -> {
          String response = new String(responseASCII.getBytes(StandardCharsets.ISO_8859_1),
                                       StandardCharsets.UTF_8);

          if (response.contains("�")) response = responseASCII;

          Log.d("RESPONSE: " + PG_LOGIN, response);

          Resp r = ResponseParser.parseResponse(response, PG_LOGIN, 0, 0, this::callOnError,
                                                this::callOnAccessDenied);

          if (r == Resp.DENIED) {
            callOnAccessDenied(PG_LOGIN, 0, 0, getContext().getString(R.string.login_invalid));

          } else if (r == Resp.OK) {
            isValid = true;
            isLogging = false;

            new LoginParser(this::callOnDialog,
                            this::callOnRenewalAvailable,
                            PG_LOGIN,
                            UserUtils.getYear(pos),
                            UserUtils.getPeriod(pos),
                            false,
                            this::callOnFinish,
                            this::callOnError).execute(response);
          }
        }, error -> onError(PG_LOGIN, 0, 0, error)) {

          @Override
          protected Response<String> parseNetworkResponse(NetworkResponse response) {
            setCookie(response);
            return super.parseNetworkResponse(response);
          }

          @Override
          public Map<String, String> getHeaders() {
            return params;
          }

          @Override
          protected Map<String, String> getParams() {
            return UserUtils.getLoginParams(keyA, keyB);
          }

          @Override
          public Priority getPriority() {
            return Priority.IMMEDIATE;
          }

        }, PG_LOGIN, 0, 0), 0, 0));
  }

  private synchronized void addToQueue(int pg,
                                       String url,
                                       int year,
                                       int period,
                                       int method,
                                       Map<String, String> form,
                                       boolean notify,
                                       Object[] payload,
                                       BaseParser.OnFinish onFinish) {
    boolean isNew = true;

    synchronized (requestsHelper) {
      for (RequestHelper h : requestsHelper)
        if (h.pg == pg && h.year == year && h.period == period) {
          Log.d(TAG, "Duplicate queue request: " + pg + " in " + year + "/" + period);
          isNew = false;
          break;
        }
    }

    synchronized (requestsRunning) {
      for (RequestRunning r : requestsRunning)
        if (r.pg == pg && r.year == year && r.period == period && r.pg != PG_CLASSES) {
          Log.d(TAG, "Duplicate queue request: " + pg + " in " + year + "/" + period);
          isNew = false;
          break;
        }
    }

    if (isNew) {
      synchronized (requestsHelper) {
        requestsHelper.add(
            new RequestHelper(pg, url, year, period, method, form, notify, payload, onFinish));
      }
    }
  }

  private void fetchParams(Response.Listener<String> listener, int year, int period) {
    addRequest(new StringRequest(GET, URL + GERADOR, responseASCII -> {
      String response =
          new String(responseASCII.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

      if (response.contains("�")) response = responseASCII;

      try {
        String keys =
            response.substring(response.indexOf("RSAKeyPair("), response.lastIndexOf(")"));
        keys = keys.substring(keys.indexOf("\"") + 1, keys.lastIndexOf("\""));

        keyA = keys.substring(0, keys.indexOf("\""));
        Log.d("Key A", keyA);
        keyB = keys.substring(keys.lastIndexOf("\"") + 1);
        Log.d("Key B", keyB);

        Log.v(TAG, "Keys fetched");
      } catch (Exception e) {
        e.printStackTrace();
        crashlytics.recordException(e);
      }

      listener.onResponse(response);

    }, error -> onError(PG_GENERATOR, year, period, error)) {

      @Override
      protected Response<String> parseNetworkResponse(NetworkResponse response) {
        setCookie(response);
        return super.parseNetworkResponse(response);
      }

      @Override
      public Priority getPriority() {
        return Priority.IMMEDIATE;
      }

    }, PG_GENERATOR, 0, 0);
  }

  private void setCookie(NetworkResponse response) {
    if (response == null)
      return;

    if (response.headers == null)
      return;

    String setCookie = response.headers.get("Set-Cookie");

    if (setCookie == null)
      return;

    if (setCookie.contains("ASP.NET")) {
      aspCookie = setCookie;

    } else if (!setCookie.contains("QSESSIONID")) {
      qCookie = setCookie;
    }

    setCookie = qCookie + "; " + aspCookie;

    params.put("Cookie", setCookie);

    CookieManager.getInstance()
                 .setCookie(getURL(), setCookie);

    Log.d("Cookies", params.toString());
  }

  public void close() {
    Log.i(TAG, "Logout");
    requestsHelper.clear();
    requestsQueue.cancelAll(request -> true);
    params.clear();
    onResponses.clear();
    onUpdates.clear();
    instance = null;
  }

  private void onError(int pg,
                       int year,
                       int period,
                       VolleyError error) {
    String msg = null;

    error.printStackTrace();

    if (isConnected()) {
      if (pg == PG_GENERATOR) {
        msg = getContext().getResources()
                          .getString(R.string.client_host);

      } else if (pg == PG_LOGIN) {
        isLogging = false;
        isValid = false;
      }
    } else {
      msg = getContext().getResources()
                        .getString(R.string.client_no_connection);
    }

    if (msg == null) {
      if (error instanceof TimeoutError) msg = getContext().getResources()
                                                           .getString(R.string.client_timeout);

      else if (error instanceof NoConnectionError) msg = getContext().getResources()
                                                                     .getString(
                                                                         R.string.client_no_connection);

      else {
        msg = error.getLocalizedMessage() == null ? getContext().getResources()
                                                                .getString(R.string.client_error)
                                                  : error.getLocalizedMessage();

        crashlytics.log(String.valueOf(pg));
        crashlytics.recordException(error);
      }
    }

    callOnError(pg, year, period, msg);
  }

  public static boolean isConnected() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    Network nw = connectivityManager.getActiveNetwork();
    if (nw == null) return false;
    NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
    return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                             actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                             actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                             actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
  }

  public void addOnResponseListener(OnResponse onResponse) {
    if (onResponse != null && !onResponses.contains(onResponse)) {
      if (background) {
        onResponses.clear();
      }
      onResponses.add(onResponse);
    }
  }

  public void removeOnResponseListener(OnResponse onResponse) {
    if (onResponses != null && onResponse != null) {
      onResponses.remove(onResponse);
    }
  }

  public void addOnUpdateListener(OnUpdate onUpdate) {
    if (onUpdate != null && !onUpdates.contains(onUpdate)) {
      onUpdates.add(onUpdate);
      Log.v(TAG, "addOnUpdateListener: " + onUpdate);
    }
  }

  public void removeOnUpdateListener(OnUpdate onUpdate) {
    if (onUpdate != null) {
      onUpdates.remove(onUpdate);
      Log.v(TAG, "removeOnUpdateListener: " + onUpdate);
    }
  }

  public void addOnEventListener(OnEvent onEvent) {
    if (onEvent != null && !onEvents.contains(onEvent)) {
      onEvents.add(onEvent);
      Log.v(TAG, "addOnEventListener: " + onEvent);
    }
  }

  public void removeOnEventListener(OnEvent onEvent) {
    if (onEvent != null) {
      onEvents.remove(onEvent);
      Log.v(TAG, "removeOnEventListener: " + onEvent);
    }
  }

  private void callOnDialog(String title,
                            String msg) {
    handler.post(() -> {
      Log.v(TAG, "Dialog: " + title);
      for (OnEvent onEvent : onEvents) {
        onEvent.onDialog(title, msg);
      }
    });
  }

  private void callOnRenewalAvailable() {
    handler.post(() -> {
      Log.v(TAG, "RenewalAvailable");
      for (OnEvent onEvent : onEvents) {
        onEvent.onRenewalAvailable();
      }
    });
  }

  private void callOnError(int pg,
                           int year,
                           int period,
                           String error) {
    handler.post(() -> {
      requestsQueue.cancelAll(request -> true);
      isLogging = false;
      isValid = false;
      Log.v(TAG, "Error: " + pg);
      for (OnResponse onResponse : onResponses) {
        onResponse.onError(pg, year, period, error);
      }
    });

    checkQueue(pg, year, period);
  }

  private void callOnStart(int pg) {
    handler.post(() -> {
      Log.v(TAG, "Start: " + pg);
      for (OnResponse onResponse : onResponses) {
        onResponse.onStart(pg);
      }
    });
  }

  private void callOnFinish(int pg,
                            int year,
                            int period) {
    handler.post(() -> {
      Log.v(TAG, "Finish: " + pg);
      for (OnResponse onResponse : onResponses) {
        onResponse.onFinish(pg, year, period);
      }
    });

    checkQueue(pg, year, period);
  }

  private void checkQueue(int pg,
                          int year,
                          int period) {
    synchronized (requestsHelper) {
      for (RequestHelper h : requestsHelper)
        if (h.pg == pg && h.year == year && h.period == period) {
          requestsHelper.remove(h);
          break;
        }
    }

    synchronized (requestsRunning) {
      for (RequestRunning r : requestsRunning)
        if (r.pg == pg && r.year == year && r.period == period && r.pg != PG_CLASSES) {
          requestsRunning.remove(r);
          break;
        }
    }

    executors.execute(() -> {
      synchronized (requestsHelper) {
        while (!requestsHelper.isEmpty()) {
          RequestHelper helper = requestsHelper.get(0);
          requestsHelper.remove(0);
          createRequest(helper.pg, helper.url, helper.year, helper.period, helper.method,
                        helper.form, helper.notify, helper.payload, helper.onFinish);
        }
      }
    });
  }

  private void callOnAccessDenied(int pg,
                                  int year,
                                  int period,
                                  String message) {
    handler.post(() -> {
      Log.v(TAG, pg + ": " + message);
      isValid = false;
      isLogging = false;
      requestsHelper.clear();
      requestsQueue.cancelAll(request -> true);
      params.clear();
      keyA = "";
      keyB = "";
      for (OnResponse onResponse : onResponses) {
        onResponse.onAccessDenied(pg, message);
      }
    });
  }

  public void changeDate(int pos) {
    handler.post(() -> {
      synchronized (requestsQueue) {
        if (pos != Client.pos) {
          Client.pos = pos;

          requestsQueue.cancelAll(request -> true);
          isLogging = false;
          for (OnUpdate onUpdate : onUpdates) {
            onUpdate.onDateChanged();
          }
          loadYear(pos);
        }
      }
    });
  }

  private int posBackup = -1;

  public void restorePreviousDate() {
    if (posBackup >= 0) changeDate(posBackup);

    posBackup = -1;
  }

  public void changeDateWithBackup(int pos) {
    if (posBackup == -1) posBackup = Client.pos;

    changeDate(pos);
  }

  public void requestDelayedUpdate() {
    handler.postDelayed(() -> {
      for (OnUpdate onUpdate : onUpdates) {
        onUpdate.onDateChanged();
      }
    }, 10);
  }

  private void downloadImage(String cod) {
    File picture = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +
                            UserUtils.getCredential(UserUtils.REGISTRATION));

    Log.d("Picture", picture.getAbsolutePath());

    if (!picture.exists()) {
      DownloadReceiver.downloadImage(getContext(), cod);
    }
  }

  public boolean isValid() {
    return isValid;
  }

  public boolean isLogging() {
    return isLogging;
  }

  public void setURL(String url) {
    URL = url;
  }

  public String getURL() {
    return URL;
  }

  public String getCookie() {
    return params.get("Cookie");
  }

  public void loadYear(int pos) {
    executors.execute(() -> {
      int year = UserUtils.getYear(pos);
      int period = UserUtils.getPeriod(pos);

      Log.d(TAG, "Loading journals");
      load(PG_JOURNALS, year, period, (pg0, year0, period0) -> {

        Log.d(TAG, "Loading materials");
        load(PG_MATERIALS, year, period);

        Log.d(TAG, "Loading calendar");
        load(PG_CALENDAR, year, period);

        Log.d(TAG, "Loading report");
        load(PG_REPORT, year, period, (pg1, year1, period1) -> {

          Log.d(TAG, "Loading schedule");
          load(PG_SCHEDULE, year, period, (pg2, year2, period2) -> {

            Log.d(TAG, "Loading classes");
            for (Matter m : DataBase.get()
                                    .getBoxStore()
                                    .boxFor(Matter.class)
                                    .query()
                                    .equal(Matter_.year_, year)
                                    .and()
                                    .equal(Matter_.period_, period)
                                    .build()
                                    .find()) {
              Client.get()
                    .load(m);
            }
            callOnFinish(pg2, year2, period2);
          });
          callOnFinish(pg1, year1, period1);
        });
        callOnFinish(pg0, year0, period0);
      });
    });
  }
}
