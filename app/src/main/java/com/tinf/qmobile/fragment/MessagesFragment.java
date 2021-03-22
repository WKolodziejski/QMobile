package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.MessagesAdapter;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.message.Messenger;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.model.ViewType.MESSAGE;

public class MessagesFragment extends Fragment implements OnResponse {
    @BindView(R.id.recycler_messages)   RecyclerView recyclerView;
    @BindView(R.id.message_refresh)     SwipeRefreshLayout refresh;
    /*@BindView(R.id.message_next)        Button nxt;
    @BindView(R.id.message_previous)    Button prv;

    @BindView(R.id.message_webview)
    WebView webView;*/

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        getActivity().finish();
    });

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

        /*prv.setOnClickListener(v -> messenger.loadPage(21));
        nxt.setOnClickListener(v -> {
            DataBase.get().getBoxStore().boxFor(Message.class).removeAll();
        });*/

        Messenger messenger = new Messenger(getContext(), this);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        MessagesAdapter adapter = new MessagesAdapter(getContext(), messenger, getArguments());

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
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

        if (getArguments() != null) {
            long id = getArguments().getLong("ID2");
            /*int p = adapter.highlight(id);

            if (p >= 0) {
                layout.scrollToPosition(p);
                messenger.openMessage(p);

                Intent intent = new Intent(getContext(), EventViewActivity.class);
                intent.putExtra("TYPE", MESSAGE);
                intent.putExtra("ID", id);
                launcher.launch(intent);
            }*/

            Intent intent = new Intent(getContext(), EventViewActivity.class);
            intent.putExtra("TYPE", MESSAGE);
            intent.putExtra("ID", id);
            launcher.launch(intent);
        }
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
