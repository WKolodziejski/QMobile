package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShortcutViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.shortcut_title)  public TextView title;
    @BindView(R.id.shortcut_img)    public ImageView icon;
    @BindView(R.id.shortcut_layout) public LinearLayout layout;

    public ShortcutViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
