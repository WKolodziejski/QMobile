package com.tinf.qmobile.utility;

import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import org.jsoup.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class Design {

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

    public static int getColorForGrade(Context context, float grade) {
        if (grade < 6.0)
            return context.getResources().getColor(R.color.bad);

        //if (grade <= 5.0)
        //return context.getResources().getColor(R.color.yellow_500);

        if (grade < 8)
            return context.getResources().getColor(R.color.ok);

        if (grade <= 10.0)
            return context.getResources().getColor(R.color.good);

        return context.getResources().getColor(R.color.transparent);
    }

    public static int getColorForWarning(Context context, String color) {
        if (color == null)
            return context.getResources().getColor(R.color.colorPrimaryDark);

        if (color.equalsIgnoreCase("red"))
            return context.getResources().getColor(R.color.warningRed);

        if (color.equalsIgnoreCase("yellow"))
            return context.getResources().getColor(R.color.warningYellow);

        if (color.equalsIgnoreCase("green"))
            return context.getResources().getColor(R.color.warningGreen);

        return context.getResources().getColor(R.color.colorPrimaryDark);
    }

    public static void syncToolbar(MaterialToolbar toolbar, boolean canExpand) {
        if (toolbar == null)
            return;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(canExpand ? SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS | SCROLL_FLAG_SNAP : 0);
        toolbar.setLayoutParams(params);
    }

    public static boolean canScroll(FrameLayout scroll) {
        if (scroll == null)
            return false;

        View child = scroll.getChildAt(0);

        if (child == null)
            return false;

        return scroll.getHeight() < child.getHeight();
    }

    public static RecyclerView.OnScrollListener getRefreshBehavior(SwipeRefreshLayout refresh) {
        if (refresh == null)
            return new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            };

        return new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refresh.setEnabled(p == 0);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

        };
    }

    public static int dpiToPixels(int dpi) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpi,
                App.getContext().getResources().getDisplayMetrics()
        );
    }

    public interface OnDesign {
        void onToolbar(boolean canExpand);
    }

    public interface OnFragment {
        MaterialToolbar getToolbar();
        SwipeRefreshLayout getSwipeRefresh();
        FloatingActionButton getFab();
    }

}
