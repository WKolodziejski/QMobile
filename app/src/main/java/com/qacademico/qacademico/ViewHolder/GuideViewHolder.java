package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

public class GuideViewHolder extends RecyclerView.ViewHolder {
    public final TextView title;
    public final TextView description;
    public final ImageView icon;
    public final LinearLayout layout;
    public final LinearLayout header;

    public GuideViewHolder(View view) {
        super(view);

        title = (TextView) view.findViewById(R.id.guide_title);
        description = (TextView) view.findViewById(R.id.guide_description);
        icon = (ImageView) view.findViewById(R.id.guide_img);
        layout = (LinearLayout) view.findViewById(R.id.guide_layout);
        header = (LinearLayout) view.findViewById(R.id.guide_header);
    }
}
