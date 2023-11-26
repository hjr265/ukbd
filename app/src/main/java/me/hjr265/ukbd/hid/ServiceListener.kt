package me.hjr265.ukbd.hid

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothProfile
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.concurrent.Executors

class ServiceListener(
    private val bluetoothAdapter: BluetoothAdapter,
    private val connection: Connection
) : BluetoothProfile.ServiceListener {
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

    @SuppressLint("InlinedApi")
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        Log.d("", "Service connected")
        Log.d("", ".. Profile: ${profile}")
        Log.d("", ".. Profile: ${proxy}")
        if (profile != BluetoothProfile.HID_DEVICE || proxy !is BluetoothHidDevice) return
        val callback = object : BluetoothHidDevice.Callback() {
            private val handler = Handler(Looper.getMainLooper())

            @SuppressLint("InlinedApi")
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
                super.onAppStatusChanged(pluggedDevice, registered)
                handler.post {
                    if (registered) connection.onAppRegistered(proxy)
                    else bluetoothAdapter.closeProfileProxy(BluetoothProfile.HID_DEVICE, proxy)
                }
            }

            @SuppressLint("InlinedApi")
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
                super.onConnectionStateChanged(device, state)
                if (device != null) connection.onConnectionStateChanged(device, state)
            }

            @SuppressLint("InlinedApi")
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onInterruptData(device: BluetoothDevice?, reportId: Byte, data: ByteArray) {
                super.onInterruptData(device, reportId, data)
                if (device != null) connection.onInterruptData(device, reportId, data)
                Log.d("", "! $reportId ${data.joinToString(" ")}")
            }
        }
        proxy.registerApp(SDP, null, null, Executors.newCachedThreadPool(), callback)
    }

    override fun onServiceDisconnected(profile: Int) {}
}