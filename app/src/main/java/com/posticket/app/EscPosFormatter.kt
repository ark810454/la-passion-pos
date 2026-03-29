package com.posticket.app

import java.io.ByteArrayOutputStream

object EscPosFormatter {

    fun buildTicket(request: TicketRequest): ByteArray {
        val output = ByteArrayOutputStream()

        initialize(output)
        alignCenter(output)
        bold(output, true)
        doubleHeight(output, true)
        line(output, request.storeName.uppercase())
        doubleHeight(output, false)
        bold(output, false)

        request.storeAddress?.takeIf { it.isNotBlank() }?.let { line(output, it) }
        request.storePhone?.takeIf { it.isNotBlank() }?.let { line(output, "Tel: $it") }
        feed(output, 1)

        alignLeft(output)
        separator(output)
        request.headerLines.forEach { line(output, it) }
        line(output, "Ticket: ${request.ticketNumber}")
        line(output, "Date: ${request.date}")
        separator(output)

        request.items.forEach { item ->
            line(output, formatItem(item))
        }

        separator(output)
        bold(output, true)
        line(output, padRight("TOTAL", 18) + padLeft(request.total, 14))
        bold(output, false)
        request.footerLines.forEach { line(output, it) }

        request.qrData?.takeIf { it.isNotBlank() }?.let { qr ->
            feed(output, 1)
            alignCenter(output)
            writeQr(output, qr)
            alignLeft(output)
        }

        feed(output, 4)
        cut(output)

        return output.toByteArray()
    }

    private fun initialize(output: ByteArrayOutputStream) {
        output.write(byteArrayOf(0x1B, 0x40))
        output.write(byteArrayOf(0x1B, 0x74, 0x00))
    }

    private fun separator(output: ByteArrayOutputStream) = line(output, "--------------------------------")

    private fun line(output: ByteArrayOutputStream, text: String) {
        output.write(text.toByteArray(Charsets.UTF_8))
        output.write('\n'.code)
    }

    private fun feed(output: ByteArrayOutputStream, lines: Int) {
        repeat(lines) { output.write('\n'.code) }
    }

    private fun alignLeft(output: ByteArrayOutputStream) {
        output.write(byteArrayOf(0x1B, 0x61, 0x00))
    }

    private fun alignCenter(output: ByteArrayOutputStream) {
        output.write(byteArrayOf(0x1B, 0x61, 0x01))
    }

    private fun bold(output: ByteArrayOutputStream, enabled: Boolean) {
        output.write(byteArrayOf(0x1B, 0x45, if (enabled) 0x01 else 0x00))
    }

    private fun doubleHeight(output: ByteArrayOutputStream, enabled: Boolean) {
        output.write(byteArrayOf(0x1D, 0x21, if (enabled) 0x11 else 0x00))
    }

    private fun cut(output: ByteArrayOutputStream) {
        output.write(byteArrayOf(0x1D, 0x56, 0x42, 0x00))
    }

    private fun writeQr(output: ByteArrayOutputStream, data: String) {
        val bytes = data.toByteArray(Charsets.UTF_8)
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00))
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, 0x06))
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30))

        val size = bytes.size + 3
        val pL = (size % 256).toByte()
        val pH = (size / 256).toByte()
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x31, 0x50, 0x30))
        output.write(bytes)
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))
        output.write('\n'.code)
    }

    private fun formatItem(item: TicketItem): String {
        val label = "${item.quantity} x ${item.label}"
        val left = truncate(label, 20)
        val middle = padLeft(item.unitPrice, 5)
        val right = padLeft(item.totalPrice, 7)
        return left + middle + right
    }

    private fun truncate(value: String, size: Int): String {
        return if (value.length <= size) {
            padRight(value, size)
        } else {
            value.take(size - 1) + " "
        }
    }

    private fun padRight(value: String, size: Int): String = value.padEnd(size, ' ')

    private fun padLeft(value: String, size: Int): String = value.padStart(size, ' ')
}
