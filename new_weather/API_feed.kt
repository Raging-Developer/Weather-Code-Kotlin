package com.app.new_weather

import android.os.Handler
import android.os.Looper
import com.app.new_weather.data.Current
import com.app.new_weather.data.Forecast
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

//Handler goes inside the execute, well nobody mentioned that.
fun getJsonFromAPI(location: String, callback: ApiResponse) {
    val executor: Executor = Executors.newSingleThreadExecutor()
    val weak_weather = WeakReference(callback)

    executor.execute { //This be your doInBackground
        var result = StringBuilder()
        val handler = Handler(Looper.getMainLooper())
        var error: Exception? = null
        val client_id = "391a795e9a804ff4be3235120211006"
        val api_url = "https://api.weatherapi.com/v1/"

        try {
            val url = URL(api_url + "forecast.json?key=" + client_id + "&q=" + location + "&days=5")
            val conn = url.openConnection() as HttpURLConnection
            conn.connect()

            val input = conn.inputStream
//            val reader = BufferedReader(InputStreamReader(input))
            val reader = input.bufferedReader()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }
        }
        catch (e: Exception){
            error = e
        }

        handler.postDelayed( { //This be your onPostExecute
            handleApiResponse(result, error, weak_weather.get())
        }, 10L)
    }
}

// New internal function to handle the API response logic
internal fun handleApiResponse(
    result: StringBuilder,
    error: Exception?,
    weak_ref: ApiResponse?
) {
    if (weak_ref == null) return

    if (error != null) {
        weak_ref.feed_failure(error)
        return
    }

    if (result.isEmpty()) {
        weak_ref.feed_failure(Exception("Empty response from server"))
        return
    }

    try {
        //current is today's weather, forc is three days hence including today (?)
        val data = JSONObject(result.toString())
        //The feed does not have a single root but forecast, current and location.
        //And we don't need location.
        Current().populate(data.optJSONObject("current"))
        Forecast().populate(data.getJSONObject("forecast"))
        weak_ref.feed_success()
    } catch (e: Exception) { // Handles JSON parsing or data population errors
        weak_ref.feed_failure(e)
    }
}
