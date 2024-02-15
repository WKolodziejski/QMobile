package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.DAY;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATTER;
import static com.tinf.qmobile.model.ViewType.MONTH;
import static com.tinf.qmobile.model.ViewType.PADDING;
import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.ViewType.USER;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.ColorRoles;
import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EmptyViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.CalendarHeaderViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.CalendarPaddingViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.DayViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventClazzVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventJournalVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventSimpleVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventUserVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.MonthViewHolder;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.Day;
import com.tinf.qmobile.model.calendar.Header;
import com.tinf.qmobile.model.calendar.Month;
import com.tinf.qmobile.model.calendar.Padding;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.widget.calendar.view.CompactCalendarView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder<CalendarBase>>
    implements KmStickyListener, OnData<CalendarBase> {
  private final Context context;
  private final AsyncListDiffer<CalendarBase> list;
  private final CompactCalendarView calendar;
  private OnCalendar onCalendar;

  public CalendarAdapter(Context context,
                         CompactCalendarView calendar,
                         OnCalendar onCalendar) {
    this.context = context;
    this.calendar = calendar;
    this.onCalendar = onCalendar;
    this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<CalendarBase>() {
      @Override
      public boolean areItemsTheSame(
          @NonNull
          CalendarBase oldItem,
          @NonNull
          CalendarBase newItem) {
        return oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();
      }

      @Override
      public boolean areContentsTheSame(
          @NonNull
          CalendarBase oldItem,
          @NonNull
          CalendarBase newItem) {
        return oldItem.isSame(newItem);
      }
    });

    onUpdate(DataBase.get()
                     .getCalendarDataProvider()
                     .getList());
  }

  public List<CalendarBase> getList() {
    return list.getCurrentList();
  }

  @NonNull
  @Override
  public CalendarViewHolder onCreateViewHolder(
      @NonNull
      ViewGroup parent,
      int viewType) {
    switch (viewType) {
      case JOURNAL:
        return new EventJournalVerticalViewHolder(LayoutInflater.from(context)
                                                                .inflate(
                                                                    R.layout.calendar_event_journal_v,
                                                                    parent, false));

      case SIMPLE:
        return new EventSimpleVerticalViewHolder(LayoutInflater.from(context)
                                                               .inflate(
                                                                   R.layout.calendar_event_simple_v,
                                                                   parent, false));

      case USER:
        return new EventUserVerticalViewHolder(LayoutInflater.from(context)
                                                             .inflate(
                                                                 R.layout.calendar_event_user_v,
                                                                 parent, false));

      case CLASS:
        return new EventClazzVerticalViewHolder(LayoutInflater.from(context)
                                                              .inflate(
                                                                  R.layout.calendar_event_clazz_v,
                                                                  parent, false));

      case MONTH:
        return new MonthViewHolder(LayoutInflater.from(context)
                                                 .inflate(R.layout.calendar_header_month, parent,
                                                          false));

      case DAY:
        return new DayViewHolder(LayoutInflater.from(context)
                                               .inflate(R.layout.calendar_header_day_range, parent,
                                                        false));

      case MATTER:
        return new CalendarHeaderViewHolder(LayoutInflater.from(context)
                                                          .inflate(R.layout.calendar_header_empty,
                                                                   parent,
                                                                   false));

      case PADDING:
        return new CalendarPaddingViewHolder(LayoutInflater.from(context)
                                                           .inflate(
                                                               R.layout.calendar_header_padding,
                                                               parent,
                                                               false));

      case EMPTY:
        return new EmptyViewHolder(LayoutInflater.from(context)
                                                 .inflate(R.layout.calendar_event_empty, parent,
                                                          false));
    }

    return null;
  }

  @Override
  public int getItemViewType(int i) {
    return list.getCurrentList()
               .get(i)
               .getItemType();
  }

  @Override
  public void onBindViewHolder(
      @NonNull
      CalendarViewHolder holder,
      int i) {
    holder.bind(list.getCurrentList()
                    .get(i), context, i);
  }

  @Override
  public int getItemCount() {
    return list.getCurrentList()
               .size();
  }

  @Override
  public Integer getHeaderPositionForItem(Integer i) {
    CalendarBase e = list.getCurrentList()
                         .get(i);

    if (e instanceof Month || e instanceof Day) return i;

    while (!(e instanceof Header) && !(e instanceof Padding) && i > 0)
      e = list.getCurrentList()
              .get(--i);

    return i < 0 ? 0 : i;
  }

  @Override
  public Integer getHeaderLayout(Integer i) {
    CalendarBase e = list.getCurrentList()
                         .get(i);

    if (e instanceof Header) return R.layout.calendar_header_day_single;
    if (e instanceof Padding) return R.layout.calendar_header_padding;
    else return R.layout.header_empty;
  }

  @Override
  public void bindHeaderData(View header,
                             Integer i) {
    if (!(list.getCurrentList()
              .get(i) instanceof Header)) {
      return;
    }

    Header h = (Header) list.getCurrentList()
                            .get(i);

    TextView number = header.findViewById(R.id.number);
    TextView day = header.findViewById(R.id.day);

    number.setText(h.getDayString());
    day.setText(h.getWeekString());

    if (h.isToday()) {
      ColorRoles colorRoles = ColorsUtils.getColorRoles(context,
                                                        com.google.android.material.R.attr.colorPrimaryContainer);

      number.setBackgroundTintList(ColorStateList.valueOf(colorRoles.getAccentContainer()));
      number.setTextColor(colorRoles.getOnAccentContainer());
    } else {
      number.setBackgroundTintList(ColorStateList.valueOf(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorSurface)));
      number.setTextColor(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorOnSurface));
    }
  }

  @Override
  public Boolean isHeader(Integer i) {
    if (i < 0) return false;

    CalendarBase e = list.getCurrentList()
                         .get(i);

    return e instanceof Header || e instanceof Day || e instanceof Month || e instanceof Padding;
  }

  @Override
  public void onUpdate(List<CalendarBase> list) {
    this.list.submitList(DataBase.get()
                                 .getCalendarDataProvider()
                                 .getList(), () -> calendar.postDelayed(() -> {
      if (onCalendar != null && onCalendar.scrollToToday()) {
        onCalendar = null;
      }
    }, 500));
    this.calendar.removeAllEvents();
    this.calendar.addEvents(DataBase.get()
                                    .getCalendarDataProvider()
                                    .getEvents());
  }

  @Override
  public void onDetachedFromRecyclerView(
      @NonNull
      RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    DataBase.get()
            .getCalendarDataProvider()
            .removeOnDataListener(this);
  }

  @Override
  public void onAttachedToRecyclerView(
      @NonNull
      RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    DataBase.get()
            .getCalendarDataProvider()
            .addOnDataListener(this);
  }

  public interface OnCalendar {
    boolean scrollToToday();
  }

}
