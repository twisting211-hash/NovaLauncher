package com.example.utils

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log

object SystemControlUtil {
    private const val TAG = "SystemControlUtil"
    private var isTorchOn = false

    fun isFlashlightOn(): Boolean = isTorchOn

    fun toggleFlashlight(context: Context): Boolean {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                ?: return false
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return false

            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            return isTorchOn
        } catch (e: Exception) {
            Log.e(TAG, "Flashlight error", e)
            isTorchOn = false
            return false
        }
    }

    fun getVolumePercent(context: Context): Float {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return 0.5f
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            if (max > 0) current.toFloat() / max.toFloat() else 0.5f
        } catch (e: Exception) {
            0.5f
        }
    }

    fun setVolumePercent(context: Context, percent: Float) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val target = (percent.coerceIn(0f, 1f) * max).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Volume set error", e)
        }
    }

    fun openWifiSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openGeneralSettings(context)
        }
    }

    fun openBluetoothSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openGeneralSettings(context)
        }
    }

    fun openAirplaneModeSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openGeneralSettings(context)
        }
    }

    fun openDataUsageSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_DATA_USAGE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openGeneralSettings(context)
        }
    }

    fun openCamera(context: Context) {
        try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Camera open error", e)
        }
    }

    fun openCalculator(context: Context) {
        val calcPackages = listOf(
            "com.google.android.calculator",
            "com.android.calculator2",
            "com.sec.android.app.popupcalculator",
            "com.miui.calculator"
        )
        for (pkg in calcPackages) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
                return
            }
        }
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_APP_CALCULATOR)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Calculator open error", e)
        }
    }

    fun openTimer(context: Context) {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_TIMERS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                Log.e(TAG, "Clock/Timer error", e2)
            }
        }
    }

    fun openGeneralSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Settings open error", e)
        }
    }
}
