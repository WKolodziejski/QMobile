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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import org.mozilla.javascript.tools.jsc.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private ActionMode action;
    private MaterialsAdapter adapter;
    private List<String> list = new ArrayList<>();

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).refreshLayout.setEnabled(false);

            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.materials, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_delete) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.dialog_delete))
                        .setMessage(list.size() > 1 ? String.format(Locale.getDefault(), getString(R.string.contextual_remove_txt_plu), String.valueOf(list.size())) : getString(R.string.contextual_remove_txt_sing))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.dialog_delete), (dialogInterface, d) -> {
                            if (action != null) {

                                while (!list.isEmpty()) {
                                    String f = list.get(0);

                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                            + "/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos) + "/" + f);

                                    Log.d("File", f);

                                    if (file.exists()) {
                                        if (file.delete())
                                            list.remove(f);

                                    } else break;
                                }

                                action.setTitle(String.valueOf(list.size()));

                                if (list.isEmpty()) {
                                    action.finish();
                                    action = null;
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), null)
                        .create()
                        .show();

                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            list.clear();
            adapter.unSelectAll();
            action = null;

            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).refreshLayout.setEnabled(true);
        }
    };

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

            adapter = new MaterialsAdapter(getContext(), getArguments(), new MaterialsAdapter.OnInteractListener() {

                @Override
                public boolean onLongClick(Queryable q) {
                    if (q instanceof Material) {
                        Material m = (Material) q;

                        if (m.isDownloaded) {

                            if (action == null)
                                action = getActivity().startActionMode(callback);

                            if (m.isSelected) {
                                m.isSelected = false;
                                list.remove(m.getFileName());

                                action.setTitle(String.valueOf(list.size()));

                                if (list.isEmpty()) {
                                    action.finish();
                                    action = null;
                                }
                            } else {
                                if (!list.contains(m.getFileName())) {
                                    m.isSelected = true;
                                    list.add(m.getFileName());
                                }

                                action.setTitle(String.valueOf(list.size()));
                            }

                            return true;
                        }
                    }

                    return false;
                }

                @Override
                public boolean onClick(Queryable q) {
                    if (q instanceof Matter) {
                        if (action == null) {
                            Intent intent = new Intent(getContext(), MatterActivity.class);
                            intent.putExtra("ID", ((Matter) q).id);
                            intent.putExtra("PAGE", MatterActivity.MATERIALS);
                            startActivity(intent);
                        }
                        return false;
                    } else {
                        Material m = (Material) q;

                        if (action == null) {
                            if (m.isDownloaded) {
                                openFile(m);
                            } else {
                                if (isConnected()) {
                                    m.isDownloading = true;

                                    DownloadManager manager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);

                                    long lastDownloadL = manager.enqueue(Client.get().downloadMaterial(m));

                                    Box<Material> box = DataBase.get().getBoxStore().boxFor(Material.class);
                                    m.setMime(manager.getMimeTypeForDownloadedFile(lastDownloadL));
                                    m.setPath("/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos));
                                    m.see();
                                    box.put(m);

                                    material = m;

                                    Toast.makeText(getContext(), getResources().getString(R.string.materiais_downloading), Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getContext(), getResources().getString(R.string.client_no_connection), Toast.LENGTH_SHORT).show();
                                }
                            }
                            return true;
                        } else {
                            if (m.isDownloaded) {
                                if (m.isSelected) {
                                    m.isSelected = false;
                                    list.remove(m.getFileName());

                                    action.setTitle(String.valueOf(list.size()));

                                    if (list.isEmpty()) {
                                        action.finish();
                                        action = null;
                                    }
                                } else {
                                    if (!list.contains(m.getFileName())) {
                                        m.isSelected = true;
                                        list.add(m.getFileName());
                                    }

                                    action.setTitle(String.valueOf(list.size()));
                                }
                            }
                            return false;
                        }
                    }
                }
            });

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
        }
    }

    private void openFile(Material material) {
        if (material != null) {

            material.isDownloading = false;

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
        if (!list.isEmpty() && action == null)
            action = getActivity().startActionMode(callback);
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
