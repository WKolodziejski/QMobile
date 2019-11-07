package com.tinf.qmobile.adapter.journal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.holder.DiariosViewHolder;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.journal.Journal;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter {
    private List<Journal> journals;
    private Context context;

    public JournalAdapter(Context context, List<Journal> journals) {
        this.context = context;
        this.journals = journals;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiariosViewHolder(LayoutInflater.from(context).inflate(R.layout.list_diarios, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final DiariosViewHolder holder = (DiariosViewHolder) viewHolder;

        holder.title.setText(journals.get(i).getTitle());
        holder.date.setText(journals.get(i).formatDate());
        holder.type.setText(journals.get(i).getShort());
        holder.type.setTextColor(journals.get(i).getColor());
        holder.weight.setText(String.format("・" + context.getString(R.string.diarios_Peso), journals.get(i).getWeight()));
        holder.grade.setText(String.format(context.getString(R.string.diarios_Nota), journals.get(i).getGrade(), journals.get(i).getMax()));
        holder.header.setOnClickListener(v -> {
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
