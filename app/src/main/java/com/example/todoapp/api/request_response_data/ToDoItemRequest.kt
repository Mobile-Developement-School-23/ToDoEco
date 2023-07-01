package com.example.todoapp.api.request_response_data

import com.google.gson.annotations.SerializedName

class ToDoItemRequest(
    @SerializedName("status") val status : String,
    @SerializedName("element") val element : ToDoItemResponse
)