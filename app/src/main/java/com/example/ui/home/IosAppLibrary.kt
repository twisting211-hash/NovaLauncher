package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.apps.AppInfo
import com.example.ui.components.AppIconItem

data class IosAppCategoryGroup(
    val title: String,
    val apps: List<AppInfo>
)

@Composable
fun IosAppLibrary(
    allApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onOpenSearch: () -> Unit,
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val groups = remember(allApps) {
        val social = allApps.filter {
            it.category == "Ijtimoiy" || it.category == "Muloqot" ||
            it.packageName.contains("telegram") || it.packageName.contains("whatsapp") ||
            it.packageName.contains("instagram") || it.packageName.contains("facebook") ||
            it.packageName.contains("message") || it.packageName.contains("dialer")
        }
        val media = allApps.filter {
            (it.category == "Media" || it.category == "O'yinlar" ||
             it.packageName.contains("youtube") || it.packageName.contains("music") ||
             it.packageName.contains("camera") || it.packageName.contains("gallery") ||
             it.packageName.contains("photo") || it.packageName.contains("video")) &&
            !social.contains(it)
        }
        val tools = allApps.filter {
            (it.category == "Asboblar" || it.category == "Ish unumi" ||
             it.packageName.contains("calculator") || it.packageName.contains("clock") ||
             it.packageName.contains("setting") || it.packageName.contains("file") ||
             it.packageName.contains("chrome") || it.packageName.contains("browser")) &&
            !social.contains(it) && !media.contains(it)
        }
        val others = allApps.filter {
            !social.contains(it) && !media.contains(it) && !tools.contains(it)
        }

        val result = mutableListOf<IosAppCategoryGroup>()
        if (allApps.isNotEmpty()) {
            result.add(IosAppCategoryGroup("Tavsiyalar", allApps.sortedByDescending { it.launchCount }.take(6)))
        }
        if (social.isNotEmpty()) result.add(IosAppCategoryGroup("Ijtimoiy & Muloqot", social))
        if (media.isNotEmpty()) result.add(IosAppCategoryGroup("Media & Ko'ngilochar", media))
        if (tools.isNotEmpty()) result.add(IosAppCategoryGroup("Asboblar & Unumdorlik", tools))
        if (others.isNotEmpty()) result.add(IosAppCategoryGroup("Boshqa Ilovalar", others))
        result
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ios_app_library")
    ) {
        // Search Pill at the top of App Library
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .clickable(onClick = onOpenSearch)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Qidiruv",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ilovalar Kutubxonasida qidirish",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(groups) { group ->
                IosFolderCard(
                    group = group,
                    onAppClick = onAppClick,
                    onAppLongClick = onAppLongClick,
                    hapticEnabled = hapticEnabled
                )
            }
        }
    }
}

@Composable
private fun IosFolderCard(
    group: IosAppCategoryGroup,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    hapticEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(26.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 2x2 grid inside folder
            val displayApps = group.apps.take(4)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    displayApps.getOrNull(0)?.let { app ->
                        AppIconItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) },
                            iconSize = "small",
                            iconShape = "squircle",
                            showLabel = false,
                            textColor = Color.White,
                            hapticEnabled = hapticEnabled,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    displayApps.getOrNull(1)?.let { app ->
                        AppIconItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) },
                            iconSize = "small",
                            iconShape = "squircle",
                            showLabel = false,
                            textColor = Color.White,
                            hapticEnabled = hapticEnabled,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    displayApps.getOrNull(2)?.let { app ->
                        AppIconItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) },
                            iconSize = "small",
                            iconShape = "squircle",
                            showLabel = false,
                            textColor = Color.White,
                            hapticEnabled = hapticEnabled,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    displayApps.getOrNull(3)?.let { app ->
                        AppIconItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) },
                            iconSize = "small",
                            iconShape = "squircle",
                            showLabel = false,
                            textColor = Color.White,
                            hapticEnabled = hapticEnabled,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = group.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
