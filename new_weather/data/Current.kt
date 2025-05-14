package com.app.new_weather.data

import org.json.JSONObject

class Current {
    companion object{
        var condition: Condition? = Condition()
        var last_updated_epoch: Long = 0
        var last_updated: String? = null
        var is_day: String? = null
        var temp_c: String = ""
        var wind_mph: String = ""
        var wind_dir: String = ""
        var pressure_mb: String? = null
        var precip_in: String? = null
        var humidty: String? = null
        var feelslike_c: String = ""
        var vis_miles: String? = null
        var UV: String? = null
        var gust_mph: String? = null
    }

    @Throws(Exception::class)
    fun populate(data: JSONObject) {
        condition!!.populate(data.getJSONObject("condition"))
        last_updated_epoch = data.getLong("last_updated_epoch")
        last_updated = data.optString("last_updated")
        is_day = data.optString("is_day")
        temp_c = data.optString("temp_c")
        wind_mph = data.optString("wind_mph")
        wind_dir = data.optString("wind_dir")
        pressure_mb = data.optString("pressure_mb")
        precip_in = data.optString("precip_in")
        humidty = data.optString("humidity")
        feelslike_c = data.optString("feelslike_c")
        vis_miles = data.optString("vis_miles")
        UV = data.optString("uv")
        gust_mph = data.optString("gust_mph")
    }
}
