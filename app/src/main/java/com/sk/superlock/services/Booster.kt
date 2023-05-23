package com.sk.superlock.services

import android.app.ActivityManager
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast

@Suppress("DEPRECATION")
class Booster(private val context: Context) : AsyncTask<Void, Void, Int>() {

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Int {
        // get list of running tasks
        val runningTasks = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = runningTasks.getRunningTasks(Integer.MAX_VALUE)

        // iterate over the list of running tasks and kill them
        for (task in tasks) {
            runningTasks.killBackgroundProcesses(task.baseActivity?.packageName)
        }

//        Toast.makeText(context, "${tasks.size} killed", Toast.LENGTH_LONG).show()
        Log.d("Booster", "${tasks.size} killed")
        return tasks.size
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: Int?) {
        Toast.makeText(context, "$result background tasks killed", Toast.LENGTH_LONG).show()
        Log.d("Booster", "Killed $result tasks")
    }
}