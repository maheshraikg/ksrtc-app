package com.example.ksrtc.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.ui.components.BusTypeBadge
import com.example.ksrtc.ui.components.LanguageUtils
import com.example.ksrtc.ui.viewmodel.RouteDetailViewModel
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedDark
import com.example.ui.theme.KsrtcRedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    timetableId: Long,
    viewModel: RouteDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val busDetail by viewModel.busDetail.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val context = LocalContext.current

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(timetableId) {
        viewModel.loadRouteDetail(timetableId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = LanguageUtils.getString("Bus Details", "ಬಸ್ ವಿವರಗಳು", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button_detail")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier.testTag("detail_fav_btn")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) KsrtcGold else Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            busDetail?.let { bus ->
                                val shareText = "KSRTC Bus Timing: ${bus.busNumber} (${bus.busType})\n" +
                                        "Route: ${bus.fromStation} -> ${bus.toStation}\n" +
                                        "Departure: ${bus.departureTime} | Arrival: ${bus.arrivalTime}\n" +
                                        "Via: ${bus.viaStops}\n" +
                                        "Platform: ${bus.platformNo} | Depot: ${bus.depot}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share KSRTC Route"))
                            }
                        },
                        modifier = Modifier.testTag("detail_share_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KsrtcRedDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        val item = busDetail
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading bus schedule details...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(KsrtcRedDark, KsrtcRedPrimary)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val headerTitle = when {
                                    item.busNumber.isNotBlank() -> item.busNumber
                                    item.busName.isNotBlank() -> item.busName
                                    else -> "${LanguageUtils.getFromStationName(item, language)} → ${LanguageUtils.getToStationName(item, language)}"
                                }
                                Text(
                                    text = headerTitle,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                if (item.busType.isNotBlank()) {
                                    BusTypeBadge(busType = item.busType)
                                }
                            }

                            if (item.busNumber.isNotBlank() && item.busName.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.busName,
                                    fontSize = 15.sp,
                                    color = KsrtcGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val standInfo = listOfNotNull(
                                if (item.platformNo.isNotBlank()) "${LanguageUtils.getString("Platform", "ಪ್ಲಾಟ್‌ಫಾರ್ಮ್", language)}: ${item.platformNo}" else null,
                                item.busStand.ifBlank { null }
                            ).joinToString(" • ")

                            if (standInfo.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = standInfo,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // Route Timeline Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = LanguageUtils.getString("Journey Schedule Timeline", "ಪ್ರಯಾಣದ ಸಮಯದ ವೇಳಾಪಟ್ಟಿ", language),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Start Station Node
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.height(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = LanguageUtils.getFromStationName(item, language),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (item.departureTime.isNotBlank()) {
                                        Text(
                                            text = "${LanguageUtils.getString("Departure", "ಹೊರಡುವ ಸಮಯ", language)}: ${item.departureTime}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // Connecting Line & Via Stops
                            val viaText = LanguageUtils.getViaStops(item, language)
                            val showVia = viaText.isNotBlank()
                            val showDuration = item.journeyDuration.isNotBlank()

                            Row(modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(if (showVia || showDuration) 64.dp else 32.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                                    if (showVia) {
                                        Text(
                                            text = "${LanguageUtils.getString("Via Stops", "ಮಾರ್ಗದ ನಿಲ್ದಾಣಗಳು", language)}:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = viaText,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (showDuration) {
                                        Text(
                                            text = "${LanguageUtils.getString("Total Duration", "ಒಟ್ಟು ಸಮಯ", language)}: ${item.journeyDuration}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }

                            // End Station Node
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.height(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = LanguageUtils.getToStationName(item, language),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (item.arrivalTime.isNotBlank()) {
                                        Text(
                                            text = "${LanguageUtils.getString("Arrival", "ತಲುಪುವ ಸಮಯ", language)}: ${item.arrivalTime}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Operational & Depot Details
                val showDepot = item.depot.isNotBlank()
                val showDivision = item.division.isNotBlank()
                val notesText = LanguageUtils.getNotes(item, language)
                val showNotes = notesText.isNotBlank()

                if (showDepot || showDivision || showNotes) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = LanguageUtils.getString("Official PDF Timetable Info", "ಅಧಿಕೃತ ಪಿಡಿಎಫ್ ವೇಳಾಪಟ್ಟಿ ಮಾಹಿತಿ", language),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (showDepot || showDivision) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            if (showDepot) {
                                                Text(
                                                    text = "${LanguageUtils.getString("Depot", "ಘಟಕ", language)}: ${item.depot}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            if (showDivision) {
                                                Text(
                                                    text = "${LanguageUtils.getString("Division", "ವಿಭಾಗ", language)}: ${item.division}",
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }

                                if (showNotes) {
                                    if (showDepot || showDivision) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider()
                                        Spacer(modifier = Modifier.height(12.dp))
                                    } else {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${LanguageUtils.getString("Notes", "ಸೂಚನೆಗಳು", language)}: $notesText",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (item.updatedDate.isNotBlank()) {
                                                Text(
                                                    text = "${LanguageUtils.getString("Last Updated", "ಕೊನೆಯ ಬಾರಿ ನವೀಕರಿಸಲಾಗಿದೆ", language)}: ${item.updatedDate}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleFavorite() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_save_favorite"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFavorite) LanguageUtils.getString("Saved", "ಉಳಿಸಲಾಗಿದೆ", language) else LanguageUtils.getString("Save Favorite", "ನೆಚ್ಚಿನ ಮಾರ್ಗ", language)
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val shareText = "KSRTC Bus Timing: ${item.busNumber} (${item.busType})\n" +
                                        "Route: ${item.fromStation} -> ${item.toStation}\n" +
                                        "Departure: ${item.departureTime} | Arrival: ${item.arrivalTime}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share KSRTC Route"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_share_route")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = LanguageUtils.getString("Share Route", "ಹಂಚಿಕೊಳ್ಳಿ", language))
                        }
                    }
                }
            }
        }
    }
}
