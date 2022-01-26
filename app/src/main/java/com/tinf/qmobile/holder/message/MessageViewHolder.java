package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.View;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.AttachmentsAdapter;
import com.tinf.qmobile.databinding.MessageHeaderBinding;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.network.message.Messenger;
import static com.tinf.qmobile.model.ViewType.MESSAGE;

public class MessageViewHolder extends MessagesViewHolder<Message> {
    private final MessageHeaderBinding binding;

    public MessageViewHolder(View view) {
        super(view);
        binding = MessageHeaderBinding.bind(view);
    }

    @Override
    public void bind(Context context, Messenger messenger, Message message) {
        binding.header.setText(message.sender.getTarget().getSign());
        binding.header.setBackgroundTintList(ColorStateList.valueOf(message.getColor()));
        binding.subject.setText(message.getSubject_());
        binding.sender.setText(message.getSender());
        binding.date.setText(message.formatDate());
        binding.preview.setText(message.getPreview());

        binding.preview.setVisibility(message.getPreview().isEmpty() ? View.GONE : View.VISIBLE);

        int c = context.getResources().getColor(message.getContent().isEmpty() && !message.isSeen_() ? R.color.message_not_seen : R.color.message_seen);
        int t = message.getContent().isEmpty() && !message.isSeen_() ? Typeface.BOLD : Typeface.NORMAL;

        binding.att.setImageTintList(ColorStateList.valueOf(c));

        binding.subject.setTextColor(c);
        binding.sender.setTextColor(c);
        binding.date.setTextColor(c);

        binding.subject.setTypeface(null, t);
        binding.sender.setTypeface(null, t);
        binding.date.setTypeface(null, t);

        binding.att.setVisibility(message.isHasAtt_() && message.attachments.isEmpty() ? View.VISIBLE : View.GONE);

        if (message.isSolved_()) {
            binding.subject.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            TextViewCompat.setCompoundDrawableTintList(binding.subject, ColorStateList.valueOf(context.getResources().getColor(R.color.amber_a700)));
        } else {
            binding.subject.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (!message.attachments.isEmpty()) {
            binding.attachments.setVisibility(View.VISIBLE);
            binding.attachments.setHasFixedSize(true);
            binding.attachments.setItemViewCacheSize(3);
            binding.attachments.setDrawingCacheEnabled(true);
            binding.attachments.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.attachments.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            binding.attachments.setAdapter(new AttachmentsAdapter(context, message.attachments, true));
        } else {
            binding.attachments.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(v -> {
            if (message.getContent().isEmpty())
                messenger.openMessage(getAdapterPosition());

            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", MESSAGE);
            intent.putExtra("ID", Long.valueOf(message.id));
            intent.putExtra("LOOKUP", false);
            context.startActivity(intent);
        });

        if (message.highlight) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationBackground));
        }
    }

}
