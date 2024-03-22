package com.app.new_weather.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Forecast {
    private var forecast_day: JSONArray = JSONArray()
    private var astro: Astronomy? = null
    private var date: String? = null

    fun getForecast_day(): JSONArray {
        return forecast_day
    }

    fun getAstro(): Astronomy? {
        return astro
    }

    @Throws(JSONException::class)
    fun populate(data: JSONObject) {
        forecast_day = data.getJSONArray("forecastday")
        date = forecast_day?.getJSONObject(1)?.getString("date")
        astro = Astronomy()
        astro!!.populate(forecast_day!!.getJSONObject(0).optJSONObject("astro"))
    }
}
