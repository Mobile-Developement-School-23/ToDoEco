package com.example.todoapp.api.request_response_data

import com.google.gson.annotations.SerializedName

data class TaskListRequest(
    @SerializedName("status") val status : String,
    @SerializedName("list") val list : List<ToDoItemResponse>)