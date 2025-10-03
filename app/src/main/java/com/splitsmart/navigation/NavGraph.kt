package com.splitsmart.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.splitsmart.feature.events.EventListScreen
import com.splitsmart.feature.events.EventEditorScreen

object Routes {
	const val EventList = "event_list"
	const val EventEditor = "event_editor"
}

@Composable
fun AppNavHost(navController: NavHostController) {
	NavHost(navController = navController, startDestination = Routes.EventList) {
		composable(Routes.EventList) { EventListScreen(navController) }
		composable(Routes.EventEditor) { EventEditorScreen(navController) }
	}
}
