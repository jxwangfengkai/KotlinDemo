package com.example.launchdemo

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.orhanobut.logger.*

class AppContext: Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger()
    }

    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .tag("launchdemo")
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        Logger.addLogAdapter(object : LogAdapter {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return !BuildConfig.DEBUG
            }

            override fun log(priority: Int, tag: String?, message: String) {
                if (priority == Log.ERROR) {
                    //MobclickAgent.reportError(this@AppContext, message)
                }
            }

        })
        Logger.addLogAdapter(object : DiskLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                if (BuildConfig.DEBUG) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val checkSelfPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        return checkSelfPermission != PackageManager.PERMISSION_GRANTED
                    }
                    return true
                }

                return false
            }
        })
    }
}