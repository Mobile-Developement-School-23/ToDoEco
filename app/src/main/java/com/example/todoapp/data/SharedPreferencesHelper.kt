package com.example.todoapp.data

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    companion object {

        private const val PREF_NAME = "MySharedPreferences"
        private const val DEFAULT_INT_VALUE = 0
        private const val DEFAULT_STRING_VALUE = ""
        private const val DEFAULT_BOOLEAN_VALUE = false

    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun addInt(key: String, value: Int) {

        editor.putInt(key, value)
        editor.apply()

    }

    fun getInt(key: String): Int {

        return sharedPreferences.getInt(key, DEFAULT_INT_VALUE)

    }

    fun addString(key: String, value: String) {

        editor.putString(key, value)
        editor.apply()

    }

    fun getString(key: String): String {

        return sharedPreferences.getString(key, DEFAULT_STRING_VALUE) ?: DEFAULT_STRING_VALUE

    }

    fun addBoolean(key: String, value: Boolean) {

        editor.putBoolean(key, value)
        editor.apply()

    }

    fun getBoolean(key: String): Boolean {

        return sharedPreferences.getBoolean(key, DEFAULT_BOOLEAN_VALUE)

    }

}