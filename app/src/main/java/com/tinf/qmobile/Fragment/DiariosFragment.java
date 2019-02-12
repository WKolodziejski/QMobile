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

import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Activity.MateriaActivity;
import com.tinf.qmobile.Adapter.Diarios.DiariosListAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;
import java.util.Objects;

import io.objectbox.query.Query;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscriptionList;

public class DiariosFragment extends Fragment {
    private static String TAG = "DiariosFragment";
    private RotateAnimation rotate;
    private DiariosListAdapter adapter;
    private List<Materia> materiaList;
    private View.OnClickListener open, expand;
    private DataObserver<List<Materia>> observer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "New instace created");

        rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        open = view1 -> {
            Integer pos = (Integer) view1.getTag();

            Intent intent = new Intent(getContext(), MateriaActivity.class);
            intent.putExtra("NAME", materiaList.get(pos).getName());
            intent.putExtra("YEAR", materiaList.get(pos).getYear());

            startActivity(intent);

                /*Fragment fragment = new MateriaFragment();
                fragment.setArguments(intent.getExtras());

                ((FragmentActivity) view1.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_fragment, fragment)
                        .commit();

                ((MainActivity) getActivity()).hideTabLayout();
                ((MainActivity) getActivity()).hideExpandBtn();*/
        };

        expand = view2 -> {
            ConstraintLayout expandAct = (ConstraintLayout) view2;
            ExpandableLayout expandableLayout = (ExpandableLayout) expandAct.getChildAt(2);
            ImageView arrow = (ImageView) expandAct.getChildAt(1);

            expandableLayout.toggle();
            arrow.startAnimation(rotate);

            Integer pos = (Integer) view2.getTag();

            materiaList.get(pos).setExpanded(!materiaList.get(pos).isExpanded());

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (materiaList.get(pos).isExpanded()) {
                        arrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        arrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        };

        observer = data -> {
            materiaList = data;
            if (adapter == null) {
                adapter = new DiariosListAdapter(getActivity(), materiaList, open, expand);
                adapter.setHasStableIds(true);
            } else {
                adapter.update(materiaList);
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "View created");
        showDiarios(view);
    }

    private void showDiarios(View view) {

        //SingletonWebView webView = SingletonWebView.get();

        Query<Materia> query = App.getBox().boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, Client.getYear()).build();

        query.subscribe(new DataSubscriptionList()).observer(observer);

        view.post(() -> {

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_diarios);

            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);

            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(layout);
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(adapter);

            adapter.setOnExpandListener(position -> {

                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(Objects.requireNonNull(getActivity())) {
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

            ((NotasFragment) getParentFragment()).setOnTopScrollRequestedDListener(() -> {
                recyclerView.smoothScrollToPosition(0);
            });

            ((MainActivity) Objects.requireNonNull(getActivity())).fab_expand.setOnClickListener(v -> {
                adapter.toggleAll();
            });

            ((MainActivity) getActivity()).showExpandBtn();
        });
    }

}
