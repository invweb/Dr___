package com.vsk.dr.tech

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.DisplayMetrics
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.core.graphics.createBitmap


object Fun {

    fun getIconFromPackageName(packageName: String, context: Context): Drawable? {
        val pm = context.packageManager
        try {
            val pi = pm.getPackageInfo(packageName, 0)
            val otherAppCtx =
                context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)

            val displayMetrics: IntArray? = intArrayOf(
                DisplayMetrics.DENSITY_XHIGH,
                DisplayMetrics.DENSITY_HIGH,
                DisplayMetrics.DENSITY_TV
            )

            for (displayMetric in displayMetrics!!) {
                try {
                    val d = otherAppCtx.resources
                        .getDrawableForDensity(
                            pi.applicationInfo!!.icon,
                            displayMetric,
                            otherAppCtx.theme
                            )
                    if (d != null) {
                        return d
                    }
                } catch (e: Resources.NotFoundException) {
//                      Log.d(TAG, "NameNotFound for" + packageName + " @ density: " + displayMetric);
                    continue
                }
            }
        } catch (e: Exception) {
            // Handle Error here
        }

        var appInfo: ApplicationInfo? = null
        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
        getBitmapFromImage(context = context, db = appInfo.loadIcon(pm))
        return appInfo.loadIcon(pm)
    }

    private fun getBitmapFromImage(context: Context, db: Drawable): Bitmap {
        // in below line we are creating our bitmap and initializing it.
        val bit = createBitmap(db.intrinsicWidth, db.intrinsicHeight)

        // on below line we are
        // creating a variable for canvas.
        val canvas = Canvas(bit.asImageBitmap())

        // on below line we are setting bounds for our bitmap.
        db.setBounds(
            0,
            0,
            canvas.nativeCanvas.width,
            canvas.nativeCanvas.height
        )

        // on below line we are simply
        // calling draw to draw our canvas.
        db.draw(canvas.nativeCanvas)

        // on below line we are
        // returning our bitmap.
        return bit
    }

    fun getAppIcon(mPackageManager: PackageManager, packageName: String): Any? {
        try {
            val drawable = mPackageManager.getApplicationIcon(packageName)
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            } else if (drawable is AdaptiveIconDrawable) {
                val backgroundDr = drawable.background
                val foregroundDr = drawable.foreground
                val drr = arrayOfNulls<Drawable>(2)
                drr[0] = backgroundDr
                drr[1] = foregroundDr
                val layerDrawable = LayerDrawable(drr)
                val width = layerDrawable.intrinsicWidth
                val height = layerDrawable.intrinsicHeight
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                return bitmap.asImageBitmap()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}