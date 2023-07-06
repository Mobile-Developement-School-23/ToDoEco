package com.example.todoapp.data.db

sealed class RoomState <out T> {
    object Initial : RoomState<Nothing>()
    data class Success<T>(val data: T, val revision: Int) : RoomState<T>()
    data class Failure(val err: Throwable): RoomState<Nothing>()
}