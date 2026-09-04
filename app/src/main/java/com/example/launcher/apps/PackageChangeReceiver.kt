package com.example.launcher.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PackageEvents {
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val events = _events.asSharedFlow()

    fun notifyChanged(packageName: String) {
        _events.tryEmit(packageName)
    }
}

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val data = intent.data ?: return
        val packageName = data.schemeSpecificPart ?: return

        when (action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                PackageEvents.notifyChanged(packageName)
            }
        }
    }
}
