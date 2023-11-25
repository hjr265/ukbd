package me.hjr265.ukbd

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import me.hjr265.ukbd.hid.Connection
import java.util.Date

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("MissingPermission")
@Composable
fun Touchpad(
    hidConnection: Connection?,
    togglePlum: @Composable () -> Unit,
    settingsPlum: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Row {
                togglePlum()
                settingsPlum()
            }
        }
        Row {
            Column {
                Plum(
                    modifier = Modifier.weight(0.5f),
                    onDown = { hidConnection?.mouseDown(0) },
                    onUp = { hidConnection?.mouseUp(0) },
                    enabled = hidConnection != null,
                    imageId = R.drawable.leftmouse,
                )
                Plum(
                    modifier = Modifier.weight(0.5f),
                    onDown = { hidConnection?.mouseDown(1) },
                    onUp = { hidConnection?.mouseUp(1) },
                    enabled = hidConnection != null,
                    imageId = R.drawable.rightmouse
                )
            }

            var index by remember { mutableStateOf(-1) }
            var lastX by remember { mutableStateOf(0f) }
            var lastY by remember { mutableStateOf(0f) }
            var leftDownAt by remember { mutableStateOf(0L) }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .background(
                            color = with(MaterialTheme.colorScheme.secondary) {
                                if (hidConnection == null) this.copy(alpha = 0.38f) else this
                            }
                        )
                        .pointerInteropFilter {
                            var consume = true
                            when (it.actionMasked) {
                                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                                    if (index == -1) {
                                        index = it.actionIndex
                                        leftDownAt = Date().time
                                    }
                                }

                                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                                    if (it.actionIndex == index) {
                                        if ((Date().time - leftDownAt) in (51..149)) {
                                            leftDownAt = 0
                                            hidConnection?.mouseClick(0)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        index = -1
                                    }
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val deltaX = (it.getX(index) - lastX).toInt()
                                    val deltaY = (it.getY(index) - lastY).toInt()
                                    hidConnection?.mouseMove(deltaX, deltaY)
                                }
                            }
                            if (index != -1) {
                                lastX = it.getX(index)
                                lastY = it.getY(index)
                            }
                            consume
                        }
                )
            }

            Column {
                Plum(
                    modifier = Modifier.weight(1f),
                    onDown = { hidConnection?.mouseDown(2) },
                    onUp = { hidConnection?.mouseUp(2) },
                    enabled = hidConnection != null,
                    imageId = R.drawable.scroll
                )
            }
        }
    }
}