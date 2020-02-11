package com.tinf.qmobile.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.HorarioActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.activity.calendar.CalendarioActivity;
import com.tinf.qmobile.activity.calendar.EventCreateActivity;
import com.tinf.qmobile.adapter.calendar.EventsAdapter;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.calendar.EventImage;
import com.tinf.qmobile.model.calendar.EventImage_;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventSimple_;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.EventUser_;
import com.tinf.qmobile.model.calendar.base.EventBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

import static com.tinf.qmobile.activity.calendar.EventCreateActivity.EVENT;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;

public class HomeFragment extends Fragment implements OnUpdate {
    private NestedScrollView nestedScrollView;
    private EventsAdapter calendarioAdapter;
    private List<Matter> matters;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    private void loadData() {
        matters = DataBase.get().getMatters();

        Calendar current = Calendar.getInstance();
        current.setTime(new Date());
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        Box<EventUser> eventUserBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
        Box<Journal> eventJournalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
        Box<EventImage> eventImageBox = DataBase.get().getBoxStore().boxFor(EventImage.class);
        Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);

        List<EventBase> events = new ArrayList<>();

        events.addAll(eventUserBox.query().greater(EventUser_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventJournalBox.query().greater(Journal_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventImageBox.query().greater(EventImage_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventSimpleBox.query().greater(EventSimple_.startTime, current.getTimeInMillis() - 1).build().find());

        Collections.sort(events, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

        if (events.size() < 5) {
            events = new ArrayList<>();

            events.addAll(eventUserBox.query().build().find());
            events.addAll(eventJournalBox.query().build().find());
            events.addAll(eventImageBox.query().build().find());
            events.addAll(eventSimpleBox.query().build().find());

            Collections.sort(events, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

            if (events.size() > 5) {
                events = events.subList(0, 5);
            } else {
                events = events.subList(0, events.size());
            }

        } else {
            events = events.subList(0, 5);
        }

        if (calendarioAdapter == null) {
            calendarioAdapter = new EventsAdapter(getActivity(), events);
        } else {
            calendarioAdapter.update(events);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showHorario(view);

        view.post(() -> {

            ((MainActivity) getActivity()).fab.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EventCreateActivity.class);
                intent.putExtra("TYPE", EVENT);
                startActivity(intent);
            });

            /*ImageView image = (ImageView) view.findViewById(R.id.home_image);
            Bitmap bitmap = BitmapFactory.decodeFile(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    + "/" + User.getCredential(User.REGISTRATION));

            Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());

            image.setImageBitmap(croppedBmp);*/

            nestedScrollView = (NestedScrollView) view.findViewById(R.id.home_scroll);

            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);
                        if (scrollY < oldScrollY && !((MainActivity) getActivity()).fab.isShown())
                            ((MainActivity) getActivity()).fab.show();
                        else if(scrollY > oldScrollY && ((MainActivity) getActivity()).fab.isShown())
                            ((MainActivity) getActivity()).fab.hide();
                    });

            showOffline(view);
            showCalendar(view);

            ((MainActivity) getActivity()).fab.setIconResource(R.drawable.ic_add);
            ((MainActivity) getActivity()).fab.show();
        });
    }

    @OnClick(R.id.home_website)
    public void openQSite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(User.getURL() + INDEX + PG_LOGIN));
        startActivity(browserIntent);
    }

    private void showOffline(View view) {

            CardView offline = (CardView) view.findViewById(R.id.home_offline);

            if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
                offline.setVisibility(View.VISIBLE);

                TextView text = (TextView) view.findViewById(R.id.offline_last_update);
                text.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
            } else {
                offline.setVisibility(View.GONE);
            }
    }

    private void showCalendar(View view) {

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_home);

            recyclerView.setAdapter(calendarioAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            LinearLayout calendario = (LinearLayout) view.findViewById(R.id.home_calendario);

            calendario.setOnClickListener(v -> {
                //Pair statusAnim = Pair.create(recyclerView, recyclerView.getTransitionName());
                Pair driverBundleAnim = Pair.create(((MainActivity) getActivity()).fab, ((MainActivity) getActivity()).fab.getTransitionName());

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), driverBundleAnim);
                startActivity(new Intent(getActivity(), CalendarioActivity.class), options.toBundle());
            });

    }

    private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_home);

        weekView.setWeekViewLoader(() -> {
            double firstHour = 24;

            List<WeekViewEvent> events = new ArrayList<>();
            List<Schedule> schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                    .equal(Schedule_.year, User.getYear(pos)).and()
                    .equal(Schedule_.period, User.getPeriod(pos)).and()
                    .equal(Schedule_.isFromSite_, true)
                    .build().find();

            for (Schedule schedule : schedules) {
                WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), schedule.getTitle(),
                        schedule.getStartTime(), schedule.getEndTime());
                event.setColor(schedule.getColor());
                events.add(event);

                if (event.getStartTime().getHour() < firstHour) {
                    firstHour = event.getStartTime().getHour();
                    firstHour += event.getStartTime().getMinute() * 0.0167;
                }
            }

            weekView.goToDay(DayOfWeek.MONDAY);
            weekView.goToHour(firstHour);

            return events;
        });

        weekView.notifyDatasetChanged();
        weekView.setOnEventClickListener((event, eventRect) -> {
            Matter matter = DataBase.get().getBoxStore().boxFor(Matter.class).query()
                    .equal(Matter_.title_, event.getName())
                    .equal(Matter_.year_, User.getYear(pos)).and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build().findUnique();
            if (matter != null) {
                Intent intent = new Intent(getContext(), MateriaActivity.class);
                intent.putExtra("ID", matter.id);
                intent.putExtra("PAGE", MateriaActivity.SCHEDULE);
                startActivity(intent);
            }
        });

        LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);

        horario.setOnClickListener(v -> {
            Pair driverBundleAnim = Pair.create(((MainActivity) getActivity()).fab, ((MainActivity) getActivity()).fab.getTransitionName());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), driverBundleAnim);
            startActivity(new Intent(getActivity(), HorarioActivity.class), options.toBundle());
        });
    }

    @Override
    public void onScrollRequest() {
        if (nestedScrollView != null) {
            nestedScrollView.smoothScrollTo(0, 0);
        }
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_LOGIN || pg == UPDATE_REQUEST) {
            loadData();
            if (getView() != null) {
                showHorario(getView());
                showOffline(getView());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //((MainActivity) getActivity()).fab.hide();
    }

}
