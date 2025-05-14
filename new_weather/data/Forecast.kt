package com.app.new_weather.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Forecast {
    companion object{
        var forecast_day: JSONArray = JSONArray()
        var astro: Astronomy? = Astronomy()
        var date: String? = null
    }

    @Throws(JSONException::class)
    fun populate(data: JSONObject) {
        forecast_day = data.getJSONArray("forecastday")
        date = forecast_day?.getJSONObject(1)?.getString("date")
        astro!!.populate(forecast_day!!.getJSONObject(0).optJSONObject("astro")!!)
    }
}
