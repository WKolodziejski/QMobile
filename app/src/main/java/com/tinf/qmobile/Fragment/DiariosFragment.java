package com.tinf.qmobile.Fragment;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Activity.MateriaActivity;
import com.tinf.qmobile.Adapter.Diarios.DiariosListAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import net.cachapa.expandablelayout.ExpandableLayout;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.Network.Client.pos;
import static com.tinf.qmobile.Network.OnResponse.PG_DIARIOS;

public class DiariosFragment extends Fragment implements OnUpdate {
    private static String TAG = "DiariosFragment";
    private DiariosListAdapter adapter;
    private List<Materia> materiaList;
    private FloatingActionButton fab;
    private RecyclerView.LayoutManager layout;
    @BindView(R.id.recycler_diarios) RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "New instace created");

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_expand);

        fab.setOnClickListener(v -> {
            adapter.toggleAll();
        });

        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        loadData();

        adapter = new DiariosListAdapter(getContext(), materiaList, view -> {
            Integer pos = (Integer) view.getTag();

            Intent intent = new Intent(getContext(), MateriaActivity.class);
            intent.putExtra("NAME", materiaList.get(pos).getName());
            intent.putExtra("YEAR", materiaList.get(pos).getYear());
            intent.putExtra("PERIOD", materiaList.get(pos).getPeriod());

            startActivity(intent);

            /*Fragment fragment = new MateriaFragment();
            fragment.setArguments(intent.getExtras());

            ((FragmentActivity) view1.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_fragment, fragment)
                    .commit();

            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        }, view -> {
            ConstraintLayout expandAct = (ConstraintLayout) view;
            ExpandableLayout expandableLayout = (ExpandableLayout) expandAct.getChildAt(2);
            ImageView arrow = (ImageView) expandAct.getChildAt(1);

            expandableLayout.toggle();
            arrow.startAnimation(rotate);

            Integer pos = (Integer) view.getTag();

            materiaList.get(pos).setExpanded(!materiaList.get(pos).isExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (materiaList.get(pos).isExpanded()) {
                        arrow.setImageResource(R.drawable.ic_less);
                    } else {
                        arrow.setImageResource(R.drawable.ic_more);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
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
        materiaList = App.getBox().boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, User.getYear(pos)).and()
                .equal(Materia_.period, User.getPeriod(pos))
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
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            fab.show();
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fab.hide();
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
        ((MainActivity) getActivity()).addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
    }
}
