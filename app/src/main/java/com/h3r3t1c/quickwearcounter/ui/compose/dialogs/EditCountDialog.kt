package com.h3r3t1c.quickwearcounter.ui.compose.dialogs

import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AlertDialogDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.ui.compose.common.ColumnItemType
import com.h3r3t1c.quickwearcounter.ui.compose.common.rememberResponsiveColumnPadding


/**
 * I'm using and EditText here to *fix* the problems with entering numbers using the Samsung keyboard and compose
 */

@Composable
fun EditTextDialog(visible: Boolean, value: Int,  onDismiss: () -> Unit, onUpdate: (Int) -> Unit){
    val context = LocalContext.current
    Dialog(
        visible = visible,
        onDismissRequest = onDismiss,
    ) {
        val view = remember {
            EditText(context)
        }
        val save = {
            val s = view.text.toString().trim()
            if(s.isEmpty()){
                Toast.makeText(context, R.string.text_cannot_be_empty, Toast.LENGTH_SHORT).show()
            }else{
                val i = s.toIntOrNull()
                if(i == null){
                    Toast.makeText(context, R.string.text_must_be_a_number, Toast.LENGTH_SHORT).show()
                }else {
                    onUpdate(i)
                }
            }
        }
        ScreenScaffold {
            val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
            val contentPadding = rememberResponsiveColumnPadding(
                first = ColumnItemType.ListHeader,
                last = ColumnItemType.ButtonRow,
            )

            ScreenScaffold(
                scrollState = listState,
                contentPadding = contentPadding
            ) {
                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    autoCentering = null,
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = contentPadding,
                ) {
                    item{
                        Text(text = stringResource(R.string.edit_value), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    item{
                        AndroidView(
                            modifier = Modifier.fillMaxWidth(),
                            factory = { context ->

                                view.isSingleLine = true
                                view.text = SpannableStringBuilder(value.toString())
                                view.setBackgroundResource(R.drawable.input_background)
                                view.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                                view.setOnEditorActionListener { tv, actionId, event ->
                                    if(actionId == EditorInfo.IME_ACTION_DONE){
                                        save()
                                    }
                                    false
                                }
                                view.imeOptions = EditorInfo.IME_ACTION_DONE

                                view
                            },
                            update = {

                            }
                        )
                    }

                    item{
                        Row(
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            AlertDialogDefaults.DismissButton(onDismiss)
                            Spacer(modifier = Modifier.width(12.dp))
                            AlertDialogDefaults.ConfirmButton( {
                                save()
                            })
                        }
                    }
                }
            }
        }
    }
}