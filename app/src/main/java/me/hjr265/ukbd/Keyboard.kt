package me.hjr265.ukbd

import android.Manifest
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hjr265.ukbd.hid.Connection


@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun Keyboard(
    hidConnection: Connection?,
    togglePlum: @Composable () -> Unit,
    settingsPlum: @Composable () -> Unit,
    capsLock: Boolean
) {
    Column {
        Row {
            Row {
                PlumSymbol(hidConnection, "ESC", label = "ESC")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Row {
                PlumSymbol(hidConnection, "HOME", label = "HOME")
                PlumSymbol(hidConnection, "END", label = "END")
                PlumSymbol(hidConnection, "PAGEUP", label = "PGUP")
                PlumSymbol(hidConnection, "PAGEDOWN", label = "PGDN")
                PlumSymbol(hidConnection, "DELETE", label = "DEL")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Row {
                PlumSymbol(hidConnection, "SYSRQ", label = "PRTSC")
            }
            Spacer(modifier = Modifier.weight(1f))
            Row {
                togglePlum()
                settingsPlum()
            }
        }
        Row {
            PlumSymbol(hidConnection, "GRAVE", label = "`")
            PlumSymbol(hidConnection, "1", imageId = R.drawable.one)
            PlumSymbol(hidConnection, "2", imageId = R.drawable.two)
            PlumSymbol(hidConnection, "3", imageId = R.drawable.three)
            PlumSymbol(hidConnection, "4", imageId = R.drawable.four)
            PlumSymbol(hidConnection, "5", imageId = R.drawable.five)
            PlumSymbol(hidConnection, "6", imageId = R.drawable.six)
            PlumSymbol(hidConnection, "7", imageId = R.drawable.seven)
            PlumSymbol(hidConnection, "8", imageId = R.drawable.eight)
            PlumSymbol(hidConnection, "9", imageId = R.drawable.nine)
            PlumSymbol(hidConnection, "0", imageId = R.drawable.zero)
            PlumSymbol(hidConnection, "MINUS", label = "-")
            PlumSymbol(hidConnection, "EQUAL", label = "=")
            PlumSymbol(
                hidConnection,
                "BACKSPACE",
                modifier = Modifier.weight(1f),
                imageId = R.drawable.backspace
            )
        }
        Row {
            PlumSymbol(
                hidConnection,
                "TAB",
                modifier = Modifier.defaultMinSize(minWidth = 75.dp),
                imageId = R.drawable.tab
            )
            PlumSymbol(hidConnection, "Q", imageId = R.drawable.q)
            PlumSymbol(hidConnection, "W", imageId = R.drawable.w)
            PlumSymbol(hidConnection, "E", imageId = R.drawable.e)
            PlumSymbol(hidConnection, "R", imageId = R.drawable.r)
            PlumSymbol(hidConnection, "T", imageId = R.drawable.t)
            PlumSymbol(hidConnection, "Y", imageId = R.drawable.y)
            PlumSymbol(hidConnection, "U", imageId = R.drawable.u)
            PlumSymbol(hidConnection, "I", imageId = R.drawable.i)
            PlumSymbol(hidConnection, "O", imageId = R.drawable.o)
            PlumSymbol(hidConnection, "P", imageId = R.drawable.p)
            PlumSymbol(hidConnection, "LEFTBRACE", imageId = R.drawable.leftbrace)
            PlumSymbol(hidConnection, "RIGHTBRACE", imageId = R.drawable.rightbrace)
            PlumSymbol(
                hidConnection,
                "BACKSLASH",
                modifier = Modifier.weight(1f),
                label = "\\"
            )
        }
        Row {
            PlumSymbol(
                hidConnection,
                "CAPSLOCK",
                modifier = Modifier.weight(0.85f),
                imageId = R.drawable.capslock,
                activeDot = true,
                active = capsLock
            )
            PlumSymbol(hidConnection, "A", imageId = R.drawable.a)
            PlumSymbol(hidConnection, "S", imageId = R.drawable.s)
            PlumSymbol(hidConnection, "D", imageId = R.drawable.d)
            PlumSymbol(hidConnection, "F", imageId = R.drawable.f)
            PlumSymbol(hidConnection, "G", imageId = R.drawable.g)
            PlumSymbol(hidConnection, "H", imageId = R.drawable.h)
            PlumSymbol(hidConnection, "J", imageId = R.drawable.j)
            PlumSymbol(hidConnection, "K", imageId = R.drawable.k)
            PlumSymbol(hidConnection, "L", imageId = R.drawable.l)
            PlumSymbol(hidConnection, "SEMICOLON", label = ";")
            PlumSymbol(hidConnection, "APOSTROPHE", label = "'")
            PlumSymbol(
                hidConnection,
                "ENTER",
                modifier = Modifier.weight(1f),
                imageId = R.drawable.enter
            )
        }
        Row {
            PlumModifier(
                hidConnection,
                "MOD_LSHIFT",
                modifier = Modifier.weight(1f),
                imageId = R.drawable.shift
            )
            PlumSymbol(hidConnection, "Z", imageId = R.drawable.z)
            PlumSymbol(hidConnection, "X", imageId = R.drawable.x)
            PlumSymbol(hidConnection, "C", imageId = R.drawable.c)
            PlumSymbol(hidConnection, "V", imageId = R.drawable.v)
            PlumSymbol(hidConnection, "B", imageId = R.drawable.b)
            PlumSymbol(hidConnection, "N", imageId = R.drawable.n)
            PlumSymbol(hidConnection, "M", imageId = R.drawable.m)
            PlumSymbol(hidConnection, "COMMA", label = ",")
            PlumSymbol(hidConnection, "DOT", label = ".")
            PlumSymbol(hidConnection, "SLASH", label = "/")
            PlumModifier(
                hidConnection,
                "MOD_RSHIFT",
                modifier = Modifier.weight(1f),
                imageId = R.drawable.shift
            )
        }
        Row {
            PlumModifier(hidConnection, "MOD_LCTRL", label = "CTRL")
            PlumModifier(hidConnection, "MOD_LMETA", imageId = R.drawable.meta)
            PlumModifier(hidConnection, "MOD_LALT", label = "ALT")
            PlumSymbol(
                hidConnection,
                "SPACE",
                modifier = Modifier.weight(1f),
                imageId = R.drawable.space
            )
            PlumModifier(hidConnection, "MOD_RALT", label = "ALT")
            PlumModifier(hidConnection, "MOD_RCTRL", label = "CTRL")
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                PlumSymbol(
                    hidConnection,
                    "LEFT",
                    modifier = Modifier.height(height = 40.dp),
                    imageId = R.drawable.left
                )
                Column {
                    PlumSymbol(
                        hidConnection,
                        "UP",
                        modifier = Modifier.height(height = 40.dp),
                        imageId = R.drawable.up
                    )
                    PlumSymbol(
                        hidConnection,
                        "DOWN",
                        modifier = Modifier.height(height = 40.dp),
                        imageId = R.drawable.down
                    )
                }
                PlumSymbol(
                    hidConnection,
                    "RIGHT",
                    modifier = Modifier.height(height = 40.dp),
                    imageId = R.drawable.right
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Plum(
    modifier: Modifier = Modifier,
    onDown: () -> Unit = {},
    onUp: () -> Unit = {},
    enabled: Boolean = true,
    @DrawableRes imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    activeDot: Boolean = false,
    active: Boolean = false,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    var pressed by remember { mutableStateOf(false) }
    var activePointerId by remember { mutableStateOf(-1) }

    Box(
        modifier = modifier
            .defaultMinSize(55.dp, 50.dp)
            .padding(1.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .matchParentSize()
                .pointerInteropFilter {
                    var consume = true
                    when (it.actionMasked) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                            if (!enabled) {
                                consume = false
                            } else if (activePointerId == -1) {
                                activePointerId = it.getPointerId(it.actionIndex)
                                pressed = true
                                onDown()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                            val pointerId = it.getPointerId(it.actionIndex)
                            if (pointerId == activePointerId) {
                                activePointerId = -1
                                pressed = false
                                onUp()
                            }
                        }
                    }
                    consume
                },
            enabled = enabled,
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary.let {
                    if (pressed) it.copy(alpha = 0.76f) else it
                },
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.38f)
            ),
            contentPadding = PaddingValues(0.dp),
        ) {
            if (imageId != 0) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = imageAlt,
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                )
            }
            if (label != "") {
                Text(
                    label,
                    fontFamily = FontFamily.Monospace,
                    fontSize = if (label.length == 1) 20.sp else TextUnit.Unspecified,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            if (content != null)
                content()
        }
        if (activeDot) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(-5.dp, 5.dp)
                    .size(5.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.19f))
            )
        }
    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun PlumSymbol(
    hidConnection: Connection?,
    key: String,
    modifier: Modifier = Modifier,
    imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    activeDot: Boolean = false,
    active: Boolean = false,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Plum(
        onDown = { hidConnection?.keyDown(key) },
        onUp = { hidConnection?.keyUp(key) },
        enabled = hidConnection != null,
        modifier = modifier,
        imageId = imageId,
        imageAlt = imageAlt,
        label = label,
        activeDot = activeDot,
        active = active,
        content = content
    )
}

@Composable
fun PlumModifier(
    hidConnection: Connection?,
    key: String,
    modifier: Modifier = Modifier,
    imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Plum(
        onDown = { hidConnection?.modifierDown(key) },
        onUp = { hidConnection?.modifierUp(key) },
        enabled = hidConnection != null,
        modifier = modifier,
        imageId = imageId,
        imageAlt = imageAlt,
        label = label,
        content = content
    )
}