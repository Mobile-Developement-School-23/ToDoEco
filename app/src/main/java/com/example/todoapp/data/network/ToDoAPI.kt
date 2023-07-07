package com.example.todoapp.data.network

import com.example.todoapp.data.network.request_response_data.request.TaskListRequest
import com.example.todoapp.data.network.request_response_data.request.TaskRequest
import com.example.todoapp.data.network.request_response_data.responce.TaskListResponse
import com.example.todoapp.data.network.request_response_data.responce.TaskResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

    interface ToDoAPI {
        @GET("list")
        suspend fun getTasks(): TaskListResponse
        @PATCH("list")
        suspend fun patchTasks(
            @Header("X-Last-Known-Revision") header: Int,
            @Body body: TaskListRequest
        ):  TaskListResponse
        @GET("list/{id}")
        suspend fun getTask(
            @Path("id") id: UUID
        ): TaskResponse
        @POST("list")
        suspend fun postTask(
            @Header("X-Last-Known-Revision") header: Int,
            @Body body: TaskRequest
        ): TaskResponse
        @PUT("list/{id}")
        suspend fun putTask(
            @Header("X-Last-Known-Revision") header: Int,
            @Path("id") id: UUID,
            @Body body: TaskRequest
        ): TaskResponse
        @DELETE("list/{id}")
        suspend fun deleteTask(
            @Header("X-Last-Known-Revision") header: Int,
            @Path("id") id: UUID
        ): TaskResponse
    }
