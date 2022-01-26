package com.tinf.qmobile.fragment;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.CalendarActivity;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.PerformanceActivity;
import com.tinf.qmobile.activity.ScheduleActivity;
import com.tinf.qmobile.adapter.HomeAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentHomeBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.Design;
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

public class HomeFragment extends Fragment implements OnUpdate {
    private FragmentHomeBinding binding;
    private DataSubscription sub1, sub2;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refresh;
    private MaterialToolbar toolbar;

    public void setParams(SwipeRefreshLayout refresh, FloatingActionButton fab, MaterialToolbar toolbar) {
        this.refresh = refresh;
        this.fab = fab;
        this.toolbar = toolbar;
    }

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
            intent.putExtra("LOOKUP", true);
            startActivity(intent);
        });

        binding.weekView.setWeekViewLoader(this::updateSchedule);

        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    refresh.setEnabled(scrollY == 0);

                    if (scrollY < oldScrollY && !fab.isShown())
                        fab.show();
                    else if (scrollY > oldScrollY && fab.isShown())
                        fab.hide();
                });

        Design.syncToolbar(toolbar, Design.canScroll(binding.scroll));

        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.recycler.setAdapter(new HomeAdapter(getContext()));

        binding.calendarLayout.setOnClickListener(view1 ->
                startActivity(new Intent(getActivity(), CalendarActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                Pair.create(fab, fab.getTransitionName()))
                                .toBundle()));

        binding.scheduleLayout.setOnClickListener(view1 ->
                startActivity(new Intent(getActivity(), ScheduleActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                Pair.create(fab, fab.getTransitionName()))
                                .toBundle()));

        binding.chartText.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), PerformanceActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(fab, fab.getTransitionName()))
                            .toBundle());
        });

        updateChart();
    }

    private List<WeekViewEvent> updateSchedule() {
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
            int maxInterval = 0;

            for (int h = firstIndex; h < 24; h++) {
                int sum = 0;
                for (int d = 0; d < 7; d++) {
                    if (hours[h][d])
                        sum++;
                }
                if (sum == 0)
                    maxInterval++;

                if (maxInterval > 1)
                    break;
                else
                    lastIndex = h;
            }

            while (minutes[lastIndex] == null)
                lastIndex--;

            params.height = Design.dpiToPixels(
                    ((minutes[lastIndex].getEndTime().getHour() * 60) + minutes[lastIndex].getEndTime().getMinute()) -
                    ((minutes[firstIndex].getStartTime().getHour() * 60) + minutes[firstIndex].getStartTime().getMinute()) + 45);

            binding.weekView.goToDay(DayOfWeek.MONDAY);
            binding.weekView.goToHour(firstIndex + (minutes[firstIndex].getStartTime().getMinute() * 0.0167));

            binding.emptySchedule.setVisibility(View.GONE);
        } else {
            params.height = Design.dpiToPixels(0);

            binding.emptySchedule.setVisibility(View.VISIBLE);
        }

        binding.weekView.setLayoutParams(params);

        return events;
    }

    private void updateChart() {
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

        boolean allEmpty = true;

        for (int i = 0; i < matters.size(); i++) {
            Matter matter = matters.get(i);

            axisMatter.add(new AxisValue(i).setLabel(matter.getLabel()));

            List<SubcolumnValue> values = new ArrayList<>();
            Period period = matter.getLastPeriod();

            if (period != null)
                allEmpty = false;

            values.add(new SubcolumnValue(period == null ? 0 : period.getPlotGrade(), matter.getColor())
                    .setLabel(period == null ? "" : period.getLabel()));

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

        binding.chartLayout.setVisibility(allEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onScrollRequest() {
        binding.scroll.smoothScrollTo(0, 0);
    }

    @Override
    public void onDateChanged() {
        binding.weekView.notifyDatasetChanged();
        binding.calendarLayout.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
        updateSchedule();
        updateChart();
        Design.syncToolbar(toolbar, Design.canScroll(binding.scroll));
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
                    updateSchedule();
                    updateChart();
                    Design.syncToolbar(toolbar, Design.canScroll(binding.scroll));
                });

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> {
                    binding.weekView.notifyDatasetChanged();
                    updateSchedule();
                    updateChart();
                    Design.syncToolbar(toolbar, Design.canScroll(binding.scroll));
                });
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
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

}
