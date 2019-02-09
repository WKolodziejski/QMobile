package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Class.Materias.Diarios;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.DiariosListViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.BoxStore;
import io.objectbox.reactive.DataSubscriptionList;

public class DiariosListAdapter extends RecyclerView.Adapter {
    private List<Materia> materiaList;
    private Context context;
    private OnExpandListener onExpandListener;
    private View.OnClickListener open, expand;

    public DiariosListAdapter(Context context, List<Materia> materiaList, View.OnClickListener open, View.OnClickListener expand) {
        this.context = context;
        this.materiaList = materiaList;
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

         holder.title.setText(materiaList.get(i).getName());
         holder.expand.setExpanded(materiaList.get(i).isExpanded(), materiaList.get(i).isAnim());

         setView(holder, i);

         holder.open.setTag(i);
         holder.expandAct.setTag(i);

         holder.open.setOnClickListener(open);
         holder.expandAct.setOnClickListener(expand);

         holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

            Integer pos = (Integer) holder.open.getTag();
            setView(holder, pos);

            if (state == ExpandableLayout.State.EXPANDED && materiaList.get(pos).isExpanded()) {
                onExpandListener.onExpand(pos);
            }
        });
    }

    private void setView(DiariosListViewHolder holder, int i) {
        if (materiaList.get(i).isExpanded()) {
            holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.title.setTextColor(context.getResources().getColor(materiaList.get(i).getColor()));

            List<Diarios> diarios = materiaList.get(i).etapas.get(getLast(i)).diarios;

            if (diarios.isEmpty()) {
                holder.nothing.setVisibility(View.VISIBLE);
                holder.open.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                DiariosAdapter adapter = new DiariosAdapter(diarios, context);
                adapter.setHasStableIds(true);
                holder.nothing.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.VISIBLE);
                holder.open.setVisibility(View.VISIBLE);
                holder.recyclerView.setHasFixedSize(true);
                holder.recyclerView.setItemViewCacheSize(20);
                holder.recyclerView.setDrawingCacheEnabled(true);
                holder.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                holder.recyclerView.setAdapter(adapter);
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            }
        } else {
            holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
            holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.recyclerView.removeAllViewsInLayout();
        }
    }

    private int getLast(int i) {
        int k = 0;
        for (int j = 0; j < materiaList.get(i).etapas.size(); j++) {
            if (!materiaList.get(i).etapas.get(j).diarios.isEmpty()) {
                k = j;
            }
        }
        return k;
    }

    public void update(List<Materia> materiaList){
        this.materiaList = materiaList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return materiaList.size();
    }

    @Override
    public long getItemId(int position) {
        return materiaList.get(position).hashCode();
    }

    public void toggleAll(){
        int a = 0;
        int f = 0;
        for (int i = 0; i < materiaList.size(); i++) {
            if (!materiaList.get(i).etapas.get(getLast(i)).diarios.isEmpty()) {
                if (materiaList.get(i).isExpanded()) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < materiaList.size(); i++) {
                if (materiaList.get(i).isExpanded()) {
                    materiaList.get(i).setAnim(true);
                } else {
                    materiaList.get(i).setAnim(false);
                }
                materiaList.get(i).setExpanded(false);
            }
            Toast.makeText(context, R.string.message_collapsed, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < materiaList.size(); i++) {
                if (!materiaList.get(i).etapas.get(getLast(i)).diarios.isEmpty()) {
                    if (materiaList.get(i).isExpanded()) {
                        materiaList.get(i).setAnim(false);
                    } else {
                        materiaList.get(i).setAnim(true);
                    }
                    materiaList.get(i).setExpanded(true);
                }
            }
            Toast.makeText(context, R.string.message_expanded, Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

    public void setOnExpandListener(OnExpandListener onExpandListener){
        this.onExpandListener = onExpandListener;
    }

    public interface OnExpandListener {
        void onExpand(int position);
    }
}
