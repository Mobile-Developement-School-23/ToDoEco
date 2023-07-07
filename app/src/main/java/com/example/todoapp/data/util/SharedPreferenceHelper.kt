package com.example.todoapp.data.util

import android.content.Context
import javax.inject.Inject

class SharedPreferenceHelper @Inject constructor(
    val context: Context
) {
    private val preferences = context.getSharedPreferences("ToDoPref", 0)
    fun getIntValue(): Int = preferences.getInt("REVISION", 0)

    fun setIntValue(revision: Int) {
        preferences.edit().putInt("REVISION", revision).apply()
    }

    fun updateRevision() {

    }
}