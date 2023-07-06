package com.example.todoapp.data.util

import com.example.todoapp.data.db.room.TaskEntity
import com.example.todoapp.data.network.request_response_data.responce.Task
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import java.util.Locale
import java.util.UUID

fun TaskEntity.toModel(): TaskModel = TaskModel(
    UUID.fromString(id), text, importance, isDone, creationTime, deadline, modifyingTime
)

fun TaskModel.toEntity(): TaskEntity = TaskEntity(
    id.toString(), text, priority, isDone, creationTime, deadline, modifyingTime
)

fun TaskModel.toDto(): Task = Task(
    id,
    text,
    priority.toString(),
    deadline,
    isDone,
    null,
    creationTime,
    modifyingTime ?: 0,
    "Mick-Android"
)

fun Task.toModel(): TaskModel = TaskModel(
    id,
    text,
    Importance.valueOf(importance.uppercase(Locale.ROOT)),
    done,
    createdAt,
    deadline,
    changedAt
)