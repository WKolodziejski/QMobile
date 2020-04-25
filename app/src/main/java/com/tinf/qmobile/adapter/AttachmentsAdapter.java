package com.tinf.qmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.message.AttachmentViewHolder;
import com.tinf.qmobile.holder.message.MessagesViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.message.Attachment;

import java.util.ArrayList;
import java.util.List;

public class AttachmentsAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private List<Queryable> attachments;
    private Context context;
    private WebView webView;

    public AttachmentsAdapter(Context context, WebView webView, List<Attachment> attachments) {
        this.attachments = new ArrayList<>(attachments);
        this.context = context;
        this.webView = webView;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttachmentViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.attachment_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int i) {
        holder.bind(context, webView, attachments.get(i));
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

}
