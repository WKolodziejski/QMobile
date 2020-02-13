package com.tinf.qmobile.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;

import static com.tinf.qmobile.model.journal.JournalBase.ViewType.JOURNAL;

public class JournalDecoration extends DividerItemDecoration {
    private Context context;

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public JournalDecoration(Context context, int orientation) {
        super(context, orientation);
        this.context = context;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        Drawable dividerDrawable = getDrawable();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + dividerDrawable.getIntrinsicHeight();

            if (parent.getAdapter().getItemViewType(i) == JOURNAL)
                left = Math.round(56 * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

            dividerDrawable.setBounds(left, top, right, bottom);

            if ((parent.getChildAdapterPosition(child) == parent.getAdapter().getItemCount() - 1) && parent.getBottom() < bottom) { // this prevent a parent to hide the last item's divider
                parent.setPadding(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingRight(), bottom - parent.getBottom());
            }

            dividerDrawable.draw(canvas);
        }
    }

}
