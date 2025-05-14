package com.app.new_weather.data

import org.json.JSONObject

class Condition {
    companion object{
        var condText: String? = null
        var condIcon: String? = null
        var condCode: String? = null
    }

    fun populate(data: JSONObject) {
        condText = data.optString("text")
        condIcon = data.optString("icon")
        condCode = data.optString("code")
    }
}
