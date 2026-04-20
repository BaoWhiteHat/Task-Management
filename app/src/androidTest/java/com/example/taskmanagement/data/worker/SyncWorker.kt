import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.di.Graph

class SyncWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val taskDao by lazy { AppDatabase.getDatabase(appContext).taskDao() }
    private val apiService by lazy { Graph.apiService }
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notificationId = 1337

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}