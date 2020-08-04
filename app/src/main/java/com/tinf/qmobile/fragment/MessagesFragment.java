package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MessagesAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.message.Messenger;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesFragment extends Fragment implements OnResponse {
    @BindView(R.id.recycler_messages)   RecyclerView recyclerView;
    @BindView(R.id.message_refresh)     SwipeRefreshLayout refresh;
    /*@BindView(R.id.message_next)        Button nxt;
    @BindView(R.id.message_previous)    Button prv;

    @BindView(R.id.message_webview)
    WebView webView;*/

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

        Messenger messenger = new Messenger(getContext(), this);

        /*prv.setOnClickListener(v -> messenger.loadPage(21));
        nxt.setOnClickListener(v -> {
            DataBase.get().getBoxStore().boxFor(Message.class).removeAll();
        });*/

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MessagesAdapter(getContext(), messenger));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refresh.setEnabled(p == 0);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int j = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (j == recyclerView.getAdapter().getItemCount() - 1)
                    messenger.loadPage(Math.round(j / 20) + 2);
            }

        });

        refresh.setOnRefreshListener(messenger::loadFirstPage);
    }

    @Override
    public void onStart(int pg, int pos) {
        refresh.setRefreshing(true);
    }

    @Override
    public void onFinish(int pg, int pos) {
        refresh.setRefreshing(false);
    }

    @Override
    public void onError(int pg, String error) {
        refresh.setRefreshing(false);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        refresh.setRefreshing(false);
    }

}
