package org.neshan.kotlinsample.task

import android.os.AsyncTask
import org.json.JSONObject
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class DownloadTask(// hold reference of activity to emit messages
    private val callback: Callback) : AsyncTask<String?, Void?, JSONObject?>() {

    override fun doInBackground(vararg p0: String?): JSONObject? {
        val response = downloadRawFile(p0[0])
        try {
            // convert raw to JSON object
            return JSONObject(response)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(jsonObject: JSONObject?) {
        super.onPostExecute(jsonObject)
        callback.onJsonDownloaded(jsonObject)
    }

    // download file from link (in this sample link is https://api.neshan.org/points.geojson)
    private fun downloadRawFile(link: String?): String? {
        val response = StringBuilder()
        try {
            //Prepare the URL and the connection
            val u = URL(link)
            val conn = u.openConnection() as HttpURLConnection
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                //Get the Stream reader ready
                val input = BufferedReader(InputStreamReader(conn.inputStream), 8192)
                //Loop through the return data and copy it over to the response object to be processed
                var line: String?
                while (input.readLine().also { line = it } != null) {
                    response.append(line)
                }
                input.close()
            }
            return response.toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    interface Callback {
        fun onJsonDownloaded(jsonObject: JSONObject?)
    }
}