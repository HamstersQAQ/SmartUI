package com.yppcat.smartui.util

import android.content.Context


object DensityUtil {
    fun dip2px(context: Context, n: Int): Int {
        return (n * context.resources.displayMetrics.density + 0.5f).toInt()
    }


    fun px2dip(context: Context, n: Float): Int {
        return (n / context.resources.displayMetrics.density).toInt()
    }

    fun px2sp(context: Context, n: Float): Float {
        return n / context.resources.displayMetrics.scaledDensity
    }

    fun sp2px(context: Context, n: Float): Float {
        return n * context.resources.displayMetrics.scaledDensity
    }
}
