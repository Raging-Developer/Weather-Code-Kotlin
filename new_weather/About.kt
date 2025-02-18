package com.app.new_weather

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class About : Activity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aboutlayout)

        val about_text: TextView = findViewById(R.id.about_text)
        val b: Bundle? = intent.extras
        val title = b?.getString("title")
        val body = b?.getString("body")

        setTitle(title)
        about_text.text = body
    }
}