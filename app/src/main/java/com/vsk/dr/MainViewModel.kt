package com.vsk.dr

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import java.security.NoSuchAlgorithmException


class MainViewModel(application: Application) : AndroidViewModel(application) {
    fun getInstalledApps(): MutableList<PackageInfo> {
        val flags = PackageManager.GET_META_DATA or
                PackageManager.GET_SHARED_LIBRARY_FILES

        val pm: PackageManager = application.packageManager
        val applications: MutableList<PackageInfo> = pm.getInstalledPackages(flags)
        return applications
    }

    fun getApplicationName(resolveInfo: ApplicationInfo?): String? {
        return resolveInfo?.name?.toString()
//        return resolveInfo?.nonLocalizedLabel?.toString()
    }

    fun getVersionName(packageInfo: PackageInfo): String {
        return PackageInfoCompat.getLongVersionCode(packageInfo).toString()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getVersionNo(packageName: String): Long? {
        var packageInfo: PackageInfo? = null
        try {
//            PackageInfoCompat()
            packageInfo = application.packageManager.getPackageInfo(packageName, 0)
            return packageInfo!!.longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    fun getVersionName(packageName: String): String? {
        try {
            val packageInfo = application.packageManager.getPackageInfo(packageName, 0)
            return packageInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
        return null
    }

    fun getFullResIcon(info: ActivityInfo): Drawable? {
        val resources = try {
            application.packageManager.getResourcesForApplication(info.applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        if (resources != null) {
            val iconId = info.iconResource
            if (iconId != 0) {
                return getFullResIcon(info)
            }
        }
        return null
    }

    fun openApp(context: Context, packageName: String): Boolean {
        val manager = context.packageManager
        try {
            val i = manager.getLaunchIntentForPackage(packageName) ?: return false
            //throw new ActivityNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            context.startActivity(i)
            return true
        } catch (e: ActivityNotFoundException) {
            return false
        }
    }
}