package me.hjr265.ukbd

import android.Manifest
import android.view.MotionEvent
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Touchpad(
    hidConnection: Connection?,
    togglePlum: @Composable () -> Unit,
    settingsPlum: @Composable () -> Unit
) {
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Axon(
                    modifier = Modifier.fillMaxSize(),
                    onTap = { hidConnection?.mouseClick(0) },
                    onSlide = { deltaX: Int, deltaY: Int ->
                        hidConnection?.mouseMove(
                            deltaX,
                            deltaY
                        )
                    },
                    enabled = hidConnection != null
                )
            }

            Column {
                Axon(
                    modifier = Modifier.weight(1f),
                    onTap = { hidConnection?.mouseClick(2) },
                    onSlide = { _: Int, deltaY: Int ->
                        val delta = (deltaY.coerceAtLeast(-127).coerceAtMost(127) * 16) / 127
                        hidConnection?.mouseWheel(delta)
                    },
                    enabled = hidConnection != null
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.scroll),
                            contentDescription = "",
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Axon(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onSlide: (deltaX: Int, deltaY: Int) -> Unit = { _: Int, _: Int -> },
    enabled: Boolean = true,
    content: @Composable (BoxScope.() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    var activePointerId by remember { mutableStateOf(-1) }
    var lastX by remember { mutableStateOf(0f) }
    var lastY by remember { mutableStateOf(0f) }
    var leftDownAt by remember { mutableStateOf(0L) }

    Box(
        modifier = modifier
            .defaultMinSize(55.dp, 50.dp)
            .padding(1.dp)
            .clip(shape = RoundedCornerShape(5.dp))
            .background(
                color = with(MaterialTheme.colorScheme.secondary) {
                    if (enabled) this else this.copy(alpha = 0.38f)
                }
            )
            .pointerInteropFilter {
                var consume = true
                when (it.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        if (activePointerId == -1) {
                            activePointerId = it.getPointerId(it.actionIndex)
                            leftDownAt = Date().time
                            lastX = it.getX(it.actionIndex)
                            lastY = it.getY(it.actionIndex)
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                        val pointerId = it.getPointerId(it.actionIndex)
                        if (pointerId == activePointerId) {
                            if ((Date().time - leftDownAt) in (51..149)) {
                                leftDownAt = 0
                                onTap()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            activePointerId = -1
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        for (index in 0..it.pointerCount - 1) {
                            if (it.getPointerId(index) == activePointerId) {
                                val x = it.getX(index)
                                val y = it.getY(index)
                                val deltaX = (x - lastX).toInt()
                                val deltaY = (y - lastY).toInt()
                                onSlide(deltaX, deltaY)
                                lastX = x
                                lastY = y
                            }
                        }
                    }
                }
                consume
            },
        contentAlignment = Alignment.Center
    ) {
        if (content != null)
            content()
    }
}