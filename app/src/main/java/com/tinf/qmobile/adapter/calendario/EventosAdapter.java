package com.tinf.qmobile.adapter.calendario;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tinf.qmobile.model.calendario.Base.CalendarBase;
import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.Calendar.CalendarioViewHolder;
import com.tinf.qmobile.holder.Calendar.EventJournalViewHolder;
import com.tinf.qmobile.holder.Calendar.EventImageViewHolder;
import com.tinf.qmobile.holder.Calendar.EventSimpleViewHolder;
import com.tinf.qmobile.holder.Calendar.MonthViewHolder;
import com.tinf.qmobile.holder.Calendar.DayViewHolder;
import com.tinf.qmobile.holder.Calendar.EventUserViewHolder;

import java.util.List;

public class EventosAdapter extends RecyclerView.Adapter<CalendarioViewHolder> {
    private List<? extends CalendarBase> events;
    private Context context;

    public EventosAdapter(Context context, List<? extends CalendarBase> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public CalendarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CalendarBase.ViewType.JOURNAL:
                return new EventJournalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_event_journal, parent, false));

            case CalendarBase.ViewType.SIMPLE:
                return new EventSimpleViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_event_simple, parent, false));

            case CalendarBase.ViewType.USER:
                return new EventUserViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_event_user, parent, false));

            case CalendarBase.ViewType.IMAGE:
                return new EventImageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_event_image, parent, false));

            case CalendarBase.ViewType.MONTH:
                return new MonthViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.header_month, parent, false));

            case CalendarBase.ViewType.DAY:
                return new DayViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.header_day, parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return events.get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarioViewHolder holder, int i) {
        holder.bind(events.get(i), context);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void update(List<? extends CalendarBase> events) {
        this.events = events;
        notifyDataSetChanged();
    }

}