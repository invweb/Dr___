package com.vsk.dr

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application

class MainViewModel(application: Application) : AndroidViewModel(application) {
    fun getInstalledApps(): MutableList<PackageInfo> {
        val flags = PackageManager.GET_META_DATA or
                PackageManager.GET_SHARED_LIBRARY_FILES

        val pm: PackageManager = application.packageManager
        val applications: MutableList<PackageInfo> = pm.getInstalledPackages(flags)
        return applications
    }

    fun getApplicationName(resolveInfo: ApplicationInfo?): String? {
        return resolveInfo?.nonLocalizedLabel?.toString()
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
}