package com.tinf.qacademico.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
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
import com.tinf.qacademico.Activity.CalendarioActivity;
import com.tinf.qacademico.Activity.HorarioActivity;
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.Calendario.CalendarioAdapter;
import com.tinf.qacademico.Class.Calendario.Mes;
import com.tinf.qacademico.Class.Calendario.Mes_;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.Class.Materias.Materia_;
import com.tinf.qacademico.Utilities.Utils;
import com.tinf.qacademico.WebView.SingletonWebView;
import com.tinf.qacademico.Widget.HorarioView;
import com.tinf.qacademico.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import io.objectbox.BoxStore;

import static android.content.Context.MODE_PRIVATE;
import static com.tinf.qacademico.Utilities.Utils.LAST_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_NAME;
import static com.tinf.qacademico.Utilities.Utils.PG_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class HomeFragment extends Fragment implements MainActivity.OnPageUpdated {
    private SingletonWebView webView = SingletonWebView.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setTitle(getContext().getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(LOGIN_NAME, ""));

        showHorario(view);
        showCalendar(view);

        if (!Utils.isConnected(getContext())) {
            showOffline(view);
        }

        LinearLayout website = (LinearLayout) view.findViewById(R.id.home_website);
        website.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(URL + PG_LOGIN));
            startActivity(browserIntent);
        });
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

        HorarioView.congifWeekView(weekView,
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
    public void onPageUpdate(List<?> list) {
        showHorario(getView());
        showOffline(getView());
    }
}
