package com.tinf.qmobile.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.databinding.FragmentMaterialBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.Design;
import com.tinf.qmobile.widget.divider.JournalItemDivider;
import com.tinf.qmobile.widget.divider.MaterialItemDivider;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIALS;

public class MaterialsFragment extends Fragment implements OnUpdate {
    private FragmentMaterialBinding binding;
    private ActionMode action;
    private MaterialsAdapter adapter;
    private BroadcastReceiver receiver;
    private MaterialToolbar toolbar;
    private NestedScrollView scroll;
    private SwipeRefreshLayout refresh;

    public void setParams(MaterialToolbar toolbar, NestedScrollView scroll, SwipeRefreshLayout refresh) {
        this.toolbar = toolbar;
        this.scroll = scroll;
        this.refresh = refresh;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new DownloadReceiver((DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE), id -> adapter.notifyItemDownloaded(id));

        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (hasPermission())
            Client.get().load(PG_MATERIALS);
        else
            requestPermission();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());

        adapter = new MaterialsAdapter(getContext(), getArguments(), new MaterialsAdapter.OnInteractListener() {

            @Override
            public boolean isSelectionMode() {
                return action != null;
            }

            @Override
            public void setSelectionMode(ActionMode.Callback callback) {
                action = getActivity().startActionMode(callback);

                if (getActivity() instanceof MainActivity)
                    refresh.setEnabled(false);
            }

            @Override
            public void onSelectedCount(int size) {
                if (size > 0) {
                    action.setTitle(String.valueOf(size));

                    if (getActivity() instanceof MainActivity)
                        Design.syncToolbar(toolbar, false);
                } else {
                    action.finish();
                    action = null;

                    if (getActivity() instanceof MainActivity)
                        Design.syncToolbar(toolbar, true);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                if (getActivity() instanceof MainActivity)
                    refresh.setEnabled(false);

                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.materials, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                action = null;

                if (getActivity() instanceof MainActivity) {
                    Design.syncToolbar(toolbar, true);
                    refresh.setEnabled(true);
                }
            }

        }, canExpand -> {
            if (getActivity() instanceof MainActivity)
                Design.syncToolbar(toolbar, Design.canScroll(scroll) && canExpand);
        });

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemViewCacheSize(20);
        binding.recycler.setDrawingCacheEnabled(true);
        binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recycler.setLayoutManager(layout);
        binding.recycler.addItemDecoration(new MaterialItemDivider(getContext(), 70));
        binding.recycler.setItemAnimator(null);
        binding.recycler.setAdapter(adapter);

        if (getActivity() instanceof MainActivity) {
            binding.recycler.addOnScrollListener(Design.getRefreshBehavior(refresh));
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
        binding = FragmentMaterialBinding.bind(view);
        return view;
    }

    @Override
    public void onScrollRequest() {
        if (binding.recycler != null) {
            binding.recycler.smoothScrollToPosition(0);
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
