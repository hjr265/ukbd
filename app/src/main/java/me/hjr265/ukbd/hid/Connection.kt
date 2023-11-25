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
import android.widget.Toast
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.util.Timer
import java.util.TimerTask
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

    private var modifierByte: Byte = 0x00

    @SuppressLint("MissingPermission")
    fun keyDown(key: String) {
        val code = keyCodes[key]
        Log.d("", "keyDown " + modifierByte.toString(16) + " " + code?.toString(16))
        service?.sendReport(hostDevice, 0x02, byteArrayOf(modifierByte, code!!))
    }

    @SuppressLint("MissingPermission")
    fun keyUp(key: String) {
        Log.d("", "keyUp " + modifierByte.toString(16))
        service?.sendReport(hostDevice, 0x02, byteArrayOf(modifierByte, 0x00))
    }

    fun modifierDown(key: String) {
        val code = keyCodes[key]
        synchronized(this::class.java) { modifierByte = modifierByte or code!! }
        Log.d("", "modDown " + modifierByte.toString(16))
    }

    fun modifierUp(key: String) {
        val code = keyCodes[key]
        synchronized(this::class.java) { modifierByte = modifierByte and code!!.inv() }
        Log.d("", "modUp " + modifierByte.toString(16))
    }

    private var mouseButtonByte: Byte = 0x00

    @SuppressLint("MissingPermission")
    fun sendMouseReport(buttonByte: Byte? = null, deltaX: Byte = 0, deltaY: Byte = 0) {
        service?.sendReport(
            hostDevice,
            0x01,
            byteArrayOf(
                buttonByte ?: mouseButtonByte,
                deltaX,
                deltaY,
                0
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun mouseMove(deltaX: Int, deltaY: Int) {
        Log.d("", "mouseMove ${deltaX} ${deltaY}")
        sendMouseReport(
            deltaX = deltaX.coerceAtLeast(-128).coerceAtMost(127).toByte(),
            deltaY = deltaY.coerceAtLeast(-128).coerceAtMost(127).toByte()
        )
    }

    fun mouseDown(button: Int) {
        Log.d("", "mouseDown ${button}")
        synchronized(this::class.java) {
            mouseButtonByte = mouseButtonByte or ((1 shl button).toByte())
            sendMouseReport()
        }
    }

    fun mouseUp(button: Int) {
        Log.d("", "mouseUp ${button}")
        synchronized(this::class.java) {
            mouseButtonByte = mouseButtonByte and ((1 shl button).toByte()).inv()
            sendMouseReport()
        }
    }

    fun mouseClick(button: Int) {
        synchronized(this::class.java) { mouseButtonByte = mouseButtonByte or (1 shl button).toByte() }
        sendMouseReport()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                synchronized(this::class.java) {
                    mouseButtonByte = mouseButtonByte and (1 shl button).toByte().inv()
                    sendMouseReport()
                }
                cancel()
            }
        }, 20)
    }
}