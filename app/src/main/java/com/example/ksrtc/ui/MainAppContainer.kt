package com.example.ksrtc.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.AppThemeMode
import com.example.ksrtc.data.repository.KsrtcRepository
import com.example.ksrtc.ui.components.LanguageUtils
import com.example.ksrtc.ui.screens.AdminScreen
import com.example.ksrtc.ui.screens.FavoritesScreen
import com.example.ksrtc.ui.screens.HomeScreen
import com.example.ksrtc.ui.screens.RouteDetailScreen
import com.example.ksrtc.ui.screens.SearchScreen
import com.example.ksrtc.ui.screens.SettingsScreen
import com.example.ksrtc.ui.viewmodel.AdminViewModel
import com.example.ksrtc.ui.viewmodel.FavoritesViewModel
import com.example.ksrtc.ui.viewmodel.HomeViewModel
import com.example.ksrtc.ui.viewmodel.RouteDetailViewModel
import com.example.ksrtc.ui.viewmodel.SearchViewModel
import com.example.ksrtc.ui.viewmodel.SettingsViewModel

sealed class Screen(val route: String, val titleEn: String, val titleKn: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", "ಮುಖ್ಯ ಪುಟ", Icons.Default.Home)
    object Search : Screen("search", "Search", "ಹುಡುಕಾಟ", Icons.Default.Search)
    object Favorites : Screen("favorites", "Favourites", "ನೆಚ್ಚಿನವು", Icons.Default.Favorite)
    object Settings : Screen("settings", "Settings", "ಸಂಯೋಜನೆ", Icons.Default.Settings)
    object Admin : Screen("admin", "Admin", "ಅಡ್ಮಿನ್", Icons.Default.Settings)
    object Detail : Screen("detail/{timetableId}", "Bus Details", "ವಿವರಗಳು", Icons.Default.Home)
}

@Composable
fun MainAppContainer(
    repository: KsrtcRepository,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    routeDetailViewModel: RouteDetailViewModel,
    favoritesViewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel,
    adminViewModel: AdminViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val language by repository.currentLanguage.collectAsState(initial = AppLanguage.ENGLISH)
    val themeMode by repository.currentThemeMode.collectAsState(initial = AppThemeMode.LIGHT)

    val safeNavigateBack: () -> Unit = {
        if (!navController.popBackStack()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    com.example.ui.theme.MyApplicationTheme(darkTheme = isDark) {
        val bottomNavItems = listOf(
            Screen.Home,
            Screen.Search,
            Screen.Favorites,
            Screen.Settings
        )

        val showBottomBar = currentRoute in bottomNavItems.map { it.route }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationBar(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent,
                            tonalElevation = 0.dp
                        ) {
                            bottomNavItems.forEach { screen ->
                                val selected = currentRoute == screen.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.titleEn
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = LanguageUtils.getString(screen.titleEn, screen.titleKn, language),
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    selected = selected,
                                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    ),
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    modifier = Modifier.testTag("nav_item_${screen.route}")
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToSearch = { from, to ->
                            navController.navigate("search?from=$from&to=$to")
                        },
                        onNavigateToAdmin = {
                            navController.navigate(Screen.Admin.route)
                        },
                        onNavigateToDetail = { id ->
                            navController.navigate("detail/$id")
                        },
                        onToggleLanguage = {
                            val newLang = if (language == AppLanguage.ENGLISH) AppLanguage.KANNADA else AppLanguage.ENGLISH
                            repository.setLanguage(newLang)
                        }
                    )
                }

                composable(
                    route = "search?from={from}&to={to}",
                    arguments = listOf(
                        navArgument("from") { type = NavType.StringType; defaultValue = "" },
                        navArgument("to") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStack ->
                    val fromArg = backStack.arguments?.getString("from") ?: ""
                    val toArg = backStack.arguments?.getString("to") ?: ""
                    SearchScreen(
                        viewModel = searchViewModel,
                        initialFrom = fromArg,
                        initialTo = toArg,
                        onNavigateBack = safeNavigateBack,
                        onNavigateToDetail = { id ->
                            navController.navigate("detail/$id")
                        }
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = searchViewModel,
                        onNavigateBack = safeNavigateBack,
                        onNavigateToDetail = { id ->
                            navController.navigate("detail/$id")
                        }
                    )
                }

                composable(
                    route = Screen.Detail.route,
                    arguments = listOf(
                        navArgument("timetableId") { type = NavType.LongType }
                    )
                ) { backStack ->
                    val timetableId = backStack.arguments?.getLong("timetableId") ?: 0L
                    RouteDetailScreen(
                        timetableId = timetableId,
                        viewModel = routeDetailViewModel,
                        onNavigateBack = safeNavigateBack
                    )
                }

                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        viewModel = favoritesViewModel,
                        onNavigateToDetail = { id ->
                            navController.navigate("detail/$id")
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateToAdmin = {
                            navController.navigate(Screen.Admin.route)
                        }
                    )
                }

                composable(Screen.Admin.route) {
                    AdminScreen(
                        viewModel = adminViewModel,
                        onNavigateBack = safeNavigateBack
                    )
                }
            }
        }
    }
}
