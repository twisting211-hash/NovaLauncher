package com.example.ui.appdrawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.apps.AppInfo
import com.example.ui.components.AppIconItem
import com.example.ui.components.EmptyStateView
import kotlinx.coroutines.launch

@Composable
fun AppDrawerSheet(
    apps: List<AppInfo>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onClose: () -> Unit,
    iconSize: String = "medium",
    iconShape: String = "rounded",
    showLabels: Boolean = true,
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    val categories = listOf("Barchasi", "Muloqot", "Ijtimoiy", "O'yinlar", "Asboblar", "Media", "Ish unumi", "Boshqa")

    // Group apps by first letter for alphabetical navigation
    val groupedApps = remember(apps) {
        apps.groupBy { it.label.firstOrNull()?.uppercaseChar() ?: '#' }
    }

    val alphabetLetters = remember(groupedApps) {
        groupedApps.keys.toList().sorted()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("app_drawer")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Search Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Ilovalarni qidirish...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Qidiruv",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Tozalash"
                                )
                            }
                        } else {
                            IconButton(onClick = onClose) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Yopish"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("drawer_search_input")
                )
            }

            // Categories Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(cat) },
                        label = {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Content Area
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (apps.isEmpty()) {
                EmptyStateView(
                    message = "Ilovalar topilmadi",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Apps Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        state = gridState,
                        contentPadding = PaddingValues(start = 12.dp, end = 28.dp, bottom = 40.dp, top = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        groupedApps.forEach { (letter, letterApps) ->
                            // Header for letter
                            item(span = { GridItemSpan(4) }) {
                                Text(
                                    text = letter.toString(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 6.dp)
                                )
                            }

                            items(letterApps, key = { it.packageName }) { app ->
                                AppIconItem(
                                    app = app,
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onAppLongClick(app) },
                                    iconSize = iconSize,
                                    iconShape = iconShape,
                                    showLabel = showLabels,
                                    textColor = MaterialTheme.colorScheme.onSurface,
                                    hapticEnabled = hapticEnabled
                                )
                            }
                        }
                    }

                    // Fast Alphabetical Index Strip on the Right
                    if (searchQuery.isEmpty() && alphabetLetters.size > 2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(28.dp)
                                .padding(vertical = 12.dp)
                        ) {
                            alphabetLetters.forEach { letter ->
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            // Scroll to position
                                            scope.launch {
                                                // Calculate index
                                                var targetIdx = 0
                                                for ((l, lApps) in groupedApps) {
                                                    if (l == letter) break
                                                    targetIdx += 1 + lApps.size // 1 for header
                                                }
                                                gridState.animateScrollToItem(targetIdx)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letter.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
