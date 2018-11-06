package com.tinf.qmobile.Adapter.Calendario;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.tinf.qmobile.Class.Calendario.Dia;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.CalendarioViewHolder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarioAdapter extends RecyclerView.Adapter {
    private List<Dia> calendarioList;
    private Context context;
    private boolean isSubList;

    public CalendarioAdapter(Context context, List<Dia> calendarioList, boolean isSubList) {
        this.context = context;
        this.isSubList = isSubList;
        this.calendarioList = clearList(calendarioList);
    }

    private List<Dia> clearList(List<Dia> list) {
        List<Dia> clean = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).eventos.isEmpty()) {
                clean.add(list.get(i));
            }
        }

        if (isSubList) {

            Calendar today = Calendar.getInstance();
            today.setTime(new Date());

            int start = 0;

            for (int i = 0; i < clean.size(); i++) {
                if (clean.get(i).getDay() >= today.get(Calendar.DAY_OF_MONTH)) {
                    start = i;
                    break;
                }
            }

            return clean.subList(start, clean.size() < 5 ? clean.size() : 5);

        } else {
            return clean;
        }
    }

    public List<Dia> getCalendarioList() {
        return calendarioList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new CalendarioViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_calendario, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final CalendarioViewHolder holder = (CalendarioViewHolder) viewHolder;

        holder.dia.setText(String.valueOf(calendarioList.get(position).getDay()));
        holder.eventos.setAdapter(new EventosAdapter(context, calendarioList.get(position).eventos, isSubList));
        holder.eventos.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.layout.setTag(position);
    }

    @Override
    public int getItemCount() {
        return calendarioList.size();
    }
}
