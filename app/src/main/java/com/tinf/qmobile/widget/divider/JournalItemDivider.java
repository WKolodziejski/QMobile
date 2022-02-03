package com.tinf.qmobile.widget.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.model.ViewType;
import com.tinf.qmobile.utility.Design;

public class JournalItemDivider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private final Drawable divider;
    private final int padding;

    public JournalItemDivider(Context context, int margin) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        divider = styledAttributes.getDrawable(0);
        padding = Design.dpiToPixels(margin);
        styledAttributes.recycle();
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || divider == null)
            return;

        canvas.save();

        final int left;
        final int right;

        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int childCount = parent.getChildCount() - 1;

        RecyclerView.Adapter adapter = parent.getAdapter();

        if (adapter == null)
            return;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            int p = parent.getChildAdapterPosition(child);

            int viewType = adapter.getItemViewType(p);

            if (p < 0 || p > childCount)
            {
                viewType = ViewType.JOURNAL;
            }

            if (viewType == ViewType.FOOTERJ || viewType == ViewType.FOOTERP) {
                divider.setBounds(left, top, right, bottom);
            } else {
                divider.setBounds(left + padding, top, right, bottom);
            }

            divider.draw(canvas);
        }

        canvas.restore();
    }

}