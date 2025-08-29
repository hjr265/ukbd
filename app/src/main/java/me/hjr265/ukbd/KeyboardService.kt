package me.hjr265.ukbd

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.hjr265.ukbd.hid.Connection
import me.hjr265.ukbd.hid.comboDescriptor
import java.util.concurrent.Executors

class KeyboardService : Service() {
    companion object {
        private val SDP: BluetoothHidDeviceAppSdpSettings by lazy {
            BluetoothHidDeviceAppSdpSettings(
                "uKbd",
                "µKbd",
                "Mahmud Ridwan",
                BluetoothHidDevice.SUBCLASS1_COMBO,
                comboDescriptor
            )
        }
    }

    private val tag = KeyboardService::class.java.simpleName

    inner class LocalBinder : Binder() {
        fun getService(): KeyboardService = this@KeyboardService
    }

    private val binder = LocalBinder()

    private var bluetoothAdapter: BluetoothAdapter? = null

    private var hidDeviceProxy: BluetoothHidDevice? = null
    private var registered: Boolean = false

    private var targetDeviceAddress: String? = null

    private var connection: Connection? = null

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile != BluetoothProfile.HID_DEVICE) return
            hidDeviceProxy = proxy as? BluetoothHidDevice
            registerApp()
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile != BluetoothProfile.HID_DEVICE) return
            hidDeviceProxy = null
        }
    }

    private val callback = object : BluetoothHidDevice.Callback() {
        private val handler = Handler(Looper.getMainLooper())

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onAppStatusChanged(
            pluggedDevice: BluetoothDevice?,
            registered: Boolean
        ) {
            super.onAppStatusChanged(pluggedDevice, registered)
            handler.post {
                this@KeyboardService.registered = registered
                if (!registered) {
                    bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDeviceProxy)
                    return@post
                }
                connection?.onAppRegistered(hidDeviceProxy)
                if (connection != null)
                    listener?.onConnection(connection!!)
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChanged(
            device: BluetoothDevice?,
            state: Int
        ) {
            super.onConnectionStateChanged(device, state)
            if (device != null) connection?.onConnectionStateChanged(device, state)
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onInterruptData(
            device: BluetoothDevice?,
            reportId: Byte,
            data: ByteArray
        ) {
            super.onInterruptData(device, reportId, data)
            if (device != null) connection?.onInterruptData(device, reportId, data)
        }
    }

    interface Listener {
        fun onConnection(connection: Connection) {}
        fun onCapsLock(enabled: Boolean) {}
    }

    private var listener: Listener? = null
    fun setListener(l: Listener?) {
        listener = l
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        bluetoothAdapter = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
        startForegroundIfNeeded()
    }

    override fun onDestroy() {
        releaseHidProxy()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = binder

    fun initialize() {
        if (registered) return
        requestHidProxy()
    }

    fun setTargetDeviceAddress(address: String) {
        targetDeviceAddress = address
        connectToDeviceIfReady()
    }

    private fun connectToDeviceIfReady() {
        val address = targetDeviceAddress ?: return
        val adapter = bluetoothAdapter ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(tag, "Missing BLUETOOTH_CONNECT permission; cannot get remote device")
            return
        }

        val device = try {
            adapter.getRemoteDevice(address)
        } catch (_: IllegalArgumentException) {
            Log.w(tag, "Invalid device address ($address); cannot get remote device")
            return
        }

        connection = Connection(
            this,
            device,
            onCapsLock = { enabled ->
                listener?.onCapsLock(enabled)
            }
        )

        if (registered) {
            connection?.onAppRegistered(hidDeviceProxy)
            listener?.onConnection(connection!!)
        }
    }

    private fun startForegroundIfNeeded() {
        val channelId = "hid_service_channel"
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val chan = NotificationChannel(
            channelId,
            "HID Proxy Service",
            NotificationManager.IMPORTANCE_LOW
        )
        nm.createNotificationChannel(chan)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("HID Proxy Service")
            .setContentText("Managing Bluetooth HID proxy")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    private fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun registerApp() {
        val qos = BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT, 800, 0, 0, 0, 0
        )
        hidDeviceProxy?.registerApp(
            SDP,
            null,
            qos,
            Executors.newCachedThreadPool(),
            callback
        )
    }

    private fun requestHidProxy() {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            Log.e(tag, "No Bluetooth adapter available")
            return
        }

        if (!hasConnectPermission()) {
            Log.e(tag, "Missing BLUETOOTH_CONNECT permission; cannot request profile proxy")
            return
        }

        serviceScope.launch {
            val ok = bluetoothAdapter?.getProfileProxy(
                this@KeyboardService,
                serviceListener,
                BluetoothProfile.HID_DEVICE
            ) ?: false
            if (ok) Log.i(tag, "Requested HID device proxy")
            else Log.e(tag, "Failed to get HID device proxy")
        }
    }

    private fun releaseHidProxy() {
        try {
            val adapter = bluetoothAdapter ?: return
            adapter.closeProfileProxy(
                BluetoothProfile.HID_DEVICE,
                hidDeviceProxy as BluetoothProfile
            )
            Log.i(tag, "Closed HID proxy")
        } catch (t: Throwable) {
            Log.w(tag, "Error closing HID proxy: ${t.message}")
        } finally {
            hidDeviceProxy = null
        }
    }
}