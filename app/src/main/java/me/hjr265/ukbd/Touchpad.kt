package me.hjr265.ukbd

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import me.hjr265.ukbd.hid.Connection

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("MissingPermission")
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .pointerInteropFilter {
                        hidConnection?.touchMotion(it) ?: false
                    }
            )
        }
    }
}