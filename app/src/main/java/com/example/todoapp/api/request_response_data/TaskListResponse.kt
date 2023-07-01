package com.example.todoapp.api.request_response_data

import com.google.gson.annotations.SerializedName

data class TaskListResponse (@SerializedName("status") val status : String,
                             @SerializedName("list") val list : List<ToDoItemResponse>,
                             @SerializedName("revision")val revision: Int)