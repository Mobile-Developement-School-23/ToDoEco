package com.example.todoapp.api

import com.example.todoapp.api.request_response_data.OneToDoItemResponse
import com.example.todoapp.api.request_response_data.TaskListRequest
import com.example.todoapp.api.request_response_data.TaskListResponse
import com.example.todoapp.api.request_response_data.ToDoItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID


interface TaskAPI {

    @GET("list")
    @Headers("Authorization: Bearer leuchemia")
    suspend fun getList(): Response<TaskListResponse>

    @PATCH("list")
    @Headers("Authorization: Bearer leuchemia")
    suspend fun updateList(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body taskListRequest: TaskListRequest
    ): Response<TaskListResponse>

    @GET("list/{id}")
    @Headers("Authorization: Bearer leuchemia")
    suspend fun getListItemById(@Path("id") itemId: UUID): Response<OneToDoItemResponse>

    @POST("list")
    @Headers("Authorization: Bearer leuchemia")
    suspend fun addItemToList(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body taskRequest: ToDoItemRequest
    ): Response<OneToDoItemResponse>

    @PUT("list/{id}")
    @Headers("Authorization: Bearer leuchemia")
    suspend fun changeListItem(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Path("id") itemId: UUID,
        @Body taskRequest: ToDoItemRequest
    ): Response<OneToDoItemResponse>

    @DELETE("list/{id}")
    @Headers("Authorization: Bearer leuchemia")
    suspend fun deleteListItem(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Path("id") itemId: UUID,): Response<OneToDoItemResponse>

}