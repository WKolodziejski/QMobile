package com.tinf.qmobile.fragment.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.AttachmentsAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.network.Client;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class MessageViewFragment extends Fragment {
    @BindView(R.id.message_view_title)          TextView title;
    @BindView(R.id.message_view_content)        TextView content;
    @BindView(R.id.message_view_sender)         TextView sender;
    @BindView(R.id.message_view_date)           TextView date;
    @BindView(R.id.message_view_header)         TextView header;
    @BindView(R.id.message_view_recycler)       RecyclerView attachments;
    @BindView(R.id.message_view_progressBar)    ProgressBar progressBar;
    private DataSubscription sub1;
    private long id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null)
            id = bundle.getLong("ID");

        sub1 = DataBase.get().getBoxStore()
                .boxFor(Message.class)
                .query()
                .equal(Message_.id, id)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> setText());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_message, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setText();
    }

    private void setText() {
        Message message = DataBase.get().getBoxStore().boxFor(Message.class).get(id);

        if (message != null) {

            if (!message.isSeen_()) {
                message.see();
                DataBase.get().getBoxStore().boxFor(Message.class).put(message);
            }

            progressBar.setVisibility(message.getText_() == null ? View.VISIBLE : View.GONE);

            title.setText(message.getSubject_());
            date.setText(message.formatDate());
            sender.setText(message.sender.getTarget().getName_());
            header.setBackgroundTintList(ColorStateList.valueOf(message.sender.getTarget().getColor_()));
            header.setText(message.sender.getTarget().getSign());
            content.setText(message.getContent());

            if (message.isSolved_()) {
                title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                title.setCompoundDrawableTintList(ColorStateList.valueOf(getContext().getColor(R.color.amber_a700)));
            }

            if (!message.attachments.isEmpty()) {
                attachments.setHasFixedSize(true);
                attachments.setItemViewCacheSize(3);
                attachments.setDrawingCacheEnabled(true);
                attachments.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                attachments.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                attachments.setAdapter(new AttachmentsAdapter(getContext(), message.attachments, false));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
    }

}
