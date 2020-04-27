package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MessagesAdapter;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.message.Messenger;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesFragment extends Fragment {
    @BindView(R.id.recycler_messages)   RecyclerView recyclerView;
    @BindView(R.id.message_next)        Button nxt;
    @BindView(R.id.message_previous)    Button prv;
    WebView webView;

    public MessagesFragment(WebView webView) {
        this.webView = webView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Messenger messenger = new Messenger(getContext(), new OnResponse() {

            @Override
            public void onStart(int pg, int pos) {
                Log.d("starting", ""+pg);
            }

            @Override
            public void onFinish(int pg, int pos) {
                Log.d("finished", ""+pg);
            }

            @Override
            public void onError(int pg, String error) {
                Log.d("error", error);
            }

            @Override
            public void onAccessDenied(int pg, String message) {

            }

        }, webView);

        prv.setOnClickListener(v -> messenger.loadPreviousPage());
        nxt.setOnClickListener(v -> messenger.loadNextPage());

        MessagesAdapter adapter = new MessagesAdapter(getContext(), messenger);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }

}
