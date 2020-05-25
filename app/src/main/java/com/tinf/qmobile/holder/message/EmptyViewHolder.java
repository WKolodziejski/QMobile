package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;

import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.network.message.Messenger;

public class EmptyViewHolder extends MessagesViewHolder<Empty> {

    public EmptyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Context context, Messenger messenger, Empty empty) {

    }

}
