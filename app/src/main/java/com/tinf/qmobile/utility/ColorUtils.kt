package com.tinf.qmobile.utility

import dev.jorgecastillo.androidcolorx.library.complimentary
import dev.jorgecastillo.androidcolorx.library.darken
import dev.jorgecastillo.androidcolorx.library.lighten

object ColorUtils {

    fun darken(color: Int, amount: Float): Int {
        return color.darken(amount)
    }

    fun lighten(color: Int, amount: Float): Int {
        return color.lighten(amount)
    }

    fun complimentary(color: Int): Int {
        return color.complimentary()
    }

    fun string(color: Int): String {
        return String.format("#%06X", (0xFFFFFF and color))
    }

}