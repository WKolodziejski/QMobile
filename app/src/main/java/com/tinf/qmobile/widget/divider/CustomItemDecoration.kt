package com.tinf.qmobile.widget.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.tinf.qmobile.R
import com.tinf.qmobile.model.ViewType

class CustomItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val fullDivider = ContextCompat.getDrawable(context, R.drawable.decorator_journal_full)!!
    private val shortDivider = ContextCompat.getDrawable(context, R.drawable.decorator_journal_short)!!

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { adapter ->
            parent.children
                .forEach { view ->
                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                        .let { if (it == RecyclerView.NO_POSITION) return else it }

                    if (childAdapterPosition == adapter.itemCount - 1)
                        return

                    when (adapter.getItemViewType(childAdapterPosition)) {
                        ViewType.HEADER -> shortDivider.drawSeparator(view, parent, canvas)
                        ViewType.JOURNAL -> shortDivider.drawSeparator(view, parent, canvas)
                        ViewType.FOOTERJOURNAL -> fullDivider.drawSeparator(view, parent, canvas)
                        ViewType.FOOTERPERIOD -> fullDivider.drawSeparator(view, parent, canvas)
                        ViewType.PERIOD -> shortDivider.drawSeparator(view, parent, canvas)
                        ViewType.JOURNALEMPTY -> shortDivider.drawSeparator(view, parent, canvas)
                        else -> Unit
                    }
                }
        }
    }

    private fun Drawable.drawSeparator(view: View, parent: RecyclerView, canvas: Canvas) =
        apply {
            val top = view.bottom + parent.marginBottom
            val bottom = top + intrinsicHeight

            val left = view.marginLeft
            val right = view.width - view.marginRight

            bounds = Rect(left, top, right, bottom)
            draw(canvas)
        }

}