package com.qacademico.qacademico.Fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.AdapterGuide;
import com.qacademico.qacademico.Class.Guide;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterGuide.OnGuideClicked {
    List<Guide> guide;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_home, container, false);

        configGuide();

        AdapterGuide adapter = new AdapterGuide(guide, getActivity());
        adapter.setOnClick(this);

        SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(() -> getActivity().recreate());

        RecyclerView recyclerViewGuide = (RecyclerView) view.findViewById(R.id.recycler_guide);
        FlexboxLayoutManager layout = new FlexboxLayoutManager(getActivity());
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setJustifyContent(JustifyContent.FLEX_START);

        recyclerViewGuide.setAdapter(adapter);
        recyclerViewGuide.setLayoutManager(layout);

        updateHeaderStatus(view);

        return view;
    }

    public void updateHeaderStatus(View view) {

        SharedPreferences login_info = getActivity().getSharedPreferences("login_info", 0);

        TextView msg = (TextView) view.findViewById(R.id.welcome_msg);
        TextView status = (TextView) view.findViewById(R.id.updated_status);

        String period_of_day = "";

        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);

        if (currentHour >= 6 && currentHour < 12) {
            period_of_day = getResources().getString(R.string.home_welcome_message_dia);
        } else if (currentHour >= 12 && currentHour < 19) {
            period_of_day = getResources().getString(R.string.home_welcome_message_tarde);
        } else if (currentHour >= 19 || currentHour < 6) {
            period_of_day = getResources().getString(R.string.home_welcome_message_noite);
        }

        if (!login_info.getString("nome", "").equals("")) {
            msg.setText(String.format(getResources().getString(R.string.home_welcome_message),
                    period_of_day, login_info.getString("nome", "")));
        } else {
            msg.setText(period_of_day);
        }

        if (login_info.getInt("last_day", rightNow.get(Calendar.DAY_OF_YEAR)) != rightNow.get(Calendar.DAY_OF_YEAR)) {
            status.setText("Atualizado " + (rightNow.get(Calendar.DAY_OF_YEAR) - login_info.getInt("last_day", 0))
                    + " dias atrás");
        } else {
            if (login_info.getInt("last_hour", 0) > rightNow.get(Calendar.HOUR_OF_DAY)) {
                status.setText("Atualizado " + (rightNow.get(Calendar.HOUR_OF_DAY) - login_info.getInt("last_hour", 0))
                        + " horas atrás");
            } else {
                String hour = String.valueOf(login_info.getInt("last_hour", 0));
                String minute = String.valueOf(login_info.getInt("last_minute", 0));

                if (hour.length() < 2) {
                    hour = "0" + hour;
                }

                if (minute.length() < 2) {
                    minute = "0" + minute;
                }

                status.setText("Atualizado às " + hour + ":" + minute);
            }
        }

        LinearLayout offilne = (LinearLayout) view.findViewById(R.id.offline);

        if (Utils.isConnected(getActivity()) || ((MainActivity)getActivity()).mainWebView.pg_home_loaded) {
            offilne.setVisibility(View.GONE);
        } else {
            offilne.setVisibility(View.VISIBLE);
        }
    }

    protected void configGuide() { //Configura os botões da página Home
        guide = new ArrayList<>();
        guide.add(new Guide(getResources().getString(R.string.title_diarios), getResources().getString(R.string.home_diarios_description), R.drawable.ic_newspaper, R.color.orange_500));
        guide.add(new Guide(getResources().getString(R.string.title_boletim), getResources().getString(R.string.home_boletim_description), R.drawable.ic_list, R.color.teal_500));
        guide.add(new Guide(getResources().getString(R.string.title_horario), getResources().getString(R.string.home_horario_description), R.drawable.ic_access_alarm_black_24dp, R.color.blue_500));

        guide.add(new Guide(getResources().getString(R.string.title_materiais), getResources().getString(R.string.home_materiais_description), R.drawable.ic_closed_diary, R.color.cyan_500));
        guide.add(new Guide(getResources().getString(R.string.title_calendario), getResources().getString(R.string.home_calendario_description), R.drawable.ic_event_black_24dp, R.color.dark_purple_500));
        guide.add(new Guide(getResources().getString(R.string.title_documentos), getResources().getString(R.string.home_documentos_description), R.drawable.ic_check_form, R.color.brown_500));

        guide.add(new Guide(getResources().getString(R.string.email_assunto_bug), getResources().getString(R.string.home_bugreport_description), R.drawable.ic_bug_report_black_24dp, R.color.green_500));
        guide.add(new Guide(getResources().getString(R.string.email_assunto_sug), getResources().getString(R.string.home_sug_description), R.drawable.ic_chat_black_24dp, R.color.pink_500));
        guide.add(new Guide(getResources().getString(R.string.menu_share), getResources().getString(R.string.home_share_description), R.drawable.ic_share_black_24dp, R.color.amber_500));
    }

    @Override
    public void OnGuideClick(int position, View view) {
        switch (position) {
            case 0:
                ((MainActivity) getActivity()).clickDiarios();
                break;

            case 1:
                ((MainActivity) getActivity()).clickBoletim();
                break;

            case 2:
                ((MainActivity) getActivity()).clickHorario();
                break;

            case 3:
                ((MainActivity) getActivity()).clickMateriais();
                break;

            case 4:
                ((MainActivity) getActivity()).clickCalendario();
                break;

            case 5:
                ((MainActivity) getActivity()).clickDocumentos();
                break;

            case 6:
                ((MainActivity) getActivity()).clickBugReport();
                break;

            case 7:
                ((MainActivity) getActivity()).clickSug();
                break;

            case 8:
                ((MainActivity) getActivity()).clickShareApp();
                break;
        }
    }
}
