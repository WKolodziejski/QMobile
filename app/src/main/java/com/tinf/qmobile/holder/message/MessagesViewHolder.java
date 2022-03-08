package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.network.message.Messenger;

public abstract class MessagesViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(Context context, Messenger messenger, T t);

}
