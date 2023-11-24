package me.hjr265.ukbd.hid

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

@Keep
@RequiresApi(Build.VERSION_CODES.P)
class Connection(private val context: Context, private var hostDevice: BluetoothDevice? = null) {
    private var service: BluetoothHidDevice? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun onAppRegistered(proxy: BluetoothHidDevice?) {
        service = proxy
        proxy?.connect(hostDevice)
    }

    @SuppressLint("MissingPermission")
    fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
        if (device.address != hostDevice?.address) return

        val handler = Handler(Looper.getMainLooper())

        when (state) {
            BluetoothProfile.STATE_CONNECTED -> {
                hostDevice = device
                handler.post {
                    Toast.makeText(context, "Connected: ${device.name}", Toast.LENGTH_SHORT).show()
                }
            }

            BluetoothProfile.STATE_CONNECTING -> {
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                hostDevice = null
                handler.post {
                    Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
                }
            }

            BluetoothProfile.STATE_DISCONNECTING -> {
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun close() {
        service?.disconnect(hostDevice)
        service?.unregisterApp()
    }

    private var modByte: Byte = 0x00

    @SuppressLint("MissingPermission")
    fun keyDown(key: String) {
        val code = keyCodes[key]
        Log.d("", "keyDown " + modByte.toString(16) + " " + code?.toString(16))
        service?.sendReport(hostDevice, 0x02, byteArrayOf(modByte, code!!))
    }

    @SuppressLint("MissingPermission")
    fun keyUp(key: String) {
        Log.d("", "keyUp " + modByte.toString(16))
        service?.sendReport(hostDevice, 0x02, byteArrayOf(modByte, 0x00))
    }

    fun modDown(key: String) {
        val code = keyCodes[key]
        synchronized(this::class.java) { modByte = modByte or code!! }
        Log.d("", "modDown " + modByte.toString(16))
    }

    fun modUp(key: String) {
        val code = keyCodes[key]
        synchronized(this::class.java) { modByte = modByte and code!!.inv() }
        Log.d("", "modUp " + modByte.toString(16))
    }

    private var lastX = 0f
    private var lastY = 0f

    @SuppressLint("MissingPermission")
    fun touchMotion(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = (event.x - lastX).toInt()
                val deltaY = (event.y - lastY).toInt()
                Log.d("", "touchMove ${deltaX} ${deltaY}")
                service?.sendReport(
                    hostDevice,
                    0x01,
                    byteArrayOf(0, deltaX.toByte(), deltaY.toByte(), 0)
                )
                lastX = event.x
                lastY = event.y
            }
        }

        return true
    }
}