package com.example.todoapp.data.network.request_response_data.responce

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("status") val status: String,
    @SerializedName("element") val element: Task,
    @SerializedName("revision") val revision: Int
)
