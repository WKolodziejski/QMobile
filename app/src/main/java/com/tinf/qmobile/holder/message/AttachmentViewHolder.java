package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.network.message.Messenger;
import com.tinf.qmobile.service.DownloadReceiver;

import java.io.File;

import butterknife.BindView;

public class AttachmentViewHolder extends MessagesViewHolder<Attachment> {
    @BindView(R.id.message_attachment_icon)      public ImageView icon;
    @BindView(R.id.message_attachment_title)      public TextView title;

    public AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Context context, Messenger messenger, Attachment attachment) {
        title.setText(attachment.getTitle());
        icon.setImageDrawable(context.getDrawable(attachment.getIcon()));

        itemView.setOnClickListener(v -> {
            if (new File(DownloadReceiver.getAttachmentPath(attachment.getTitle())).exists())
                DownloadReceiver.openFile(attachment.getTitle());
            else
                DownloadReceiver.download(context, attachment.getUrl_(), attachment.getTitle(), null);
        });
    }

}
