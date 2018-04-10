package com.qacademico.qacademico.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.qacademico.qacademico.Adapter.AdapterMateriais;
import com.qacademico.qacademico.Class.Materiais;
import com.qacademico.qacademico.R;

import java.util.List;

public class MateriaisActivity extends AppCompatActivity {
    private int revealX;
    private int revealY;
    private int pageId;

    View rootLayout;
    RecyclerView recyclerViewMateriais;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materiais);

        toolbar = (Toolbar) findViewById(R.id.toolbar_second);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.title_materiais);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = findViewById(R.id.materiaisContainer);
        recyclerViewMateriais = (RecyclerView) findViewById(R.id.recycler_materiais);

        final Intent intent = getIntent();

        pageId = intent.getIntExtra("PAGE", 0);

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra("EXTRA_CIRCULAR_REVEAL_X") &&
                intent.hasExtra("EXTRA_CIRCULAR_REVEAL_Y")) {

            rootLayout.setVisibility(View.INVISIBLE);
            recyclerViewMateriais.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra("EXTRA_CIRCULAR_REVEAL_X", 0);
            revealY = intent.getIntExtra("EXTRA_CIRCULAR_REVEAL_Y", 0);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
            recyclerViewMateriais.setVisibility(View.VISIBLE);
            rootLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }

        Bundle bundle = getIntent().getExtras();
        List<Materiais> materiaisList = (List<Materiais>) bundle.getSerializable("Materiais");

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        AdapterMateriais adapter = new AdapterMateriais(materiaisList, getApplicationContext());
        recyclerViewMateriais.setAdapter(adapter);
        recyclerViewMateriais.setLayoutManager(layout);
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootLayout.setBackgroundColor(getResources().getColor(R.color.cyan_500));

            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(250);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();

            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ValueAnimator bck = ValueAnimator.ofObject(new ArgbEvaluator(),
                                getResources().getColor(R.color.cyan_500), getResources().getColor(R.color.white));
                        bck.addUpdateListener(animator -> {
                            rootLayout.setBackgroundColor((Integer) animator.getAnimatedValue());
                        });
                        bck.setDuration(250);
                        bck.setStartDelay(0);
                        bck.start();

                        ValueAnimator visibility = ValueAnimator.ofObject(new IntEvaluator(),
                                View.INVISIBLE, View.VISIBLE);
                        visibility.addUpdateListener(animator -> {
                            recyclerViewMateriais.setVisibility((Integer) animator.getAnimatedValue());
                        });
                        visibility.setDuration(250);
                        visibility.setStartDelay(250);
                        visibility.start();
                    }
                }
            });
        }
    }

    protected void unRevealActivity() {
        if (revealX != 0 && revealY != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ValueAnimator bck = ValueAnimator.ofObject(new ArgbEvaluator(),
                        getResources().getColor(R.color.white), getResources().getColor(R.color.cyan_500));
                bck.addUpdateListener(animator -> {
                    rootLayout.setBackgroundColor((Integer) animator.getAnimatedValue());
                });
                bck.setDuration(250);
                bck.setStartDelay(250);
                bck.start();

                ValueAnimator visibility = ValueAnimator.ofObject(new IntEvaluator(),
                        View.VISIBLE, View.INVISIBLE);
                visibility.addUpdateListener(animator -> {
                    recyclerViewMateriais.setVisibility((Integer) animator.getAnimatedValue());
                });
                visibility.setDuration(250);
                visibility.setStartDelay(0);
                visibility.start();

                bck.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
                        Animator circularReveal = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            circularReveal = ViewAnimationUtils.createCircularReveal(
                                    rootLayout, revealX, revealY, finalRadius, 0);
                            circularReveal.setDuration(250);
                            circularReveal.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    rootLayout.setVisibility(View.INVISIBLE);
                                    finish();
                                }
                            });
                            circularReveal.start();
                        }
                    }
                });
            }
        } else {
            //fade
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                unRevealActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        unRevealActivity();
    }
}
