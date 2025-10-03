package com.splitsmart.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.splitsmart.feature.events.EventListScreen
import com.splitsmart.feature.events.EventEditorScreen
import com.splitsmart.feature.home.HomeScreen

object Routes {
    const val Home = "home"
    const val EventList = "event_list"
	const val EventEditor = "event_editor"
	const val EventEditorArg = "eventId"
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home) {
        composable(Routes.Home) { HomeScreen(navController) }
        composable(Routes.EventList) { EventListScreen(navController) }
        composable("${Routes.EventEditor}?${Routes.EventEditorArg}={${Routes.EventEditorArg}}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString(Routes.EventEditorArg)?.toLongOrNull()
            EventEditorScreen(navController, initialEventId = id)
        }
    }
}
