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
    togglePlum: @Composable (Modifier) -> Unit,
    settingsPlum: @Composable (Modifier) -> Unit,
    capsLock: Boolean,
    scale: Float = 1f
) {
    var layer by remember { mutableStateOf(Layer.DEFAULT) }

    Column {
        Row {
            val itemModifier = Modifier
                .width((scale * 55).dp)
                .height((scale * 50).dp)

            PlumSymbol(hidConnection, "ESC", label = "ESC", modifier = itemModifier)
            Spacer(modifier = Modifier.width(5.dp))
            when (layer) {
                Layer.DEFAULT, Layer.SHIFT -> {
                    PlumSymbol(hidConnection, "F1", label = "F1", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F2", label = "F2", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F3", label = "F3", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F4", label = "F4", modifier = itemModifier)
                    Spacer(modifier = Modifier.width(5.dp))
                    PlumSymbol(hidConnection, "F5", label = "F5", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F6", label = "F6", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F7", label = "F7", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F8", label = "F8", modifier = itemModifier)
                    Spacer(modifier = Modifier.width(5.dp))
                    PlumSymbol(hidConnection, "F9", label = "F9", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F10", label = "F10", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F11", label = "F11", modifier = itemModifier)
                    PlumSymbol(hidConnection, "F12", label = "F12", modifier = itemModifier)
                }

                Layer.FUNCTION -> {
                    PlumMedia(
                        hidConnection,
                        "VOLUMEMUTE",
                        imageId = R.drawable.volumemute,
                        modifier = itemModifier
                    )
                    PlumMedia(
                        hidConnection,
                        "PLAYPAUSE",
                        imageId = R.drawable.playpause,
                        modifier = itemModifier
                    )
                    PlumMedia(
                        hidConnection,
                        "TRACKNEXT",
                        imageId = R.drawable.tracknext,
                        modifier = itemModifier
                    )
                    PlumMedia(
                        hidConnection,
                        "TRACKPREVIOUS",
                        imageId = R.drawable.trackprevious,
                        modifier = itemModifier
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    PlumMedia(
                        hidConnection,
                        "VOLUMEUP",
                        imageId = R.drawable.volumeup,
                        modifier = itemModifier
                    )
                    PlumMedia(
                        hidConnection,
                        "VOLUMEDOWN",
                        imageId = R.drawable.volumedown,
                        modifier = itemModifier
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            togglePlum(itemModifier)
            settingsPlum(itemModifier)
        }
        Row {
            val itemModifier = Modifier
                .width((scale * 55).dp)
                .height((scale * 50).dp)

            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> {
                    PlumSymbol(hidConnection, "GRAVE", label = "`", modifier = itemModifier)
                    PlumSymbol(
                        hidConnection, "1", imageId = R.drawable.one, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "2", imageId = R.drawable.two, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "3", imageId = R.drawable.three, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "4", imageId = R.drawable.four, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "5", imageId = R.drawable.five, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "6", imageId = R.drawable.six, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "7", imageId = R.drawable.seven, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "8", imageId = R.drawable.eight, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "9", imageId = R.drawable.nine, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "0", imageId = R.drawable.zero, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "MINUS", imageId = R.drawable.minus, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "EQUAL", imageId = R.drawable.equals, modifier = itemModifier
                    )
                }

                Layer.SHIFT -> {
                    PlumSymbol(
                        hidConnection, "GRAVE", imageId = R.drawable.tilde, modifier = itemModifier
                    )
                    PlumSymbol(hidConnection, "1", label = "!", modifier = itemModifier)
                    PlumSymbol(hidConnection, "2", imageId = R.drawable.at, modifier = itemModifier)
                    PlumSymbol(
                        hidConnection, "3", imageId = R.drawable.hash, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "4", imageId = R.drawable.dollar, modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection, "5", imageId = R.drawable.percentage, modifier = itemModifier
                    )
                    PlumSymbol(hidConnection, "6", imageId = R.drawable.up, modifier = itemModifier)
                    PlumSymbol(hidConnection, "7", label = "&", modifier = itemModifier)
                    PlumSymbol(hidConnection, "8", label = "*", modifier = itemModifier)
                    PlumSymbol(
                        hidConnection,
                        "9",
                        imageId = R.drawable.leftbracket,
                        modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection,
                        "0",
                        imageId = R.drawable.rightbracket,
                        modifier = itemModifier
                    )
                    PlumSymbol(hidConnection, "MINUS", label = "_", modifier = itemModifier)
                    PlumSymbol(
                        hidConnection, "EQUAL", imageId = R.drawable.plus, modifier = itemModifier
                    )
                }
            }
            when (layer) {
                Layer.DEFAULT, Layer.SHIFT -> PlumSymbol(
                    hidConnection,
                    "BACKSPACE",
                    modifier = itemModifier.weight(1.5f),
                    imageId = R.drawable.backspace
                )

                Layer.FUNCTION -> PlumSymbol(
                    hidConnection, "DELETE", modifier = itemModifier.weight(1.5f), label = "DELETE"
                )
            }
        }
        Row {
            val itemModifier = Modifier
                .width((scale * 55).dp)
                .height((scale * 50).dp)

            PlumSymbol(
                hidConnection,
                "TAB",
                modifier = itemModifier.weight(1.25f),
                imageId = R.drawable.tab
            )
            PlumSymbol(hidConnection, "Q", imageId = R.drawable.q, modifier = itemModifier)
            PlumSymbol(hidConnection, "W", imageId = R.drawable.w, modifier = itemModifier)
            PlumSymbol(hidConnection, "E", imageId = R.drawable.e, modifier = itemModifier)
            PlumSymbol(hidConnection, "R", imageId = R.drawable.r, modifier = itemModifier)
            PlumSymbol(hidConnection, "T", imageId = R.drawable.t, modifier = itemModifier)
            PlumSymbol(hidConnection, "Y", imageId = R.drawable.y, modifier = itemModifier)
            PlumSymbol(hidConnection, "U", imageId = R.drawable.u, modifier = itemModifier)
            PlumSymbol(hidConnection, "I", imageId = R.drawable.i, modifier = itemModifier)
            PlumSymbol(hidConnection, "O", imageId = R.drawable.o, modifier = itemModifier)
            PlumSymbol(hidConnection, "P", imageId = R.drawable.p, modifier = itemModifier)
            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> {
                    PlumSymbol(
                        hidConnection,
                        "LEFTBRACE",
                        imageId = R.drawable.leftbrace,
                        modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection,
                        "RIGHTBRACE",
                        imageId = R.drawable.rightbrace,
                        modifier = itemModifier
                    )
                }

                Layer.SHIFT -> {
                    PlumSymbol(
                        hidConnection,
                        "LEFTBRACE",
                        imageId = R.drawable.leftcurlybrace,
                        modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection,
                        "RIGHTBRACE",
                        imageId = R.drawable.rightcurlybrace,
                        modifier = itemModifier
                    )
                }
            }
            PlumSymbol(
                hidConnection, "BACKSLASH", modifier = itemModifier.weight(1.25f), label = "\\"
            )
        }
        Row {
            val itemModifier = Modifier
                .width((scale * 55).dp)
                .height((scale * 50).dp)

            PlumSymbol(
                hidConnection,
                "CAPSLOCK",
                modifier = itemModifier.weight(1.5f),
                imageId = R.drawable.capslock,
                activeDot = true,
                active = capsLock
            )
            PlumSymbol(hidConnection, "A", imageId = R.drawable.a, modifier = itemModifier)
            PlumSymbol(hidConnection, "S", imageId = R.drawable.s, modifier = itemModifier)
            PlumSymbol(hidConnection, "D", imageId = R.drawable.d, modifier = itemModifier)
            PlumSymbol(hidConnection, "F", imageId = R.drawable.f, modifier = itemModifier)
            PlumSymbol(hidConnection, "G", imageId = R.drawable.g, modifier = itemModifier)
            PlumSymbol(hidConnection, "H", imageId = R.drawable.h, modifier = itemModifier)
            PlumSymbol(hidConnection, "J", imageId = R.drawable.j, modifier = itemModifier)
            PlumSymbol(hidConnection, "K", imageId = R.drawable.k, modifier = itemModifier)
            PlumSymbol(hidConnection, "L", imageId = R.drawable.l, modifier = itemModifier)
            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> {
                    PlumSymbol(hidConnection, "SEMICOLON", label = ";", modifier = itemModifier)
                    PlumSymbol(hidConnection, "APOSTROPHE", label = "'", modifier = itemModifier)
                }

                Layer.SHIFT -> {
                    PlumSymbol(hidConnection, "SEMICOLON", label = ":", modifier = itemModifier)
                    PlumSymbol(hidConnection, "APOSTROPHE", label = "\"", modifier = itemModifier)
                }
            }
            PlumSymbol(
                hidConnection,
                "ENTER",
                modifier = itemModifier.weight(1.75f),
                imageId = R.drawable.enter
            )
        }
        Row {
            val itemModifier = Modifier
                .width((scale * 55).dp)
                .height((scale * 50).dp)

            PlumModifier(
                hidConnection,
                "MOD_LSHIFT",
                modifier = itemModifier.weight(1.75f),
                onDown = { layer = Layer.SHIFT },
                onUp = { layer = Layer.DEFAULT },
                imageId = R.drawable.shift
            )
            PlumSymbol(hidConnection, "Z", imageId = R.drawable.z, modifier = itemModifier)
            PlumSymbol(hidConnection, "X", imageId = R.drawable.x, modifier = itemModifier)
            PlumSymbol(hidConnection, "C", imageId = R.drawable.c, modifier = itemModifier)
            PlumSymbol(hidConnection, "V", imageId = R.drawable.v, modifier = itemModifier)
            PlumSymbol(hidConnection, "B", imageId = R.drawable.b, modifier = itemModifier)
            PlumSymbol(hidConnection, "N", imageId = R.drawable.n, modifier = itemModifier)
            PlumSymbol(hidConnection, "M", imageId = R.drawable.m, modifier = itemModifier)
            when (layer) {
                Layer.DEFAULT, Layer.FUNCTION -> {
                    PlumSymbol(hidConnection, "COMMA", label = ",", modifier = itemModifier)
                    PlumSymbol(hidConnection, "DOT", label = ".", modifier = itemModifier)
                    PlumSymbol(hidConnection, "SLASH", label = "/", modifier = itemModifier)
                }

                Layer.SHIFT -> {
                    PlumSymbol(
                        hidConnection,
                        "COMMA",
                        imageId = R.drawable.lessthan,
                        modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection,
                        "DOT",
                        imageId = R.drawable.greaterthan,
                        modifier = itemModifier
                    )
                    PlumSymbol(
                        hidConnection,
                        "SLASH",
                        imageId = R.drawable.question,
                        modifier = itemModifier
                    )
                }
            }
            PlumModifier(
                hidConnection,
                "MOD_RSHIFT",
                modifier = itemModifier.weight(2.25f),
                onDown = { layer = Layer.SHIFT },
                onUp = { layer = Layer.DEFAULT },
                imageId = R.drawable.shift
            )
        }
        Row {
            val itemModifier = Modifier
                .width((scale * 55).dp)
                .height((scale * 50).dp)

            PlumModifier(hidConnection, "MOD_LCTRL", label = "CTRL", modifier = itemModifier)
            Plum(
                onDown = { layer = Layer.FUNCTION },
                onUp = { layer = Layer.DEFAULT },
                label = "FN",
                enabled = hidConnection != null,
                modifier = itemModifier
            )
            PlumModifier(
                hidConnection,
                "MOD_LMETA",
                imageId = R.drawable.meta,
                modifier = itemModifier
            )
            PlumModifier(hidConnection, "MOD_LALT", label = "ALT", modifier = itemModifier)
            PlumSymbol(
                hidConnection,
                "SPACE",
                modifier = itemModifier.weight(5f),
                imageId = R.drawable.space
            )
            PlumModifier(hidConnection, "MOD_RALT", label = "ALT", modifier = itemModifier)
            PlumSymbol(hidConnection, "SYSRQ", label = "PRTSC", modifier = itemModifier)
            PlumModifier(hidConnection, "MOD_RCTRL", label = "CTRL", modifier = itemModifier)
            Column {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    when (layer) {
                        Layer.DEFAULT, Layer.SHIFT -> {
                            PlumSymbol(
                                hidConnection,
                                "LEFT",
                                modifier = itemModifier.height(height = 40.dp),
                                imageId = R.drawable.left
                            )
                            Column {
                                PlumSymbol(
                                    hidConnection,
                                    "UP",
                                    modifier = itemModifier.height(height = 40.dp),
                                    imageId = R.drawable.up
                                )
                                PlumSymbol(
                                    hidConnection,
                                    "DOWN",
                                    modifier = itemModifier.height(height = 40.dp),
                                    imageId = R.drawable.down
                                )
                            }
                            PlumSymbol(
                                hidConnection,
                                "RIGHT",
                                modifier = itemModifier.height(height = 40.dp),
                                imageId = R.drawable.right
                            )
                        }

                        Layer.FUNCTION -> {
                            PlumSymbol(
                                hidConnection,
                                "HOME",
                                label = "HOME",
                                modifier = itemModifier.height(height = 40.dp)
                            )
                            Column {
                                PlumSymbol(
                                    hidConnection,
                                    "END",
                                    label = "END",
                                    modifier = itemModifier.height(height = 40.dp)
                                )
                                PlumSymbol(
                                    hidConnection,
                                    "PAGEUP",
                                    label = "PGUP",
                                    modifier = itemModifier.height(height = 40.dp)
                                )
                            }
                            PlumSymbol(
                                hidConnection,
                                "PAGEDOWN",
                                label = "PGDN",
                                modifier = itemModifier.height(height = 40.dp)
                            )
                        }
                    }
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
//            .defaultMinSize(55.dp, 50.dp)
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
                }, disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.38f)
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
            if (content != null) content()
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
                        })
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
    onDown: () -> Unit = {},
    onUp: () -> Unit = {},
    imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    activeDot: Boolean = false,
    active: Boolean = false,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Plum(
        onDown = {
            hidConnection?.keyDown(key)
            onDown()
        },
        onUp = {
            hidConnection?.keyUp(key)
            onUp()
        },
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