package com.example.todoapp.domain

enum class Importance {
    LOW,
    BASIC,
    IMPORTANT;

    override fun toString(): String {
        return when(this) {
            LOW -> "low"
            BASIC -> "basic"
            IMPORTANT -> "important"
        }
    }
}