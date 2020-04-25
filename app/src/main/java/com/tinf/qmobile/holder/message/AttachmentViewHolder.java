package com.tinf.qmobile.holder.message;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.message.Attachment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AttachmentViewHolder extends MessagesViewHolder<Attachment> {
    @BindView(R.id.attachment_img)      public ImageView icon;
    @BindView(R.id.attachment_txt)      public TextView title;

    public AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(Context context, WebView webView, Attachment attachment) {
        title.setText(attachment.getTitle());
        icon.setImageDrawable(context.getDrawable(attachment.getIcon()));

        itemView.setOnClickListener(v -> {
            //TODO download
        });
    }

}
