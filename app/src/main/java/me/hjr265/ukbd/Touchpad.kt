package me.hjr265.ukbd

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
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
            var lastX = 0f
            var lastY = 0f

            Column {
                Plum(
                    modifier = Modifier.weight(0.5f),
                    onDown = { hidConnection?.mouseDown(0) },
                    onUp = { hidConnection?.mouseDown(0) },
                    enabled = hidConnection != null,
                    imageId = R.drawable.leftmouse,
                )
                Plum(
                    modifier = Modifier.weight(0.5f),
                    onDown = { hidConnection?.mouseDown(1) },
                    onUp = { hidConnection?.mouseDown(1) },
                    enabled = hidConnection != null,
                    imageId = R.drawable.rightmouse
                )
            }

            var leftDownAt: Long = 0

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
                            when (it.action) {
                                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                                    leftDownAt = Date().time
                                }

                                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                                    if ((Date().time - leftDownAt) in (51..149)) {
                                        leftDownAt = 0
                                        hidConnection?.mouseClick(0)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val deltaX = (it.x - lastX).toInt()
                                    val deltaY = (it.y - lastY).toInt()
                                    hidConnection?.mouseMove(deltaX, deltaY)
                                }
                            }
                            lastX = it.x
                            lastY = it.y
                            consume
                        }
                )
            }

            Column {
                Plum(
                    modifier = Modifier.weight(1f),
                    onDown = { hidConnection?.mouseDown(2) },
                    onUp = { hidConnection?.mouseDown(2) },
                    enabled = hidConnection != null,
                    imageId = R.drawable.scroll
                )
            }
        }
    }
}