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

//Handler goes inside the execute, well nobody told me that.
fun getJsonFromAPI(location: String, weather: MainActivity) {
    val executor: Executor = Executors.newSingleThreadExecutor()

    executor.execute { //This be your doInBackground
        var result = StringBuilder()
        val handler = Handler(Looper.getMainLooper())
        var error: Exception? = null
        val client_id = ""
        val api_url = "https://api.weatherapi.com/v1/"
        val weak_weather = WeakReference<MainActivity>(weather).get()

        try {
            val url = URL(api_url + "forecast.json?key=" + client_id + "&q=" + location + "&days=5")
            val conn = url.openConnection() as HttpURLConnection
            conn.connect()

            val input = conn.inputStream
            val reader = input.bufferedReader()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }
        }
        catch (e: Exception){
            error = e
        }

        handler.postDelayed( { //This be your onPostExecute, which used to be a seperate task            

            if (error != null){
                weak_weather?.feed_failure(error)
            }

            try {
                if (result.isNotEmpty()) {
                     val data = JSONObject(result.toString())
                    //The feed does not have a single root but forecast, current and location.
                    //And we don't need location.
                    Current().populate(data.optJSONObject("current")!!)
                    Forecast().populate(data.getJSONObject("forecast"))
                    weak_weather?.feed_success()
                }
            }
            catch (e: Exception) {
                weak_weather?.feed_failure(e)
            }
        }, 0L)
    }
}







