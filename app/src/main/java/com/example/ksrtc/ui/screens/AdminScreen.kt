package com.example.ksrtc.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.ui.components.BusTypeBadge
import com.example.ksrtc.ui.components.LanguageUtils
import com.example.ksrtc.ui.viewmodel.AdminViewModel
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val allTimetables by viewModel.allTimetables.collectAsState()
    val activeCount by viewModel.activeCount.collectAsState()
    val draftExtractedRows by viewModel.draftExtractedRows.collectAsState()
    val isExtractingPdf by viewModel.isExtractingPdf.collectAsState()
    val adminSubmissions by viewModel.adminSubmissions.collectAsState()
    val adminMessage by viewModel.adminMessage.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()

    BackHandler {
        onNavigateBack()
    }

    var passwordInput by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: PDF Extractor, 2: Database CRUD

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<BusTimetableEntity?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(adminMessage) {
        adminMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
            }
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = LanguageUtils.getString("KSRTC Admin Control Panel", "ಕೆಎಸ್‌ಆರ್‌ಟಿಸಿ ಅಡ್ಮಿನ್ ನಿಯಂತ್ರಣ ಫಲಕ", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("admin_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isAdminLoggedIn) {
                        TextButton(
                            onClick = { viewModel.logoutAdmin() },
                            modifier = Modifier.testTag("admin_logout_btn")
                        ) {
                            Text("Logout", color = KsrtcGold, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KsrtcRedDark,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!isAdminLoggedIn) {
                // Admin Login View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.height(56.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Administrator Authentication",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Enter secure admin password or PIN to access timetable database management and PDF extraction.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Admin Password / PIN") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_password_input")
                            )

                            Text(
                                text = "Default Admin Passcode: admin123",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
                            )

                            Button(
                                onClick = { viewModel.loginAdmin(passwordInput) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("admin_login_submit_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Authenticate", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Admin Authenticated View
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Dashboard") },
                            modifier = Modifier.testTag("tab_admin_dashboard")
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("PDF Timetable Import") },
                            modifier = Modifier.testTag("tab_admin_pdf_import")
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("Database CRUD (${allTimetables.size})") },
                            modifier = Modifier.testTag("tab_admin_crud")
                        )
                    }

                    when (selectedTabIndex) {
                        0 -> AdminDashboardView(
                            activeCount = activeCount,
                            totalCount = allTimetables.size,
                            submissions = adminSubmissions,
                            onExportClick = { showExportDialog = true },
                            onGoToPdfTab = { selectedTabIndex = 1 }
                        )
                        1 -> AdminPdfImportView(
                            viewModel = viewModel,
                            drafts = draftExtractedRows,
                            isExtracting = isExtractingPdf
                        )
                        2 -> AdminCrudView(
                            viewModel = viewModel,
                            allTimetables = allTimetables,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onAddNewClick = { showAddDialog = true },
                            onEditClick = { editingItem = it }
                        )
                    }
                }
            }
        }
    }

    // Add Timetable Dialog
    if (showAddDialog) {
        TimetableEditorDialog(
            item = null,
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                viewModel.saveNewTimetable(newItem)
                showAddDialog = false
            }
        )
    }

    // Edit Timetable Dialog
    editingItem?.let { item ->
        TimetableEditorDialog(
            item = item,
            onDismiss = { editingItem = null },
            onSave = { updated ->
                viewModel.updateTimetable(updated)
                editingItem = null
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        val csvContent = viewModel.generateCsvExport()
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Timetable Data (CSV)") },
            text = {
                OutlinedTextField(
                    value = csvContent,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun AdminDashboardView(
    activeCount: Int,
    totalCount: Int,
    submissions: List<com.example.ksrtc.data.model.AdminSubmissionEntity>,
    onExportClick: () -> Unit,
    onGoToPdfTab: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "System Overview",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Schedules", fontSize = 12.sp)
                        Text("$activeCount", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total DB Records", fontSize = 12.sp)
                        Text("$totalCount", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onGoToPdfTab,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload KSRTC PDF")
                }

                Button(
                    onClick = onExportClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV")
                }
            }
        }

        item {
            Text(
                text = "Recent PDF Upload History",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        itemsIndexed(submissions) { _, sub ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(sub.fileName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${sub.recordsExtracted} records extracted • ${sub.uploadDate}", fontSize = 11.sp)
                        }
                    }
                    Text(sub.status, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AdminPdfImportView(
    viewModel: AdminViewModel,
    drafts: List<BusTimetableEntity>,
    isExtracting: Boolean
) {
    var pastedText by remember { mutableStateOf("") }
    var selectedPdfFile by remember { mutableStateOf("KSRTC_Kempegowda_Majestic_Platform10_Timetable.pdf") }

    val samplePdfOptions = listOf(
        "KSRTC_Kempegowda_Majestic_Platform10_Timetable.pdf",
        "KSRTC_Mysuru_Suburb_Terminal_Timetable.pdf",
        "KSRTC_Mangaluru_Kuntikana_Timetable.pdf"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Official KSRTC Bus Stand PDF Timetable Extractor",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Select an official KSRTC timetable PDF file to automatically extract bus rows into structured database records.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    samplePdfOptions.forEach { pdf ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPdfFile = pdf }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = if (selectedPdfFile == pdf) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pdf,
                                fontWeight = if (selectedPdfFile == pdf) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedPdfFile == pdf) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pastedText,
                        onValueChange = { pastedText = it },
                        label = { Text("Or paste raw timetable table data (CSV/tab formatted)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.simulatePdfUpload(selectedPdfFile, pastedText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("extract_pdf_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isExtracting) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.height(20.dp))
                        } else {
                            Icon(Icons.Default.FileUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Extract PDF Timetable Data")
                        }
                    }
                }
            }
        }

        if (drafts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Preview Extracted Rows (${drafts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Row {
                        TextButton(onClick = { viewModel.clearDrafts() }) {
                            Text("Clear")
                        }
                        Button(
                            onClick = { viewModel.approveDraftToDatabase() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("approve_drafts_btn")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve All")
                        }
                    }
                }
            }

            itemsIndexed(drafts) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.busNumber, fontWeight = FontWeight.Bold)
                            BusTypeBadge(busType = item.busType)
                        }
                        Text("${item.fromStation} → ${item.toStation}", fontSize = 13.sp)
                        Text("Departure: ${item.departureTime} | Arrival: ${item.arrivalTime}", fontSize = 12.sp)
                        Text("Via: ${item.viaStops}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { viewModel.removeDraftRow(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCrudView(
    viewModel: AdminViewModel,
    allTimetables: List<BusTimetableEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddNewClick: () -> Unit,
    onEditClick: (BusTimetableEntity) -> Unit
) {
    val filteredList = if (searchQuery.isBlank()) allTimetables else {
        allTimetables.filter {
            it.busNumber.contains(searchQuery, true) ||
            it.fromStation.contains(searchQuery, true) ||
            it.toStation.contains(searchQuery, true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Filter database records...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("crud_search_field"),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAddNewClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("add_new_timetable_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = filteredList,
                key = { index, item -> if (item.id != 0L) "admin_id_${item.id}" else "admin_bus_${item.busNumber}_$index" }
            ) { _, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.busNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BusTypeBadge(busType = item.busType)
                            }
                            Text("${item.fromStation} → ${item.toStation}", fontSize = 13.sp)
                            Text("${item.departureTime} - ${item.arrivalTime} • ${item.status}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                        }

                        Row {
                            IconButton(onClick = { onEditClick(item) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteTimetable(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimetableEditorDialog(
    item: BusTimetableEntity?,
    onDismiss: () -> Unit,
    onSave: (BusTimetableEntity) -> Unit
) {
    var busNumber by remember { mutableStateOf(item?.busNumber ?: "") }
    var busName by remember { mutableStateOf(item?.busName ?: "Karnataka Express") }
    var busType by remember { mutableStateOf(item?.busType ?: "Karnataka Sarige") }
    var fromStation by remember { mutableStateOf(item?.fromStation ?: "Bengaluru Majestic") }
    var toStation by remember { mutableStateOf(item?.toStation ?: "Mysuru Suburb") }
    var viaStops by remember { mutableStateOf(item?.viaStops ?: "Ramanagara, Mandya") }
    var departureTime by remember { mutableStateOf(item?.departureTime ?: "08:00 AM") }
    var arrivalTime by remember { mutableStateOf(item?.arrivalTime ?: "11:15 AM") }
    var depot by remember { mutableStateOf(item?.depot ?: "Bengaluru Depot 1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Add Bus Timetable" else "Edit Bus Timetable") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = busNumber, onValueChange = { busNumber = it }, label = { Text("Bus Number") }) }
                item { OutlinedTextField(value = busName, onValueChange = { busName = it }, label = { Text("Bus Name") }) }
                item { OutlinedTextField(value = busType, onValueChange = { busType = it }, label = { Text("Bus Type") }) }
                item { OutlinedTextField(value = fromStation, onValueChange = { fromStation = it }, label = { Text("From Station") }) }
                item { OutlinedTextField(value = toStation, onValueChange = { toStation = it }, label = { Text("To Station") }) }
                item { OutlinedTextField(value = viaStops, onValueChange = { viaStops = it }, label = { Text("Via Stops") }) }
                item { OutlinedTextField(value = departureTime, onValueChange = { departureTime = it }, label = { Text("Departure Time") }) }
                item { OutlinedTextField(value = arrivalTime, onValueChange = { arrivalTime = it }, label = { Text("Arrival Time") }) }
                item { OutlinedTextField(value = depot, onValueChange = { depot = it }, label = { Text("Depot") }) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val entity = item?.copy(
                        busNumber = busNumber,
                        busName = busName,
                        busType = busType,
                        fromStation = fromStation,
                        toStation = toStation,
                        viaStops = viaStops,
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                        depot = depot
                    ) ?: BusTimetableEntity(
                        busNumber = busNumber,
                        busName = busName,
                        busType = busType,
                        fromStation = fromStation,
                        fromStationKn = fromStation,
                        toStation = toStation,
                        toStationKn = toStation,
                        viaStops = viaStops,
                        viaStopsKn = viaStops,
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                        journeyDuration = "3h 15m",
                        busStand = "Platform 1",
                        depot = depot,
                        division = "Division",
                        platformNo = "Platform 1",
                        status = "APPROVED"
                    )
                    onSave(entity)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
