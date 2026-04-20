package com.example.taskmanagement.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SynchWorker (
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}
