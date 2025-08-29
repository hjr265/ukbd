package me.hjr265.ukbd

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jamal.composeprefs3.ui.GroupHeader
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.ListPref
import com.jamal.composeprefs3.ui.prefs.TextPref
import me.hjr265.ukbd.ui.theme.UKbdTheme

class SettingsActivity : ComponentActivity() {
    val discoverableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val code = result.resultCode
        if (code != RESULT_CANCELED) {
            Toast.makeText(
                this,
                "Device discoverable for $code seconds",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        var missingPermission = false
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            missingPermission = true
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
                                if (bluetoothAdapter == null) {
                                    prefsItem {
                                        TextPref(
                                            title = "Bluetooth not supported",
                                            summary = "The device does not seem to have a bluetooth adapter.",
                                        )
                                    }
                                    return@prefsGroup
                                }
                                if (missingPermission) {
                                    prefsItem {
                                        TextPref(
                                            title = "Permission",
                                            summary = "µKbd needs permission to access Bluetooth features.",
                                            onClick = {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    val launcher = registerForActivityResult(
                                                        ActivityResultContracts.RequestPermission()
                                                    ) {}
                                                    launcher.launch(
                                                        Manifest.permission.BLUETOOTH_CONNECT
                                                    )
                                                }
                                            },
                                            enabled = true
                                        )
                                    }
                                    return@prefsGroup
                                }
                                prefsItem {
                                    BondedDevicesListPref(
                                        key = DEVICE_ADDRESS.name,
                                        title = "Device",
                                        bluetoothAdapter = bluetoothAdapter
                                    )
                                }
                                prefsItem {
                                    MakeDiscoverableTextPerf()
                                }
                            }
                            prefsGroup({
                                GroupHeader(
                                    title = "About",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }) {
                                prefsItem {
                                    TextPref(
                                        title = "µKbd",
                                        summary = "Virtual Bluetooth keyboard and trackpad combo"
                                    )
                                }
                                prefsItem {
                                    TextPref(
                                        title = "Version",
                                        summary = "0.0.1"
                                    )
                                }
                                prefsItem {
                                    TextPref(
                                        title = "Licenses",
                                        summary = "µKbd uses other open source projects"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun BondedDevicesListPref(
        key: String,
        title: String,
        bluetoothAdapter: BluetoothAdapter
    ) {
        val context = LocalContext.current

        val devices = remember { mutableStateListOf<Pair<String, String>>() }

        fun loadBonded() {
            val set = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    bluetoothAdapter.bondedDevices
                } else null
            } else bluetoothAdapter.bondedDevices

            devices.clear()
            set?.forEach { d ->
                val name = d.name ?: d.address
                devices += name to d.address
            }
        }

        LaunchedEffect(bluetoothAdapter) {
            loadBonded()
        }
        DisposableEffect(context) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED)
                        loadBonded()
                }
            }
            val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )
            onDispose { context.unregisterReceiver(receiver) }
        }

        val entries = devices.associate { it.second to it.first }

        ListPref(
            key = key,
            title = title,
            summary = "",
            useSelectedAsSummary = true,
            entries = entries
        )
    }

    @Composable
    fun MakeDiscoverableTextPerf() {
        TextPref(
            title = "Make Discoverable",
            onClick = {
                discoverableLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
            },
            enabled = true
        )
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val DEVICE_ADDRESS = stringPreferencesKey("device_address")
