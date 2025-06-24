package com.app.new_weather

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.new_weather.LatLong.Companion.place
import com.app.new_weather.LatLong.Companion.post_code
import com.app.new_weather.data.Astronomy.Companion.moonphase
import com.app.new_weather.data.Astronomy.Companion.moonrise
import com.app.new_weather.data.Astronomy.Companion.moonset
import com.app.new_weather.data.Astronomy.Companion.sunrise
import com.app.new_weather.data.Astronomy.Companion.sunset
import com.app.new_weather.data.Condition.Companion.condIcon
import com.app.new_weather.data.Condition.Companion.condText
import com.app.new_weather.data.Current.Companion.feelslike_c
import com.app.new_weather.data.Current.Companion.pressure_mb
import com.app.new_weather.data.Current.Companion.temp_c
import com.app.new_weather.data.Current.Companion.wind_dir
import com.app.new_weather.data.Current.Companion.wind_mph
import com.app.new_weather.data.Forecast.Companion.forecast_day
import com.app.new_weather.ui.theme.New_WeatherTheme
import java.text.SimpleDateFormat

@Composable
fun setComposableContent(composeView: ComposeView){
      composeView.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
          BackGroundImage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackGroundImage() {
    val backImage = painterResource(R.drawable.night_time)
    var scrollState = rememberScrollState()
    var isRefreshing by remember{ mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val coScope = rememberCoroutineScope()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coScope.launch {
            delay(1000)
            isRefreshing = false
        }
    }

    Image(
        painter = backImage,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )
    
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state)
    {
        Box(Modifier
        .verticalScroll(scrollState)
        .height(800.dp),
        contentAlignment = Alignment.TopCenter)
        {
            WeatherLayout()
        }
    }
}

@Composable
fun WeatherLayout( modifier: Modifier = Modifier) {
    var lcondIcon = condIcon
    var condString: String

    if (condIcon == null) {
        lcondIcon = "na"
    }
    else {
        condString = lcondIcon!!.substring(lcondIcon.length - 7)
        lcondIcon = condString.substring(0, 3)
    }
    //This is ugly, but it avoids getIdentifier
    val iconImage = painterResource(R.drawable::class.java.getField("icon_$lcondIcon").getInt(null))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 60.dp)) //Make room for the toolbar
    {
        Image(
            painter = iconImage,
            contentDescription = null,
            modifier = Modifier.size(150.dp))
//            contentScale = ContentScale.Fit)

        //The only thing I want scrolling is fore_cast
        Temp_today()
        Condition_text()
        Location_text()
        Fore_cast()
        SunRiseSet()
        MoonPhase()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .align(Alignment.End)
        ) {}
        ApiLinkButton()
    }
}

@Composable
fun Temp_today( modifier: Modifier = Modifier) {

    Box {
        Row(
            modifier = Modifier)
        {
            Text(
                text = "$temp_c\u00B0",
                fontSize = 20.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(0.dp),
                color = Color.LightGray)
            Chill_factor()
        }
    }
}

@Composable
fun Chill_factor(modifier: Modifier = Modifier) {
    val chill =
        "(Which feels like $feelslike_c\u00B0 \nin a $wind_dir $wind_mph mph wind)"

    Text(
        text = chill,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(start = 12.dp),
        color = Color.LightGray)
}

@Composable
fun Condition_text(modifier: Modifier = Modifier) {

    Text(
        textAlign = TextAlign.Center,
        text = "The weather today is $condText at $pressure_mb mb",
        fontSize = 20.sp,
        lineHeight = 20.sp,
        modifier = modifier.padding(0.dp),
        color = Color.LightGray)
}

@Composable
fun Location_text(modifier: Modifier = Modifier) {

    Text(
        text = "Which is not bad for $place",
        fontSize = 20.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(0.dp),
        color = Color.LightGray)
}

@Composable
fun Fore_cast() {
    var tomoz_day = ""
    var tomoz_text = ""
    var fore_array = mutableListOf<String>()

    for (i in 0 until forecast_day.length()) {
        val o = forecast_day.getJSONObject(i)
        val oday = SimpleDateFormat("EEEE, d MMM").format(o.getString("date_epoch").toLong() * 1000)
        val otext = o.getJSONObject("day").getJSONObject("condition").getString("text")

        if (i == 1) {
            tomoz_text = otext
            tomoz_day = oday
        }
        fore_array.add("$oday $otext")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Tomorrow(tomoz_text, tomoz_day)
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(fore_array.count()){
                index ->
                Text(text = fore_array[index],
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Visible)
            }
        }
    }
}

@Composable
fun Tomorrow(
    tomoz_text: String,
    tomoz_day: String,
    modifier: Modifier = Modifier)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Tomorrow $tomoz_day will be\n $tomoz_text",
            fontSize = 15.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(10.dp),
            color = Color.LightGray)
    }
}

@Composable
fun SunRiseSet( modifier: Modifier = Modifier) {

    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "Sunrise is at $sunrise and sunset is at $sunset",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray,
            modifier = modifier.padding(top = 10.dp))
    }
}

@Composable
fun MoonPhase( modifier: Modifier = Modifier){

    var moonIcon = when (moonphase) {
        "First Quarter" -> "firstquarter"
        "Full Moon" -> "fullmoon"
        "Last Quarter" -> "lastquarter"
        "New Moon" -> "newmoon"
        "Waning Crescent" -> "waningcrescent"
        "Waning Gibbous" -> "waninggibbous"
        "Waxing Crescent" -> "waxingcrescent"
        "Waxing Gibbous" -> "waxinggibbous"
        else -> {
            "fullmoon"
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "The phase of the moon is $moonphase",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray,
            modifier = modifier.padding(top = 10.dp))
        Image(
            painter = painterResource(R.drawable::class.java.getField(moonIcon).getInt(null)),
            contentDescription = null,
            modifier = Modifier.size(125.dp)
        )
        Text(
            text = "With the moon rising at $moonrise " +
                    "and setting at $moonset",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray)
    }
}

@Composable
fun ApiLinkButton() {
    val context = LocalContext.current
    val icon = painterResource(R.drawable.weatherapi_logo)
    val i = Intent()

    i.setAction(Intent.ACTION_VIEW)
    i.addCategory(Intent.CATEGORY_BROWSABLE)
    i.setData(Uri.parse("https://www.weatherapi.com/weather/q/$post_code"))

    TextButton(
        onClick = { context.startActivity(i) },
        content = {
            Image(
                painter = icon,
                contentDescription = "Link",
                modifier = Modifier.size(40.dp)
                    .weight(1f))
        })
}





