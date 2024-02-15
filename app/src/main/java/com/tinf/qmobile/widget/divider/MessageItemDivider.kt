package com.tinf.qmobile.widget.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.tinf.qmobile.R
import com.tinf.qmobile.utility.DesignUtils

class MessageItemDivider(context: Context) : RecyclerView.ItemDecoration() {
    private val divider =
        DesignUtils.getDrawable(context, R.drawable.decorator_messages)!!

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { adapter ->
            parent.children
                .forEach { view ->
                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                        .let { if (it == RecyclerView.NO_POSITION) return else it }

                    if (childAdapterPosition == adapter.itemCount - 1)
                        return

                    divider.drawSeparator(view, parent, canvas)
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