package com.example.todoapp.data.network.request_response_data.request

import com.example.todoapp.data.network.request_response_data.responce.Task
import com.google.gson.annotations.SerializedName

data class TaskRequest(
    @SerializedName("element") val element: Task
)
