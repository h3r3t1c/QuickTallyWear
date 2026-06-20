package com.h3r3t1c.quickwearcounter.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.ui.MainActivity

object OngoingActivityHelper {

    const val ID_ONGOING_ACTIVITY = 1
    const val CHANNEL_ID_ONGOING_ACTIVITY = "ongoing_activity"


    fun showOngoingActivity(context: Context) {

        if(!PermissionHelper.hasNotificationPermission(context)) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
            notificationManager = notificationManager,
            channelName = context.getString(R.string.ongoing_activity),
            channelDescription = context.getString(R.string.ongoing_activity_desc)
        )

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val noteBuilder = createNotification(context)
        val ongoingActivity = OngoingActivity
            .Builder(
                context,
                ID_ONGOING_ACTIVITY,
                noteBuilder
            )
            .setOngoingActivityId(1)
            .setStaticIcon(R.drawable.ic_tally)
            .setTouchIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
        ongoingActivity.apply(context)
        notificationManager.notify(ID_ONGOING_ACTIVITY, noteBuilder.build())
    }
    fun hideOngoingActivity(context: Context) {

        if(!PermissionHelper.hasNotificationPermission(context)) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ID_ONGOING_ACTIVITY)
    }

    private fun createNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(context, CHANNEL_ID_ONGOING_ACTIVITY)
            .setContentTitle(context.getString(R.string.ongoing_activity))
            .setContentText("")
            .setSmallIcon(R.drawable.ic_tally)
            .setOngoing(true)
            .setLocalOnly(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_STATUS)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager,  channelName: String, channelDescription: String){

        val channel = NotificationChannel(
            CHANNEL_ID_ONGOING_ACTIVITY,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = channelDescription
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setAllowBubbles(false)
            }
        }
        channel.enableLights(false)
        channel.enableVibration(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            channel.isBlockable = true
        }
        channel.setShowBadge(false)
        channel.setBypassDnd(true)
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

}