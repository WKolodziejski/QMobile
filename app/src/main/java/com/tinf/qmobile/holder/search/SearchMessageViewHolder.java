package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.activity.MessagesActivity;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchMessageBinding;
import com.tinf.qmobile.model.message.Message;

public class SearchMessageViewHolder extends SearchViewHolder<Message> {
    private final SearchMessageBinding binding;

    public SearchMessageViewHolder(@NonNull View view) {
        super(view);
        binding = SearchMessageBinding.bind(view);
    }

    @Override
    public void bind(Message message, Context context, String query, SearchAdapter adapter) {
        binding.title.setText(message.getSubject_());
        binding.subtitle.setText(message.getSender());
        binding.date.setText(message.formatDate());
        binding.icon.setText(message.sender.getTarget().getSign());
        binding.icon.setBackgroundTintList(ColorStateList.valueOf(message.getColor()));

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessagesActivity.class);
            intent.putExtra("ID2", message.id);
            context.startActivity(intent);

            saveQuery(query);
        });
    }

}
