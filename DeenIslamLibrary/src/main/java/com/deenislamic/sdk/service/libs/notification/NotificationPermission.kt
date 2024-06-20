package com.deenislamic.sdk.service.libs.notification;

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.deenislamic.sdk.DeenSDKCore
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal class NotificationPermission {

    var hasNotificationPermissionGranted = false

    var notificationPermissionLauncher: ActivityResultLauncher<String>? = null


    companion object {
        var instance: NotificationPermission? = null
    }

    fun getInstance(): NotificationPermission {
        if (instance == null)
            instance = NotificationPermission()

        return instance as NotificationPermission
    }

    fun setPermissionGranted(bol:Boolean){
        Log.e("SETP_CALLED",bol.toString())
        instance?.hasNotificationPermissionGranted =bol
    }

   /* fun setupLauncher(fragment:Fragment,mContext:Context,isShowDialog:Boolean,activityContext:Context)
    {
        notificationPermissionLauncher =  fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {

                if(isShowDialog) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (fragment.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            NotificationPermission().getInstance()
                                .showNotificationPermissionRationale(mContext,activityContext)
                        } else {
                            NotificationPermission().getInstance().showSettingDialog(mContext,activityContext)
                        }
                    }
                }
            }
            else
                instance?.setPermissionGranted(isGranted)
        }
    }*/

    fun setupLauncher(
        fragment: Fragment,
        mContext: Context,
        isShowDialog: Boolean,
        activityContext: Context
    ) {
        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                if (isShowDialog) {
                    if (fragment.shouldShowRequestPermissionRationale(permission)) {
                        ActivityCompat.requestPermissions(
                            activityContext as Activity,
                            arrayOf(permission),
                            100
                        )
                    }/* else {

                    NotificationPermission().getInstance()
                            .showSettingDialog(mContext, activityContext)

                        NotificationPermission().getInstance().showSettingDialog(mContext, activityContext)
                    }*/
                }

                if (!fragment.shouldShowRequestPermissionRationale(permission)) {
                    ActivityCompat.requestPermissions(
                        activityContext as Activity,
                        arrayOf(permission),
                        100
                    )
                }

            } else {
                instance?.setPermissionGranted(true)
            }
        }
        else
            instance?.setPermissionGranted(true)
    }

    fun reCheckNotificationPermission(mContext: Context) {
        if (Build.VERSION.SDK_INT >= 33) {
            instance?.setPermissionGranted(
                ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        }
        else
            instance?.setPermissionGranted(true)

    }

    fun showNotificationPermissionRationale(mContext: Context, activityContext: Context) {

        MaterialAlertDialogBuilder(activityContext, R.style.MaterialAlertDialog_MaterialComponents)
            .setTitle(mContext.getString(com.deenislamic.sdk.R.string.alert))
            .setMessage(mContext.getString(com.deenislamic.sdk.R.string.dialog_notification_context))
            .setPositiveButton(mContext.getString(com.deenislamic.sdk.R.string.okay)) { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    instance?.notificationPermissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(mContext.getString(com.deenislamic.sdk.R.string.cancel), null)
            .show()
    }

    fun showSettingDialog(mContext: Context, activityContext: Context) {
        MaterialAlertDialogBuilder(activityContext, R.style.MaterialAlertDialog_MaterialComponents)
            .setTitle(mContext.getString(com.deenislamic.sdk.R.string.alert))
            .setMessage(mContext.getString(com.deenislamic.sdk.R.string.dialog_notification_context))
            .setPositiveButton(mContext.getString(com.deenislamic.sdk.R.string.okay)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${mContext.packageName}")
                DeenSDKCore.baseContext?.startActivity(intent)
            }
            .setNegativeButton(mContext.getString(com.deenislamic.sdk.R.string.cancel), null)
            .show()
    }

    fun askPermission()
    {
        if (Build.VERSION.SDK_INT >= 33) {
            instance?.notificationPermissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            instance?.hasNotificationPermissionGranted = true
        }
    }

    fun isNotificationPermitted(context: Context? = null):Boolean {

        return if (Build.VERSION.SDK_INT >= 33) {
            instance?.setPermissionGranted(
                context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.POST_NOTIFICATIONS)
                } == PackageManager.PERMISSION_GRANTED)
            instance?.hasNotificationPermissionGranted ?: false
        } else
            true

    }

    fun hasAlarm(context: Context, notificationId: Int): Boolean {

        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        notifyIntent.putExtra("pid",notificationId)

        val notifyPendingIntent: PendingIntent? =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                notifyIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE
            )


        return notifyPendingIntent!=null

    }



}
