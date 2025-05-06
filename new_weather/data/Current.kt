package com.app.new_weather.data

import org.json.JSONObject

class Current {
    internal var condition: Condition? = null
    internal var last_updated_epoch: Long = 0
    internal var last_updated: String? = null
    internal var is_day: String? = null
    internal var temp_c: String = ""
    internal var wind_mph: String = ""
    internal var wind_dir: String = ""
    internal var pressure_mb: String? = null
    internal var precip_in: String? = null
    internal var humidty: String? = null
    internal var feelslike_c: String = ""
    internal var vis_miles: String? = null
    internal var UV: String? = null
    internal var gust_mph: String? = null

    fun getCondition(): Condition? {
        return condition
    }

    fun getLast_updated_epoch(): Long {
        return last_updated_epoch
    }
    fun getLast_updated(): String? {
        return last_updated
    }
    fun getIs_day(): String? {
        return is_day
    }
    fun getTemp_c(): String? {
        return temp_c
    }
    fun getWind_mph(): String {
        return wind_mph
    }
    fun getWind_dir(): String {
        return wind_dir
    }
    fun getPressure_mb(): String? {
        return pressure_mb
    }
    fun getPrecip_in(): String? {
        return precip_in
    }
    fun getHumidty(): String? {
        return humidty
    }
    fun getFeelslike_c(): String? {
        return feelslike_c
    }
    fun getVis_miles(): String? {
        return vis_miles
    }
    fun getUV(): String? {
        return UV
    }
    fun getGust_mph(): String? {
        return gust_mph
    }

    @Throws(Exception::class)
    fun populate(data: JSONObject) {
        condition = Condition()
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
