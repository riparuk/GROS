package com.bukitvista.gros.sharedpreferences

import android.content.Context

object SharedPreferencesManager {
    private const val PREF_NAME = "UserAppPreferences"
    private const val KEY_STAFF_ID = "staff_id"
    private const val PROPERTY_ID = "property_id"

    fun saveStaffId(context: Context, staffId: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_STAFF_ID, staffId).apply()
    }

    fun getStaffId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_STAFF_ID, null)
    }

    fun clearStaffId(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_STAFF_ID).apply()
    }

    fun savePropertyId(context: Context, propertyId: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(PROPERTY_ID, propertyId).apply()
    }

    fun getPropertyId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(PROPERTY_ID, "") ?: ""
    }
}