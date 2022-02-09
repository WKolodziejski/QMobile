package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.model.Empty;

public class JournalEmptyViewHolder extends JournalBaseViewHolder<Empty> {

    public JournalEmptyViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Context context, Empty empty, JournalAdapter adapter, boolean lookup) {

    }

}
