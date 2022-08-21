package com.majd_alden.storyviewerlibrary.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class PaletteExtraction(
    view: View,
    var lifecycleCoroutineScope: LifecycleCoroutineScope,
    resource: Bitmap?
) {
    private val viewWeakReference: WeakReference<View>
    private val mBitmapWeakReference: WeakReference<Bitmap?>

    fun execute() {
//        viewWeakReference.get()?.findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.IO) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            var palette: Palette? = null
            val bitmap = mBitmapWeakReference.get()
            if (bitmap != null) {
                try {
                    palette = Palette.from(bitmap).generate()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            withContext(Dispatchers.Main) {
                if (palette == null) return@withContext
                val view = viewWeakReference.get() ?: return@withContext
                try {
                    val vibrantSwatch = palette.vibrantSwatch
                    val vibrantSwatchRGB = palette.vibrantSwatch?.rgb ?: 0
                    val mutedSwatch = palette.mutedSwatch
                    val mutedSwatchRGB = palette.mutedSwatch?.rgb ?: 0
                    val dominantSwatch = palette.dominantSwatch
                    val dominantSwatchRGB = palette.dominantSwatch?.rgb ?: 0
                    val darkVibrantSwatch = palette.darkVibrantSwatch
                    val darkVibrantSwatchRGB = palette.darkVibrantSwatch?.rgb ?: 0
                    val darkMutedSwatch = palette.darkMutedSwatch
                    val darkMutedSwatchRGB = palette.darkMutedSwatch?.rgb ?: 0
                    val lightVibrantSwatch = palette.lightVibrantSwatch
                    val lightVibrantSwatchRGB = palette.lightVibrantSwatch?.rgb ?: 0
                    val lightMutedSwatch = palette.lightMutedSwatch
                    val lightMutedSwatchRGB = palette.lightMutedSwatch?.rgb ?: 0


                    val colors = intArrayOf(
                        if (darkVibrantSwatchRGB != 0) darkVibrantSwatchRGB else darkMutedSwatchRGB,
                        if (lightMutedSwatchRGB != 0) lightMutedSwatchRGB else lightVibrantSwatchRGB
                    )
                        .filter { it != 0 }
                        .toMutableList()

                    if (colors.isEmpty()) {
                        colors.add(Color.BLACK)
                    }

                    val drawable = if (colors.size == 1) {
                        ColorDrawable(colors.first())
                    } else {
                        val backgroundDrawable = GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            colors.toIntArray()
                        )
                        backgroundDrawable.cornerRadius = 0f

                        backgroundDrawable
                    }

                    view.background = drawable
                } catch (e: Throwable) {
                    view.background = ColorDrawable(Color.BLACK)
                    e.printStackTrace()
                }
            }
        }
    }

    init {
        viewWeakReference = WeakReference(view)
        mBitmapWeakReference = WeakReference(resource)
    }
}
