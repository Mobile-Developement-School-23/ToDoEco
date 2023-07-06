package com.example.todoapp.domain

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class TaskModel(
    val id: UUID,
    var text: String,
    var priority: Importance,
    var isDone: Boolean,
    val creationTime: Long,
    var deadline: Long? = null,
    var modifyingTime: Long? = null
) : Parcelable {

    constructor(id: UUID, creationTime: Long) : this(
        id, "", Importance.BASIC, false, creationTime
    )

    constructor(id: UUID) : this(
        id, "", Importance.BASIC, false, System.currentTimeMillis()
    )

    private constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()),
        parcel.readString() ?: "",
        Importance.values()[parcel.readInt()],
        parcel.readInt() != 0,
        parcel.readLong(),
        parcel.readLong().let { if (it == -1L) null else it },
        parcel.readLong().let { if (it == -1L) null else it }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id.toString())
        parcel.writeString(text)
        parcel.writeInt(priority.ordinal)
        parcel.writeInt(if (isDone) 1 else 0)
        parcel.writeLong(creationTime)
        parcel.writeLong(deadline ?: -1L)
        parcel.writeLong(modifyingTime ?: -1L)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskModel> {
        override fun createFromParcel(parcel: Parcel): TaskModel {
            return TaskModel(parcel)
        }

        override fun newArray(size: Int): Array<TaskModel?> {
            return arrayOfNulls(size)
        }
    }
}

