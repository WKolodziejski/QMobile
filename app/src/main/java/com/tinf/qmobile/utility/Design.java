package com.tinf.qmobile.utility;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.tinf.qmobile.R;

import java.util.HashMap;
import java.util.Map;

public class Design {

    public static void setStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setStatusBarLight(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

    public static Map<String, Integer> icons = new HashMap<>();

    public static int parseIcon(String ext) {
        if (icons.isEmpty()) {
            icons.put(".pdf", R.drawable.ic_pdf);
            icons.put(".doc", R.drawable.ic_doc);
            icons.put(".docx", R.drawable.ic_doc);
            icons.put(".ppt", R.drawable.ic_ppt);
            icons.put(".pptx", R.drawable.ic_ppt);
            icons.put(".xls", R.drawable.ic_xls);
            icons.put(".xlsx", R.drawable.ic_xls);
            icons.put(".zip", R.drawable.ic_zip);
            icons.put(".rtf", R.drawable.ic_rtf);
            icons.put(".txt", R.drawable.ic_txt);
            icons.put(".csv", R.drawable.ic_csv);
            icons.put(".svg", R.drawable.ic_svg);
            icons.put(".rar", R.drawable.ic_comp);
            icons.put(".7z", R.drawable.ic_comp);
            icons.put(".css", R.drawable.ic_css);
            icons.put(".dbf", R.drawable.ic_dbf);
            icons.put(".dwg", R.drawable.ic_dwg);
            icons.put(".exe", R.drawable.ic_exe);
            icons.put(".fla", R.drawable.ic_fla);
            icons.put(".html", R.drawable.ic_html);
            icons.put(".xml", R.drawable.ic_xml);
            icons.put(".iso", R.drawable.ic_iso);
            icons.put(".js", R.drawable.ic_js);
            icons.put(".jpg", R.drawable.ic_jpg);
            icons.put(".jpeg", R.drawable.ic_jpg);
            icons.put(".json", R.drawable.ic_json);
            icons.put(".mp3", R.drawable.ic_mp3);
            icons.put(".mp4", R.drawable.ic_mp4);
            icons.put(".ai", R.drawable.ic_ai);
            icons.put(".avi", R.drawable.ic_avi);
            icons.put(".png", R.drawable.ic_png);
            icons.put(".psd", R.drawable.ic_psd);
        }

        Integer icon = icons.get(ext);

        if (icon == null)
            icon = R.drawable.ic_file;

        return icon;
    }
}
