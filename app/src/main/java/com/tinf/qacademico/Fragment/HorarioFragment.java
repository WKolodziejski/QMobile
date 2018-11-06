package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alamkanak.weekview.WeekView;
import com.tinf.qacademico.Activity.HorarioActivity;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.Class.Materias.Materia_;
import com.tinf.qacademico.Interfaces.Fragments.OnUpdate;
import com.tinf.qacademico.WebView.SingletonWebView;
import com.tinf.qacademico.Widget.HorarioView;
import com.tinf.qacademico.R;
import java.util.Objects;
import io.objectbox.BoxStore;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.UPDATE_REQUEST;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class HorarioFragment extends Fragment implements OnUpdate {

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

        SingletonWebView webView = SingletonWebView.getInstance();
        HorarioView.congifWeekView(weekView, getBox().boxFor(Materia.class).query().equal(Materia_.year,
                Integer.valueOf(webView.data_year[webView.year_position])).build().find());

    }

    private BoxStore getBox() {
        return ((HorarioActivity) getActivity()).getBox();
    }

    @Override
    public void onUpdate(String url_p) {
        if (url_p.equals(URL + PG_HORARIO) || url_p.equals(UPDATE_REQUEST)) {
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
