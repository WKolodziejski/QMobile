package com.tinf.qmobile.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.tinf.qmobile.BuildConfig.APPLICATION_ID;
import static com.tinf.qmobile.network.Client.isConnected;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIAIS;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class MaterialsFragment extends Fragment implements OnUpdate {
    @BindView(R.id.recycler_materiais)        RecyclerView recyclerView;
    private Material material;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            openFile(material);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null)
            getActivity().setTitle(User.getYears()[pos]);

        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Client.get().load(PG_MATERIAIS);

        if (!hasPermission())
            requestPermission();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (hasPermission()) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(new MaterialsAdapter(getContext(), getArguments(), material -> {
                if (material.isDownloaded) {
                    openFile(material);
                } else {
                    if (isConnected()) {

                        DownloadManager manager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);

                        long lastDownloadL = manager.enqueue(Client.get().downloadMaterial(material));

                        Box<Material> box = DataBase.get().getBoxStore().boxFor(Material.class);
                        material.setMime(manager.getMimeTypeForDownloadedFile(lastDownloadL));
                        material.setPath("/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos));
                        material.see();
                        box.put(material);

                        this.material = box.query().equal(Material_.id, material.id).build().findUnique();

                        Toast.makeText(getContext(), getResources().getString(R.string.materiais_downloading), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.client_no_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }));

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
        }
    }

    private void openFile(Material material) {
        if (material != null) {
            Intent intent = new Intent(ACTION_VIEW);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + material.getPath() + "/" + material.getFileName());

            Log.d("Fragment", file.getPath());

            Uri uri = FileProvider.getUriForFile(getContext(), APPLICATION_ID, file);

            intent.setDataAndType(uri, material.getMime());

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), getResources().getString(R.string.text_no_handler), Toast.LENGTH_LONG).show();
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
        getActivity().setTitle(User.getYears()[pos]);
        Client.get().load(PG_MATERIAIS);
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

}
