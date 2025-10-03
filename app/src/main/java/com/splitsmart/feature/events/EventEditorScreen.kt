package com.splitsmart.feature.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorScreen(navController: NavHostController) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("New Event") },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
					}
				}
			)
		}
	) { padding ->
		EditorContent(padding)
	}
}

@Composable
private fun EditorContent(padding: PaddingValues) {
	val (eventName, setEventName) = remember { mutableStateOf("") }
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp)
	) {
		TextField(
			value = eventName,
			onValueChange = setEventName,
			label = { Text("Event name") },
			modifier = Modifier.fillMaxSize(fraction = 0.0f)
		)
		Text("More fields coming soon…", modifier = Modifier.padding(top = 16.dp))
	}
}


