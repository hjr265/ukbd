package me.hjr265.ukbd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow
import me.hjr265.ukbd.hid.Connection
import me.hjr265.ukbd.hid.ServiceListener

@SuppressLint("MissingPermission")
@Composable
fun Combo(
    bluetoothAdapter: BluetoothAdapter,
    deviceAddress: String,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current

    var hidConnection by remember { mutableStateOf<Connection?>(null) }

    DisposableEffect(lifeCycleOwner, deviceAddress) {
        val observer = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                Log.d("", "Disconnecting")
                hidConnection?.close()
                hidConnection = null
            }
            if (event == Lifecycle.Event.ON_RESUME && hidConnection == null) {
                Log.d("", "Connecting to ${deviceAddress}")
                if (deviceAddress == "") {
                    return@LifecycleEventObserver
                }
                val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
                Log.d("", ".. Device Name: ${device.name}")
                hidConnection = Connection(context, device)
                val hidServiceListener = ServiceListener(bluetoothAdapter, hidConnection!!)
                bluetoothAdapter.getProfileProxy(
                    context,
                    hidServiceListener,
                    BluetoothProfile.HID_DEVICE
                )
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var mode by remember { mutableStateOf("keyboard") }

    val togglePlum: @Composable () -> Unit = {
        Plum(onUp = {
            mode = if (mode == "keyboard") "touchpad" else "keyboard"
        }) {
            Image(
                painter = painterResource(id = if (mode == "keyboard") R.drawable.mouse else R.drawable.keyboard),
                contentDescription = "Settings",
                modifier = Modifier.size(16.dp)
            )
        }
    }

    val settingsPlum: @Composable () -> Unit = {
        Plum(onUp = {
            // context.startActivity(
            //     Intent(
            //         context,
            //         SettingsActivity::class.java
            //     )
            // )
        }) {
            Image(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings",
                modifier = Modifier.size(16.dp)
            )
        }
    }

    if (mode == "keyboard")
        Keyboard(hidConnection, togglePlum, settingsPlum)
    else
        Touchpad(hidConnection, togglePlum, settingsPlum)
}