package com.tinf.qmobile.fragment;

import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventCreateActivity;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentScheduleBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.ColorUtils;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.ScheduleUtils;
import com.tinf.qmobile.utility.UserUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.QueryBuilder;
import io.objectbox.reactive.DataSubscription;
import me.jlurena.revolvingweekview.WeekViewEvent;

public class ScheduleFragment extends Fragment {
  private FragmentScheduleBinding binding;
  private DataSubscription sub1, sub2;
  private Bundle bundle;
  private ConstraintLayout daysLayout;

  public void setDaysLayout(ConstraintLayout daysLayout) {
    this.daysLayout = daysLayout;
  }

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bundle = getArguments();

    sub1 = DataBase.get().getBoxStore().subscribe(Schedule.class)
                   .onlyChanges()
                   .on(AndroidScheduler.mainThread())
                   .onError(Throwable::printStackTrace)
                   .observer(data -> {
                     binding.weekView.notifyDatasetChanged();
                     updateFABColor();
                     updateSchedule(false);
                   });

    sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                   .onlyChanges()
                   .on(AndroidScheduler.mainThread())
                   .onError(Throwable::printStackTrace)
                   .observer(data -> {
                     binding.weekView.notifyDatasetChanged();
                     updateFABColor();
                     updateSchedule(false);
                   });
  }

  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_schedule, container, false);
    binding = FragmentScheduleBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    updateFABColor();

    binding.header.setVisibility(bundle == null ? View.VISIBLE : View.GONE);

    binding.weekView.setWeekViewLoader(ArrayList::new);

    binding.weekView.setOnEventClickListener((event, eventRect) -> {
      Intent intent = new Intent(getActivity(), EventViewActivity.class);
      intent.putExtra("TYPE", SCHEDULE);
      intent.putExtra("ID", Long.valueOf(event.getIdentifier()));
      intent.putExtra("LOOKUP", bundle == null);
      startActivity(intent);
    });

    binding.weekView.setWeekViewLoader(() -> updateSchedule(true));

    binding.fab.setOnClickListener(view1 -> {
      Intent intent = new Intent(getContext(), EventCreateActivity.class);
      intent.putExtra("TYPE", SCHEDULE);

      if (getActivity() instanceof MatterActivity)
        intent.putExtra("ID2", bundle.getLong("ID"));

      startActivity(intent);
    });
  }

  private List<WeekViewEvent> updateSchedule(boolean scroll) {
    boolean[][] hours = new boolean[24][7];
    //WeekViewEvent[] minutes = new WeekViewEvent[24];

    List<WeekViewEvent> events = new ArrayList<>();

    List<Schedule> schedules;

    if (bundle == null) {
      schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                          .equal(Schedule_.year, UserUtils.getYear(pos)).and()
                          .equal(Schedule_.period, UserUtils.getPeriod(pos))
                          .build().find();
    } else {
      QueryBuilder<Schedule> builder = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                                               .equal(Schedule_.year, UserUtils.getYear(pos)).and()
                                               .equal(Schedule_.period, UserUtils.getPeriod(pos));

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

        int day = event.getStartTime().getDay().getValue() % 7;
        int hour = event.getStartTime().getHour();

        if (!hours[hour][day]) {
          //minutes[hour] = event;
          hours[hour][day] = true;
        }
      }

      if (scroll) {
        int scrollTo;

        if (!ScheduleUtils.isAuto()) {
          scrollTo = ScheduleUtils.getStartHour();

        } else {
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

          while (r && firstIndex > 0) {
            if (!hours[firstIndex - 1][d1] && d1 < 7) {
              d1++;
              if (d1 == 7)
                r = false;
            } else {
              firstIndex--;
              d1 = 0;
            }
          }

          scrollTo = firstIndex;
        }

        binding.scroll.scrollTo(0, scrollTo * 150);
      }

      binding.layout.setVisibility(View.VISIBLE);
      binding.empty.setVisibility(View.GONE);

      if (daysLayout != null)
        daysLayout.setVisibility(View.VISIBLE);
      else
        binding.daysLayout.setVisibility(View.VISIBLE);

    } else {
      binding.layout.setVisibility(View.GONE);
      binding.empty.setVisibility(View.VISIBLE);

      if (daysLayout != null)
        daysLayout.setVisibility(View.GONE);
      else
        binding.daysLayout.setVisibility(View.GONE);
    }

    return events;
  }

  private void updateFABColor() {
    binding.fab.setVisibility(Client.pos == 0 ? View.VISIBLE : View.GONE);

    if (bundle != null) {
      long id = bundle.getLong("ID");

      if (id != 0) {
        int color = DataBase
            .get().getBoxStore().boxFor(Matter.class).get(id).getColor();

        ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(getContext(), color);

        binding.fab.setBackgroundTintList(ColorStateList.valueOf(colorRoles.getAccentContainer()));
        binding.fab.setImageTintList(ColorStateList.valueOf(colorRoles.getOnAccentContainer()));
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    sub1.cancel();
    sub2.cancel();
  }

}
