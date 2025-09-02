package me.hjr265.ukbd

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import me.hjr265.ukbd.hid.Connection

@Composable
fun Combo(
    hidConnection: Connection?,
    capsLock: Boolean,
    scale: Float = 1f,
) {
    val context = LocalContext.current

    var mode by remember { mutableStateOf("keyboard") }

    val togglePlum: @Composable (Modifier) -> Unit = { modifier ->
        Plum(
            onUp = {
                mode = if (mode == "keyboard") "touchpad" else "keyboard"
            },
            imageId = if (mode == "keyboard") R.drawable.mouse else R.drawable.keyboard,
            imageAlt = "Change Mode",
            modifier = modifier,
        )
    }

    val settingsPlum: @Composable (Modifier) -> Unit = { modifier ->
        Plum(
            onUp = {
                context.startActivity(
                    Intent(
                        context,
                        SettingsActivity::class.java
                    )
                )
            },
            imageId = R.drawable.settings,
            imageAlt = "Settings",
            modifier = modifier,
        )
    }

    if (mode == "keyboard")
        Keyboard(hidConnection, togglePlum, settingsPlum, capsLock, scale)
    else
        Touchpad(hidConnection, togglePlum, settingsPlum, scale)
}