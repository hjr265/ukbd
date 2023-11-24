package me.hjr265.ukbd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jamal.composeprefs3.ui.GroupHeader
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.ListPref
import me.hjr265.ukbd.ui.theme.UKbdTheme

class SettingsActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        val devices = mutableMapOf<String, String>()
        for (device in bluetoothAdapter?.bondedDevices!!) {
            devices[device.address] = device.name
        }

        setContent {
            UKbdTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("µKbd Settings") }
                            )
                        }
                    ) { innerPadding ->
                        PrefsScreen(
                            modifier = Modifier.padding(innerPadding),
                            dataStore = LocalContext.current.dataStore
                        ) {
                            prefsGroup({
                                GroupHeader(
                                    title = "Bluetooth",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }) {
                                prefsItem {
                                    ListPref(
                                        key = DEVICE_ADDRESS.name,
                                        title = "Device",
                                        summary = "",
                                        useSelectedAsSummary = true,
                                        entries = devices
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val DEVICE_ADDRESS = stringPreferencesKey("device_address")