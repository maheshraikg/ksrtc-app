package com.example.ksrtc.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.ui.components.BusCard
import com.example.ksrtc.ui.components.LanguageUtils
import com.example.ksrtc.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    initialFrom: String = "",
    initialTo: String = "",
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedBusType by viewModel.selectedBusType.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    BackHandler {
        onNavigateBack()
    }

    var showSortMenu by remember { mutableStateOf(false) }

    val busTypeFilters = listOf(
        "All", "Karnataka Sarige", "Rajahamsa", "Airavat", "Ambari", "Sleeper", "Electric EV", "Express", "Volvo"
    )

    LaunchedEffect(initialFrom, initialTo) {
        if (initialFrom.isNotBlank() && initialTo.isNotBlank()) {
            viewModel.updateQuery("$initialFrom $initialTo")
        } else if (initialFrom.isNotBlank()) {
            viewModel.updateQuery(initialFrom)
        } else if (initialTo.isNotBlank()) {
            viewModel.updateQuery(initialTo)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = LanguageUtils.getString("Search KSRTC Timings", "ಬಸ್ ವೇಳಾಪಟ್ಟಿ ಹುಡುಕಿ", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier.testTag("sort_menu_btn")
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = "Sort")
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Departure Time") },
                            onClick = {
                                viewModel.setSortBy("Departure Time")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Bus Type") },
                            onClick = {
                                viewModel.setSortBy("Bus Type")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Journey Duration") },
                            onClick = {
                                viewModel.setSortBy("Duration")
                                showSortMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateQuery(it) },
                    placeholder = {
                        Text(
                            LanguageUtils.getString(
                                "Search station, bus no, route or via...",
                                "ನಿಲ್ದಾಣ, ಬಸ್ ನಂ, ಮಾರ್ಗ ಹುಡುಕಿ...",
                                language
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                            IconButton(
                                onClick = {
                                    val clipText = clipboardManager.getText()?.text?.toString() ?: ""
                                    if (clipText.isNotBlank()) {
                                        viewModel.updateQuery(clipText)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste from Clipboard",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true
                )
            }

            // Bus Type Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(busTypeFilters) { busType ->
                    val isSelected = selectedBusType == busType
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectBusTypeFilter(busType) },
                        label = { Text(busType) },
                        modifier = Modifier.testTag("filter_chip_$busType")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Results Counter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${searchResults.size} ${LanguageUtils.getString("Buses Found", "ಬಸ್‌ಗಳು ಲಭ್ಯ", language)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${LanguageUtils.getString("Sorted by", "ಕ್ರಮ:", language)} $sortBy",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Results List
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.height(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = LanguageUtils.getString(
                                    "No bus timetables found matching your search.",
                                    "ನಿಮ್ಮ ಹುಡುಕಾಟಕ್ಕೆ ಯಾವುದೇ ಬಸ್ ವೇಳಾಪಟ್ಟಿ ಸಿಗಲಿಲ್ಲ.",
                                    language
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = LanguageUtils.getString(
                                    "Try clearing filters or searching another station.",
                                    "ದಯವಿಟ್ಟು ಬೇರೆ ಬಸ್ ನಿಲ್ದಾಣವನ್ನು ಹುಡುಕಿ.",
                                    language
                                ),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = searchResults,
                        key = { index, timetable ->
                            if (timetable.id != 0L) "search_id_${timetable.id}" else "search_bus_${timetable.busNumber}_${timetable.departureTime}_$index"
                        }
                    ) { _, timetable ->
                        BusCard(
                            timetable = timetable,
                            language = language,
                            isFavorite = false,
                            onCardClick = { onNavigateToDetail(timetable.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(timetable, false) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
