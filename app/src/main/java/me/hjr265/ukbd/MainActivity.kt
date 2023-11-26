package me.hjr265.ukbd

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.hjr265.ukbd.ui.theme.UKbdTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (bluetoothAdapter.isEnabled == false) {
            val launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
            launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
            launcher.launch(
                Manifest.permission.BLUETOOTH_CONNECT
            )
        }

        val deviceAddress: Flow<String> = dataStore.data.map { currentPreferences ->
            currentPreferences[DEVICE_ADDRESS] ?: ""
        }

        setContent {
            UKbdTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Combo(bluetoothAdapter, deviceAddress)
                }
            }
        }
    }
}