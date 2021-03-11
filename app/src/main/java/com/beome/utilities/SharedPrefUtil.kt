package com.beome.utilities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPrefUtil {
    private var sharedPref: SharedPreferences? = null

    fun start(activity: Activity, PREFS: String) {
        sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun destroy() { this.sharedPref = null }

    fun <T> set(PREFS: String, value: T) {
        this.sharedPref?.let {
            with(it.edit()) {
                putString(PREFS, value.toString())
                apply()
            }
        }
    }

    fun clear() {
        this.sharedPref?.edit()?.clear()?.apply()
    }

    fun get(PREFS: String): String? {
        if (sharedPref != null) return sharedPref!!.getString(PREFS, null)
        return null
    }
}