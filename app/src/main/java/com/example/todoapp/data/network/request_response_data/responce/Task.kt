package com.example.todoapp.data.network.request_response_data.responce

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Task(
    @SerializedName("id") val id: UUID,
    @SerializedName("text") val text: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("deadline") val deadline: Long?,
    @SerializedName("done") val done: Boolean,
    @SerializedName("color") val color: String?,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("changed_at") val changedAt: Long,
    @SerializedName("last_updated_by") val deviceID: String
)
