package com.example.todoapp.data.network.request_response_data.responce

import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("list") val list: List<Task>,
    @SerializedName("revision") val revision: Int
)