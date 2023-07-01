package com.example.todoapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.api.request_response_data.ToDoItemRequest
import com.example.todoapp.api.request_response_data.ToDoItemResponse
import java.io.Serializable
import java.util.UUID
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "todo_items")
data class ToDoItemEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : String,

    @ColumnInfo(name = "text")
    var text : String,

    @ColumnInfo(name = "importance")
    var importance : ToDoItemResponse.Importance,

    @ColumnInfo(name = "deadline")
    var dateDeadline : Long?,

    @ColumnInfo(name = "complete")
    var isComplete : Boolean,

    @ColumnInfo(name = "color")
    var color : String,

    @ColumnInfo(name = "creation")
    var dateCreation : Long,

    @ColumnInfo(name = "changing")
    var dateChanging : Long?

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readSerializable() as ToDoItemResponse.Importance,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(text)
        parcel.writeSerializable(importance)
        parcel.writeValue(dateDeadline)
        parcel.writeByte(if (isComplete) 1 else 0)
        parcel.writeString(color)
        parcel.writeLong(dateCreation)
        parcel.writeValue(dateChanging)
    }

    companion object CREATOR : Parcelable.Creator<ToDoItemEntity> {
        override fun createFromParcel(parcel: Parcel): ToDoItemEntity {
            return ToDoItemEntity(parcel)
        }

        override fun newArray(size: Int): Array<ToDoItemEntity?> {
            return arrayOfNulls(size)
        }
    }

    fun toRequest() = ToDoItemRequest("ok", ToDoItemResponse(
        UUID.fromString(this.id),
        this.text, this.importance, this.dateDeadline, this.isComplete, this.color,
        this.dateCreation, this.dateChanging, "TEST"))
}

fun List<ToDoItemEntity>.toResponseList(): List<ToDoItemResponse> {
    return map { entity ->
        ToDoItemResponse(
            id = UUID.fromString(entity.id),
            text = entity.text,
            importance = entity.importance,
            deadline = entity.dateDeadline,
            done = entity.isComplete,
            color = entity.color,
            created_at = entity.dateCreation,
            changed_at = entity.dateChanging,
            last_updated_by = "TEST"
        )
    }
}

