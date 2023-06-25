package com.deenislam.sdk.service.libs.notification;

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotificationPermission {

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

    fun setupLauncher(fragment:Fragment,mContext:Context,isShowDialog:Boolean)
    {
        notificationPermissionLauncher =  fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            instance?.setPermissionGranted(isGranted)
            if (!isGranted) {

                if(isShowDialog) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        /*if (fragment.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            NotificationPermission().getInstance()
                                .showNotificationPermissionRationale(mContext)
                        } else {
                            NotificationPermission().getInstance().showSettingDialog(mContext)
                        }*/
                        NotificationPermission().getInstance().showSettingDialog(mContext)
                    }
                }
            }
        }
    }




     fun showSettingDialog(mContext:Context) {
        MaterialAlertDialogBuilder(mContext, R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${mContext.packageName}")
                mContext.startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

     fun askPermission()
    {
        instance?.hasNotificationPermissionGranted = Build.VERSION.SDK_INT < 33
    }

    fun isNotificationPermitted():Boolean {

        if (Build.VERSION.SDK_INT >= 33) {
            return instance?.hasNotificationPermissionGranted ?: false
        }
        else
            return true

    }

    fun hasAlarm(context: Context, notificationId: Int): Boolean {

        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        notifyIntent.putExtra("pid",notificationId)

        val notifyPendingIntent: PendingIntent? =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                notifyIntent,
                PendingIntent.FLAG_NO_CREATE
            )


        return notifyPendingIntent!=null

    }


}
