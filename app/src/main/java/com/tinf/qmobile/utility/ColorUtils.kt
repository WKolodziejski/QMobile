package com.tinf.qmobile.utility

import android.graphics.Color
import dev.jorgecastillo.androidcolorx.library.complimentary
import dev.jorgecastillo.androidcolorx.library.darken
import dev.jorgecastillo.androidcolorx.library.lighten

object ColorUtils {

    private fun darken(color: Int, amount: Float): Int {
        return color.darken(amount)
    }

    private fun lighten(color: Int, amount: Float): Int {
        return color.lighten(amount)
    }

    fun complimentary(color: Int): Int {
        return color.complimentary()
    }

    fun contrast(color: Int, amount: Float): Int {
        return if (isColorDark(color))
            lighten(color, amount)
        else
            darken(color, amount)
    }

    fun string(color: Int): String {
        return String.format("#%06X", (0xFFFFFF and color))
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness: Double = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

}