package com.example.todoapp.ui.util

interface OnItemListener {

    fun onItemClick(position: Int)

    fun onCheckBoxClicked(position: Int, isChecked: Boolean)

    fun longClickPrepare(position: Int)

}