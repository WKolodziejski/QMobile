package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.adapter.diarios.DiariosListAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.adapter.diarios.ExpandableAdapter;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.User;

import net.cachapa.expandablelayout.ExpandableLayout;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.App.getBox;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_DIARIOS;

public class JournalFragment extends Fragment implements OnUpdate {
    private static String TAG = "DiariosFragment";
    private ExpandableAdapter adapter;
    private List<Matter> materiaList;
    private RecyclerView.LayoutManager layout;
    @BindView(R.id.recycler_diarios) RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "New instace created");

        setHasOptionsMenu(true);

        loadData();

        adapter = new ExpandableAdapter(getContext(), materiaList, view -> {
            Integer pos = (Integer) view.getTag();

            Intent intent = new Intent(getContext(), MateriaActivity.class);
            intent.putExtra("ID", materiaList.get(pos).id);

            startActivity(intent);

            /*Fragment fragment = new MateriaFragment();
            fragment.setArguments(intent.getExtras());

            ((FragmentActivity) view1.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_fragment, fragment)
                    .commit();

            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        });

        adapter.setHasStableIds(true);

        layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        adapter.setOnExpandListener(position -> {

            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_ANY;
                }
            };
            if (position != 0) {
                smoothScroller.setTargetPosition(position);
                layout.startSmoothScroll(smoothScroller);
            }
        });
    }

    private void loadData() {
        materiaList = getBox().boxFor(Matter.class).query().order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos)).and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build().find();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarios, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "View created");

        view.post(() -> {

            ((MainActivity) getActivity()).fab.setOnClickListener(v -> {
                adapter.toggleAll();
            });

            DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                    LinearLayoutManager.VERTICAL);

            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(layout);
            recyclerView.addItemDecoration(decoration);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(p == 0);
                    if (dy < 0 && !((MainActivity) getActivity()).fab.isShown())
                        ((MainActivity) getActivity()).fab.show();
                    else if(dy > 0 && ((MainActivity) getActivity()).fab.isShown())
                        ((MainActivity) getActivity()).fab.hide();
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            ((MainActivity) getActivity()).fab.setIconResource(R.drawable.ic_expand);
            ((MainActivity) getActivity()).fab.extend(((MainActivity) getActivity()).fab.isShown());
            ((MainActivity) getActivity()).fab.show();
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_column);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //((MainActivity) getActivity()).fab.hide();
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_DIARIOS || pg == UPDATE_REQUEST) {
            loadData();
            adapter.update(materiaList);
        }
    }

    @Override
    public void onScrollRequest() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
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

}
