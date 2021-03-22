package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MessagesActivity;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.message.Message;

import butterknife.BindView;

public class SearchMessageViewHolder extends SearchViewHolder<Message> {
    @BindView(R.id.search_message_title)        TextView title;
    @BindView(R.id.search_message_subtitle)     TextView subtitle;
    @BindView(R.id.search_message_date)         TextView date;
    @BindView(R.id.search_message_icon)         TextView icon;

    public SearchMessageViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Message message, Context context, String query, SearchAdapter adapter) {
        title.setText(message.getSubject_());
        subtitle.setText(message.getSender());
        date.setText(message.formatDate());
        icon.setText(message.sender.getTarget().getSign());
        icon.setBackgroundTintList(ColorStateList.valueOf(message.getColor()));

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessagesActivity.class);
            intent.putExtra("ID2", message.id);
            context.startActivity(intent);

            saveQuery(query);
        });
    }

}
