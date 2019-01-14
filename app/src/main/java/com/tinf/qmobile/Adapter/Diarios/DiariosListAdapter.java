package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataSubscriptionList;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.tinf.qmobile.Activity.MateriaActivity;
import com.tinf.qmobile.Class.Materias.Diarios;
import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.DiariosListViewHolder;
import com.tinf.qmobile.WebView.SingletonWebView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

public class DiariosListAdapter extends RecyclerView.Adapter {
    private List<Materia> diariosList;
    private BoxStore box;
    private Context context;
    private OnExpandListener onExpandListener;

    public DiariosListAdapter(BoxStore box, Context context) {
        SingletonWebView webView = SingletonWebView.getInstance();

        this.box = box;

        Query<Materia> query = box.boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position])).build();

        query.subscribe(new DataSubscriptionList()).observer(data -> {
            this.diariosList = data;
        });

        //this.diariosList = query.find();
        this.context = context;
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
         
         RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         rotate.setDuration(250);
         rotate.setInterpolator(new LinearInterpolator());

         holder.title.setText(diariosList.get(i).getName());
         holder.expand.setExpanded(diariosList.get(i).isExpanded(), diariosList.get(i).isAnim());

         setView(holder, i);

         diariosList.get(i).setAnim(false);

         holder.expandAct.setTag(i);

         holder.open.setOnClickListener(v -> {
             Integer pos = (Integer) holder.expandAct.getTag();

             Intent intent = new Intent(context, MateriaActivity.class);
             intent.putExtra("NAME", diariosList.get(pos).getName());
             intent.putExtra("YEAR", diariosList.get(pos).getYear());
             context.startActivity(intent);

             /*boolean isEmpty = true;

             for (int j = 0; j < diariosList.get(pos).etapas.size(); j++) {
                 if (!diariosList.get(pos).etapas.get(j).diarios.isEmpty()) {
                     isEmpty = false;
                     break;
                 }
             }

             if (!isEmpty) {
                 Intent intent = new Intent(context, MateriaActivity.class);
                 intent.putExtra("NAME", diariosList.get(pos).getName());
                 intent.putExtra("YEAR", diariosList.get(pos).getYear());
                 context.startActivity(intent);
             } else {
                 Toast.makeText(context, R.string.message_empty, Toast.LENGTH_SHORT).show();
             }*/
         });

         holder.expandAct.setOnClickListener(v -> {
             holder.expand.toggle();
             holder.arrow.startAnimation(rotate);

             Integer pos = (Integer) holder.expandAct.getTag();

             diariosList.get(pos).setExpanded(!diariosList.get(pos).isExpanded());

             rotate.setAnimationListener(new Animation.AnimationListener() {
                 @Override
                 public void onAnimationStart(Animation animation) {}

                 @Override
                 public void onAnimationEnd(Animation animation) {
                     if (diariosList.get(pos).isExpanded()) {
                         holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
                     } else {
                         holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
                     }
                 }

                 @Override
                 public void onAnimationRepeat(Animation animation) {}
             });

             holder.expand.setOnExpansionUpdateListener((expansionFraction, state) -> {

                 setView(holder, pos);

                 if (state == ExpandableLayout.State.EXPANDED && diariosList.get(pos).isExpanded()) {
                     onExpandListener.onExpand(pos);
                 }
             });
         });
    }

    private void setView(DiariosListViewHolder holder, int i) {
        if (diariosList.get(i).isExpanded()){
            holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
            holder.title.setTextColor(context.getResources().getColor(diariosList.get(i).getColor()));

            if (diariosList.get(i).etapas.get(diariosList.get(i).etapas.size() - 1).diarios.isEmpty()) {
                holder.nothing.setVisibility(View.VISIBLE);
                holder.open.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                holder.nothing.setVisibility(View.GONE);
                holder.recyclerView.setVisibility(View.VISIBLE);
                holder.open.setVisibility(View.VISIBLE);
                holder.recyclerView.setAdapter(new DiariosAdapter(diariosList.get(i).etapas.get(diariosList.get(i).etapas.size() - 1).diarios, context));
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                        RecyclerView.VERTICAL, false));
            }
        } else {
            holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
            holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.recyclerView.removeAllViewsInLayout();
        }
    }

    @Override
    public int getItemCount() {
        return diariosList.size();
    }

    public void toggleAll(){
        int a = 0;
        int f = 0;
        for (int i = 0; i < diariosList.size(); i++) {
            if (!diariosList.get(i).etapas.get(diariosList.get(i).etapas.size() - 1).diarios.isEmpty()) {
                if (diariosList.get(i).isExpanded()) {
                    a++;
                } else {
                    f++;
                }
            }
        }
        if (a > f) {
            for (int i = 0; i < diariosList.size(); i++) {
                if (diariosList.get(i).isExpanded()) {
                    diariosList.get(i).setAnim(true);
                } else {
                    diariosList.get(i).setAnim(false);
                }
                diariosList.get(i).setExpanded(false);
            }
            Toast.makeText(context, R.string.message_collapsed, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < diariosList.size(); i++) {
                if (!diariosList.get(i).etapas.get(diariosList.get(i).etapas.size() - 1).diarios.isEmpty()) {
                    if (diariosList.get(i).isExpanded()) {
                        diariosList.get(i).setAnim(false);
                    } else {
                        diariosList.get(i).setAnim(true);
                    }
                    diariosList.get(i).setExpanded(true);
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
