package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventCreateActivity;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentScheduleBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.utility.User;
import java.util.ArrayList;
import java.util.List;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.QueryBuilder;
import io.objectbox.reactive.DataSubscription;
import me.jlurena.revolvingweekview.WeekViewEvent;
import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

public class ScheduleFragment extends Fragment {
    private FragmentScheduleBinding binding;
    private DataSubscription sub1, sub2;
    private Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();

        sub1 = DataBase.get().getBoxStore().subscribe(Schedule.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> binding.weekView.notifyDatasetChanged());

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> binding.weekView.notifyDatasetChanged());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        binding = FragmentScheduleBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.weekView.setWeekViewLoader(ArrayList::new);

        binding.weekView.setOnEventClickListener((event, eventRect) -> {
            Intent intent = new Intent(getActivity(), EventViewActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            intent.putExtra("ID", Long.valueOf(event.getIdentifier()));
            startActivity(intent);
        });

        binding.weekView.setWeekViewLoader(() -> {
            boolean[][] hours = new boolean[24][7];
            WeekViewEvent[] minutes = new WeekViewEvent[24];

            List<WeekViewEvent> events = new ArrayList<>();

            List<Schedule> schedules;

            if (bundle == null) {
                schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                        .equal(Schedule_.year, User.getYear(pos)).and()
                        .equal(Schedule_.period, User.getPeriod(pos))
                        .build().find();
            } else {

                QueryBuilder<Schedule> builder = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                        .equal(Schedule_.year, User.getYear(pos)).and()
                        .equal(Schedule_.period, User.getPeriod(pos));

                builder.link(Schedule_.matter)
                        .equal(Matter_.id, bundle.getLong("ID"));

                schedules = builder.build().find();
            }

            if (!schedules.isEmpty()) {

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

                boolean r = true;
                int d1 = 0;

                while (r) {
                    if (!hours[firstIndex - 1][d1] && d1 < 7) {
                        d1++;
                        if (d1 == 7)
                            r = false;
                    } else {
                        firstIndex--;
                        d1 = 0;
                    }
                }

                binding.weekView.goToHour(firstIndex + (minutes[firstIndex].getStartTime().getMinute() * 0.0167));
                binding.weekView.setHeaderColumnTextColor(getResources().getColor(R.color.colorPrimary));
                binding.weekView.setTodayHeaderTextColor(getResources().getColor(R.color.colorPrimary));
                binding.empty.setVisibility(View.GONE);

            } else {
                binding.weekView.setHeaderColumnTextColor(getResources().getColor(R.color.transparent));
                binding.weekView.setTodayHeaderTextColor(getResources().getColor(R.color.transparent));
                binding.empty.setVisibility(View.VISIBLE);
            }

            return events;
        });

        binding.fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), EventCreateActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

}
