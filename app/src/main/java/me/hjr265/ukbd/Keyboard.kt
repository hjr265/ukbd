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


enum class Layer { DEFAULT, SHIFT, FUNCTION }

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun Keyboard(
    hidConnection: Connection?,
    togglePlum: @Composable () -> Unit,
    settingsPlum: @Composable () -> Unit,
    capsLock: Boolean
) {
    var layer by remember { mutableStateOf(Layer.DEFAULT) }

    Column {
        Row {
            Row {
                PlumSymbol(hidConnection, "ESC", label = "ESC")
            }
            Spacer(modifier = Modifier.width(5.dp))
            when (layer) {
                Layer.DEFAULT, Layer.SHIFT -> Row {
                    Row {
                        PlumSymbol(hidConnection, "F1", label = "F1")
                        PlumSymbol(hidConnection, "F2", label = "F2")
                        PlumSymbol(hidConnection, "F3", label = "F3")
                        PlumSymbol(hidConnection, "F4", label = "F4")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Row {
                        PlumSymbol(hidConnection, "F5", label = "F5")
                        PlumSymbol(hidConnection, "F6", label = "F6")
                        PlumSymbol(hidConnection, "F7", label = "F7")
                        PlumSymbol(hidConnection, "F8", label = "F8")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Row {
                        PlumSymbol(hidConnection, "F9", label = "F9")
                        PlumSymbol(hidConnection, "F10", label = "F10")
                        PlumSymbol(hidConnection, "F11", label = "F11")
                        PlumSymbol(hidConnection, "F12", label = "F12")
                    }
                }

                Layer.FUNCTION -> Row {
                    Row {
                        PlumMedia(hidConnection, "VOLUMEMUTE", imageId = R.drawable.volumemute)
                        PlumMedia(hidConnection, "PLAYPAUSE", imageId = R.drawable.playpause)
                        PlumMedia(hidConnection, "TRACKNEXT", imageId = R.drawable.tracknext)
                        PlumMedia(
                            hidConnection,
                            "TRACKPREVIOUS",
                            imageId = R.drawable.trackprevious
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Row {
                        PlumMedia(hidConnection, "VOLUMEUP", imageId = R.drawable.volumeup)
                        PlumMedia(hidConnection, "VOLUMEDOWN", imageId = R.drawable.volumedown)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row {
                togglePlum()
                settingsPlum()
            }
        }
        Row {
            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> Row {
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
                    PlumSymbol(hidConnection, "MINUS", imageId = R.drawable.minus)
                    PlumSymbol(hidConnection, "EQUAL", imageId = R.drawable.equals)
                }

                Layer.SHIFT -> Row {
                    PlumSymbol(hidConnection, "GRAVE", imageId = R.drawable.tilde)
                    PlumSymbol(hidConnection, "1", label = "!")
                    PlumSymbol(hidConnection, "2", imageId = R.drawable.at)
                    PlumSymbol(hidConnection, "3", imageId = R.drawable.hash)
                    PlumSymbol(hidConnection, "4", imageId = R.drawable.dollar)
                    PlumSymbol(hidConnection, "5", imageId = R.drawable.percentage)
                    PlumSymbol(hidConnection, "6", imageId = R.drawable.up)
                    PlumSymbol(hidConnection, "7", label = "&")
                    PlumSymbol(hidConnection, "8", label = "*")
                    PlumSymbol(hidConnection, "9", imageId = R.drawable.leftbracket)
                    PlumSymbol(hidConnection, "0", imageId = R.drawable.rightbracket)
                    PlumSymbol(hidConnection, "MINUS", label = "_")
                    PlumSymbol(hidConnection, "EQUAL", imageId = R.drawable.plus)
                }
            }
            when (layer) {
                Layer.DEFAULT, Layer.SHIFT ->
                    PlumSymbol(
                        hidConnection,
                        "BACKSPACE",
                        modifier = Modifier.weight(1f),
                        imageId = R.drawable.backspace
                    )

                Layer.FUNCTION -> PlumSymbol(
                    hidConnection,
                    "DELETE",
                    modifier = Modifier.weight(1f),
                    label = "DELETE"
                )
            }
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
            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> Row {
                    PlumSymbol(hidConnection, "LEFTBRACE", imageId = R.drawable.leftbrace)
                    PlumSymbol(hidConnection, "RIGHTBRACE", imageId = R.drawable.rightbrace)
                }

                Layer.SHIFT -> Row {
                    PlumSymbol(hidConnection, "LEFTBRACE", imageId = R.drawable.leftcurlybrace)
                    PlumSymbol(hidConnection, "RIGHTBRACE", imageId = R.drawable.rightcurlybrace)
                }
            }
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
            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> Row {
                    PlumSymbol(hidConnection, "SEMICOLON", label = ";")
                    PlumSymbol(hidConnection, "APOSTROPHE", label = "'")
                }

                Layer.SHIFT -> Row {
                    PlumSymbol(hidConnection, "SEMICOLON", label = ":")
                    PlumSymbol(hidConnection, "APOSTROPHE", label = "\"")
                }
            }
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
                onDown = { layer = Layer.SHIFT },
                onUp = { layer = Layer.DEFAULT },
                imageId = R.drawable.shift
            )
            PlumSymbol(hidConnection, "Z", imageId = R.drawable.z)
            PlumSymbol(hidConnection, "X", imageId = R.drawable.x)
            PlumSymbol(hidConnection, "C", imageId = R.drawable.c)
            PlumSymbol(hidConnection, "V", imageId = R.drawable.v)
            PlumSymbol(hidConnection, "B", imageId = R.drawable.b)
            PlumSymbol(hidConnection, "N", imageId = R.drawable.n)
            PlumSymbol(hidConnection, "M", imageId = R.drawable.m)
            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> Row {
                    PlumSymbol(hidConnection, "COMMA", label = ",")
                    PlumSymbol(hidConnection, "DOT", label = ".")
                    PlumSymbol(hidConnection, "SLASH", label = "/")
                }

                Layer.SHIFT -> Row {
                    PlumSymbol(hidConnection, "COMMA", imageId = R.drawable.lessthan)
                    PlumSymbol(hidConnection, "DOT", imageId = R.drawable.greaterthan)
                    PlumSymbol(hidConnection, "SLASH", imageId = R.drawable.question)
                }
            }
            PlumModifier(
                hidConnection,
                "MOD_RSHIFT",
                modifier = Modifier.weight(1f),
                onDown = { layer = Layer.SHIFT },
                onUp = { layer = Layer.DEFAULT },
                imageId = R.drawable.shift
            )
        }
        Row {
            PlumModifier(hidConnection, "MOD_LCTRL", label = "CTRL")
            Plum(
                onDown = { layer = Layer.FUNCTION },
                onUp = { layer = Layer.DEFAULT },
                label = "FN",
                enabled = hidConnection != null
            )
            PlumModifier(hidConnection, "MOD_LMETA", imageId = R.drawable.meta)
            PlumModifier(hidConnection, "MOD_LALT", label = "ALT")
            PlumSymbol(
                hidConnection,
                "SPACE",
                modifier = Modifier.weight(1f),
                imageId = R.drawable.space
            )
            PlumModifier(hidConnection, "MOD_RALT", label = "ALT")
            PlumSymbol(hidConnection, "SYSRQ", label = "PRTSC")
            PlumModifier(hidConnection, "MOD_RCTRL", label = "CTRL")
            when (layer) {
                Layer.DEFAULT, Layer.SHIFT -> Row(
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

                Layer.FUNCTION -> Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    PlumSymbol(
                        hidConnection,
                        "HOME",
                        label = "HOME",
                        modifier = Modifier.height(height = 40.dp)
                    )
                    Column {
                        PlumSymbol(
                            hidConnection,
                            "END",
                            label = "END",
                            modifier = Modifier.height(height = 40.dp)
                        )
                        PlumSymbol(
                            hidConnection,
                            "PAGEUP",
                            label = "PGUP",
                            modifier = Modifier.height(height = 40.dp)
                        )
                    }
                    PlumSymbol(
                        hidConnection,
                        "PAGEDOWN",
                        label = "PGDN",
                        modifier = Modifier.height(height = 40.dp)
                    )
                }
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
                    .offset((-5).dp, 5.dp)
                    .size(5.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(
                        color = with(MaterialTheme.colorScheme) {
                            if (active) primary else onSecondary.copy(alpha = 0.19f)
                        }
                    )
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
    onDown: () -> Unit = {},
    onUp: () -> Unit = {},
    imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Plum(
        onDown = {
            hidConnection?.modifierDown(key)
            onDown()
        },
        onUp = {
            hidConnection?.modifierUp(key)
            onUp()
        },
        enabled = hidConnection != null,
        modifier = modifier,
        imageId = imageId,
        imageAlt = imageAlt,
        label = label,
        content = content
    )
}

@Composable
fun PlumMedia(
    hidConnection: Connection?,
    key: String,
    modifier: Modifier = Modifier,
    imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Plum(
        onDown = { hidConnection?.mediaDown(key) },
        onUp = { hidConnection?.mediaUp(key) },
        enabled = hidConnection != null,
        modifier = modifier,
        imageId = imageId,
        imageAlt = imageAlt,
        label = label,
        content = content
    )
}