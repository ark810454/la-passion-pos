package com.posticket.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class LocalStoreManager(context: Context) {

    companion object {
        private const val PREFS = "la_passion_local_store"
        private const val KEY_CATALOG = "catalog_json"
        private const val KEY_STATS = "stats_json"
        private const val KEY_HISTORY = "history_json"
        private const val KEY_PENDING = "pending_json"
    }

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun loadCatalog(): JSONObject? = prefs.getString(KEY_CATALOG, null)?.let(::JSONObject)

    fun saveCatalog(value: JSONObject) {
        prefs.edit().putString(KEY_CATALOG, value.toString()).apply()
    }

    fun loadStats(): JSONObject? = prefs.getString(KEY_STATS, null)?.let(::JSONObject)

    fun saveStats(value: JSONObject) {
        prefs.edit().putString(KEY_STATS, value.toString()).apply()
    }

    fun loadHistory(): JSONArray = prefs.getString(KEY_HISTORY, null)?.let(::JSONArray) ?: JSONArray()

    fun saveHistory(value: JSONArray) {
        prefs.edit().putString(KEY_HISTORY, value.toString()).apply()
    }

    fun loadPendingOrders(): JSONArray = prefs.getString(KEY_PENDING, null)?.let(::JSONArray) ?: JSONArray()

    fun savePendingOrders(value: JSONArray) {
        prefs.edit().putString(KEY_PENDING, value.toString()).apply()
    }
}
