package com.tinf.qmobile.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.model.ViewType.MATTER;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

import android.app.ActivityOptions;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.CalendarActivity;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.activity.PerformanceActivity;
import com.tinf.qmobile.activity.ScheduleActivity;
import com.tinf.qmobile.adapter.EventsAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.databinding.FragmentHomeBinding;
import com.tinf.qmobile.model.calendar.Event;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.ChartUtils;
import com.tinf.qmobile.utility.DesignUtils;
import com.tinf.qmobile.utility.EventsUtils;
import com.tinf.qmobile.utility.ScheduleUtils;
import com.tinf.qmobile.utility.UserUtils;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscription;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import me.jlurena.revolvingweekview.DayTime;
import me.jlurena.revolvingweekview.WeekViewEvent;

public class HomeFragment extends BaseFragment implements OnData<Event>, OnUpdate {
  private FragmentHomeBinding binding;
  private DataSubscription sub1, sub2;
  private FloatingActionButton fab;

  public void setParams(MaterialToolbar toolbar, SwipeRefreshLayout refresh,
                        FloatingActionButton fab) {
    super.setParams(toolbar, refresh);
    this.fab = fab;
  }

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    sub1 = DataBase.get().getBoxStore().subscribe(Schedule.class)
                   .onlyChanges()
                   .on(AndroidScheduler.mainThread())
                   .onError(Throwable::printStackTrace)
                   .observer(data -> {
                     binding.weekView.notifyDatasetChanged();
                     updateSchedule();
                     updateChart();
                     DesignUtils.syncToolbar(toolbar, DesignUtils.canScroll(binding.scroll));
                   });

    sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                   .onlyChanges()
                   .on(AndroidScheduler.mainThread())
                   .onError(Throwable::printStackTrace)
                   .observer(data -> {
                     binding.weekView.notifyDatasetChanged();
                     updateSchedule();
                     updateChart();
                     DesignUtils.syncToolbar(toolbar, DesignUtils.canScroll(binding.scroll));
                   });
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    binding = FragmentHomeBinding.bind(view);
    updateSchedule();
    updateDate();
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    registerForContextMenu(binding.scheduleTune);

    binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                                                 (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                                                   refresh.setEnabled(scrollY == 0);

                                                   if (scrollY < oldScrollY && !fab.isShown())
                                                     fab.show();
                                                   else if (scrollY > oldScrollY && fab.isShown())
                                                     fab.hide();
                                                 });

    buildSchedule();
    buildEvents();
    buildChart();

    new Handler(Looper.getMainLooper()).postDelayed(
        () -> DesignUtils.syncToolbar(toolbar, DesignUtils.canScroll(binding.scroll)), 50);
  }

  private void updateDate() {
    if (UserUtils.getYears().length > pos)
      binding.date.setText(UserUtils.getYears()[pos]);
  }

  private void buildChart() {
    binding.chartLayout.setOnClickListener(v ->
                                               startActivity(new Intent(getContext(),
                                                                        PerformanceActivity.class)));

    binding.chart.setZoomEnabled(false);
    binding.chart.setZoomType(ZoomType.HORIZONTAL);
    binding.chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

    binding.chartTune.setOnClickListener(v -> {
      PopupMenu popup = new PopupMenu(getContext(), v);
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.tune_chart, popup.getMenu());

      popup.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == R.id.chart_10) {
          ChartUtils.setAverageGrade(10);
          updateChart();
          return true;
        }

        if (item.getItemId() == R.id.chart_100) {
          ChartUtils.setAverageGrade(100);
          updateChart();
          return true;
        }

        return false;
      });
      popup.show();
    });
  }

  private void buildEvents() {
    binding.calendarLayout.setVisibility(pos == 0 ? VISIBLE : GONE);

    binding.recycler.setLayoutManager(
        new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    binding.recycler.setItemAnimator(null);
    binding.recycler.setAdapter(new EventsAdapter(getContext(), this::onUpdate));

    binding.calendarBtn.setOnClickListener(v -> {
      Bundle transition = null;

      if (fab != null) {
        try {
          transition = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                                                    Pair.create(fab,
                                                                                fab.getTransitionName()))
                                      .toBundle();
        } catch (Exception ignored) {
        }
      }

      startActivity(new Intent(getContext(), CalendarActivity.class), pos == 0 ? transition : null);
    });

    binding.calendarTune.setOnClickListener(v -> {
      PopupMenu popup = new PopupMenu(getContext(), v);
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.tune_events, popup.getMenu());

      popup.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == R.id.events_1week) {
          EventsUtils.setEventsLength(1);
          DataBase.get().getEventsDataProvider().updateList();
          return true;
        }

        if (item.getItemId() == R.id.events_2week) {
          EventsUtils.setEventsLength(2);
          DataBase.get().getEventsDataProvider().updateList();
          return true;
        }

        if (item.getItemId() == R.id.events_all) {
          EventsUtils.setEventsLength(0);
          DataBase.get().getEventsDataProvider().updateList();
          return true;
        }

        return false;
      });
      popup.show();
    });
  }

  private void buildSchedule() {
    binding.weekView.setWeekViewLoader(ArrayList::new);
    binding.scheduleTune.setVisibility(pos == 0 ? VISIBLE : GONE);
    DesignUtils.syncToolbar(toolbar, DesignUtils.canScroll(binding.scroll));
    updateFab();
    updateChart();

    binding.weekView.setOnEventClickListener((event, eventRect) -> {
      Log.d("WEEK", event.getName());
      Intent intent = new Intent(getActivity(), EventViewActivity.class);
      intent.putExtra("TYPE", SCHEDULE);
      intent.putExtra("ID", Long.valueOf(event.getIdentifier()));
      intent.putExtra("LOOKUP", true);
      startActivity(intent);
    });

    binding.weekView.setWeekViewLoader(this::updateSchedule);

    binding.scheduleLayout.setOnClickListener(v -> {
      Bundle transition = null;

      if (fab != null) {
        try {
          transition = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                                                    Pair.create(fab,
                                                                                fab.getTransitionName()))
                                      .toBundle();
        } catch (Exception ignored) {
        }
      }

      startActivity(new Intent(getContext(), ScheduleActivity.class), pos == 0 ? transition : null);
    });

    binding.scheduleTune.setOnClickListener(v -> {
      PopupMenu popup = new PopupMenu(getContext(), v);
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.tune_schedule, popup.getMenu());

      popup.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == R.id.schedule_start) {
          new TimePickerDialog(getContext(), (timePicker, h, m) -> {
            DayTime current =
                new DayTime(1, ScheduleUtils.getStartHour(), ScheduleUtils.getStartMin());
            DayTime end = new DayTime(1, ScheduleUtils.getEndHour(), ScheduleUtils.getEndMin());
            DayTime start = new DayTime(1, h, m);

            if (start.isAfter(end))
              end = new DayTime(start);

            if (current.isSame(start))
              return;

            ScheduleUtils.setStartHour(h);
            ScheduleUtils.setStartMin(m);
            ScheduleUtils.setEndHour(end.getHour());
            ScheduleUtils.setEndMin(end.getMinute());
            ScheduleUtils.setAuto(false);
            binding.weekView.notifyDatasetChanged();
          }, ScheduleUtils.getStartHour(), ScheduleUtils.getStartMin(), true).show();

          return true;
        }

        if (item.getItemId() == R.id.schedule_end) {
          new TimePickerDialog(getContext(), (timePicker, h, m) -> {
            DayTime current = new DayTime(1, ScheduleUtils.getEndHour(), ScheduleUtils.getEndMin());
            DayTime start =
                new DayTime(1, ScheduleUtils.getStartHour(), ScheduleUtils.getStartMin());
            DayTime end = new DayTime(1, h, m);

            if (end.isBefore(start))
              start = new DayTime(end);

            if (current.isSame(start))
              return;

            ScheduleUtils.setEndHour(h);
            ScheduleUtils.setEndMin(m);
            ScheduleUtils.setStartHour(start.getHour());
            ScheduleUtils.setStartMin(start.getMinute());
            ScheduleUtils.setAuto(false);
            binding.weekView.notifyDatasetChanged();
          }, ScheduleUtils.getEndHour(), ScheduleUtils.getEndMin(), true).show();

          return true;
        }

        if (item.getItemId() == R.id.schedule_auto) {
          ScheduleUtils.setAuto(true);
          binding.weekView.notifyDatasetChanged();
          return true;
        }

        return false;
      });
      popup.show();
    });
  }

  private void calculateScheduleSize1(boolean[][] week, WeekViewEvent[] minutes,
                                      ViewGroup.LayoutParams params) {
    int firstIndex = 0; //First index
    int parc1 = 0;      //Last biggest sum
    boolean firstIndexFound = false;

    // For each hour of the day
    for (int h = 0; h < 24; h++) {
      int sum = 0;

      // For each day of the week
      for (int d = 1; d < 6; d++) {

        // Sum of events in the same hour, for the whole week
        if (week[h][d]) {
          sum++;
        }
      }

      // If sum is bigger than the last one, the first index is set to the current hour
      if (sum > (parc1 + 1)) {
        firstIndex = h;
        parc1 = sum;
        firstIndexFound = true;
      }
    }

    // If no first index found, repeats the search without the parc1 + 1 param
    if (!firstIndexFound) {
      for (int h = 0; h < 24; h++) {
        int sum = 0;

        // For each day of the week
        for (int d = 1; d < 6; d++) {

          // Sum of events in the same hour, for the whole week
          if (week[h][d]) {
            sum++;
          }
        }

        // If sum is bigger than the last one, the first index is set to the current hour
        if (sum > parc1) {
          firstIndex = h;
          parc1 = sum;
        }
      }
    }

    // To this point, firstIndex the first index with most schedules
    // Now, we need to find the first index taking into account some minutes gap

    int lastIndex = firstIndex;
    int maxInterval = 0;

    Log.d("FIRST", String.valueOf(firstIndex));

    for (int h = firstIndex; h < 24; h++) {
      int sum = 0;

      for (int d = 0; d < 7; d++)
        if (week[h][d])
          sum++;

      if (sum == 0)
        maxInterval++;

      if (maxInterval > 1)
        break;
      else
        lastIndex = h;
    }

    Log.d(String.valueOf(lastIndex), Arrays.toString(minutes));

    while (lastIndex > 1 && minutes[lastIndex] == null)
      lastIndex--;

    if (minutes[lastIndex] == null) {
      while (lastIndex < 23 && minutes[lastIndex] == null)
        lastIndex++;
    }

    while (firstIndex > 1 && minutes[firstIndex] == null)
      firstIndex--;

    if (minutes[firstIndex] == null) {
      while (firstIndex < 23 && minutes[firstIndex] == null)
        firstIndex++;
    }

    if (minutes[lastIndex] == null) {
      params.height = DesignUtils.dpiToPixels(0);
      binding.emptySchedule.setVisibility(VISIBLE);
      binding.weekLayout.setVisibility(GONE);
    } else {
      Log.d("First index", String.valueOf(firstIndex));
      Log.d("Last index", String.valueOf(lastIndex));

      int startHour = minutes[firstIndex].getStartTime().getHour();
      int startMin = minutes[firstIndex].getStartTime().getMinute();
      int endHour = minutes[lastIndex].getEndTime().getHour();
      int endMin = minutes[lastIndex].getEndTime().getMinute();

      params.height =
          DesignUtils.dpiToPixels(((endHour * 60) + endMin) - ((startHour * 60) + startMin) + 8);

      binding.weekView.goToDay(DayOfWeek.MONDAY);
      binding.weekView.goToHour(firstIndex + (startMin * 0.0167));

      binding.emptySchedule.setVisibility(GONE);
      binding.weekLayout.setVisibility(VISIBLE);

      ScheduleUtils.setStartHour(startHour);
      ScheduleUtils.setStartMin(startMin);
      ScheduleUtils.setEndHour(endHour);
      ScheduleUtils.setEndMin(endMin);
    }
  }

  private void calculateScheduleSize2(List<Schedule> schedules, ViewGroup.LayoutParams params) {
    int hoursCount[] = new int[24];

    for (Schedule schedule : schedules) {
      int startHour = schedule.getStartTime().getHour();
      int endHour = schedule.getEndTime().getHour();

      for (int h = startHour; h < endHour; h++) {
        hoursCount[h]++;
      }
    }

    int firstHour = 0;
    int lastHour = 24;

    for (int i = 0; i < hoursCount.length; i++) {
      if (hoursCount[i] > 0) {
        firstHour = i;
        break;
      }
    }

    for (int i = 0; i < hoursCount.length; i++) {
      if (hoursCount[i] > 0) {
        lastHour = i;
      }
    }

    // Até aqui, temos a primeira e última horas
    // Precisamos agora refinar a busca para remover espaços muito grandes de tempo

    // Se a primeira hora não for no fim do dia e está em um gap
    // Deve buscar a próxima primeira hora
    if (firstHour < 23 && hoursCount[firstHour + 1] == 0) {
      int gap = 1;

      for (int i = firstHour + 1; i < lastHour; i++) {
        if (hoursCount[i] == 0) {
          gap++;
        }

        if (gap > 1 && hoursCount[i] > 0) {
          break;
        }
      }

      if (firstHour + gap < lastHour) {
        firstHour += gap;
      }
    }

    // Se a última hora não for no início do dia e está em um gap
    // Deve buscar a próxima última hora
    if (lastHour > 0 && hoursCount[lastHour - 1] == 0) {
      int gap = 1;

      for (int i = lastHour - 1; i > firstHour; i--) {
        if (hoursCount[i] == 0) {
          gap++;
        }

        if (gap > 1 && hoursCount[i] > 0) {
          break;
        }
      }

      if (lastHour - gap > firstHour) {
        lastHour -= gap;
      }
    }

    int startHour = firstHour;
    int startMin = 0;
    int endHour = lastHour + 1;
    int endMin = 0;

    params.height =
        DesignUtils.dpiToPixels(((endHour * 60) + endMin) - ((startHour * 60) + startMin) + 8);

    binding.weekView.goToDay(DayOfWeek.MONDAY);
    binding.weekView.goToHour(startHour + (startMin * 0.0167));

    binding.emptySchedule.setVisibility(GONE);
    binding.weekLayout.setVisibility(VISIBLE);

    ScheduleUtils.setStartHour(startHour);
    ScheduleUtils.setStartMin(startMin);
    ScheduleUtils.setEndHour(endHour);
    ScheduleUtils.setEndMin(endMin);
  }

  private List<WeekViewEvent> updateSchedule() {
    boolean[][] week = new boolean[24][7];
    WeekViewEvent[] minutes = new WeekViewEvent[24];

    List<WeekViewEvent> events = new ArrayList<>();
    List<Schedule> schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                                       .equal(Schedule_.year, UserUtils.getYear(pos)).and()
                                       .equal(Schedule_.period, UserUtils.getPeriod(pos))
                                       .build().find();

    ViewGroup.LayoutParams params = binding.weekView.getLayoutParams();

    if (schedules.isEmpty()) {
      params.height = DesignUtils.dpiToPixels(0);
      binding.emptySchedule.setVisibility(VISIBLE);
      binding.weekLayout.setVisibility(GONE);

    } else {
      for (Schedule schedule : schedules) {
        WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), schedule.getTitle(),
                                                schedule.getStartTime(), schedule.getEndTime());
        event.setColor(schedule.getColor());
        events.add(event);

        int day = event.getStartTime().getDay().getValue() % 7;
        int hour = event.getStartTime().getHour();

        if (day == 0 || day == 6)
          continue;

        if (minutes[hour] == null || event.getEndTime().isAfter(minutes[hour].getEndTime())) {
          minutes[hour] = event;
          week[hour][day] = true;
        }
      }

      if (ScheduleUtils.isAuto()) {
        calculateScheduleSize1(week, minutes, params);
      } else {
        int startHour = ScheduleUtils.getStartHour();
        int startMin = ScheduleUtils.getStartMin();
        int endHour = ScheduleUtils.getEndHour();
        int endMin = ScheduleUtils.getEndMin();

        params.height =
            DesignUtils.dpiToPixels(((endHour * 60) + endMin) - ((startHour * 60) + startMin) + 8);

        binding.weekView.goToDay(DayOfWeek.MONDAY);
        binding.weekView.goToHour(startHour + (startMin * 0.0167));

        binding.emptySchedule.setVisibility(GONE);
        binding.weekLayout.setVisibility(VISIBLE);
      }
    }

    binding.weekView.setLayoutParams(params);

    return events;
  }

  private void updateChart() {
    List<Matter> matters = DataBase.get().getBoxStore()
                                   .boxFor(Matter.class)
                                   .query()
                                   .order(Matter_.title_)
                                   .equal(Matter_.year_, UserUtils.getYear(pos))
                                   .and()
                                   .equal(Matter_.period_, UserUtils.getPeriod(pos))
                                   .build()
                                   .find();

    List<AxisValue> axisMatter = new ArrayList<>();
    List<Column> columns = new ArrayList<>();

    int m = 0;
    boolean isCurrentPeriod = false;

    for (Matter matter : matters) {
      if (matter.getSituation().equals("Cursando")) {
        isCurrentPeriod = true;
        break;
      }
    }

    if (isCurrentPeriod) {
      for (int i = 0; i < matters.size(); i++) {
        Matter matter = matters.get(i);

        axisMatter.add(new AxisValue(i).setLabel(matter.getLabel()));

        List<SubcolumnValue> values = new ArrayList<>();

        Period period = matter.getLastPeriod();

        if (period != null)
          for (Journal journal : period.journals)
            if (journal.getGrade_() >= 0)
              m++;

        values.add(new SubcolumnValue(period == null ? 0 : period.getPartialGrade(), matter.getColor())
                       .setLabel(period == null ? "" : period.getPartialGradeString()));

        columns.add(new Column(values)
                        .setHasLabels(true));
      }
    } else {
      for (int i = 0; i < matters.size(); i++) {
        Matter matter = matters.get(i);

        axisMatter.add(new AxisValue(i).setLabel(matter.getLabel()));

        List<SubcolumnValue> values = new ArrayList<>();

        for (Period period : matter.periods)
          for (Journal journal : period.journals)
            if (journal.getGrade_() >= 0)
              m++;

        values.add(new SubcolumnValue(matter.getMean_(), matter.getColor())
                       .setLabel(matter.getMean()));

        columns.add(new Column(values)
                        .setHasLabels(true));
      }
    }

    if (m > 1) {
      binding.chart.setVisibility(VISIBLE);
      binding.emptyChart.setVisibility(GONE);

      ColumnChartData data = new ColumnChartData();
      data.setColumns(columns);
      data.setValueLabelBackgroundEnabled(false);
      data.setValueLabelsTextColor(App.getContext().getColor(R.color.colorPrimaryLight));
      data.setAxisXBottom(new Axis(axisMatter));
      binding.chart.setColumnChartData(data);
      binding.chart.setZoomLevel(0f, 0f, matters.size() / 10f);
      binding.chart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {

        @Override
        public void onValueSelected(int l, int p, SubcolumnValue subcolumnValue) {
          Intent intent = new Intent(getContext(), MatterActivity.class);
          intent.putExtra("ID", matters.get(l).id);
          intent.putExtra("PAGE", MATTER);
          intent.putExtra("LOOKUP", false);
          startActivity(intent);
        }

        @Override
        public void onValueDeselected() {

        }

      });
    } else {
      binding.chart.setVisibility(GONE);
      binding.emptyChart.setVisibility(VISIBLE);
    }
  }

  private void updateFab() {
    if (fab != null) {
      fab.setVisibility(Client.pos == 0 ? VISIBLE : GONE);
    }
  }

  @Override
  public void onDateChanged() {
    binding.weekView.notifyDatasetChanged();
    binding.calendarLayout.setVisibility(pos == 0 ? VISIBLE : GONE);
    binding.scheduleTune.setVisibility(pos == 0 ? VISIBLE : GONE);
    updateFab();
    updateChart();
    updateDate();
    DesignUtils.syncToolbar(toolbar, DesignUtils.canScroll(binding.scroll));
  }

  @Override
  protected void onAddListeners() {
    DataBase.get().getEventsDataProvider().addOnDataListener(this);
    Client.get().addOnUpdateListener(this);
//        Design.syncToolbar(toolbar, Design.canScroll(binding.scroll));
  }

  @Override
  protected void onRemoveListeners() {
    DataBase.get().getEventsDataProvider().removeOnDataListener(this);
    Client.get().removeOnUpdateListener(this);
  }

  @Override
  protected void onScrollRequest() {
    binding.scroll.smoothScrollTo(0, 0);
  }

  @Override
  public void onUpdate(List<Event> list) {
    DesignUtils.syncToolbar(toolbar, DesignUtils.canScroll(binding.scroll));
    binding.emptyCalendar.setVisibility(list.isEmpty() ? VISIBLE : GONE);
    binding.recycler.setVisibility(list.isEmpty() ? GONE : VISIBLE);
    Log.d("ONUPDATE", String.valueOf(list.isEmpty()));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    sub1.cancel();
    sub2.cancel();
  }

}
