package com.h3r3t1c.quickwearcounter.ui.compose.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.ui.compose.common.ColumnItemType
import com.h3r3t1c.quickwearcounter.ui.compose.common.rememberResponsiveColumnPadding
import com.h3r3t1c.quickwearcounter.util.ColorsHelper

@Composable
fun ColorPickerDialog(visible: Boolean, onDismiss: () -> Unit, onColorSelected: (Int?) -> Unit) {

    Dialog(
        visible = visible,
        onDismissRequest = onDismiss
    ) {
        val contentPadding = rememberResponsiveColumnPadding(
            first = ColumnItemType.Button,
            last = ColumnItemType.Button,
        )
        var showShadesDialog by remember { mutableStateOf(false) }
        var customDialog by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf(intArrayOf()) }

        val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
        ScreenScaffold(
            contentPadding = contentPadding,
            scrollState = listState
        ) {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                contentPadding = contentPadding,
                autoCentering = null,
                state = listState
            ) {

                item{
                    Button(
                        onClick = {onColorSelected(null)},
                        colors = ButtonDefaults.buttonColors(),
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.use_system_theme))
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_palette),
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                            )
                        }
                    )
                }
                item{
                    Button(
                        onClick = { customDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.custom_color))
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_palette),
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                            )
                        }
                    )
                }
                ColorsHelper.arrayOfAllColorArrays.forEach { colors ->
                    item{
                        ColorChipOption(colors, onColorSelected) {
                            showShadesDialog = true
                            selectedCategory = colors
                        }
                    }
                }
            }
        }
        ColorCategory(showShadesDialog,selectedCategory, {showShadesDialog = false; onColorSelected(it)}) {
            showShadesDialog = false
        }
        CustomColorDialog(customDialog, { customDialog = false }) {
            customDialog = false
            onColorSelected(it)
        }
    }
}

@Composable
fun ColorChipOption( colors: IntArray, onColorSelected: (Int) -> Unit, onClickShade: () -> Unit){
    Row(
        modifier = Modifier

            .clip(RoundedCornerShape(24.dp))
            .height(48.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    Color(colors[5]),
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 4.dp, bottomEnd = 4.dp)
                )
                .clickable {
                    onColorSelected(colors[5])
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
        }
        Spacer(modifier = Modifier
            .width(2.dp)
            .fillMaxHeight()
            .background(Color.Black))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_swatch),
            contentDescription = "",
            tint = Color(colors[1]),
            modifier = Modifier
                .size(48.dp)
                .background(Color(colors[5]).copy(alpha = .5f), RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp, topStart = 4.dp, bottomStart = 4.dp))
                .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp, topStart = 4.dp, bottomStart = 4.dp))
                .clickable {
                    onClickShade()
                }
                .padding(10.dp)
        )
    }
}

@Composable
private fun ColorCategory(visible: Boolean, colors: IntArray, onColorSelected: (Int) -> Unit, onDismiss: () ->Unit) {


    Dialog(
        visible = visible,
        onDismissRequest = onDismiss
    ) {
        val colorGroups = remember(colors) {
            val groups = mutableListOf<ArrayList<Int>>()
            var index = 0
            while (index < colors.size) {
                val group = arrayListOf<Int>()
                groups.add(group)
                while (group.size < 3 && index < colors.size) {
                    if(index != 5)
                        group.add(colors[index])
                    index++
                }
            }
            groups
        }
        val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
        ScreenScaffold {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    top = 20.dp,
                    bottom = 48.dp
                ),
                autoCentering = null,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                state = listState
            ) {
                item{
                    ColorOption(colors[5]) {
                        onColorSelected(it)
                    }
                }
                colorGroups.forEach { group ->
                    item{
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            group.forEach {
                                ColorOption(it) { c ->
                                    onColorSelected(c)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun ColorOption(color: Int, onColorSelected: (Int) -> Unit){

    Box(modifier = Modifier
        .padding(4.dp)
        .background(color = Color(color), shape = RoundedCornerShape(24.dp))

        .clickable {
            onColorSelected(color)
        },

    )
    { Spacer(modifier = Modifier
        .width(48.dp)
        .height(48.dp)) }
}