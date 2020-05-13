package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.os.Environment;
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
import butterknife.ButterKnife;

import static com.tinf.qmobile.service.DownloadReceiver.PATH;

public class AttachmentCardViewHolder extends MessagesViewHolder<Attachment> {
    @BindView(R.id.attachment_img)      public ImageView icon;
    @BindView(R.id.attachment_txt)      public TextView title;

    public AttachmentCardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(Context context, Messenger messenger, Attachment attachment) {
        title.setText(attachment.getTitle());
        icon.setImageDrawable(context.getDrawable(attachment.getIcon()));

        itemView.setOnClickListener(v -> {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/" + PATH+ "/" + attachment.getTitle();

            if (new File(path).exists())
                DownloadReceiver.openFile(path);
            else
                DownloadReceiver.download(context, attachment.getUrl_(), attachment.getTitle(), null);
        });
    }

}
