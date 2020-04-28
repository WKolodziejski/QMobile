package com.tinf.qmobile.holder.message;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.message.Messenger;
import com.tinf.qmobile.utility.User;

import java.net.FileNameMap;
import java.net.URLConnection;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.tinf.qmobile.utility.User.REGISTRATION;

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
            if (Client.isConnected()) {
                DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(new DownloadManager.Request(Uri.parse(attachment.getUrl_()))
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setAllowedOverRoaming(false)
                        .setMimeType(URLConnection.guessContentTypeFromName(attachment.getTitle()))
                        .setTitle(attachment.getTitle())
                        .setDescription(attachment.getObs())
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                "QMobile/" + User.getCredential(REGISTRATION) + "/" + attachment.getTitle()));

                Toast.makeText(context, context.getResources().getString(R.string.materiais_downloading), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.client_no_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
