package com.tinf.qmobile.fragment;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.tinf.qmobile.utility.PermissionsUtils.hasPermission;
import static com.tinf.qmobile.utility.PermissionsUtils.requestPermission;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.adapter.OnInteractListener;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.databinding.FragmentMaterialBinding;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.Design;
import com.tinf.qmobile.widget.divider.MaterialItemDivider;

import java.util.List;

public class MaterialsFragment extends BaseFragment implements OnData<Queryable> {
    private FragmentMaterialBinding binding;
    private BroadcastReceiver receiver;
    private MaterialsAdapter adapter;
    private ActionMode action;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new DownloadReceiver((DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE),
                id -> adapter.notifyItemDownloaded(id));

        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (!hasPermission(getContext()))
            requestPermission(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MaterialsAdapter(getContext(), new OnInteractListener() {

            @Override
            public boolean isSelectionMode() {
                return action != null;
            }

            @Override
            public void setSelectionMode(ActionMode.Callback callback) {
                action = getActivity().startActionMode(callback);
                refresh.setEnabled(false);
            }

            @Override
            public void onSelectedCount(int size) {
                if (size > 0) {
                    action.setTitle(String.valueOf(size));
                } else {
                    action.finish();
                    action = null;
                }

                Design.syncToolbar(toolbar, Design.canScroll(scroll) && canExpand());
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                refresh.setEnabled(false);

                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.materials, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                action = null;
                refresh.setEnabled(true);
                Design.syncToolbar(toolbar, Design.canScroll(scroll) && canExpand());
            }
        });

        LinearLayoutManager layout = new LinearLayoutManager(getContext());

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemViewCacheSize(20);
        binding.recycler.setDrawingCacheEnabled(true);
        binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recycler.setLayoutManager(layout);
        binding.recycler.addItemDecoration(new MaterialItemDivider(getContext()));
        binding.recycler.setItemAnimator(null);
        binding.recycler.setAdapter(adapter);

        binding.recycler.addOnScrollListener(Design.getRefreshBehavior(refresh));

        if (getArguments() != null) {
            int p = adapter.highlight(getArguments().getLong("ID2"));

            if (p >= 0) {
                layout.scrollToPosition(p);
                adapter.handleDownload(p);
            }
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Design.syncToolbar(toolbar, Design.canScroll(scroll) && canExpand());
        }, 100);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_material, container, false);
        binding = FragmentMaterialBinding.bind(view);
        return view;
    }

    private boolean canExpand() {
        return !DataBase.get().getMaterialsDataProvider().getList().isEmpty();
    }

    @Override
    protected void onAddListeners() {
        DataBase.get().getMaterialsDataProvider().addOnDataListener(this);
        Design.syncToolbar(toolbar, Design.canScroll(scroll) && canExpand());
    }

    @Override
    protected void onRemoveListeners() {
        DataBase.get().getMaterialsDataProvider().removeOnDataListener(this);
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

    @Override
    public void onUpdate(List<Queryable> list) {
        Design.syncToolbar(toolbar, Design.canScroll(scroll) && !list.isEmpty());
    }

}
