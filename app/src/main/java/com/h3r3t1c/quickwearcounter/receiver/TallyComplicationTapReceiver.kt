package com.h3r3t1c.quickwearcounter.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import com.h3r3t1c.quickwearcounter.complication.TallyComplicationService
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.tile.TallyTileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TallyComplicationTapReceiver: BroadcastReceiver() {

    companion object{
        fun getPendingIntent(context: Context) = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, TallyComplicationTapReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        val async = goAsync()
        vibrate(context)
        CoroutineScope(Dispatchers.IO).launch {
            val count = DataStorePrefs.getCurrentCount(context) ?: 0
            DataStorePrefs.updateCurrentCount(context, count + 1)
            TallyTileService.setNeedUpdate()
            TallyComplicationService.updateAll(context)
            async.finish()
        }
    }
    private fun vibrate(context: Context){
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM) //key to make it work from bkg
            .build()

        val vb = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vb.vibrate(VibrationEffect.createOneShot(250, 128), audioAttributes)
    }
}