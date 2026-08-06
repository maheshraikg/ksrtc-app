package com.example.ksrtc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.AppThemeMode
import com.example.ksrtc.ui.components.LanguageUtils
import com.example.ksrtc.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToAdmin: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.currentThemeMode.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    var showLangDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = LanguageUtils.getString("Settings & Info", "ಸಂಯೋಜನೆಗಳು ಹಾಗೂ ಮಾಹಿತಿ", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preferences Section
            item {
                Text(
                    text = LanguageUtils.getString("App Preferences", "ಅಪ್ಲಿಕೇಶನ್ ಆಯ್ಕೆಗಳು", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        // Language Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLangDialog = true }
                                .padding(16.dp)
                                .testTag("setting_language_item"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = LanguageUtils.getString("Display Language", "ಭಾಷೆ", language),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (language == AppLanguage.ENGLISH) "English" else "ಕನ್ನಡ",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        // Theme Mode Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showThemeDialog = true }
                                .padding(16.dp)
                                .testTag("setting_theme_item"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = LanguageUtils.getString("Theme Mode", "ಥೀಮ್", language),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = when (themeMode) {
                                            AppThemeMode.SYSTEM -> LanguageUtils.getString("System Default", "ಸಿಸ್ಟಮ್ ಡೀಫಾಲ್ಟ್", language)
                                            AppThemeMode.LIGHT -> LanguageUtils.getString("Light Mode", "ಬೆಳಕಿನ ಥೀಮ್", language)
                                            AppThemeMode.DARK -> LanguageUtils.getString("Dark Mode", "ಕತ್ತಲೆಯ ಥೀಮ್", language)
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        // Clear Cache Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.clearCache()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Local search cache cleared successfully.")
                                    }
                                }
                                .padding(16.dp)
                                .testTag("setting_clear_cache"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = LanguageUtils.getString("Clear Cache & Data", "ಕ್ಯಾಶ್ ತೆರವುಗೊಳಿಸಿ", language),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = LanguageUtils.getString("Purge temporary offline search data", "ಇತ್ತೀಚಿನ ತಾತ್ಕಾಲಿಕ ಮಾಹಿತಿಯನ್ನು ಅಳಿಸಿ", language),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Administration Section
            item {
                Text(
                    text = LanguageUtils.getString("Administration", "ಆಡಳಿತ ಮಂಡಳಿ", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToAdmin() }
                            .padding(16.dp)
                            .testTag("setting_admin_panel"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = LanguageUtils.getString("Admin Management Panel", "ಅಡ್ಮಿನ್ ನಿರ್ವಹಣಾ ಫಲಕ", language),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAdminLoggedIn) "Logged in as Administrator" else "Upload PDF Timetables & Manage Database",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // About & Legal
            item {
                Text(
                    text = LanguageUtils.getString("About & Privacy", "ಮಾಹಿತಿ ಹಾಗೂ ಗೌಪ್ಯತೆ", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAboutDialog = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = LanguageUtils.getString("About KSRTC Timings Karnataka", "ಅಪ್ಲಿಕೇಶನ್ ಬಗ್ಗೆ", language),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrivacyDialog = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = LanguageUtils.getString("Privacy Policy & Terms", "ಗೌಪ್ಯತಾ ನೀತಿ", language),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Language Dialog
    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text(LanguageUtils.getString("Select Language", "ಭಾಷೆಯನ್ನು ಆಯ್ಕೆ ಮಾಡಿ", language)) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setLanguage(AppLanguage.ENGLISH)
                                showLangDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = language == AppLanguage.ENGLISH,
                            onClick = {
                                viewModel.setLanguage(AppLanguage.ENGLISH)
                                showLangDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("English")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setLanguage(AppLanguage.KANNADA)
                                showLangDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = language == AppLanguage.KANNADA,
                            onClick = {
                                viewModel.setLanguage(AppLanguage.KANNADA)
                                showLangDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ಕನ್ನಡ (Kannada)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLangDialog = false }) { Text("Close") }
            }
        )
    }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(LanguageUtils.getString("Select Theme Mode", "ಥೀಮ್ ಆಯ್ಕೆ ಮಾಡಿ", language)) },
            text = {
                Column {
                    AppThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when (mode) {
                                    AppThemeMode.SYSTEM -> "System Default"
                                    AppThemeMode.LIGHT -> "Light Mode"
                                    AppThemeMode.DARK -> "Dark Mode"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Close") }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("KSRTC Timings Karnataka v1.0") },
            text = {
                Text(
                    "Production-ready application displaying official KSRTC bus timings across Karnataka.\n\n" +
                    "Includes schedules for Karnataka Sarige, Rajahamsa, Airavat Club Class, Ambari Dream Class, Electric EV, Sleeper, and Express buses.\n\n" +
                    "Designed with offline Room database persistence and Admin PDF timetable import capabilities."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("OK") }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Text(
                    "This app does not track or collect personal identification information. All timetable schedule data is stored locally in your device's Room database for fast offline search access."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text("Close") }
            }
        )
    }
}
