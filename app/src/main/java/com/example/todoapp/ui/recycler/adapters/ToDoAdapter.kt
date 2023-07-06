package com.example.todoapp.ui.recycler.adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ToDoLayoutBinding
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.ui.recycler.MyDiffUtil
import java.text.SimpleDateFormat
import java.util.*

interface ToDoActionListener {


    fun onToDoItemDelete(todoItem: TaskModel)

    fun onEditTask(todoItem: TaskModel)

    fun onCheckTask(todoItem: TaskModel, isChecked: Boolean)

    fun onLongClick(todoItem: TaskModel)

    fun onToDoItemCopy(todoItem: TaskModel)

    fun onToDoItemInfo(todoItem: TaskModel)

}

class ToDoAdapter( private val actionListener: ToDoActionListener) :
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>(), View.OnClickListener {

    var toDoList: List<TaskModel> = emptyList()

        set(newValue) {

            val diffCallBack = MyDiffUtil(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallBack, true)
            field = newValue
            diffResult.dispatchUpdatesTo(this)

        }

    override fun onClick(v: View) {

        val todoItem = v.tag as TaskModel

        when(v.id) {

            R.id.hamburger -> {

                showPopUpMenu(v)

            }

            else -> { actionListener.onEditTask(todoItem) }

        }

    }

    override fun getItemCount(): Int = toDoList.size

    fun getItem(position: Int) : TaskModel = toDoList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ToDoLayoutBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        binding.hamburger.setOnClickListener(this)

        return ToDoViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {

        val format = SimpleDateFormat("yyyy-MM-dd")

        holder.binding.doOrNo.setOnCheckedChangeListener(null)

        val todoItem = toDoList[position]


        with(holder.binding) {

            holder.itemView.tag = todoItem
            hamburger.tag = todoItem

            val isChecked = todoItem.isDone

            doOrNo.isChecked = isChecked

            if (isChecked) {

                task.paintFlags = task.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                task.setTextColor(Color.GRAY)

            } else {

                task.paintFlags = task.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                task.setTextColor(Color.rgb(59, 58, 54))

            }

            doOrNo.setOnCheckedChangeListener { _, isChecked ->

                if (position != RecyclerView.NO_POSITION) {

                    actionListener.onCheckTask(todoItem, isChecked)

                }

                if (isChecked) {

                    task.paintFlags = task.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    task.setTextColor(Color.GRAY)

                } else {

                    task.paintFlags = task.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    task.setTextColor(Color.rgb(59, 58, 54))

                }

            }

            task.text = todoItem.text

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            if (todoItem.deadline != null) {

                deadline.visibility = View.VISIBLE

                val timestamp: Long = todoItem!!.deadline!!

                val date = Date(timestamp)

                val dateString = format.format(date)

                deadline.text = dateString

            } else {

                deadline.visibility = View.INVISIBLE

            }

            val importanceIcon = when (todoItem.priority) {

                Importance.LOW -> R.drawable.slow
                Importance.BASIC -> R.drawable.normally
                Importance.IMPORTANT -> R.drawable.urgently

            }

            importance.setImageResource(importanceIcon)

        }


    }


    private fun showPopUpMenu(view: View) {

        val popupMenu = PopupMenu(view.context, view)
        val context = view.context

        val todoItem = view.tag as TaskModel

        popupMenu.menu.add(0, ID_COPY, Menu.NONE, context.getString(R.string.copy))

        popupMenu.menu.add(0, ID_INFO, Menu.NONE, context.getString(R.string.info))

        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {

                ID_COPY -> {

                    actionListener.onToDoItemCopy(todoItem)

                }
                ID_INFO -> {

                    actionListener.onToDoItemInfo(todoItem)

                }
                ID_REMOVE -> {

                    actionListener.onToDoItemDelete(todoItem)

                }

            }

            return@setOnMenuItemClickListener true
        }

        popupMenu.show()

    }


    companion object {

        private const val ID_COPY = 1
        private const val ID_INFO = 2
        private const val ID_REMOVE = 3

    }

     class ToDoViewHolder( val binding : ToDoLayoutBinding
     ) : RecyclerView.ViewHolder(binding.root)

}