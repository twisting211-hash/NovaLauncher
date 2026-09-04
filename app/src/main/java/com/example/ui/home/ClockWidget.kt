package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockWidget(
    format24: Boolean = true,
    showSeconds: Boolean = false,
    showDate: Boolean = true,
    showWeather: Boolean = true,
    iosClockStyle: String = "ios_bold", // "ios_bold", "ios_thin", "ios_serif", "classic"
    textColor: Color = Color.White,
    onClockClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(showSeconds) {
        while (true) {
            currentTime = Date()
            val delayMs = if (showSeconds) 1000L else 10000L
            delay(delayMs)
        }
    }

    val timePattern = when {
        format24 && showSeconds -> "HH:mm:ss"
        format24 -> "HH:mm"
        showSeconds -> "hh:mm:ss"
        else -> "hh:mm"
    }

    val timeFormat = remember(timePattern) {
        SimpleDateFormat(timePattern, Locale.getDefault())
    }

    val formattedTime = timeFormat.format(currentTime)

    // Uzbek date formatting
    val dayOfWeek = when (SimpleDateFormat("u", Locale.getDefault()).format(currentTime)) {
        "1" -> "Dushanba"
        "2" -> "Seshanba"
        "3" -> "Chorshanba"
        "4" -> "Payshanba"
        "5" -> "Juma"
        "6" -> "Shanba"
        else -> "Yakshanba"
    }

    val monthName = when (SimpleDateFormat("M", Locale.getDefault()).format(currentTime)) {
        "1" -> "yanvar"
        "2" -> "fevral"
        "3" -> "mart"
        "4" -> "aprel"
        "5" -> "may"
        "6" -> "iyun"
        "7" -> "iyul"
        "8" -> "avgust"
        "9" -> "sentabr"
        "10" -> "oktabr"
        "11" -> "noyabr"
        else -> "dekabr"
    }

    val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(currentTime)
    val formattedDate = "$dayOfWeek, $dayOfMonth-$monthName"

    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.35f),
        offset = Offset(0f, 4f),
        blurRadius = 8f
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClockClick)
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        // In iOS bold style, the date is placed neatly above the time like iOS 16/17/18
        if (iosClockStyle == "ios_bold" && showDate) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.85f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = formattedDate.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = textColor.copy(alpha = 0.9f),
                        shadow = textShadow
                    )
                )
            }
        }

        // Main Clock Typography
        val clockTextStyle = when (iosClockStyle) {
            "ios_bold" -> TextStyle(
                fontSize = if (showSeconds) 58.sp else 74.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-2).sp,
                color = textColor,
                shadow = textShadow
            )
            "ios_serif" -> TextStyle(
                fontSize = if (showSeconds) 58.sp else 74.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-1).sp,
                color = textColor,
                shadow = textShadow
            )
            "ios_thin" -> TextStyle(
                fontSize = if (showSeconds) 58.sp else 74.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.sp,
                color = textColor,
                shadow = textShadow
            )
            else -> TextStyle(
                fontSize = if (showSeconds) 54.sp else 68.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-1).sp,
                color = textColor,
                shadow = textShadow
            )
        }

        Text(
            text = formattedTime,
            style = clockTextStyle
        )

        if (iosClockStyle != "ios_bold" && showDate) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.9f),
                    shadow = textShadow
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        AnimatedVisibility(visible = showWeather) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.22f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Ob-havo",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "24°C Toshkent",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
