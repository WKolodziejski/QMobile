package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.tinf.qmobile.databinding.MessageAttachmentBinding;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.DesignUtils;

import java.io.File;

public class AttachmentViewHolder extends MessagesViewHolder<Attachment> {
  private final MessageAttachmentBinding binding;

  public AttachmentViewHolder(View view) {
    super(view);
    binding = MessageAttachmentBinding.bind(view);
  }

  @Override
  public void bind(Context context, Attachment attachment) {
    binding.title.setText(attachment.getTitle());
    binding.icon.setImageDrawable(DesignUtils.getDrawable(context, attachment.getIcon()));

    itemView.setOnClickListener(v -> {
      if (new File(DownloadReceiver.getAttachmentPath(attachment.getTitle())).exists())
        DownloadReceiver.openFile(attachment.getTitle());
      else
        DownloadReceiver.download(context, attachment.getUrl_(), attachment.getTitle(), null);
    });
  }

}
