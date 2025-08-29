package me.hjr265.ukbd

import android.Manifest
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.hjr265.ukbd.hid.Connection
import java.util.Date
import java.util.Timer
import java.util.TimerTask

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Touchpad(
    hidConnection: Connection?,
    togglePlum: @Composable () -> Unit,
    settingsPlum: @Composable () -> Unit
) {
    var wheelDelta by remember { mutableStateOf(0) }

    DisposableEffect(wheelDelta) {
        val timer = Timer()
        if (wheelDelta != 0) {
            timer.schedule(object : TimerTask() {
                @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
                override fun run() {
                    hidConnection?.mouseWheel(wheelDelta)
                }
            }, 125, 250)
        }
        onDispose {
            hidConnection?.mouseWheel(0)
            timer.cancel()
        }
    }

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
                    onTapDragStart = { hidConnection?.mouseDown(0) },
                    onTapDragStop = { hidConnection?.mouseUp(0) },
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
                    onStretch = { _: Float, deltaY: Float ->
                        wheelDelta = if (deltaY in -0.05f..0.05f) 0 else (deltaY * 16).toInt()
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalStdlibApi::class)
@Composable
fun Axon(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onTapDragStart: () -> Unit = {},
    onTapDragStop: () -> Unit = {},
    onSlide: (deltaX: Int, deltaY: Int) -> Unit = { _: Int, _: Int -> },
    onStretch: (deltaX: Float, deltaY: Float) -> Unit = { _: Float, _: Float -> },
    enabled: Boolean = true,
    content: @Composable (BoxScope.() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    var activePointerId by remember { mutableStateOf(-1) }
    var firstX by remember { mutableStateOf(0f) }
    var firstY by remember { mutableStateOf(0f) }
    var lastX by remember { mutableStateOf(0f) }
    var lastY by remember { mutableStateOf(0f) }
    var leftDownAt by remember { mutableStateOf(0L) }
    var tapAt by remember { mutableStateOf(0L) }
    var tapDragging by remember { mutableStateOf(false) }

    var size by remember { mutableStateOf(Size.Zero) }

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
            .onSizeChanged {
                size = Size(it.width.toFloat(), it.height.toFloat())
            }
            .pointerInteropFilter {
                when (it.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        if (activePointerId == -1) {
                            activePointerId = it.getPointerId(it.actionIndex)
                            leftDownAt = Date().time
                            firstX = it.getX(it.actionIndex)
                            firstY = it.getY(it.actionIndex)
                            lastX = it.getX(it.actionIndex)
                            lastY = it.getY(it.actionIndex)
                            Log.d("", "XXXXXXXX ${(Date().time - tapAt)}")
                            if ((Date().time - tapAt) in (21..149)) {
                                Log.d("", "XXXXXXXX")
                                tapDragging = true
                                onTapDragStart()
                            }
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                        val pointerId = it.getPointerId(it.actionIndex)
                        if (pointerId == activePointerId) {
                            onStretch(0f, 0f)
                            if ((Date().time - leftDownAt) in (21..149)) {
                                leftDownAt = 0
                                tapAt = Date().time
                                onTap()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            if (tapDragging) {
                                tapDragging = false
                                onTapDragStop()
                            }
                            activePointerId = -1
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        for (index in 0..<it.pointerCount) {
                            if (it.getPointerId(index) == activePointerId) {
                                val x = it.getX(index)
                                val y = it.getY(index)
                                val stretchX = (x - firstX).toInt()
                                val stretchY = (y - firstY).toInt()
                                onStretch(
                                    (stretchX / size.width)
                                        .coerceAtLeast(-1f)
                                        .coerceAtMost(1f),
                                    (stretchY.toFloat() / size.height)
                                        .coerceAtLeast(-1f)
                                        .coerceAtMost(1f),
                                )
                                val deltaX = (x - lastX).toInt()
                                val deltaY = (y - lastY).toInt()
                                onSlide(deltaX, deltaY)
                                lastX = x
                                lastY = y
                            }
                        }
                    }
                }
                true
            },
        contentAlignment = Alignment.Center
    ) {
        if (content != null)
            content()
    }
}