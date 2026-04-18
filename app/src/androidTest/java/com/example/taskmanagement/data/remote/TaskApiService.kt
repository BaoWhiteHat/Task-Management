package com.example.taskmanagement.data.remote

import com.example.taskmanagement.data.remote.models.TaskDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TaskApiService {

    @GET("tasks")
    suspend fun getTasks(): TaskDto

    @POST("tasks")
    suspend fun createTask(
        @Body task: TaskDto
    ): TaskDto


}