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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.adapter.materiais.MateriaisListAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.materiais.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.User;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.tinf.qmobile.BuildConfig.APPLICATION_ID;
import static com.tinf.qmobile.network.Client.isConnected;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIAIS;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class MateriaisFragment extends Fragment implements OnUpdate {
    private static String TAG = "MateriaisFragment";
    private List<Matter> materiaList;
    private MateriaisListAdapter adapter;
    private String name, mime, path;
    @BindView(R.id.recycler_materiais) RecyclerView recyclerView;
    @BindView(R.id.materiais_empty) View empty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Client.get().load(PG_MATERIAIS);

        loadData();

        adapter = new MateriaisListAdapter(getContext(), materiaList, material -> {
            if (checkPermission()) {
                if (material.isDownloaded) {
                    openFile(material.getFileName(), material.getPath(), material.getMime());
                } else {
                    downloadFile(material);
                }
            } else {
                requestPermission();
            }
        });
    }

    private void loadData() {
        path = "/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos);

        List<Matter> matters = App.getBox().boxFor(Matter.class).query().order(Matter_.title)
                .equal(Matter_.year, User.getYear(pos)).and()
                .equal(Matter_.period, User.getPeriod(pos))
                .build().find();

        materiaList = new ArrayList<>();

        for (int i = 0; i < matters.size(); i++) {
            if (!matters.get(i).materials.isEmpty()) {
                materiaList.add(matters.get(i));
            }
        }

        if (checkPermission()) {

            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + path);

            if (folder.exists()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    for (int i = 0; i < materiaList.size(); i++) {
                        for (int j = 0; j < materiaList.get(i).materials.size(); j++) {
                            if (materiaList.get(i).materials.get(j).getFileName().equals(file.getName())) {
                                materiaList.get(i).materials.get(j).isDownloaded = true;
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            requestPermission();
        }
    }

    private void downloadFile(Material material) {
        if (isConnected()) {
            DownloadManager manager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);

            name = material.getFileName();
            path = "/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos);

            long lastDownloadL = manager.enqueue(Client.get().downloadMaterial(material, path, name));

            mime = manager.getMimeTypeForDownloadedFile(lastDownloadL);

            Box<Material> materiaisBox = App.getBox().boxFor(Material.class);
            material.setMime(mime);
            material.setPath(path);
            materiaisBox.put(material);

            Toast.makeText(getContext(), getResources().getString(R.string.materiais_downloading), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.client_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materiais, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "View created");

        view.post(() -> {

            RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

            recyclerView.setLayoutManager(layout);
            recyclerView.addItemDecoration(decoration);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
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
        });
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == UPDATE_REQUEST) {
            Client.get().load(PG_MATERIAIS);
        }

        if (pg == PG_MATERIAIS || pg == UPDATE_REQUEST) {
            loadData();
            adapter.update(materiaList);

            if (!materiaList.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onScrollRequest() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    private boolean checkPermission() {
        int write = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return (write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            openFile(name, path, mime);
        }
    };

    private void openFile(String name, String path, String mime) {
        Intent i = new Intent(ACTION_VIEW);
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + path + "/" + name);

        Uri uri = FileProvider.getUriForFile(getContext(), APPLICATION_ID, file);

        i.setDataAndType(uri, mime);

        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), getResources().getString(R.string.text_no_handler), Toast.LENGTH_LONG).show();
        }
    }

    public interface OnDownloadListener {
        void onDownload(Material material);
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
        getActivity().unregisterReceiver(onComplete);
    }

}
