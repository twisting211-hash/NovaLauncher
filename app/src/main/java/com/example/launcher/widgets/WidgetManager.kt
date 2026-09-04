package com.example.launcher.widgets

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle

class NovaWidgetHost(context: Context, hostId: Int) : AppWidgetHost(context, hostId) {
    override fun onCreateView(
        context: Context,
        appWidgetId: Int,
        appWidget: AppWidgetProviderInfo?
    ): AppWidgetHostView {
        return super.onCreateView(context, appWidgetId, appWidget)
    }
}

class WidgetManager(private val context: Context) {

    companion object {
        const val HOST_ID = 2048
        const val REQUEST_BIND_APPWIDGET = 2001
        const val REQUEST_CONFIGURE_APPWIDGET = 2002
    }

    val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
    val appWidgetHost: NovaWidgetHost = NovaWidgetHost(context.applicationContext, HOST_ID)

    fun startListening() {
        try {
            appWidgetHost.startListening()
        } catch (_: Exception) {}
    }

    fun stopListening() {
        try {
            appWidgetHost.stopListening()
        } catch (_: Exception) {}
    }

    fun getInstalledWidgetProviders(): List<AppWidgetProviderInfo> {
        return try {
            appWidgetManager.installedProviders
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun allocateWidgetId(): Int {
        return appWidgetHost.allocateAppWidgetId()
    }

    fun deleteWidgetId(appWidgetId: Int) {
        try {
            appWidgetHost.deleteAppWidgetId(appWidgetId)
        } catch (_: Exception) {}
    }

    fun createWidgetView(context: Context, appWidgetId: Int): AppWidgetHostView? {
        val info = appWidgetManager.getAppWidgetInfo(appWidgetId) ?: return null
        return try {
            val view = appWidgetHost.createView(context, appWidgetId, info)
            view.setAppWidget(appWidgetId, info)
            view
        } catch (e: Exception) {
            null
        }
    }

    fun bindWidgetOrRequest(activity: Activity, appWidgetId: Int, providerInfo: AppWidgetProviderInfo): Boolean {
        val canBind = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, providerInfo.provider)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

        if (canBind) {
            return true
        } else {
            // Request bind permission from user
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, providerInfo.provider)
            }
            activity.startActivityForResult(intent, REQUEST_BIND_APPWIDGET)
            return false
        }
    }
}
