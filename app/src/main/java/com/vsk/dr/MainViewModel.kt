package com.vsk.dr

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application


class MainViewModel(application: Application) : AndroidViewModel(application) {
    fun getInstalledApps(): List<ApplicationInfo> {
        val flags = PackageManager.GET_META_DATA or
                PackageManager.GET_SHARED_LIBRARY_FILES

        val pm: PackageManager = application.packageManager
        val applications = pm.getInstalledApplications(flags)
        return applications
//        for (appInfo in applications) {
//            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1) {
//                // System application
//            } else {
//                // Installed by user
//            }
//        }
    }

    fun getApplicationName(resolveInfo: ApplicationInfo): String? {
        return resolveInfo.nonLocalizedLabel?.toString()
    }
}