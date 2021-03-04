package com.tinf.qmobile.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.User;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.tinf.qmobile.BuildConfig.APPLICATION_ID;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIALS;

public class MaterialsFragment extends Fragment implements OnUpdate {
    @BindView(R.id.recycler_materiais)        RecyclerView recyclerView;
    private ActionMode action;
    private MaterialsAdapter adapter;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new DownloadReceiver((DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE), id -> adapter.notifyItemDownloaded(id));

        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (hasPermission())
            Client.get().load(PG_MATERIALS);
        else
            requestPermission();

        adapter = new MaterialsAdapter(getContext(), getArguments(), new MaterialsAdapter.OnInteractListener() {

            @Override
            public boolean isSelectionMode() {
                return action != null;
            }

            @Override
            public void setSelectionMode(ActionMode.Callback callback) {
                action = getActivity().startActionMode(callback);

                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(false);
            }

            @Override
            public void onSelectedCount(int size) {
                if (size > 0)
                    action.setTitle(String.valueOf(size));
                else {
                    action.finish();
                    action = null;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(false);

                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.materials, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                action = null;

                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(true);
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layout);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        if (getActivity() instanceof MainActivity) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(p == 0);
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

        if (getArguments() != null) {
            int p = adapter.highlight(getArguments().getLong("ID2"));

            if (p >= 0) {
                layout.scrollToPosition(p);
                adapter.handleDownload(p);
            }
        }
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_material, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onScrollRequest() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onDateChanged() {
        Client.get().load(PG_MATERIALS);
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (action != null) {
            action.finish();
            action = null;
        }
    }

}
