package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.adapter.journal.JournalAdapter3;
import com.tinf.qmobile.model.journal.JournalBase;

import butterknife.ButterKnife;

public abstract class JournalBaseViewHolder<T extends JournalBase> extends RecyclerView.ViewHolder {

    public JournalBaseViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public abstract void bind(Context context, T t, JournalAdapter3 adapter);

}
