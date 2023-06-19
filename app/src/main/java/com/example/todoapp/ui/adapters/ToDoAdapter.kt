package com.example.todoapp.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.R
import com.example.todoapp.ui.util.OnItemListener
import com.example.todoapp.ui.util.MyDiffUtil
import java.text.SimpleDateFormat
import java.util.*

class ToDoAdapter(private val context: Context, public var todoList: List<ToDoItem>, private  val listener: OnItemListener) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.to_do_layout, parent, false)

//        view.setOnLongClickListener {
//
//            v -> v.showContextMenu()
//            true
//        }

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.completionStatus.setOnCheckedChangeListener(null)

        val todoItem = todoList[position]
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }

        holder.bind(todoItem)

    }


    override fun getItemCount(): Int {
        return todoList.size
    }

    fun getItem(position: Int) : ToDoItem {
        return todoList[position]
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val layout : View = itemView.findViewById(R.id.positionLayout)
        private val todoText: TextView = itemView.findViewById(R.id.task)
        private val deadline: TextView = itemView.findViewById(R.id.deadline)
        private val importanceIndicator: ImageView = itemView.findViewById(R.id.importance)
         val completionStatus: CheckBox = itemView.findViewById(R.id.doOrNo)


        fun bind(todoItem: ToDoItem) {

            val isChecked = todoItem.isDone

            completionStatus.isChecked = todoItem.isDone

            if (isChecked) {

                todoText.paintFlags = todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                todoText.setTextColor(Color.GRAY)

            } else {

                todoText.paintFlags = todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                todoText.setTextColor(Color.rgb(59, 58, 54))

            }

            completionStatus.setOnCheckedChangeListener { _, isChecked ->

                val position = absoluteAdapterPosition

                if (position != RecyclerView.NO_POSITION) {

                    listener.onCheckBoxClicked(position, isChecked)

                }

                if (isChecked) {

                    todoText.paintFlags = todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    todoText.setTextColor(Color.GRAY)

                } else {

                    todoText.paintFlags = todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    todoText.setTextColor(Color.rgb(59, 58, 54))

                }

            }


            todoText.text = todoItem.text

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            if (todoItem.deadline != null) {

                deadline.visibility = View.VISIBLE
                deadline.text = dateFormat.format(todoItem.deadline)

            } else {

                deadline.visibility = View.INVISIBLE

            }

            val importanceIcon = when (todoItem.importance) {

                ToDoItem.Importance.LOW -> R.drawable.slow
                ToDoItem.Importance.NORMAL -> R.drawable.normally
                ToDoItem.Importance.URGENT -> R.drawable.urgently

            }

            importanceIndicator.setImageResource(importanceIcon)

            val position = absoluteAdapterPosition

            layout.setOnLongClickListener {

                    it.showContextMenu()
                listener.longClickPrepare(position)

                true

            }

        }

    }

    fun setData(newToDoList : List<ToDoItem>) {

        val diffUtil = MyDiffUtil(oldList = todoList, newList = newToDoList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        todoList = newToDoList
        diffResults.dispatchUpdatesTo(this)

    }

}