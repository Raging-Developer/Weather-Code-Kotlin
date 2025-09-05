package com.app.new_weather

import com.app.new_weather.LatLong.Companion.client_id
import com.app.new_weather.data.Current
import com.app.new_weather.data.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL



//The same thing again, but using coroutines instead of executor/handler
suspend fun getJsonFromAPI(location: String) {
    withContext(Dispatchers.IO) {
        val result = StringBuilder()
        val client_id = client_id
        val api_url = "https://api.weatherapi.com/v1/"

        try {
            val url = URL(api_url + "forecast.json?key=" + client_id + "&q=" + location + "&days=5")
            val conn = url.openConnection() as HttpURLConnection           
             conn.connectTimeout = 15000 // 15 seconds
             conn.readTimeout = 15000    // 15 seconds
            conn.connect()

            if (conn.responseCode !in 200..299) {
                // You can get more details from conn.errorStream if needed
                throw Exception("API request failed with response code: ${'$'}{conn.responseCode}")
            }

            val input = conn.inputStream
            val reader = input.bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }
            reader.close()
            input.close()
            conn.disconnect()

        } catch (e: Exception) {
            throw Exception("Network request failed: ${'$'}{e.message}", e)
        }

        if (result.isEmpty()) {
            throw Exception("Empty response from server")
        }

        try {
            val data = JSONObject(result.toString())
            // The feed does not have a single root but forecast, current and location.
            // And we don't need location.
            Current().populate(data.optJSONObject("current"))
            Forecast().populate(data.getJSONObject("forecast"))
        } catch (e: Exception) {
            throw Exception("Failed to parse weather data: ${'$'}{e.message}", e)
        }
    }
}
