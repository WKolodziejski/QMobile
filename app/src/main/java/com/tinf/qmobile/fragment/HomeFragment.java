package com.tinf.qmobile.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.CalendarActivity;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.PerformanceActivity;
import com.tinf.qmobile.activity.ScheduleActivity;
import com.tinf.qmobile.adapter.HomeAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentHomeBinding;
import com.tinf.qmobile.fragment.dialog.CreateFragment;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.User;

import org.threeten.bp.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscription;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import me.jlurena.revolvingweekview.WeekViewEvent;
import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

public class HomeFragment extends Fragment implements OnUpdate, OnResponse {
    private FragmentHomeBinding binding;
    private DataSubscription sub1, sub2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.calendarLayout.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);

        binding.weekView.setWeekViewLoader(ArrayList::new);

        binding.weekView.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAxisValue(MotionEvent.AXIS_Y) <= 90) {
                binding.scheduleLayout.dispatchTouchEvent(motionEvent);
                return true;

            } else {
                if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL)
                    binding.scheduleLayout.dispatchTouchEvent(motionEvent);

                return false;
            }
        });

        binding.weekView.setOnEventClickListener((event, eventRect) -> {
            Log.d("WEEK", event.getName());
            Intent intent = new Intent(getActivity(), EventViewActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            intent.putExtra("ID", Long.valueOf(event.getIdentifier()));
            startActivity(intent);
        });

        binding.weekView.setWeekViewLoader(() -> {

            boolean[][] hours = new boolean[24][7];
            WeekViewEvent[] minutes = new WeekViewEvent[24];

            List<WeekViewEvent> events = new ArrayList<>();
            List<Schedule> schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                    .equal(Schedule_.year, User.getYear(pos)).and()
                    .equal(Schedule_.period, User.getPeriod(pos))
                    .build().find();

            ViewGroup.LayoutParams params = binding.weekView.getLayoutParams();

            if (!schedules.isEmpty()) {

                for (Schedule schedule : schedules) {
                    WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), schedule.getTitle(),
                            schedule.getStartTime(), schedule.getEndTime());
                    event.setColor(schedule.getColor());
                    events.add(event);

                    int day = event.getStartTime().getDay().getValue();
                    int hour = event.getStartTime().getHour();

                    if (minutes[hour] != null) {
                        if (event.getEndTime().isAfter(minutes[hour].getEndTime())) {
                            minutes[hour] = event;
                            hours[hour][day] = true;
                        }
                    } else {
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

                params.height = Math.round((((minutes[lastIndex].getEndTime().getHour() * 60) + minutes[lastIndex].getEndTime().getMinute())
                        - ((minutes[firstIndex].getStartTime().getHour() * 60) + minutes[firstIndex].getStartTime().getMinute()) + 45)
                        * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                binding.weekView.goToDay(DayOfWeek.MONDAY);
                binding.weekView.goToHour(firstIndex + (minutes[firstIndex].getStartTime().getMinute() * 0.0167));

                binding.empty.setVisibility(View.GONE);
            } else {
                params.height = Math.round((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

                binding.empty.setVisibility(View.VISIBLE);
            }

            binding.weekView.setLayoutParams(params);

            return events;
        });

        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    ((MainActivity) getActivity()).binding.refresh.setEnabled(scrollY == 0);

                    if (scrollY < oldScrollY && !binding.fab.isShown())
                        binding.fab.show();
                    else if(scrollY > oldScrollY && binding.fab.isShown())
                        binding.fab.hide();
                });

        binding.fab.setOnClickListener(v -> new CreateFragment().show(getChildFragmentManager(), "sheet_create"));

        if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
            binding.offline.setVisibility(View.VISIBLE);
            binding.offlineLastUpdate.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
        } else {
            binding.offline.setVisibility(View.GONE);
        }

        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.recycler.setAdapter(new HomeAdapter(getContext()));

        binding.calendarLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), CalendarActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(binding.fab, binding.fab.getTransitionName()))
                            .toBundle());
        });

        binding.scheduleLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), ScheduleActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(binding.fab, binding.fab.getTransitionName()))
                            .toBundle());
        });

        /*binding.chartText.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), PerformanceActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(binding.fab, binding.fab.getTransitionName()))
                            .toBundle());
        });

        updateChart();*/
    }

    /*private void updateChart() {
        List<Matter> matters = DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .query()
                .order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos))
                .and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build()
                .find();

        List<AxisValue> axisMatter = new ArrayList<>();
        List<Column> columns = new ArrayList<>();

        for (int i = 0; i < matters.size(); i++) {
            Matter matter = matters.get(i);

            axisMatter.add(new AxisValue(i).setLabel(matter.getLabel()));

            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(matter.getLastPeriod().getPlotGrade(),
                    matter.getColor())
                    .setLabel(matter.getLastPeriod().getLabel()));
            columns.add(new Column(values)
                    .setHasLabels(true));
                    //.setHasLabelsOnlyForSelected(true));
        }

        List<AxisValue> axisValues = new ArrayList<>();
        axisValues.add(new AxisValue(6).setLabel(""));
        ColumnChartData data = new ColumnChartData();
        data.setColumns(columns);
        data.setValueLabelBackgroundEnabled(false);
        data.setValueLabelsTextColor(getResources().getColor(R.color.colorPrimaryLight));
        data.setAxisYLeft(new Axis(axisValues)
                .setHasLines(true)
                .setLineColor(ContextCompat.getColor(getContext(), R.color.error))
                .setHasSeparationLine(false)
                .setHasTiltedLabels(false));
        data.setAxisXBottom(new Axis(axisMatter));
        binding.chart.setColumnChartData(data);
        binding.chart.setZoomEnabled(false);
        binding.chart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {

            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                Toast.makeText(getContext(), matters.get(i).getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }

        });
    }*/

    @Override
    public void onScrollRequest() {
        binding.scroll.smoothScrollTo(0, 0);
    }

    @Override
    public void onDateChanged() {
        binding.weekView.notifyDatasetChanged();
        binding.calendarLayout.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
        //updateChart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sub1 = DataBase.get().getBoxStore().subscribe(Schedule.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> {
                    binding.weekView.notifyDatasetChanged();
                    //updateChart();
                });

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> {
                    binding.weekView.notifyDatasetChanged();
                    //updateChart();
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
        Client.get().addOnResponseListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
        Client.get().addOnResponseListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
        Client.get().removeOnResponseListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
        Client.get().removeOnResponseListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

    @Override
    public void onStart(int pg) {

    }

    @Override
    public void onFinish(int pg) {
        if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
            binding.offline.setVisibility(View.VISIBLE);
            binding.offlineLastUpdate.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
        } else {
            binding.offline.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(int pg, String error) {

    }

    @Override
    public void onAccessDenied(int pg, String message) {
        if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
            binding.offline.setVisibility(View.VISIBLE);
            binding.offlineLastUpdate.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
        } else {
            binding.offline.setVisibility(View.GONE);
        }
    }

}
