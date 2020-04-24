package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.AttachmentsAdapter;
import com.tinf.qmobile.model.message.Message;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageViewHolder extends MessagesViewHolder<Message> {
    @BindView(R.id.message_subject)     public TextView subject;
    @BindView(R.id.message_header)      public TextView header;
    @BindView(R.id.message_date)        public TextView date;
    @BindView(R.id.message_sender)      public TextView sender;
    @BindView(R.id.message_attachments) public RecyclerView attachments;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(Context context, Message message) {
        header.setText(String.valueOf(message.getSender_().charAt(0)));
        subject.setText(message.getSubject_());
        sender.setText(message.getSender_());
        date.setText(message.formatDate());

        if (!message.attachments.isEmpty()) {
            attachments.setHasFixedSize(true);
            attachments.setItemViewCacheSize(3);
            attachments.setDrawingCacheEnabled(true);
            attachments.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            attachments.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            attachments.setAdapter(new AttachmentsAdapter(context, message.attachments));
        }
    }

}
