package com.example.ksrtc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.RecentSearchEntity
import com.example.ksrtc.data.model.StationEntity
import com.example.ksrtc.ui.components.KsrtcHeader
import com.example.ksrtc.ui.components.LanguageUtils
import com.example.ksrtc.ui.viewmodel.HomeViewModel
import com.example.ui.theme.BentoBlueAccent
import com.example.ui.theme.BentoBlueBorder
import com.example.ui.theme.BentoBlueLight
import com.example.ui.theme.BentoRedAccent
import com.example.ui.theme.BentoRedBorder
import com.example.ui.theme.BentoRedLight
import com.example.ui.theme.BentoRedPrimary
import com.example.ui.theme.KsrtcGold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: (from: String, to: String) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit,
    onToggleLanguage: () -> Unit
) {
    val fromStation by viewModel.fromStation.collectAsState()
    val toStation by viewModel.toStation.collectAsState()
    val stations by viewModel.allStations.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    var fromQuery by remember { mutableStateOf("") }
    var toQuery by remember { mutableStateOf("") }

    val filteredFromStations = stations.filter {
        val name = LanguageUtils.getStationName(it, language)
        val norm = fromQuery.lowercase()
            .replace("uppinagady", "uppinangady")
            .replace("uppinangadi", "uppinangady")
            .replace("manglore", "mangaluru")
            .replace("mangalore", "mangaluru")
            .replace("bangalore", "bengaluru")
            .replace("mysore", "mysuru")
            .replace("belgaum", "belagavi")
            .replace("hubli", "hubballi")
        norm.isBlank() || name.contains(norm, ignoreCase = true) || it.name.contains(norm, ignoreCase = true) || it.district.contains(norm, ignoreCase = true)
    }

    val filteredToStations = stations.filter {
        val name = LanguageUtils.getStationName(it, language)
        val norm = toQuery.lowercase()
            .replace("uppinagady", "uppinangady")
            .replace("uppinangadi", "uppinangady")
            .replace("manglore", "mangaluru")
            .replace("mangalore", "mangaluru")
            .replace("bangalore", "bengaluru")
            .replace("mysore", "mysuru")
            .replace("belgaum", "belagavi")
            .replace("hubli", "hubballi")
        norm.isBlank() || name.contains(norm, ignoreCase = true) || it.name.contains(norm, ignoreCase = true) || it.district.contains(norm, ignoreCase = true)
    }

    val popularDestinations = listOf(
        "Mysuru Suburb", "Mangaluru Kuntikana", "Hubballi CBT",
        "Belagavi Central", "Shivamogga Bus Stand", "Madikeri KSRTC", "Gokarna KSRTC"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            KsrtcHeader(
                language = language,
                onLanguageToggle = onToggleLanguage,
                onAdminClick = onNavigateToAdmin
            )
        }

        item {
            // Main Route Search Bento Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LanguageUtils.getString(
                                en = "Plan Your Journey",
                                kn = "ನಿಮ್ಮ ಪ್ರಯಾಣವನ್ನು ಸಂಘಟಿಸಿ",
                                lang = language
                            ),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OFFICIAL TIMETABLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // From Station Dropdown Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = fromExpanded,
                            onExpandedChange = { fromExpanded = !fromExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = fromStation,
                                onValueChange = {
                                    viewModel.setFromStation(it)
                                    fromQuery = it
                                },
                                label = {
                                    Text(LanguageUtils.getString("From Station", "ಪ್ರಾರಂಭ ನಿಲ್ದಾಣ", language))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF10B981))
                                },
                                trailingIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (fromStation.isNotEmpty()) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.setFromStation("")
                                                    fromQuery = ""
                                                }
                                            ) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                val clipText = clipboardManager.getText()?.text?.toString() ?: ""
                                                if (clipText.isNotBlank()) {
                                                    viewModel.setFromStation(clipText)
                                                    fromQuery = clipText
                                                    fromExpanded = true
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentPaste,
                                                contentDescription = "Paste from Clipboard",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded)
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("from_station_input")
                            )

                            ExposedDropdownMenu(
                                expanded = fromExpanded,
                                onDismissRequest = { fromExpanded = false }
                            ) {
                                filteredFromStations.forEach { station ->
                                    val stationName = LanguageUtils.getStationName(station, language)
                                    DropdownMenuItem(
                                        text = { Text(stationName) },
                                        onClick = {
                                            viewModel.setFromStation(station.name)
                                            fromExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Swap Connector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .height(1.dp)
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )

                        IconButton(
                            onClick = { viewModel.swapStations() },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(BentoRedPrimary, BentoRedAccent)
                                    )
                                )
                                .size(40.dp)
                                .testTag("swap_stations_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(1.dp)
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                    }

                    // To Station Dropdown Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = toExpanded,
                            onExpandedChange = { toExpanded = !toExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = toStation,
                                onValueChange = {
                                    viewModel.setToStation(it)
                                    toQuery = it
                                },
                                label = {
                                    Text(LanguageUtils.getString("To Station / Destination", "ತಲುಪುವ ನಿಲ್ದಾಣ", language))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF4444))
                                },
                                trailingIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (toStation.isNotEmpty()) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.setToStation("")
                                                    toQuery = ""
                                                }
                                            ) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                val clipText = clipboardManager.getText()?.text?.toString() ?: ""
                                                if (clipText.isNotBlank()) {
                                                    viewModel.setToStation(clipText)
                                                    toQuery = clipText
                                                    toExpanded = true
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentPaste,
                                                contentDescription = "Paste from Clipboard",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded)
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("to_station_input")
                            )

                            ExposedDropdownMenu(
                                expanded = toExpanded,
                                onDismissRequest = { toExpanded = false }
                            ) {
                                filteredToStations.forEach { station ->
                                    val stationName = LanguageUtils.getStationName(station, language)
                                    DropdownMenuItem(
                                        text = { Text(stationName) },
                                        onClick = {
                                            viewModel.setToStation(station.name)
                                            toExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Gradient Bento Search Button
                    Button(
                        onClick = {
                            viewModel.recordSearch()
                            onNavigateToSearch(fromStation, toStation)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(BentoRedPrimary, BentoRedAccent)
                                )
                            )
                            .testTag("search_buses_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LanguageUtils.getString("Search Bus Timings", "ಬಸ್ ವೇಳಾಪಟ್ಟಿ ಹುಡುಕಿ", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Bento Grid Quick Action Tiles (2-Column Tile Row)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Favourites Bento Tile
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSearch("", "") }
                        .testTag("bento_favorites_tile"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BentoRedLight
                    ),
                    border = BorderStroke(1.dp, BentoRedBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favourites",
                                tint = BentoRedPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = LanguageUtils.getString("Saved Routes", "ಉಳಿಸಿದ ಮಾರ್ಗಗಳು", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoRedPrimary
                        )

                        Text(
                            text = LanguageUtils.getString("Quick Favorites", "ತ್ವರಿತ ಪಟ್ಟಿ", language),
                            fontSize = 11.sp,
                            color = BentoRedPrimary.copy(alpha = 0.8f)
                        )
                    }
                }

                // Card 2: Recent Search Bento Tile
                val latestSearch = recentSearches.firstOrNull()
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (latestSearch != null) {
                                viewModel.onRecentSearchClicked(latestSearch)
                                onNavigateToSearch(latestSearch.fromStation, latestSearch.toStation)
                            } else {
                                onNavigateToSearch("", "")
                            }
                        }
                        .testTag("bento_recent_tile"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BentoBlueLight
                    ),
                    border = BorderStroke(1.dp, BentoBlueBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = BentoBlueAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = LanguageUtils.getString("Recent Search", "ಇತ್ತೀಚಿನ ಹುಡುಕಾಟ", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoBlueAccent
                        )

                        Text(
                            text = if (latestSearch != null) "${latestSearch.fromStation} → ${latestSearch.toStation}"
                                   else LanguageUtils.getString("No history", "ಇತಿಹಾಸವಿಲ್ಲ", language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoBlueAccent.copy(alpha = 0.85f),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Bus Station Wise Services & Official Portal PDFs Section
        item {
            val uriHandler = LocalUriHandler.current

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = "PDF Schedules",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageUtils.getString("Bus Station Wise Timetables & Official PDFs", "ಬಸ್ ನಿಲ್ದಾಣವಾರು ಅಧಿಕೃತ ವೇಳಾಪಟ್ಟಿ ಮತ್ತು ಪಿಡಿಎಫ್", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = LanguageUtils.getString("Direct Official KSRTC Portal PDF schedules", "ಕೆಎಸ್‌ಆರ್‌ಟಿಸಿ ಅಧಿಕೃತ ಪೋರ್ಟಲ್‌ನ ನೇರ ಪಿಡಿಎಫ್‌ಗಳು", language),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            try {
                                uriHandler.openUri("https://ksrtc.karnataka.gov.in/231/Bus%20station%20wise%20services/en")
                            } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoRedLight),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = LanguageUtils.getString("All Station PDFs", "ಎಲ್ಲಾ ಪಿಡಿಎಫ್", language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoRedPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = stations,
                        key = { "home_station_${it.id}" }
                    ) { station ->
                        val stationName = LanguageUtils.getStationName(station, language)
                        Card(
                            modifier = Modifier
                                .width(240.dp)
                                .testTag("station_pdf_card_${station.id}"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(BentoRedLight)
                                            .padding(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsBus,
                                            contentDescription = null,
                                            tint = BentoRedPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = station.division,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = stationName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${station.dailyServicesCount}+ Daily Scheduled Buses",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.setFromStation(station.name)
                                            onNavigateToSearch(station.name, "")
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = LanguageUtils.getString("View Schedule", "ಸೇವಾ ಪಟ್ಟಿ", language),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            try {
                                                uriHandler.openUri(station.pdfUrl)
                                            } catch (_: Exception) {}
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoRedPrimary),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "PDF",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Popular Destinations Bento Chips Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Popular",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = LanguageUtils.getString("Popular Destinations", "ಜನಪ್ರಿಯ ನಿಲ್ದಾಣಗಳು", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularDestinations.forEach { dest ->
                        val isSelected = toStation == dest
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectPopularDestination(dest) },
                            label = {
                                Text(
                                    text = dest,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BentoRedLight,
                                selectedLabelColor = BentoRedPrimary,
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) BentoRedBorder else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("popular_dest_$dest")
                        )
                    }
                }
            }
        }

        // Recent Searches Horizontal Bento Carousel
        if (recentSearches.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = LanguageUtils.getString("Recent Route History", "ಇತ್ತೀಚಿನ ಹುಡುಕಾಟಗಳ ಪಟ್ಟಿ", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = recentSearches,
                            key = { "recent_${it.id}" }
                        ) { recent ->
                            Card(
                                modifier = Modifier
                                    .clickable {
                                        viewModel.onRecentSearchClicked(recent)
                                        onNavigateToSearch(recent.fromStation, recent.toStation)
                                    }
                                    .testTag("recent_search_chip"),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBus,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(end = 4.dp)
                                    )
                                    Text(
                                        text = "${recent.fromStation} → ${recent.toStation}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
