package com.posticket.app

import org.json.JSONArray
import org.json.JSONObject

data class PrinterDevice(
    val name: String,
    val address: String
)

data class TicketItem(
    val label: String,
    val quantity: Int,
    val unitPrice: String,
    val totalPrice: String
)

data class TicketRequest(
    val storeName: String,
    val storeAddress: String?,
    val storePhone: String?,
    val ticketNumber: String,
    val date: String,
    val total: String,
    val headerLines: List<String>,
    val footerLines: List<String>,
    val items: List<TicketItem>,
    val qrData: String?
) {
    companion object {
        fun fromJson(raw: String): TicketRequest {
            val json = JSONObject(raw)
            val itemArray = json.optJSONArray("items") ?: JSONArray()

            return TicketRequest(
                storeName = json.optString("storeName", "POSTicket"),
                storeAddress = json.optString("storeAddress", ""),
                storePhone = json.optString("storePhone", ""),
                ticketNumber = json.optString("ticketNumber", "TCK-001"),
                date = json.optString("date", ""),
                total = json.optString("total", "0"),
                headerLines = jsonArrayToList(json.optJSONArray("headerLines")),
                footerLines = jsonArrayToList(json.optJSONArray("footerLines")),
                items = itemArrayToList(itemArray),
                qrData = json.optString("qrData", "")
            )
        }

        private fun jsonArrayToList(array: JSONArray?): List<String> {
            if (array == null) return emptyList()
            return List(array.length()) { index -> array.optString(index) }
        }

        private fun itemArrayToList(array: JSONArray): List<TicketItem> {
            return List(array.length()) { index ->
                val item = array.optJSONObject(index) ?: JSONObject()
                TicketItem(
                    label = item.optString("label", "Article"),
                    quantity = item.optInt("quantity", 1),
                    unitPrice = item.optString("unitPrice", "0"),
                    totalPrice = item.optString("totalPrice", "0")
                )
            }
        }
    }
}
