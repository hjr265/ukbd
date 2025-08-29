package me.hjr265.ukbd.hid

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.util.Timer
import java.util.TimerTask
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or
import kotlin.math.min

class Connection(
    private val context: Context,
    private var hostDevice: BluetoothDevice? = null,
    private val onCapsLock: (Boolean) -> Unit
) {
    private var service: BluetoothHidDevice? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun onAppRegistered(proxy: BluetoothHidDevice?) {
        service = proxy
        proxy?.connect(hostDevice)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
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

    fun onInterruptData(device: BluetoothDevice?, reportId: Byte, data: ByteArray) {
        Log.d("", "interrupt $reportId $data")
        if (reportId == 0x01.toByte()) onCapsLock((data[0] and 0x02) > 0)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun close() {
        service?.disconnect(hostDevice)
    }

    private var modifierByte: Byte = 0x00
    private var keyBytes = mutableListOf<Byte>()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun sendKeyboardReport() {
        val report = ByteArray(2 + 6)
        report[0] = modifierByte
        for (i in 0 until 6) {
            if (i < keyBytes.size)
                report[i + 2] = keyBytes[i]
        }
        service?.sendReport(hostDevice, 0x01, report)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun keyDown(key: String) {
        val code = keyCodes[key]!!
        Log.d("", "keyDown $code")
        synchronized(this::class.java) {
            if (keyBytes.contains(code))
                return
            keyBytes.add(code)
            sendKeyboardReport()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun keyUp(key: String) {
        val code = keyCodes[key]!!
        Log.d("", "keyUp $code")
        synchronized(this::class.java) {
            if (!keyBytes.contains(code))
                return
            keyBytes.remove(code)
            sendKeyboardReport()
        }
    }

    fun modifierDown(key: String) {
        val code = keyCodes[key]!!
        Log.d("", "modDown $code")
        synchronized(this::class.java) { modifierByte = modifierByte or code }
    }

    fun modifierUp(key: String) {
        val code = keyCodes[key]!!
        Log.d("", "modUp $code")
        synchronized(this::class.java) { modifierByte = modifierByte and code.inv() }
    }

    private var mouseButtonByte: Byte = 0x00

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun sendMouseReport(
        buttonByte: Byte? = null,
        deltaX: Byte = 0,
        deltaY: Byte = 0,
        deltaWheel: Byte = 0
    ) {
        service?.sendReport(
            hostDevice,
            0x02,
            byteArrayOf(
                buttonByte ?: mouseButtonByte,
                deltaX,
                deltaY,
                deltaWheel,
            )
        )
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun mouseMove(deltaX: Int, deltaY: Int) {
        Log.d("", "mouseMove $deltaX $deltaY")
        sendMouseReport(
            deltaX = deltaX.coerceAtLeast(-128).coerceAtMost(127).toByte(),
            deltaY = deltaY.coerceAtLeast(-128).coerceAtMost(127).toByte()
        )
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun mouseDown(button: Int) {
        Log.d("", "mouseDown $button")
        synchronized(this::class.java) {
            mouseButtonByte = mouseButtonByte or ((1 shl button).toByte())
            sendMouseReport()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun mouseUp(button: Int) {
        Log.d("", "mouseUp $button")
        synchronized(this::class.java) {
            mouseButtonByte = mouseButtonByte and ((1 shl button).toByte()).inv()
            sendMouseReport()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun mouseWheel(delta: Int) {
        Log.d("", "mouseWheel $delta")
        sendMouseReport(deltaWheel = delta.coerceAtLeast(-128).coerceAtMost(127).toByte())
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun mouseClick(button: Int) {
        Log.d("", "mouseClick $button")
        synchronized(this::class.java) {
            mouseButtonByte = mouseButtonByte or (1 shl button).toByte()
        }
        sendMouseReport()
        Timer().schedule(object : TimerTask() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun run() {
                synchronized(this::class.java) {
                    mouseButtonByte = mouseButtonByte and (1 shl button).toByte().inv()
                    sendMouseReport()
                }
                cancel()
            }
        }, 20)
    }

    private var mediaByte: Byte = 0x00

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun sendMediaReport() {
        val report = ByteArray(1)
        report[0] = mediaByte
        service?.sendReport(hostDevice, 0x03, report)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun mediaDown(key: String) {
        val code = keyCodes[key]!!
        Log.d("", "mediaDown $code")
        synchronized(this::class.java) {
            mediaByte = mediaByte or code
            sendMediaReport()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun mediaUp(key: String) {
        val code = keyCodes[key]!!
        Log.d("", "mediaUp $code")
        synchronized(this::class.java) {
            mediaByte = mediaByte and code.inv()
            sendMediaReport()
        }
    }
}
