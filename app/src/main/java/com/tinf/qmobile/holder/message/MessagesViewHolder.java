package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.model.Queryable;

public abstract class MessagesViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(Context context, WebView webView, T t);

}
