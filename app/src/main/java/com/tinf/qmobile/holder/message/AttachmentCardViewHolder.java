package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.tinf.qmobile.databinding.MessageAttachmentCardBinding;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.DesignUtils;

import java.io.File;

public class AttachmentCardViewHolder extends MessagesViewHolder<Attachment> {
  private final MessageAttachmentCardBinding binding;

  public AttachmentCardViewHolder(
      @NonNull
      View view) {
    super(view);
    binding = MessageAttachmentCardBinding.bind(view);
  }

  @Override
  public void bind(Context context,
                   Attachment attachment) {
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
