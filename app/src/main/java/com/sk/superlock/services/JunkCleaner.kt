package com.sk.superlock.services

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast

@Suppress("DEPRECATION")
class JunkCleaner(private val context: Context): AsyncTask<Void, Void, Int>() {

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Int {
        // get the list of cached files.
        val cachedFiles = context.externalCacheDir?.listFiles()

        // iterate over the list of cached files and delete them.
        for (file in cachedFiles!!) {
            if (file.isFile) {
                file.delete()
            }
        }

        return cachedFiles.size
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: Int) {
        Toast.makeText(context, "Deleted $result files", Toast.LENGTH_LONG).show()
        Log.d("JunkCleaner", "Deleted $result files.")
    }
}