package com.example.todoapp.api.request_response_data

import com.example.todoapp.db.ToDoItemEntity
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.UUID

data class ToDoItemResponse (

    @SerializedName("id")
    var id: UUID,

    @SerializedName("text")
    var text: String,

    @SerializedName("importance")
    var importance: Importance,

    @SerializedName("deadline")
    var deadline: Long? = null,

    @SerializedName("done")
    var done: Boolean,

    @SerializedName("color")
    var color: String,

    @SerializedName("created_at")
    val created_at: Long,

    @SerializedName("changed_at")
    var changed_at: Long? = null,

    @SerializedName("last_updated_by")
    var last_updated_by: String?

) : Serializable {

    enum class Importance {

        low, basic, important

    }

}

fun List<ToDoItemResponse>.toEntityList(): List<ToDoItemEntity> {
    return map { response ->
        ToDoItemEntity(
            id = response.id.toString(),
            text = response.text,
            importance = response.importance,
            dateDeadline = response.deadline,
            isComplete = response.done,
            color = response.color,
            dateCreation = response.created_at,
            dateChanging = response.changed_at
        )
    }
}