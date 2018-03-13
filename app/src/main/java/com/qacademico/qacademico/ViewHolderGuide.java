package com.qacademico.qacademico;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolderGuide extends RecyclerView.ViewHolder {
    final TextView title;
    final TextView description;
    final ImageView icon;
    final LinearLayout layout;
    final LinearLayout header;

    public ViewHolderGuide(View view) {
        super(view);

        title = (TextView) view.findViewById(R.id.guide_title);
        description = (TextView) view.findViewById(R.id.guide_description);
        icon = (ImageView) view.findViewById(R.id.guide_img);
        layout = (LinearLayout) view.findViewById(R.id.guide_layout);
        header = (LinearLayout) view.findViewById(R.id.guide_header);
    }
}
