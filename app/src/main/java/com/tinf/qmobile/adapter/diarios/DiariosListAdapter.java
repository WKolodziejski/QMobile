package com.tinf.qmobile.adapter.diarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tinf.qmobile.model.matter.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.DiariosListViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DiariosListAdapter extends RecyclerView.Adapter {
    private List<Matter> matters;
    private Context context;
    private OnExpandListener onExpandListener;
    private View.OnClickListener open, expand;

    public DiariosListAdapter(Context context, List<Matter> matters, View.OnClickListener open, View.OnClickListener expand) {
        this.context = context;
        this.matters = matters;
        this.open = open;
        this.expand = expand;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiariosListViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_expandable, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
         final DiariosListViewHolder holder = (DiariosListViewHolder) viewHolder;

         holder.title.setText(matters.get(i).getTitle());
         holder.expand.setExpanded(matters.get(i).isExpanded, matters.get(i).shouldAnimate);

         setView(holder, i);

         holder.open.setTag(i);
         holder.expandAct.setTag(i);

         holder.open.setOnClickListener(open);
         holder.expandAct.setOnClickListener(expand);

         holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

            Integer pos = (Integer) holder.open.getTag();
            setView(holder, pos);

            if (state == ExpandableLayout.State.EXPANDED && matters.get(pos).isExpanded) {
                onExpandListener.onExpand(pos);
            }
        });
    }

    private void setView(DiariosListViewHolder holder, int i) {
        if (matters.get(i).isExpanded) {
            List<Journal> diarios = matters.get(i).periods.get(getLast(i)).journals;

            if (diarios.isEmpty()) {
                holder.arrow.setImageResource(R.drawable.ic_less);
                holder.title.setTextColor(matters.get(i).getColor());
                holder.nothing.setVisibility(View.VISIBLE);
                holder.open.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                holder.recyclerView.post(() -> {
                    JournalAdapter adapter = new JournalAdapter(context, diarios, false);
                    adapter.setHasStableIds(true);

                    holder.nothing.setVisibility(View.GONE);

                    holder.open.setVisibility(View.VISIBLE);

                    holder.recyclerView.setHasFixedSize(true);
                    holder.recyclerView.setItemViewCacheSize(20);
                    holder.recyclerView.setDrawingCacheEnabled(true);
                    holder.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    holder.recyclerView.setAdapter(adapter);
                    holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    holder.recyclerView.setVisibility(View.VISIBLE);

                    holder.arrow.setImageResource(R.drawable.ic_less);
                    holder.title.setTextColor(matters.get(i).getColor());
                });
            }
        } else {
            holder.arrow.setImageResource(R.drawable.ic_more);
            holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.recyclerView.removeAllViewsInLayout();
        }
    }

    private int getLast(int i) {
        int k = 0;
        for (int j = 0; j < matters.get(i).periods.size(); j++) {
            if (!matters.get(i).periods.get(j).journals.isEmpty()) {
                k = j;
            }
        }
        return k;
    }

    public void update(List<Matter> materiaList){
        this.matters = materiaList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return matters.size();
    }

    @Override
    public long getItemId(int i) {
        return matters.get(i).hashCode();
    }

    public void toggleAll(){
        int a = 0;
        int f = 0;
        for (int i = 0; i < matters.size(); i++) {
            if (!matters.get(i).periods.get(getLast(i)).journals.isEmpty()) {
                if (matters.get(i).isExpanded) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < matters.size(); i++) {
                matters.get(i).shouldAnimate = matters.get(i).isExpanded;
                matters.get(i).isExpanded = false;
                notifyItemChanged(i);
            }
            Toast.makeText(context, R.string.diarios_collapsed, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < matters.size(); i++) {
                if (!matters.get(i).periods.get(getLast(i)).journals.isEmpty()) {
                    matters.get(i).shouldAnimate = !matters.get(i).isExpanded;
                    matters.get(i).isExpanded = true;
                    notifyItemChanged(i);
                }
            }
            Toast.makeText(context, R.string.diarios_expanded, Toast.LENGTH_SHORT).show();
        }
        //notifyDataSetChanged();
    }

    public void setOnExpandListener(OnExpandListener onExpandListener){
        this.onExpandListener = onExpandListener;
    }

    public interface OnExpandListener {
        void onExpand(int position);
    }

}
