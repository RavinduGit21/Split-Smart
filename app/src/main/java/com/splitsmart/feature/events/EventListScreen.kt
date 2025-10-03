package com.splitsmart.feature.events

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.splitsmart.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(navController: NavHostController) {
	Scaffold(
		topBar = { TopAppBar(title = { Text("Split Smart") }) },
		floatingActionButton = {
			FloatingActionButton(onClick = { navController.navigate(Routes.EventEditor) }) {
				Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
			}
		}
	) { padding ->
		Text("No events yet", modifier = Modifier.fillMaxSize())
	}
}

