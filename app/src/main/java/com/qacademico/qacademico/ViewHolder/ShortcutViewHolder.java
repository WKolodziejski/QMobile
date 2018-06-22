package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

public class ShortcutViewHolder extends RecyclerView.ViewHolder {
    public final TextView title;
    public final ImageView icon;
    public final LinearLayout layout;

    public ShortcutViewHolder(View view) {
        super(view);

        title = (TextView) view.findViewById(R.id.shortcut_title);
        icon = (ImageView) view.findViewById(R.id.shortcut_img);
        layout = (LinearLayout) view.findViewById(R.id.shortcut_layout);
    }
}
