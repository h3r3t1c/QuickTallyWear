package com.h3r3t1c.quickwearcounter.tile

import android.content.ComponentName
import android.content.Context
import android.os.Vibrator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.Typography
import androidx.wear.protolayout.material3.icon
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.LayoutColor
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.EventBuilders.TileInteractionEvent.ENTER
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.h3r3t1c.quickwearcounter.BuildConfig
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.complication.TallyComplicationService
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.ext.dataStore
import com.h3r3t1c.quickwearcounter.ext.toColor
import com.h3r3t1c.quickwearcounter.ext.toContrastColor
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.IMAGE_MINUS
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.IMAGE_OPEN
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.IMAGE_PLUS
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.IMAGE_RESET
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.imageMapping
import com.h3r3t1c.quickwearcounter.ui.ConfirmResetCountActivity
import com.h3r3t1c.quickwearcounter.ui.MainActivity
import com.h3r3t1c.quickwearcounter.ui.compose.WearStyleHelper
import com.h3r3t1c.quickwearcounter.util.ColorsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@OptIn(ExperimentalHorologistApi::class)
class TallyTileService : SuspendingTileService() {

    companion object{
        const val IMAGE_PLUS = "image_plus"
        const val IMAGE_MINUS = "image_minus"
        const val IMAGE_RESET = "image_reset"
        const val IMAGE_OPEN = "image_open"

        val imageMapping = listOf(
            IMAGE_PLUS to R.drawable.ic_plus,
            IMAGE_MINUS to R.drawable.ic_minus,
            IMAGE_RESET to R.drawable.ic_reset,
            IMAGE_OPEN to R.drawable.ic_open
        )

        var needUpdate = true
        fun setNeedUpdate(){
            needUpdate = true
        }
    }

   override fun onRecentInteractionEventsAsync(events: List<EventBuilders.TileInteractionEvent?>): ListenableFuture<Void?> {
        val future = SettableFuture.create<Void?>()

       CoroutineScope(Dispatchers.Default).launch {
            events.forEach { event ->
                if(event?.eventType == ENTER && needUpdate){
                    getUpdater(applicationContext).requestUpdate(TallyTileService::class.java)
                }
            }
            future.set(null)
        }
        return future
    }


    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile  {
        needUpdate = false
        val prefs = applicationContext.dataStore.data.first()
        var count = prefs[intPreferencesKey(DataStorePrefs.KEY_CURRENT_COUNT)] ?: 0
        when(requestParams.currentState.lastClickableId){
            IMAGE_PLUS -> {
                count += 1
                applicationContext.dataStore.updateData { prefs->
                    prefs.toMutablePreferences().apply {
                        set(intPreferencesKey(DataStorePrefs.KEY_CURRENT_COUNT), count)
                    }
                }
                TallyComplicationService.updateAll(applicationContext)
                vibrate(applicationContext)
            }
            IMAGE_MINUS -> {
                count -= 1
                applicationContext.dataStore.updateData { prefs->
                    prefs.toMutablePreferences().apply {
                        set(intPreferencesKey(DataStorePrefs.KEY_CURRENT_COUNT), count)
                    }
                }
                TallyComplicationService.updateAll(applicationContext)
                vibrate(applicationContext)
            }

        }
        val containerColor = prefs[intPreferencesKey(DataStorePrefs.KEY_APP_THEME_COLOR)]
        val contentColor = containerColor?.toColor()?.toContrastColor()?.toArgb()
        return tile(requestParams, this, count.toString(), containerColor, contentColor)
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources =resources(requestParams)
}

private fun vibrate(context: Context){
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(100)
}

private fun resources(requestParams: ResourcesRequest): Resources {
    val resources = Resources.Builder()
        .setVersion(requestParams.version)

    imageMapping.forEach { (id, drawable) ->
        resources.addIdToImageMapping(
            id,
            ResourceBuilders.ImageResource.Builder().setAndroidResourceByResId(
                ResourceBuilders.AndroidImageResourceByResId.Builder()
                    .setResourceId(drawable)
                    .build()
            ).build()
        )
    }

    return resources.build()
}

private fun tile(
    requestParams: RequestBuilders.TileRequest,
    context: Context,
    count: String,
    containerColor: Int?,
    contentColor: Int?
): TileBuilders.Tile {

    val isLarge = WearStyleHelper.isLargeScreen(context)

    return TileBuilders.Tile.Builder()
        .setResourcesVersion(BuildConfig.VERSION_NAME + BuildConfig.VERSION_CODE)
        .setTileTimeline(
            TimelineBuilders.Timeline.fromLayoutElement(
                materialScope(context, requestParams.deviceConfiguration) {
                    val mContainerColor = containerColor ?: colorScheme.primary.staticArgb
                    val mContentColor = contentColor ?: colorScheme.onPrimary.staticArgb
                    val col = LayoutElementBuilders.Column.Builder().setWidth(expand()).setHeight(expand())
                    col.addContent(
                        valueChangeButton(isLarge, mContainerColor, mContentColor, IMAGE_PLUS)
                    )
                    col.addContent(
                        centerContent(context, isLarge, mContainerColor, mContentColor, count)
                    )
                    col.addContent(
                        valueChangeButton(isLarge, mContainerColor, mContentColor, IMAGE_MINUS)
                    )
                    col.build()
                }
            )
        )
        .build()
}

private fun MaterialScope.centerContent(context: Context, isLarge: Boolean, containerColor: Int, contentColor: Int, count: String): LayoutElementBuilders.LayoutElement{
    val r = LayoutElementBuilders.Row.Builder()
        .setWidth(expand())
        .setHeight(expand())
        .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
        .setModifiers(
            ModifiersBuilders.Modifiers.Builder()
                .setPadding(
                    ModifiersBuilders.Padding.Builder()
                        .setStart(dp(6f))
                        .setEnd(dp(6f))
                        .build()
                )
                .build()
        )
    r.addContent(
        actionButton(
            context = context,
            isLarge = isLarge,
            containerColor = containerColor,
            contentColor = contentColor,
            icon = IMAGE_RESET,
            clickable = clickable(
                action = ActionBuilders.launchAction(ComponentName(context, ConfirmResetCountActivity::class.java))
            )
        )
    )

    r.addContent(
        centerCount(isLarge, count)
    )

    r.addContent(
        actionButton(
            context = context,
            isLarge = isLarge,
            containerColor = containerColor,
            contentColor = contentColor,
            icon = IMAGE_OPEN,
            clickable = clickable(
                action = ActionBuilders.launchAction(ComponentName(context, MainActivity::class.java))
            )
        )
    )

    return r.build()
}

private fun MaterialScope.centerCount(isLarge: Boolean, count: String): LayoutElementBuilders.LayoutElement{
    val box = LayoutElementBuilders.Box.Builder()
        .setWidth(expand())
        .setHeight(expand())
        .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
        .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)

