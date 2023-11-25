package me.hjr265.ukbd

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hjr265.ukbd.hid.Connection

@Composable
fun Keyboard(
    hidConnection: Connection?,
    togglePlum: @Composable () -> Unit,
    settingsPlum: @Composable () -> Unit
) {
    val context = LocalContext.current

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
            PlumSymbol(hidConnection, "1", R.drawable.one)
            PlumSymbol(hidConnection, "2", R.drawable.two)
            PlumSymbol(hidConnection, "3", R.drawable.three)
            PlumSymbol(hidConnection, "4", R.drawable.four)
            PlumSymbol(hidConnection, "5", R.drawable.five)
            PlumSymbol(hidConnection, "6", R.drawable.six)
            PlumSymbol(hidConnection, "7", R.drawable.seven)
            PlumSymbol(hidConnection, "8", R.drawable.eight)
            PlumSymbol(hidConnection, "9", R.drawable.nine)
            PlumSymbol(hidConnection, "0", R.drawable.zero)
            PlumSymbol(hidConnection, "MINUS", label = "-")
            PlumSymbol(hidConnection, "EQUAL", label = "=")
            PlumSymbol(
                hidConnection,
                "BACKSPACE",
                R.drawable.backspace,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            PlumSymbol(
                hidConnection,
                "TAB",
                R.drawable.tab,
                modifier = Modifier.defaultMinSize(minWidth = 75.dp)
            )
            PlumSymbol(hidConnection, "Q", R.drawable.q)
            PlumSymbol(hidConnection, "W", R.drawable.w)
            PlumSymbol(hidConnection, "E", R.drawable.e)
            PlumSymbol(hidConnection, "R", R.drawable.r)
            PlumSymbol(hidConnection, "T", R.drawable.t)
            PlumSymbol(hidConnection, "Y", R.drawable.y)
            PlumSymbol(hidConnection, "U", R.drawable.u)
            PlumSymbol(hidConnection, "I", R.drawable.i)
            PlumSymbol(hidConnection, "O", R.drawable.o)
            PlumSymbol(hidConnection, "P", R.drawable.p)
            PlumSymbol(hidConnection, "LEFTBRACE", R.drawable.leftbrace)
            PlumSymbol(hidConnection, "RIGHTBRACE", R.drawable.rightbrace)
            PlumSymbol(
                hidConnection,
                "BACKSLASH",
                label = "\\",
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            PlumSymbol(
                hidConnection,
                "CAPSLOCK",
                R.drawable.capslock,
                modifier = Modifier.weight(0.85f)
            )
            PlumSymbol(hidConnection, "A", R.drawable.a)
            PlumSymbol(hidConnection, "S", R.drawable.s)
            PlumSymbol(hidConnection, "D", R.drawable.d)
            PlumSymbol(hidConnection, "F", R.drawable.f)
            PlumSymbol(hidConnection, "G", R.drawable.g)
            PlumSymbol(hidConnection, "H", R.drawable.h)
            PlumSymbol(hidConnection, "J", R.drawable.j)
            PlumSymbol(hidConnection, "K", R.drawable.k)
            PlumSymbol(hidConnection, "L", R.drawable.l)
            PlumSymbol(hidConnection, "SEMICOLON", label = ";")
            PlumSymbol(hidConnection, "APOSTROPHE", label = "'")
            PlumSymbol(
                hidConnection,
                "ENTER",
                R.drawable.enter,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            PlumModifier(
                hidConnection,
                "MOD_LSHIFT",
                imageId = R.drawable.shift,
                modifier = Modifier.weight(1f)
            )
            PlumSymbol(hidConnection, "Z", R.drawable.z)
            PlumSymbol(hidConnection, "X", R.drawable.x)
            PlumSymbol(hidConnection, "C", R.drawable.c)
            PlumSymbol(hidConnection, "V", R.drawable.v)
            PlumSymbol(hidConnection, "B", R.drawable.b)
            PlumSymbol(hidConnection, "N", R.drawable.n)
            PlumSymbol(hidConnection, "M", R.drawable.m)
            PlumSymbol(hidConnection, "COMMA", label = ",")
            PlumSymbol(hidConnection, "DOT", label = ".")
            PlumSymbol(hidConnection, "SLASH", label = "/")
            PlumModifier(
                hidConnection,
                "MOD_RSHIFT",
                imageId = R.drawable.shift,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            PlumModifier(hidConnection, "MOD_LCTRL", label = "CTRL")
            PlumModifier(hidConnection, "MOD_LMETA", imageId = R.drawable.meta)
            PlumModifier(hidConnection, "MOD_LALT", label = "ALT")
            PlumSymbol(
                hidConnection,
                "SPACE",
                R.drawable.space,
                modifier = Modifier.weight(1f)
            )
            PlumModifier(hidConnection, "MOD_RALT", label = "ALT")
            PlumModifier(hidConnection, "MOD_RCTRL", label = "CTRL")
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                PlumSymbol(
                    hidConnection,
                    "LEFT",
                    R.drawable.left,
                    modifier = Modifier.height(height = 40.dp)
                )
                Column {
                    PlumSymbol(
                        hidConnection,
                        "UP",
                        R.drawable.up,
                        modifier = Modifier.height(height = 40.dp)
                    )
                    PlumSymbol(
                        hidConnection,
                        "DOWN",
                        R.drawable.down,
                        modifier = Modifier.height(height = 40.dp)
                    )
                }
                PlumSymbol(
                    hidConnection,
                    "RIGHT",
                    R.drawable.right,
                    modifier = Modifier.height(height = 40.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Plum(
    onDown: () -> Unit = {},
    onUp: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    @DrawableRes imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    content: @Composable (RowScope.() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    var pressed by remember { mutableStateOf(false) }
    var index by remember { mutableStateOf(-1) }

    Button(
        onClick = {},
        modifier = modifier
            .defaultMinSize(55.dp, 50.dp)
            .padding(1.dp)
            .pointerInteropFilter {
                var consume = true
                when (it.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        if (!enabled) {
                            consume = false
                        } else if (index == -1) {
                            index = it.actionIndex
                            pressed = true
                            onDown()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                        if (it.actionIndex == index) {
                            index = -1
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
            containerColor = with(MaterialTheme.colorScheme.secondary) {
                if (pressed) this.copy(alpha = 0.76f) else this
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
}

@Composable
fun PlumSymbol(
    hidConnection: Connection?,
    key: String,
    imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    modifier: Modifier = Modifier,
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
        content = content
    )
}

@Composable
fun PlumModifier(
    hidConnection: Connection?,
    key: String,
    imageId: Int = 0,
    imageAlt: String = "",
    label: String = "",
    modifier: Modifier = Modifier,
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