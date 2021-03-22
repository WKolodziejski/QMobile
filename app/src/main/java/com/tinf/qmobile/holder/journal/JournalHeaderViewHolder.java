package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.model.matter.Matter;

import butterknife.BindView;

public class JournalHeaderViewHolder extends JournalBaseViewHolder<Matter> {
    @BindView(R.id.journal_title)           public TextView title;
    @BindView(R.id.journal_color_badge)     public TextView badge;
    @BindView(R.id.journal_infos_header)    public ConstraintLayout header;
    @BindView(R.id.journal_expand_img)      public ImageView arrow;

    public JournalHeaderViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Context context, Matter matter, JournalAdapter adapter) {
        title.setText(matter.getTitle());
        badge.setBackgroundTintList(ColorStateList.valueOf(matter.getColor()));
        header.setVisibility(matter.isExpanded && !matter.getLastPeriod().journals.isEmpty() ? View.VISIBLE : View.GONE);
        arrow.setRotation(matter.isExpanded ? 180 : 0);
        itemView.setOnClickListener(view -> {
            if (matter.isExpanded) {
                adapter.collapse(getAdapterPosition(), matter);
            } else {
                adapter.expand(getAdapterPosition(), matter, true);
            }
        });

        int n = matter.getJournalNotSeenCount();

        if (n > 0) {
            badge.setText(String.valueOf(n));
        } else {
            badge.setText("");
        }
    }

}
