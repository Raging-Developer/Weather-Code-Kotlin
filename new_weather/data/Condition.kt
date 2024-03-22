package com.app.new_weather.data

import org.json.JSONObject

class Condition {
    private var text: String? = null
    private var icon: String? = null
    private var code: String? = null

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
