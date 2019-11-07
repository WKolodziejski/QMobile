package com.tinf.qmobile.adapter.journal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.holder.Journal.JournalViewHolder;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.journal.Journal;

import java.util.List;

public class JournalAdapter2 extends RecyclerView.Adapter {
    private List<Journal> journals;
    private Context context;

    public JournalAdapter2(Context context, List<Journal> journals) {
        this.context = context;
        this.journals = journals;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JournalViewHolder(LayoutInflater.from(context).inflate(R.layout.list_journal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final JournalViewHolder holder = (JournalViewHolder) viewHolder;

        holder.title.setText(journals.get(i).getTitle());
        holder.date.setText(journals.get(i).formatDate());
        holder.max.setText(journals.get(i).getMax());
        holder.weight.setText(journals.get(i).getWeight());
        holder.grade.setText(journals.get(i).getGrade());
        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", journals.get(i).id);
            intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return journals.size();
    }

    //Returns hashCode because adapter is set to stableID
    @Override
    public long getItemId(int position) {
        return journals.get(position).hashCode();
    }
}
