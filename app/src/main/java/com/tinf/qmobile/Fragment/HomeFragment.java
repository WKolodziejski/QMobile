package com.tinf.qmobile.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alamkanak.weekview.WeekView;
import com.tinf.qmobile.Activity.CalendarioActivity;
import com.tinf.qmobile.Activity.HorarioActivity;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Adapter.Calendario.CalendarioAdapter;
import com.tinf.qmobile.Class.Calendario.Mes;
import com.tinf.qmobile.Class.Calendario.Mes_;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.Fragments.OnUpdate;
import com.tinf.qmobile.Utilities.Utils;
import com.tinf.qmobile.WebView.SingletonWebView;
import com.tinf.qmobile.Widget.HorarioView;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import io.objectbox.BoxStore;
import static android.content.Context.MODE_PRIVATE;
import static com.tinf.qmobile.Utilities.Utils.LAST_LOGIN;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_NAME;
import static com.tinf.qmobile.Utilities.Utils.PG_HOME;
import static com.tinf.qmobile.Utilities.Utils.PG_LOGIN;
import static com.tinf.qmobile.Utilities.Utils.UPDATE_REQUEST;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class HomeFragment extends Fragment implements OnUpdate {
    private SingletonWebView webView = SingletonWebView.getInstance();
    private NestedScrollView nestedScrollView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setTitle(getContext().getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(LOGIN_NAME, ""));

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.home_scroll);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);
                });

        view.findViewById(R.id.home_website).setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(URL + PG_LOGIN));
            startActivity(browserIntent);
        });

        showCalendar(view);

        showHorario(view);

        if (!Utils.isConnected(getContext())) {
            showOffline(view);
        }
    }

    private void showOffline(View view) {
        CardView offline = (CardView) view.findViewById(R.id.home_offline);

        if (!Utils.isConnected(getContext())) {
            offline.setVisibility(View.VISIBLE);

            TextView text = (TextView) view.findViewById(R.id.offline_last_update);

            Date date = new Date(getContext().getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getLong(LAST_LOGIN, new Date().getTime()));

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY HH:mm", Locale.getDefault());

            text.setText(String.format(getResources().getString(R.string.home_last_login), format.format(date)));
        } else {
            offline.setVisibility(View.GONE);
        }
    }

    private void showCalendar(View view) {

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Mes mes = getBox().boxFor(Mes.class).query()
                .equal(Mes_.year, today.get(Calendar.YEAR))
                .equal(Mes_.month, today.get(Calendar.MONTH))
                .build().findUnique();

        RecyclerView recyclerViewCalendario = (RecyclerView) view.findViewById(R.id.recycler_home);

        if (mes == null) {
            List<Mes> mesList = getBox().boxFor(Mes.class).query().build().find();
            mes = mesList.get(mesList.size() - 1);
        }

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false);

        recyclerViewCalendario.setAdapter(new CalendarioAdapter(getActivity(), mes.days, true));
        recyclerViewCalendario.setLayoutManager(layout);

        LinearLayout calendario = (LinearLayout) view.findViewById(R.id.home_calendario);

        calendario.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), recyclerViewCalendario,
                            Objects.requireNonNull(ViewCompat.getTransitionName(recyclerViewCalendario)));
            startActivity(new Intent(getActivity(), CalendarioActivity.class), options.toBundle());
            ((MainActivity)getActivity()).dismissProgressbar();
        });
    }

    private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_home);

        HorarioView.congifWeekView(getContext(), weekView,
                getBox().boxFor(Materia.class).query().equal(Materia_.year,
                        Integer.valueOf(webView.data_year[webView.year_position])).build().find());

        LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);

        horario.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                            weekView, Objects.requireNonNull(ViewCompat.getTransitionName(weekView)));
            startActivity(new Intent(getActivity(), HorarioActivity.class), options.toBundle());
            ((MainActivity)getActivity()).dismissProgressbar();
        });
    }

    private BoxStore getBox() {
        return ((MainActivity) getActivity()).getBox();
    }

    @Override
    public void onUpdate(String url_p) {
        if (url_p.equals(URL + PG_HOME) || url_p.equals(UPDATE_REQUEST)) {
            showHorario(getView());
            showOffline(getView());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void requestScroll() {
        nestedScrollView.smoothScrollTo(0,0);
    }
}
