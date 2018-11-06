package com.tinf.qmobile.Utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import com.tinf.qmobile.R;

public class Design {

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
    }

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
