package com.example.taskmanagement.data.remote

import com.example.taskmanagement.data.remote.models.TaskDto
import com.example.taskmanagement.data.remote.models.TaskDtoItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApiService {

    @GET("tasks")
    suspend fun getTasks(): TaskDto

    @POST("tasks")
    suspend fun createTask(
        @Body task: TaskDtoItem
    ): TaskDtoItem

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Int,
        @Body task: TaskDtoItem
    ): TaskDtoItem

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: Int
    ): Response<Unit>
}