package com.qacademico.qacademico.Adapter.Home;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Class.Guide;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.GuideViewHolder;

import java.util.List;

public class GuideAdapter extends RecyclerView.Adapter {
    private List<Guide> guideList;
    private Context context;
    private OnGuideClicked onClick;

    public GuideAdapter(List<Guide> guideList, Context context) {
        this.guideList = guideList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.table_guide, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final GuideViewHolder holder = (GuideViewHolder) viewHolder;
        final Guide guide  = guideList.get(position) ;

        holder.title.setText(guide.getTitle());
        holder.description.setText(guide.getDescription());
        holder.icon.setImageResource(guide.getImage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //holder.icon.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(guide.getTint())));
            Drawable color = context.getResources().getDrawable(R.drawable.layout_bg_header_top);
            color.setTint(context.getResources().getColor(guide.getTint()));
            holder.header.setBackground(color);
        }

        if (position == 0 || position == 1 || position == 3 || position == 5){
            holder.icon.setPadding((int)(2 * context.getResources().getDisplayMetrics().density), (int)(2 * context.getResources().getDisplayMetrics().density),
                    (int)(2 * context.getResources().getDisplayMetrics().density), (int)(2 * context.getResources().getDisplayMetrics().density));
        }

        holder.layout.setOnClickListener(v -> {
            onClick.OnGuideClick(position, v);
        });
    }

    @Override
    public int getItemCount() {
        return guideList.size();
    }

    public interface OnGuideClicked {
        void OnGuideClick(int position, View view);
    }

    public void setOnClick(OnGuideClicked onClick) {
        this.onClick = onClick;
    }
}
