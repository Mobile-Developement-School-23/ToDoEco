package com.example.todoapp

interface OnItemListener {

    fun onItemClick(position: Int)

    fun onCheckBoxClicked(position: Int, isChecked: Boolean)

    fun longClickPrepare(position: Int)

}