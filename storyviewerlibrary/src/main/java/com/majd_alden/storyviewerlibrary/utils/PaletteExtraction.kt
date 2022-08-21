package com.majd_alden.storyviewerlibrary.utils

import android.graphics.Bitmap
import android.graphics.Color
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
                    val drawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(
                            palette.getDarkVibrantColor(Color.BLACK),
                            palette.getLightMutedColor(Color.BLACK)
                        )
                    )
                    drawable.cornerRadius = 0f
                    view.setBackgroundColor(Color.TRANSPARENT)
                    view.background = drawable
                } catch (e: Throwable) {
                    view.setBackgroundColor(Color.BLACK)
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
