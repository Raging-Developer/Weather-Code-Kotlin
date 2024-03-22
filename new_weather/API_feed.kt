package com.app.new_weather

import android.os.Handler
import android.os.Looper
import com.app.new_weather.data.Current
import com.app.new_weather.data.Forecast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun getJsonFromAPI(location: String, weather: MainActivity) {
    val executor: Executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var result: StringBuilder = StringBuilder()
    var error: Exception? = null
    val client_id = ""
    val api_url = "https://api.weatherapi.com/v1/"
    var weak_ref: WeakReference<MainActivity> = WeakReference<MainActivity>(weather)

    executor.execute { //This be your doInBackground
        try {
            val url = URL(api_url + "forecast.json?key=" + client_id + "&q=" + location + "&days=5")
            val conn = url.openConnection() as HttpURLConnection
            conn.connect()

            val input = conn.inputStream
            val reader = BufferedReader(InputStreamReader(input))
            var line: String? = ""

            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }
        }
        catch (e: Exception){
            error = e
        }
    }

    handler.post { //This be your onPostExecute
        val weak_weather: MainActivity? = weak_ref.get()

        if (error != null){
            weak_weather?.feed_failure(error)
        }

        try {
            val data = JSONObject(result.toString())
            //The feed does not have a single root but forecast, current and location.
            //And we don't need location.
            val current = Current()
            current.populate(data.optJSONObject("current"))
            val forc = Forecast()
            forc.populate(data.getJSONObject("forecast"))

            //current is today's weather, forc is three days hence including today (?)
            weak_weather?.feed_success(current, forc)
        }
        catch (e: Exception) {
            weak_weather?.feed_failure(e)
        }
    }
}







