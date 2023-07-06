package com.example.todoapp.data.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.domain.Importance

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "priority") val importance: Importance,
    @ColumnInfo(name = "is_done") val isDone: Boolean,
    @ColumnInfo(name = "creation_time") val creationTime: Long,
    @ColumnInfo(name = "deadline") val deadline: Long? = null,
    @ColumnInfo(name = "modifying_time") val modifyingTime: Long? = null
)

