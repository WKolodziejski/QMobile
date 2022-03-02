package com.tinf.qmobile.widget.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.model.ViewType;
import com.tinf.qmobile.utility.Design;

public class ClassItemDivider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private final Drawable divider;
    private final int padding;

    public ClassItemDivider(Context context, int margin) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        divider = styledAttributes.getDrawable(0);
        padding = Design.dpiToPixels(margin);
        styledAttributes.recycle();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, RecyclerView parent, @NonNull RecyclerView.State state) {
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
            View child1 = parent.getChildAt(i);
            View child2 = null;

            if (i + 1 < childCount) {
                child2 = parent.getChildAt(i + 1);
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child1.getLayoutParams();

            int top = child1.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            int p1 = parent.getChildAdapterPosition(child1);
            int p2 = -1;

            if (child2 != null) {
                p2 = parent.getChildAdapterPosition(child2);
            }

            int viewType = ViewType.CLASS;
            int nextType = ViewType.CLASS;

            if (p1 > 0 && p1 < childCount) {
                viewType = adapter.getItemViewType(p1);
            }

            if (p2 > 0 && p2 < childCount) {
                nextType = adapter.getItemViewType(p2);
            }

            if (viewType == ViewType.CLASS && nextType == ViewType.PERIOD) {
                divider.setBounds(left, top, right, bottom);
            } else {
                divider.setBounds(left + padding, top, right, bottom);
            }

            divider.draw(canvas);
        }

        canvas.restore();
    }

}
