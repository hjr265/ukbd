package me.hjr265.ukbd

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.hjr265.ukbd.hid.Connection
import me.hjr265.ukbd.ui.theme.UKbdTheme

class MainActivity : ComponentActivity(), KeyboardService.Listener {
    private var keyboardService: KeyboardService? = null
    private var keyboardServiceBound = false

    private val keyboardServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? KeyboardService.LocalBinder
            keyboardService = localBinder?.getService()
            keyboardServiceBound = true

            keyboardService?.setListener(this@MainActivity)
            keyboardService?.initialize()

            lifecycleScope.launch {
                val deviceAddress: Flow<String> = dataStore.data.map { currentPreferences ->
                    currentPreferences[DEVICE_ADDRESS] ?: ""
                }
                deviceAddress.collect { value ->
                    keyboardService!!.setTargetDeviceAddress(value)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            keyboardService = null
            keyboardServiceBound = false
        }
    }

    private val hidConnectionState = mutableStateOf<Connection?>(null)
    private val capsLockState = mutableStateOf<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothAdapter: BluetoothAdapter? =
            getSystemService(BluetoothManager::class.java).adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bindKeyboardService()

        if (!bluetoothAdapter.isEnabled) {
            val launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
            launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val launcher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted)
                        keyboardService?.initialize()
                }
            launcher.launch(
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            keyboardService?.initialize()
        }

        setContent {
            UKbdTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val scale = (maxWidth / 855.dp)
                        Combo(hidConnectionState.value, capsLockState.value, scale)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        keyboardService?.disconnect()
    }

    override fun onResume() {
        super.onResume()
        keyboardService?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (keyboardServiceBound) {
            unbindService(keyboardServiceConnection)
            keyboardServiceBound = false
            keyboardService = null
        }
    }

    private fun bindKeyboardService() {
        val intent = Intent(this, KeyboardService::class.java)
        bindService(intent, keyboardServiceConnection, Context.BIND_AUTO_CREATE)
        startForegroundService(intent)
    }

    override fun onConnection(connection: Connection) {
        hidConnectionState.value = connection
    }

    override fun onCapsLock(enabled: Boolean) {
        capsLockState.value = enabled
    }
}