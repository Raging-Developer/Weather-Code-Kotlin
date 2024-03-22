package com.app.new_weather.data

import org.json.JSONObject

class FeedLocation {
    private var name: String? = null
    private var region: String? = null
    private var country: String? = null
    private var lat: String? = null
    private var lon: String? = null
    private var localtime: String? = null

    fun getName(): String? {
        return name
    }
    fun getRegion(): String? {
        return region
    }
    fun getCountry(): String? {
        return country
    }
    fun getLat(): String? {
        return lat
    }
    fun getLon(): String? {
        return lon
    }

    fun getLocaltime(): String? {
        return localtime
    }
    @Throws(Exception::class)
    fun populate(data: JSONObject) {
        name = data.optString("name")
        region = data.optString("region")
        country = data.optString("country")
        lat = data.optString("lat")
        lon = data.optString("lon")
        localtime = data.optString("localtime")
    }
}
