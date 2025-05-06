package com.app.new_weather.data

import org.json.JSONObject

class Condition {
    internal var text: String? = null
    internal var icon: String? = null
    internal var code: String? = null

    fun getText(): String? {
        return text
    }

    fun getIcon(): String? {
        return icon
    }

    fun getCode(): String? {
        return code
    }

    fun populate(data: JSONObject) {
        text = data.optString("text")
        icon = data.optString("icon")
        code = data.optString("code")
    }
}
