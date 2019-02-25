package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.tinf.qmobile.Class.Materias.Journal;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.DiariosViewHolder;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DiariosAdapter extends RecyclerView.Adapter {
    private List<Journal> journals;
    private Context context;

    public DiariosAdapter(Context context, List<Journal> journals) {
        this.context = context;
        this.journals = journals;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiariosViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_diarios, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final DiariosViewHolder holder = (DiariosViewHolder) viewHolder;

        holder.title.setText(journals.get(i).getTitle());
        holder.date.setText(journals.get(i).getDateString());
        holder.type.setText(journals.get(i).getTypeString(context));
        holder.type.setTextColor(journals.get(i).getColor());
        holder.weight.setText(String.format(context.getResources().getString(
                R.string.diarios_Peso), journals.get(i).getWeight() == -1 ? "-" : String.valueOf(journals.get(i).getWeight())));
        holder.grade.setText(String.format(context.getResources().getString(
                R.string.diarios_Nota),
                journals.get(i).getGrade() == -1 ? "-" : String.valueOf(journals.get(i).getGrade()),
                journals.get(i).getMax() == -1 ? "-" : String.valueOf(journals.get(i).getMax())));
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
