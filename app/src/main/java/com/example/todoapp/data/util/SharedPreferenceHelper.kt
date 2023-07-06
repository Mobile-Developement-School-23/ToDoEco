package com.example.todoapp.data.util

import android.content.Context

class SharedPreferenceHelper(
    val context: Context
) {
    private val preferences = context.getSharedPreferences("ToDoPref", 0)

    fun getIntValue(): Int = preferences.getInt("REVISION", 0)

    fun setIntValue(revision: Int) {
        preferences.edit().putInt("REVISION", revision).apply()
    }

    fun updateRevision() {
        /*
        * Поскольку на бэке каждая итерация делает +1 не совсем ясно, что было раньше.
        * Если мы начинаем изменять в оффлайне, патч полностью изменит все последующие данные на сервере,
        * не синхронизировав их.
        * */
        /*val current = getRevision()
        preferences.edit().putInt("REVISION", 0).apply()
        Log.d("REVISION-CHECK-UPDATE", getRevision().toString())*/
    }
}