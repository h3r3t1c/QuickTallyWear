package com.h3r3t1c.quickwearcounter.tile

import android.content.Context
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material3.MaterialScope
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
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.IMAGE_MINUS
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.IMAGE_PLUS
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.IMAGE_RESET
import com.h3r3t1c.quickwearcounter.tile.TallyTileService.Companion.imageMapping
import com.h3r3t1c.quickwearcounter.ui.compose.WearStyleHelper
import com.h3r3t1c.quickwearcounter.util.ColorsHelper
import kotlinx.coroutines.launch


@OptIn(ExperimentalHorologistApi::class)
class TallyTileService : SuspendingTileService() {

    companion object{
        const val IMAGE_PLUS = "image_plus"
        const val IMAGE_MINUS = "image_minus"
        const val IMAGE_RESET = "image_reset"

        val imageMapping = listOf(
            IMAGE_PLUS to R.drawable.ic_plus,
            IMAGE_MINUS to R.drawable.ic_minus,
            IMAGE_RESET to R.drawable.ic_reset
        )

        var needUpdate = true
        fun setNeedUpdate(){
            needUpdate = true
        }
    }

    override fun onRecentInteractionEventsAsync(events: List<EventBuilders.TileInteractionEvent?>): ListenableFuture<Void?> {
        val future = SettableFuture.create<Void?>()
        serviceScope?.launch {
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
        val count = when(requestParams.currentState.lastClickableId){
            IMAGE_PLUS -> {
                val c = DataStorePrefs.updateCountByAmount(applicationContext, 1)
                TallyComplicationService.updateAll(applicationContext)
                c
            }
            IMAGE_MINUS -> {
                val c = DataStorePrefs.updateCountByAmount(applicationContext, -1)
                TallyComplicationService.updateAll(applicationContext)
                c
            }
            else ->{
                DataStorePrefs.getCurrentCount(applicationContext) ?: 0
            }
        }
        return tile(requestParams, this, count.toString())
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources =resources(requestParams)
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
): TileBuilders.Tile {
    val containerColor = ColorsHelper.BLUE_500
    val contentColor = ColorsHelper.WHITE
    val isLarge = WearStyleHelper.isLargeScreen(context)

    return TileBuilders.Tile.Builder()
        .setResourcesVersion(BuildConfig.VERSION_NAME + BuildConfig.VERSION_CODE)
        .setTileTimeline(
            TimelineBuilders.Timeline.fromLayoutElement(
                materialScope(context, requestParams.deviceConfiguration) {
                    val col = LayoutElementBuilders.Column.Builder().setWidth(expand()).setHeight(expand())
                    col.addContent(
                        valueChangeButton(isLarge, containerColor, contentColor, IMAGE_PLUS)
                    )
                    col.addContent(
                        centerContent(isLarge, containerColor, contentColor, count)
                    )
                    col.addContent(
                        valueChangeButton(isLarge, containerColor, contentColor, IMAGE_MINUS)
                    )
                    col.build()
                }
            )
        )
        .build()
}

private fun MaterialScope.centerContent(isLarge: Boolean, containerColor: Int, contentColor: Int, count: String): LayoutElementBuilders.LayoutElement{
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
        actionButton(isLarge, containerColor, contentColor, IMAGE_RESET)
    )

    r.addContent(
        centerCount(count)
    )

    r.addContent(
        actionButton(isLarge, containerColor, contentColor, IMAGE_RESET)
    )

    return r.build()
}

private fun MaterialScope.centerCount(count: String): LayoutElementBuilders.LayoutElement{
    val box = LayoutElementBuilders.Box.Builder()
        .setWidth(expand())
        .setHeight(expand())
        .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
        .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
        .setModifiers(
            ModifiersBuilders.Modifiers.Builder()
                .setPadding(
                    ModifiersBuilders.Padding.Builder()
                        .setStart(dp(4f))
                        .setEnd(dp(4f))
                        .build()
                )
                .build()
        )
    box.addContent(
        text(
            count.layoutString,
            scalable = false
        )
    )
    return box.build()
}

private fun MaterialScope.actionButton(isLarge: Boolean, containerColor: Int, contentColor: Int, icon: String?): LayoutElementBuilders.LayoutElement{
    val size = if(isLarge) dp(48f) else dp(40f)
    val box = LayoutElementBuilders.Box.Builder()
        .setWidth(size)
        .setHeight(size)
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
                                .setRadius(if(isLarge) dp(18f) else dp(16f) )
                                .build()
                        )
                        .build()
                )
                .build()

        )
    if(icon != null) {
        box.addContent(
            icon(
                icon,
                width = dp(24f),
                height = dp(24f),
                tintColor = LayoutColor(contentColor)
            )
        )
    }
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
    tile(it, context, "10")
}