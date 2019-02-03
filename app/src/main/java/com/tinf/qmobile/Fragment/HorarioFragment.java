package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import com.tinf.qmobile.Activity.HorarioActivity;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.Fragments.OnUpdate;
import com.tinf.qmobile.WebView.SingletonWebView;
import com.tinf.qmobile.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.Utilities.Utils.PG_HORARIO;
import static com.tinf.qmobile.Utilities.Utils.UPDATE_REQUEST;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class HorarioFragment extends Fragment implements OnUpdate {
    private SingletonWebView webView = SingletonWebView.getInstance();
    private List<Materia> materias;
    private int firstHour = 24;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        materias = getBox().boxFor(Materia.class).query().equal(Materia_.year,
                Integer.valueOf(webView.data_year[webView.year_position])).build().find();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_horario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showHorario(view);
    }

    private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

        weekView.setMonthChangeListener((startDate, endDate) -> {

            List<WeekViewDisplayable> weekHorario = new ArrayList<>();

            for (int i = 0; i < materias.size(); i++) {
                for (int j = 0; j < materias.get(i).horarios.size(); j++) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
                    startTime.set(Calendar.DAY_OF_WEEK, materias.get(i).horarios.get(j).getDay());
                    startTime.set(Calendar.HOUR_OF_DAY, materias.get(i).horarios.get(j).getStartHour());
                    startTime.set(Calendar.MINUTE, materias.get(i).horarios.get(j).getStartMinute());

                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, materias.get(i).horarios.get(j).getEndHour());
                    endTime.set(Calendar.MINUTE, materias.get(i).horarios.get(j).getEndMinute());

                    WeekViewEvent event = new WeekViewEvent(materias.get(i).horarios.get(j).id, materias.get(i).getName(), startTime, endTime);
                    event.setColor(getResources().getColor(materias.get(i).getColor()));

                    weekHorario.add(event);

                    if (startTime.get(Calendar.HOUR_OF_DAY) < firstHour) {
                        firstHour = startTime.get(Calendar.HOUR_OF_DAY);
                    }
                }
            }

            Calendar currentWeek = Calendar.getInstance();
            currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            currentWeek.set(Calendar.HOUR_OF_DAY, firstHour);
            currentWeek.set(Calendar.MINUTE, 30);
            weekView.goToDate(currentWeek);
            weekView.goToHour(firstHour);

            return weekHorario;
        });

        weekView.notifyDataSetChanged();
    }

    private BoxStore getBox() {
        return ((HorarioActivity) getActivity()).getBox();
    }

    @Override
    public void onUpdate(String url_p) {
        if (url_p.equals(URL + PG_HORARIO) || url_p.equals(UPDATE_REQUEST)) {
            materias = getBox().boxFor(Materia.class).query().equal(Materia_.year,
                    Integer.valueOf(webView.data_year[webView.year_position])).build().find();
            showHorario(getView());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((HorarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HorarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void requestScroll() {}
}
