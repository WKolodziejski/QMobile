package com.qacademico.qacademico.Utilities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.qacademico.qacademico.R;

public class Design {

    /*public static void changePageColor(final Activity activity, Toolbar toolbar, DrawerLayout drawer, ProgressBar progressBar_Top,
                                       ProgressBar progressBar_Main, View layout, int idFrom, int idTo,
                                       boolean changeStatusBarColor) { //Muda a cor do app dependendo da página

        Context context = activity.getApplicationContext();

        int colorPrimaryBtnTo = 0;
        int colorSecondaryTo = 0;
        int colorPrimaryBtnFrom = 0;
        int colorSecondaryFrom = 0;
        int colorStatusBarTo = 0;
        int colorStatusBarFrom = 0;
        int colorActionBarTo = 0;
        int colorActionBarFrom = 0;
        int colorTitleTo = 0;
        int colorTitleFrom = 0;
        int colorToolBarTo = 0;
        int colorToolBarFrom = 0;
        int colorProgressTo = 0;
        int colorProgressFrom = 0;

        if (idFrom == R.id.navigation_home) {
            colorPrimaryBtnFrom = context.getResources().getColor(R.color.white);
            colorSecondaryFrom = context.getResources().getColor(R.color.colorPrimaryDark);
            colorStatusBarFrom = context.getResources().getColor(R.color.colorPrimaryDark);
            colorActionBarFrom = context.getResources().getColor(R.color.white);
            colorTitleFrom = context.getResources().getColor(R.color.colorPrimary);
            colorToolBarFrom = context.getResources().getColor(R.color.colorAccent);
            colorProgressFrom = context.getResources().getColor(R.color.colorAccent);
        } else if (idFrom == R.id.navigation_horario) {
            colorPrimaryBtnFrom = context.getResources().getColor(R.color.horario_buttonP);
            colorSecondaryFrom = context.getResources().getColor(R.color.horario_buttonS);
            colorStatusBarFrom = context.getResources().getColor(R.color.horario_statusbar);
            colorActionBarFrom = context.getResources().getColor(R.color.horario_actionbar);
            colorTitleFrom = context.getResources().getColor(R.color.white);
            colorToolBarFrom = context.getResources().getColor(R.color.horario_toolbar);
            colorProgressFrom = context.getResources().getColor(R.color.horario_progressbar);
        } else if (idFrom == R.id.navigation_boletim) {
            colorPrimaryBtnFrom = context.getResources().getColor(R.color.boletim_buttonP);
            colorSecondaryFrom = context.getResources().getColor(R.color.boletim_buttonS);
            colorStatusBarFrom = context.getResources().getColor(R.color.boletim_statusbar);
            colorActionBarFrom = context.getResources().getColor(R.color.boletim_actionbar);
            colorTitleFrom = context.getResources().getColor(R.color.white);
            colorToolBarFrom = context.getResources().getColor(R.color.boletim_toolbar);
            colorProgressFrom = context.getResources().getColor(R.color.boletim_progressbar);
        } else if (idFrom == R.id.navigation_diarios) {
            colorPrimaryBtnFrom = context.getResources().getColor(R.color.diarios_buttonP);
            colorSecondaryFrom = context.getResources().getColor(R.color.diarios_buttonS);
            colorStatusBarFrom = context.getResources().getColor(R.color.diarios_statusbar);
            colorActionBarFrom = context.getResources().getColor(R.color.diarios_actionbar);
            colorTitleFrom = context.getResources().getColor(R.color.white);
            colorToolBarFrom = context.getResources().getColor(R.color.diarios_toolbar);
            colorProgressFrom = context.getResources().getColor(R.color.diarios_progressbar);
        }

        if (idTo == R.id.navigation_home) {
            colorPrimaryBtnTo = context.getResources().getColor(R.color.white);
            colorSecondaryTo = context.getResources().getColor(R.color.colorPrimaryDark);
            colorStatusBarTo = context.getResources().getColor(R.color.colorPrimaryDark);
            colorActionBarTo = context.getResources().getColor(R.color.white);
            colorTitleTo = context.getResources().getColor(R.color.colorPrimary);
            colorToolBarTo = context.getResources().getColor(R.color.colorAccent);
            colorProgressTo = context.getResources().getColor(R.color.colorAccent);
        } else if (idTo == R.id.navigation_diarios) {
            colorPrimaryBtnTo = context.getResources().getColor(R.color.diarios_buttonP);
            colorSecondaryTo = context.getResources().getColor(R.color.diarios_buttonS);
            colorStatusBarTo = context.getResources().getColor(R.color.diarios_statusbar);
            colorActionBarTo = context.getResources().getColor(R.color.diarios_actionbar);
            colorTitleTo = context.getResources().getColor(R.color.white);
            colorToolBarTo = context.getResources().getColor(R.color.diarios_toolbar);
            colorProgressTo = context.getResources().getColor(R.color.diarios_progressbar);
        } else if (idTo == R.id.navigation_boletim) {
            colorPrimaryBtnTo = context.getResources().getColor(R.color.boletim_buttonP);
            colorSecondaryTo = context.getResources().getColor(R.color.boletim_buttonS);
            colorStatusBarTo = context.getResources().getColor(R.color.boletim_statusbar);
            colorActionBarTo = context.getResources().getColor(R.color.boletim_actionbar);
            colorTitleTo = context.getResources().getColor(R.color.white);
            colorToolBarTo = context.getResources().getColor(R.color.boletim_toolbar);
            colorProgressTo = context.getResources().getColor(R.color.boletim_progressbar);
        } else if (idTo == R.id.navigation_horario) {
            colorPrimaryBtnTo = context.getResources().getColor(R.color.horario_buttonP);
            colorSecondaryTo = context.getResources().getColor(R.color.horario_buttonS);
            colorStatusBarTo = context.getResources().getColor(R.color.horario_statusbar);
            colorActionBarTo = context.getResources().getColor(R.color.horario_actionbar);
            colorTitleTo = context.getResources().getColor(R.color.white);
            colorToolBarTo = context.getResources().getColor(R.color.horario_toolbar);
            colorProgressTo = context.getResources().getColor(R.color.horario_progressbar);
        }

        if (idTo == R.id.navigation_home) {
            removeToolbarScrollBehavior(context, layout, toolbar);
            if (changeStatusBarColor) {
                setSystemBarTheme(activity, false);
            }
        } else {
            applyToolbarScrollBehavior(context, layout, toolbar);
            if (changeStatusBarColor) {
                setSystemBarTheme(activity, true);
            }
        }

        activity.invalidateOptionsMenu();

        changeButtonColorAnim(fab_action, colorPrimaryBtnFrom, colorPrimaryBtnTo);
        changeButtonColorAnim(fab_expand, colorSecondaryFrom, colorSecondaryTo);
        changeButtonColorAnim(fab_data, colorSecondaryFrom, colorSecondaryTo);
        changeProgressBarColor(progressBar_Top, progressBar_Main, colorProgressFrom, colorProgressTo);

        if (changeStatusBarColor) {
            changeStatusBarColor(toolbar, activity, drawer, colorStatusBarFrom, colorStatusBarTo, colorActionBarTo, colorActionBarFrom, colorTitleTo,
                    colorTitleFrom, colorToolBarFrom, colorToolBarTo);
        }
    }

    private static void changeButtonColorAnim(FloatingActionButton fab, int colorStart, int colorEnd) {  //Muda a cor dos FloatingActionButtons
        ValueAnimator fabC = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);
        fabC.addUpdateListener(animator -> {
            fab.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
        });
        fabC.setDuration(250);
        fabC.setStartDelay(0);
        fabC.start();
    }

    private static void changeProgressBarColor(ProgressBar progressBar_Top, ProgressBar progressBar_Main, int colorStart, int colorEnd) {
        ValueAnimator fabC = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);
        fabC.addUpdateListener(animator -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar_Top.setIndeterminateTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
                progressBar_Main.setIndeterminateTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
            }
        });
        fabC.setDuration(250);
        fabC.setStartDelay(0);
        fabC.start();
    }

    private static void changeStatusBarColor(Toolbar toolbar, final Activity activity, DrawerLayout drawer, int colorStatusBarFrom,
                                             int colorStatusBarTo, int colorActionBarTo, int colorActionBarFrom,
                                             int colorTitleTo, int colorTitleFrom, int colorToolBarFrom, int colorToolbarTo) { //Muda a cor da StatusBar e da ActionBar

        Context context = activity.getApplicationContext();

        ValueAnimator actionBar = ValueAnimator.ofObject(new ArgbEvaluator(), colorActionBarFrom, colorActionBarTo);
        ValueAnimator statusBar = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusBarFrom, colorStatusBarTo);
        ValueAnimator title = ValueAnimator.ofObject(new ArgbEvaluator(), colorTitleFrom, colorTitleTo);
        ValueAnimator tool = ValueAnimator.ofObject(new ArgbEvaluator(), colorToolBarFrom, colorToolbarTo);

        actionBar.addUpdateListener(animator -> {
            toolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
        });

        statusBar.addUpdateListener(animator -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
            }
        });

        title.addUpdateListener(animator -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setTitleTextColor((Integer) animator.getAnimatedValue());
            }
        });

        tool.addUpdateListener(animator -> {
            Drawable nav = ContextCompat.getDrawable(context, R.drawable.ic_menu_black_24dp);
            nav = DrawableCompat.wrap(nav);
            DrawableCompat.setTint(nav, (Integer) animator.getAnimatedValue());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setNavigationIcon(nav);
            }

            Drawable toolbar = ContextCompat.getDrawable(context, R.drawable.ic_more_vert_black_24dp);
            toolbar = DrawableCompat.wrap(toolbar);
            DrawableCompat.setTint(toolbar, (Integer) animator.getAnimatedValue());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toolbar.setOverflowIcon(toolbar);
            }
        });

        actionBar.setDuration(250);
        actionBar.setStartDelay(0);
        actionBar.start();
        statusBar.setDuration(250);
        statusBar.setStartDelay(0);
        statusBar.start();
        title.setDuration(250);
        title.setStartDelay(0);
        title.start();
        tool.setDuration(250);
        tool.setStartDelay(0);
        tool.start();

        drawer.setStatusBarBackgroundColor(colorStatusBarTo);
    }

    public static void setStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setStatusBarLight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private static void setSystemBarTheme(final Activity activity, final boolean pIsDark) { //Muda o tema do app para StatusBar Light ou Dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int lFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
            activity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
        }
    }*/

    public static void applyToolbarScrollBehavior(Context context, View layout, Toolbar toolbar) { //Habilita o actionBar a se esconder ao rolar a página
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        layout.requestLayout();
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        layoutParams.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density),
                (int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density));

        layout.setLayoutParams(layoutParams);
    }

    public static void removeToolbarScrollBehavior(Context context, View layout, Toolbar toolbar) { //Desabilita o actionBar a se esconder ao rolar a página
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.setBehavior(null);
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);

        layoutParams.setMargins((int) (0 * context.getResources().getDisplayMetrics().density), (int) (55 * context.getResources().getDisplayMetrics().density),
                (int) (0 * context.getResources().getDisplayMetrics().density), (int) (0 * context.getResources().getDisplayMetrics().density));
        layout.setLayoutParams(layoutParams);
    }

    public static void setNavigationTransparent(Activity activity) { //Configura os botões de navegação do Android para transparente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
