package com.posticket.app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

class BluetoothPrinterManager(private val context: Context) {

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun isBluetoothAvailable(): Boolean = adapter != null

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    fun hasBluetoothPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun getPairedPrinters(): List<PrinterDevice> {
        if (!hasBluetoothPermission()) {
            throw IllegalStateException("Permission Bluetooth manquante.")
        }

        val bonded = adapter?.bondedDevices ?: emptySet()
        return bonded
            .sortedBy { it.name ?: it.address }
            .map {
                PrinterDevice(
                    name = it.name ?: "Imprimante inconnue",
                    address = it.address
                )
            }
    }

    fun print(address: String, bytes: ByteArray) {
        if (!hasBluetoothPermission()) {
            throw IllegalStateException("Permission Bluetooth manquante.")
        }

        val target = adapter
            ?.bondedDevices
            ?.firstOrNull { it.address.equals(address, ignoreCase = true) }
            ?: throw IllegalArgumentException("Imprimante introuvable: $address")

        connectAndWrite(target, bytes)
    }

    fun isBluetoothPrinterAddress(address: String): Boolean {
        return address.contains(":") && !address.startsWith("INTERNAL:")
    }

    private fun connectAndWrite(device: BluetoothDevice, bytes: ByteArray) {
        var socket: BluetoothSocket? = null
        try {
            adapter?.cancelDiscovery()
            socket = device.createRfcommSocketToServiceRecord(sppUuid)
            socket.connect()
            socket.outputStream.use { stream ->
                stream.write(bytes)
                stream.flush()
            }
        } catch (error: IOException) {
            throw IOException(
                "Connexion impossible avec ${device.name ?: device.address}. " +
                    "Verifiez l'appairage et la compatibilite ESC/POS.",
                error
            )
        } finally {
            try {
                socket?.close()
            } catch (_: IOException) {
            }
        }
    }
}
