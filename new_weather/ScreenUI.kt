package com.app.new_weather

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.new_weather.LatLong.Companion.astro
import com.app.new_weather.LatLong.Companion.chill
import com.app.new_weather.LatLong.Companion.cond_text
import com.app.new_weather.LatLong.Companion.forecast_day
import com.app.new_weather.LatLong.Companion.place
import com.app.new_weather.LatLong.Companion.temp
import com.app.new_weather.LatLong.Companion.wind_dir
import com.app.new_weather.LatLong.Companion.wind_mph
import com.app.new_weather.ui.theme.New_WeatherTheme
import com.example.new_weather.R
import java.text.SimpleDateFormat

@Composable
fun BackGroundImage(context: MainActivity, modifier: Modifier = Modifier) {
    val backImage = painterResource(id = R.drawable.night_time)

    Box(
        contentAlignment = Alignment.TopCenter)
    {
            Image(
                painter = backImage,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        WeatherLayout(context)
    }
}

@Composable
fun WeatherLayout(context: MainActivity, modifier: Modifier = Modifier) {
    var condIcon = LatLong.icon
    var condString = "15"

    if (LatLong.icon == ""){
        condIcon = "na"
    }
    else {
        condString = condIcon.substring(condIcon.length - 7)
        condIcon = condString.substring(0,3)
    }

    val iconId = context
        .resources
        .getIdentifier("icon_$condIcon", "drawable", context.packageName)
    val iconImage = painterResource(id = iconId)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier)
    {
        Image(
            painter = iconImage,
            contentDescription = null,
            contentScale = ContentScale.Fit)

        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally)
        {
            item { Temp_today() }
            item { Condition_text() }
            item { Location_text() }
            item { Fore_cast() }
            item { SunRiseSet() }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {}
        ApiLinkButton {}
    }
}

@Composable
fun Location_text(modifier: Modifier = Modifier) {
    val loc = "Which is not bad for $place"
    Text(
        text = loc,
        fontSize = 20.sp,
        lineHeight = 21.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(0.dp),
        color = Color.Gray
    )
}

@Composable
fun Condition_text(modifier: Modifier = Modifier) {
    val condition = "The weather today $cond_text"
    Text(
        text = condition,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        modifier = modifier.padding(0.dp),
        color = Color.Gray
    )
}

@Composable
fun Temp_today(modifier: Modifier = Modifier) {

    Box {
        Row(
            modifier = Modifier
        )
        {
            Text(
                text = "$temp\u00B0",
                fontSize = 20.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(0.dp),
                color = Color.Gray
            )
            Chill_factor()
        }
    }
}

@Composable
fun Chill_factor(modifier: Modifier = Modifier) {
    val chill = "(Which feels like $chill\u00B0 \nin as $wind_mph mph wind\ncoming from $wind_dir)"
    Text(
        text = chill,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .padding(start = 10.dp),
        color = Color.Gray
    )
}

@Composable
fun Tomorrow(tomoz_text: String,
             tomoz_day: String,
             tomoz_date:String,
             modifier: Modifier = Modifier){

    val tomorrow = "Tomorrow $tomoz_day $tomoz_date will be\n $tomoz_text"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = tomorrow,
            fontSize = 20.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(10.dp),
            color = Color.Gray
        )
    }
}

@Composable
fun Fore_cast() {
    var tomoz_day = ""
    var tomoz_text = ""
    var tomoz_date = ""
    var fore_array = mutableListOf<String>()

    for (i in 0 until forecast_day.length()) {
        val o = forecast_day.getJSONObject(i)
        val oday = SimpleDateFormat("EEEE").format(o.getString("date_epoch").toLong() * 1000)
        val odate = o.getString("date")
        val otext = o.getJSONObject("day").getJSONObject("condition").getString("text")

        if (i == 1) {
            tomoz_text = otext
            tomoz_day = oday
            tomoz_date = odate
        }

        fore_array.add("$oday $odate $otext")
    }

    Box {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Tomorrow(tomoz_text, tomoz_day, tomoz_date)
            LazyColumn (horizontalAlignment = Alignment.CenterHorizontally){
                for (day in fore_array) {
                    item {
                        Text(
                            text = day,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SunRiseSet(modifier: Modifier = Modifier) {
    val sunrise = astro?.getSunrise()
    val sunset = astro?.getSunset()

    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "Sunrise is at $sunrise",
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = modifier.padding(top = 10.dp),
        )
        Text(
            text = "and sunset is at $sunset",
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ApiLinkButton(onClick: () -> Unit) {
    val context = LocalContext.current
    val icon = painterResource(id = R.drawable.weatherapi_logo)
    val i = Intent()
    i.setAction(Intent.ACTION_VIEW)
    i.addCategory(Intent.CATEGORY_BROWSABLE)
    i.setData(Uri.parse("https://www.weatherapi.com/"))

    TextButton(
        onClick = { context.startActivity(i) },
        content = {
            Image(
                painter = icon,
                contentDescription = null)
        })
}

@Preview(showBackground = true)
@Composable
fun Weather_Preview() {
    New_WeatherTheme {
        BackGroundImage(context = MainActivity())
    }
}



