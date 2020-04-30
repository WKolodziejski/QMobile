package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.AttachmentsAdapter;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.network.message.Messenger;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.tinf.qmobile.model.Queryable.ViewType.MESSAGE;

public class MessageViewHolder extends MessagesViewHolder<Message> {
    @BindView(R.id.message_subject)     public TextView subject;
    @BindView(R.id.message_header)      public TextView header;
    @BindView(R.id.message_date)        public TextView date;
    @BindView(R.id.message_sender)      public TextView sender;
    @BindView(R.id.message_preview)     public TextView preview;
    @BindView(R.id.message_att)         public ImageView att;
    @BindView(R.id.message_attachments) public RecyclerView attachments;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(Context context, Messenger messenger, Message message) {
        header.setText(message.sender.getTarget().getSign());
        header.setBackgroundTintList(ColorStateList.valueOf(message.sender.getTarget().getColor_()));
        subject.setText(message.getSubject_());
        sender.setText(message.sender.getTarget().getName_());
        date.setText(message.formatDate());
        preview.setText(message.getPreview());

        int c = context.getColor(message.getContent().isEmpty() && !message.isSeen_() ? R.color.message_not_seen : R.color.message_seen);
        int t = message.getContent().isEmpty() && !message.isSeen_() ? Typeface.BOLD : Typeface.NORMAL;

        att.setImageTintList(ColorStateList.valueOf(c));

        subject.setTextColor(c);
        sender.setTextColor(c);
        date.setTextColor(c);

        subject.setTypeface(null, t);
        sender.setTypeface(null, t);
        date.setTypeface(null, t);

        att.setVisibility(message.isHasAtt_() && message.attachments.isEmpty() ? View.VISIBLE : View.GONE);

        if (message.isSolved_()) {
            subject.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            subject.setCompoundDrawableTintList(ColorStateList.valueOf(context.getColor(R.color.amber_a700)));
        } else {
            subject.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (!message.attachments.isEmpty()) {
            attachments.setVisibility(View.VISIBLE);
            attachments.setHasFixedSize(true);
            attachments.setItemViewCacheSize(3);
            attachments.setDrawingCacheEnabled(true);
            attachments.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            attachments.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            attachments.setAdapter(new AttachmentsAdapter(context, message.attachments, true));
        } else {
            attachments.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(v -> {
            if (message.getContent().isEmpty())
                messenger.openMessage(getAdapterPosition());

            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", MESSAGE);
            intent.putExtra("ID", Long.valueOf(message.id));
            context.startActivity(intent);
        });
    }

}
