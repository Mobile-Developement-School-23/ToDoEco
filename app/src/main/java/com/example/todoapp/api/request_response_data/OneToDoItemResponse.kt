package com.example.todoapp.api.request_response_data

import com.google.gson.annotations.SerializedName

data class OneToDoItemResponse (@SerializedName("status") val status : String,
                                @SerializedName("element") val element : ToDoItemResponse,
                                @SerializedName("revision")val revision: Int)
