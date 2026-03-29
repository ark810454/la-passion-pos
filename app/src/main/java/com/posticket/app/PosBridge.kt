package com.posticket.app

import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONObject

class PosBridge(
    private val printerManager: BluetoothPrinterManager,
    private val integratedPrinterManager: IntegratedPrinterManager,
    private val onRequestPermissions: () -> Unit
) {

    @JavascriptInterface
    fun getStatus(): String {
        return JSONObject(
            mapOf(
                "bluetoothAvailable" to printerManager.isBluetoothAvailable(),
                "bluetoothEnabled" to printerManager.isBluetoothEnabled(),
                "hasPermission" to printerManager.hasBluetoothPermission(),
                "integratedPrinterAvailable" to integratedPrinterManager.isAvailable()
            )
        ).toString()
    }

    @JavascriptInterface
    fun requestBluetoothPermission() {
        onRequestPermissions()
    }

    @JavascriptInterface
    fun getPairedPrinters(): String {
        val devices = mutableListOf<PrinterDevice>()
        integratedPrinterManager.getDevice()?.let(devices::add)
        devices += printerManager.getPairedPrinters()
        val array = JSONArray()
        devices.forEach { device ->
            array.put(
                JSONObject(
                    mapOf(
                        "name" to device.name,
                        "address" to device.address
                    )
                )
            )
        }
        return array.toString()
    }

    @JavascriptInterface
    fun printTicket(printerAddress: String, ticketJson: String): String {
        val request = TicketRequest.fromJson(ticketJson)
        if (printerAddress == IntegratedPrinterManager.INTEGRATED_PRINTER_ID) {
            integratedPrinterManager.printText(buildIntegratedLines(request))
        } else {
            val bytes = EscPosFormatter.buildTicket(request)
            printerManager.print(printerAddress, bytes)
        }
        return JSONObject(mapOf("ok" to true)).toString()
    }

    private fun buildIntegratedLines(request: TicketRequest): List<String> {
        if (request.footerLines.isNotEmpty()) {
            return request.footerLines + listOf("", "", "")
        }

        val lines = mutableListOf<String>()
        lines += request.storeName.uppercase()
        request.storeAddress?.takeIf { it.isNotBlank() }?.let(lines::add)
        request.storePhone?.takeIf { it.isNotBlank() }?.let { lines += "Tel: $it" }
        lines += "--------------------------------"
        lines += "Ticket: ${request.ticketNumber}"
        lines += "Date: ${request.date}"
        lines += "--------------------------------"
        request.items.forEach { item ->
            lines += "${item.quantity} x ${item.label}"
            lines += "  ${item.unitPrice}    ${item.totalPrice}"
        }
        lines += "--------------------------------"
        if (request.total.isNotBlank()) {
            lines += "TOTAL: ${request.total}"
        }
        lines += request.footerLines
        return lines
    }
}
