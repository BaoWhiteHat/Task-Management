package com.example.taskmanagement.di

import com.example.taskmanagement.data.remote.TaskApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Graph {

    private const val BASE_URL = "https://69e373503327837a15532b15.mockapi.io/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val apiService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }



}