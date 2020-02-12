package com.tinf.qmobile.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import static com.tinf.qmobile.activity.calendar.EventCreateActivity.EVENT;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;

public class HomeFragment extends Fragment implements OnUpdate {
    @BindView(R.id.weekView_home)   WeekView weekView;
    @BindView(R.id.home_scroll)     NestedScrollView nestedScrollView;
    private EventsAdapter calendarAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).fab.setIconResource(R.drawable.ic_add);
        ((MainActivity) getActivity()).fab.show();
        ((MainActivity) getActivity()).fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventCreateActivity.class);
            intent.putExtra("TYPE", EVENT);
            startActivity(intent);
        });

        BoxStore boxStore = DataBase.get().getBoxStore();

        boxStore.subscribe(Schedule.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> weekView.notifyDatasetChanged());

        boxStore.subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> weekView.notifyDatasetChanged());
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

        Log.d("Home", "View Created");

        weekView.setWeekViewLoader(ArrayList::new);

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

        updateSchedule();

            /*ImageView image = (ImageView) view.findViewById(R.id.home_image);
            Bitmap bitmap = BitmapFactory.decodeFile(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    + "/" + User.getCredential(User.REGISTRATION));

            Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());

            image.setImageBitmap(croppedBmp);*/

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);
                    if (scrollY < oldScrollY && !((MainActivity) getActivity()).fab.isShown())
                        ((MainActivity) getActivity()).fab.show();
                    else if(scrollY > oldScrollY && ((MainActivity) getActivity()).fab.isShown())
                        ((MainActivity) getActivity()).fab.hide();
                });
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DataBase.get().getBoxStore().runInTxAsync(() -> {

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

            calendarAdapter = new EventsAdapter(getActivity(), events);

        }, (result, error) -> {
            recyclerView.setAdapter(calendarAdapter);
        });

    }

    private void updateSchedule() {
        Log.d("Home", "Update Schedule");

        DataBase.get().getBoxStore().runInTxAsync(() -> {
            weekView.setWeekViewLoader(() -> {
                boolean[][] hours = new boolean[24][7];
                WeekViewEvent[] minutes = new WeekViewEvent[24];

                List<WeekViewEvent> events = new ArrayList<>();
                List<Schedule> schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                        .equal(Schedule_.year, User.getYear(pos)).and()
                        .equal(Schedule_.period, User.getPeriod(pos))
                        .build().find();

                for (Schedule schedule : schedules) {
                    WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), schedule.getTitle(),
                            schedule.getStartTime(), schedule.getEndTime());
                    event.setColor(schedule.getColor());
                    events.add(event);

                    int day = event.getStartTime().getDay().getValue();
                    int hour = event.getStartTime().getHour();

                    if (!hours[hour][day]) {
                        minutes[hour] = event;
                        hours[hour][day] = true;
                    }
                }

                int firstIndex = 0;
                int parc1 = 0;

                for (int h = 0; h < 24; h++) {
                    int sum = 0;
                    for (int d = 0; d < 7; d++) {
                        if (hours[h][d]) {
                            sum++;
                        }
                    }
                    if (sum > parc1) {
                        firstIndex = h;
                        parc1 = sum;
                    }
                }

                int lastIndex = firstIndex;

                for (int h = firstIndex; h < 24; h++) {
                    int sum = 0;
                    for (int d = 0; d < 7; d++) {
                        if (hours[h][d])
                            sum++;
                    }
                    if (sum == 0)
                        break;
                    else
                        lastIndex = h;
                }

                ViewGroup.LayoutParams params = weekView.getLayoutParams();
                params.height = Math.round((((minutes[lastIndex].getEndTime().getHour() * 60) + minutes[lastIndex].getEndTime().getMinute()) - ((minutes[firstIndex].getStartTime().getHour() * 60) + minutes[firstIndex].getStartTime().getMinute()) + 45) * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                weekView.setLayoutParams(params);

                weekView.goToDay(DayOfWeek.MONDAY);
                weekView.goToHour(firstIndex + (minutes[firstIndex].getStartTime().getMinute() * 0.0167));

                Log.d("Home", "View listener");

                return events;
            });
        }, (result, error) -> {
            weekView.notifyDatasetChanged();
        });

    }

    @OnClick(R.id.home_website)
    public void openQSite(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(User.getURL() + INDEX + PG_LOGIN)));
    }

    @OnClick(R.id.home_calendario)
    public void openCalendar(View view) {
        startActivity(new Intent(getActivity(), CalendarioActivity.class),
                ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        Pair.create(((MainActivity) getActivity()).fab,
                                ((MainActivity) getActivity()).fab.getTransitionName()))
                        .toBundle());
    }

    @OnClick(R.id.home_horario)
    public void openSchedule(View view) {
        startActivity(new Intent(getActivity(), HorarioActivity.class),
                ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        Pair.create(((MainActivity) getActivity()).fab,
                                ((MainActivity) getActivity()).fab.getTransitionName()))
                        .toBundle());
    }

    @Override
    public void onScrollRequest() {
        if (nestedScrollView != null) {
            nestedScrollView.smoothScrollTo(0, 0);
        }
    }

    @Override
    public void onUpdate(int pg) {

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

}
