package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.apps.AppInfo
import com.example.ui.components.AppIconItem
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FocusModeScreen(
    allApps: List<AppInfo>,
    clockFormat24: Boolean,
    onExitFocus: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    hapticEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    val timeFormatter = remember(clockFormat24) {
        SimpleDateFormat(if (clockFormat24) "HH:mm" else "hh:mm a", Locale.getDefault())
    }

    val dateFormatter = remember {
        SimpleDateFormat("EEEE, d-MMMM", Locale.getDefault())
    }

    // Select 4 essential productivity apps (phone, messages, calendar/notes, browser)
    val essentialApps = remember(allApps) {
        val phone = allApps.find { it.packageName.contains("dialer") || it.packageName.contains("phone") }
        val msg = allApps.find { it.packageName.contains("messaging") || it.packageName.contains("mms") }
        val notesOrCal = allApps.find { it.packageName.contains("calendar") || it.packageName.contains("note") }
        val browser = allApps.find { it.packageName.contains("chrome") || it.packageName.contains("browser") }
        listOfNotNull(phone, msg, notesOrCal, browser).take(4).ifEmpty { allApps.take(4) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C))
            .statusBarsPadding()
            .testTag("focus_mode_screen")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top Badge
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = null,
                        tint = Color(0xFFFFD60A),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Zen Fokus Rejimi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Center Clock & Motivational Prompt
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatter.format(currentTime),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = dateFormatter.format(currentTime),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = Color(0xFF64D2FF),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = "Diqqatingizni jamlang. Chalg'ituvchi ijtimoiy tarmoqlar vaqtinchalik yashirildi.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Essential Apps Grid & Exit Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Asosiy aloqa va ish qurollari",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(vertical = 12.dp)
                ) {
                    essentialApps.forEach { app ->
                        AppIconItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = {},
                            iconSize = "medium",
                            iconShape = "squircle",
                            showLabel = true,
                            textColor = Color.White,
                            hapticEnabled = hapticEnabled
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onExitFocus,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Fokusdan chiqish", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