    box.addContent(
        text(
            count.layoutString,
            scalable = false,
            maxLines = 1,
            color = LayoutColor(Color.White.toArgb()),
            typography = when{
                count.length <= 2 -> Typography.NUMERAL_EXTRA_LARGE
                count.length == 3 -> if(isLarge) Typography.NUMERAL_EXTRA_LARGE else Typography.NUMERAL_LARGE
                count.length == 4 -> Typography.NUMERAL_MEDIUM
                count.length == 5 -> Typography.NUMERAL_SMALL
                else -> if (isLarge) Typography.NUMERAL_SMALL else Typography.NUMERAL_EXTRA_SMALL
            }
        )
    )
    return box.build()
}

private fun MaterialScope.actionButton(context: Context, isLarge: Boolean, containerColor: Int, contentColor: Int, icon: String, clickable: ModifiersBuilders.Clickable): LayoutElementBuilders.LayoutElement{
    val size = if(isLarge) dp(48f) else dp(40f)
    val box = LayoutElementBuilders.Box.Builder()
        .setWidth(size)
        .setHeight(size)
    box.setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
        .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
        .setModifiers(
            ModifiersBuilders.Modifiers.Builder()
                .setClickable(clickable)
                .setBackground(
                    ModifiersBuilders.Background.Builder()
                        .setColor(ColorBuilders.argb(containerColor))
                        .setCorner(
                            ModifiersBuilders.Corner.Builder()
                                .setRadius(if(isLarge) dp(18f) else dp(16f) )
                                .build()
                        )
                        .build()
                )
                .build()

        )
    box.addContent(
        icon(
            icon,
            width = dp(24f),
            height = dp(24f),
            tintColor = LayoutColor(contentColor)
        )
    )
    return box.build()
}

private fun MaterialScope.valueChangeButton(isLarge: Boolean, containerColor: Int, contentColor: Int, icon: String): LayoutElementBuilders.LayoutElement{
    val box = LayoutElementBuilders.Box.Builder()
        .setWidth(expand())
        .setHeight(if(isLarge) dp(66f) else dp(56f))
        .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
        .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
        .setModifiers(
            ModifiersBuilders.Modifiers.Builder()
                .setClickable(clickable(id = icon))
                .setBackground(
                    ModifiersBuilders.Background.Builder()
                        .setColor(ColorBuilders.argb(containerColor))
                        .setCorner(
                            ModifiersBuilders.Corner.Builder()
                                .setRadius(dp(100f))
                                .build()
                        )
                        .build()
                )
                .build()

        )
    box.addContent(
        icon(
            icon,
            width = if(isLarge) dp(40f) else dp(32f),
            height = if(isLarge) dp(40f) else dp(32f),
            tintColor = LayoutColor(contentColor)
        )
    )
    return box.build()
}

@Preview(device = WearDevices.SMALL_ROUND)
@Preview(device = WearDevices.LARGE_ROUND)
fun tilePreview(context: Context) = TilePreviewData(::resources) {
    val containerColor = ColorsHelper.BLUE_500
    val contentColor = ColorsHelper.WHITE
    tile(it, context, "10", containerColor, contentColor)
}