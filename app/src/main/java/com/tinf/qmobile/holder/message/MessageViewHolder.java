package com.tinf.qmobile.holder.message;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.model.ViewType.MESSAGE;

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
import com.tinf.qmobile.parser.messages.LoadMessageHelper;
import com.tinf.qmobile.utility.ColorsUtils;

public class MessageViewHolder extends MessagesViewHolder<Message> {
  private final MessageHeaderBinding binding;

  public MessageViewHolder(View view) {
    super(view);
    binding = MessageHeaderBinding.bind(view);
  }

  @Override
  public void bind(Context context, Message message) {
    binding.header.setText(message.sender.getTarget().getSign());
    binding.header.setBackgroundTintList(ColorStateList.valueOf(message.getColor()));
    binding.subject.setText(message.getSubject_());
    binding.sender.setText(message.getSender());
    binding.date.setText(message.formatDate());
    binding.preview.setText(message.getPreview());

    binding.preview.setVisibility(message.getPreview().isEmpty() ? View.GONE : View.VISIBLE);

    int t;
    int c;

    if (message.getContent().isEmpty() && !message.isSeen_()) {
      t = Typeface.BOLD;
      c = ColorsUtils.getColor(context, com.google.android.material.R.attr.colorOnSurface);
    } else {
      t = Typeface.NORMAL;
      c = ColorsUtils.getColor(context, com.google.android.material.R.attr.colorOnSurfaceVariant);
    }

    binding.att.setImageTintList(ColorStateList.valueOf(c));
    binding.subject.setTypeface(Typeface.create(binding.subject.getTypeface(), t), t);
    binding.sender.setTypeface(Typeface.create(binding.subject.getTypeface(), t), t);
    binding.date.setTypeface(Typeface.create(binding.subject.getTypeface(), t), t);
    binding.preview.setTypeface(Typeface.create(binding.subject.getTypeface(), t), t);

    binding.att.setVisibility(
        message.isHasAtt_() && message.attachments.isEmpty() ? View.VISIBLE : View.GONE);

//    if (message.isSolved_()) {
//      binding.subject.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
//      TextViewCompat.setCompoundDrawableTintList(binding.subject, ColorStateList.valueOf(
//          context.getColor(R.color.amber_a700)));
//    } else {
//      binding.subject.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//    }

    if (!message.attachments.isEmpty()) {
      binding.attachments.setVisibility(View.VISIBLE);
      binding.attachments.setLayoutManager(
          new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
      binding.attachments.setAdapter(new AttachmentsAdapter(context, message.attachments, true));
    } else {
      binding.attachments.setVisibility(View.GONE);
    }

    itemView.setOnClickListener(v -> {
      if (message.getContent().isEmpty()) {
        LoadMessageHelper.loadMessage(message);
      }

      Intent intent = new Intent(context, EventViewActivity.class);
      intent.putExtra("TYPE", MESSAGE);
      intent.putExtra("ID", Long.valueOf(message.id));
      intent.putExtra("LOOKUP", false);
      context.startActivity(intent);
    });

//        if (message.highlight) {
//            itemView.setBackgroundColor(context.getColor(R.color
//            .notificationBackground));
//        }
  }

}
