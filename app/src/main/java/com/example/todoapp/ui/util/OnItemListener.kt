package com.example.todoapp.ui.util

import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.ui.adapters.ToDoAdapter

interface OnItemListener {

    fun onItemClick(position: Int)

    fun onCheckBoxClicked(position: Int, isChecked: Boolean)

    fun longClickPrepare(position: Int)


}