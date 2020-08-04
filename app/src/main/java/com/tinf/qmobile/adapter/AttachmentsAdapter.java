package com.tinf.qmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.message.AttachmentCardViewHolder;
import com.tinf.qmobile.holder.message.AttachmentViewHolder;
import com.tinf.qmobile.holder.message.MessagesViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.network.message.Messenger;
import java.util.ArrayList;
import java.util.List;

public class AttachmentsAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private List<Queryable> attachments;
    private Context context;
    private boolean isCard;

    public AttachmentsAdapter(Context context, List<Attachment> attachments, boolean isCard) {
        this.attachments = new ArrayList<>(attachments);
        this.context = context;
        this.isCard = isCard;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return isCard ?
                new AttachmentCardViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.message_attachment_card, parent, false))
                : new AttachmentViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.message_attachment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int i) {
        holder.bind(context, null, attachments.get(i));
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

}
