package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tinf.qmobile.Activity.EventViewActivity;
import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.Class.Calendario.EventUser;
import com.tinf.qmobile.Class.Materias.Journal;
import com.tinf.qmobile.Fragment.JournalViewFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.DiariosViewHolder;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JournalAdapter extends RecyclerView.Adapter {
    private List<Journal> journals;
    private Context context;
    private boolean enableOnClick;

    public JournalAdapter(Context context, List<Journal> journals, boolean enableOnClick) {
        this.context = context;
        this.journals = journals;
        this.enableOnClick = enableOnClick;
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
        holder.date.setText(journals.get(i).getDateString());
        holder.type.setText(journals.get(i).getTypeString(context));
        holder.type.setTextColor(journals.get(i).getColor());
        holder.weight.setText(String.format("・" + context.getString(R.string.diarios_Peso), journals.get(i).getWeightString()));
        holder.grade.setText(String.format(context.getString(R.string.diarios_Nota), journals.get(i).getGradeString(), journals.get(i).getMaxString()));
        if (enableOnClick) {
            holder.header.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventViewActivity.class);
                intent.putExtra("ID", journals.get(i).id);
                intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);
                context.startActivity(intent);
            });
        }
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
