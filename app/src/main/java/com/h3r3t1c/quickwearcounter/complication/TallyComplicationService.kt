package com.h3r3t1c.quickwearcounter.complication

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.receiver.TallyComplicationTapReceiver


class TallyComplicationService : SuspendingComplicationDataSourceService() {

    companion object{
        fun updateAll(context: Context){
            ComplicationDataSourceUpdateRequester
                .create(context, ComponentName(context, TallyComplicationService::class.java))
                .requestUpdateAll()
        }
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData("10")
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val count = DataStorePrefs.getCurrentCount(applicationContext) ?: 0
        return createComplicationData(count.toString())
    }

    private fun createComplicationData(countText: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(countText).build(),
            contentDescription = PlainComplicationText.Builder(getString(R.string.tally)).build()
        ).setMonochromaticImage(
            MonochromaticImage.Builder(
                image = Icon.createWithResource(this, R.drawable.ic_tally),
            ).build()
        ).setTapAction(
            TallyComplicationTapReceiver.getPendingIntent(applicationContext)
        ).build()
}