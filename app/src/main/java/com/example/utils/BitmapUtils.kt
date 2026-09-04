package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build

object BitmapUtils {
    fun drawableToBitmap(drawable: Drawable, targetSize: Int = 144): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val bm = drawable.bitmap
            if (bm.width > 0 && bm.height > 0) {
                // If reasonably sized, return scaled copy
                return if (bm.width == targetSize && bm.height == targetSize) {
                    bm
                } else {
                    Bitmap.createScaledBitmap(bm, targetSize, targetSize, true)
                }
            }
        }

        val bitmap = Bitmap.createBitmap(
            targetSize,
            targetSize,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
