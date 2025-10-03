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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.splitsmart.navigation.Routes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.Divider
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(navController: NavHostController, vm: EventListViewModel = viewModel()) {
	Scaffold(
		topBar = { TopAppBar(title = { Text("Split Smart") }) },
		floatingActionButton = {
			FloatingActionButton(onClick = { navController.navigate(Routes.EventEditor) }) {
				Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
			}
		}
	) { padding ->
		EventListContent(padding = padding, vm = vm, navController = navController)
	}
}

@Composable
private fun EventListContent(padding: PaddingValues, vm: EventListViewModel, navController: NavHostController) {
	val events by vm.events.collectAsState()
	// Refresh when screen resumes
	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) vm.refresh()
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}
	if (events.isEmpty()) {
		Text("No events yet", modifier = Modifier.fillMaxSize())
		return
	}
	val currency = NumberFormat.getCurrencyInstance(Locale("si", "LK"))
	currency.currency = java.util.Currency.getInstance("LKR")
	val dateFmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    LazyColumn(modifier = Modifier
        .padding(padding)
        .padding(12.dp)) {
        items(events) { e ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable { navController.navigate("${Routes.EventEditor}?${Routes.EventEditorArg}=${'$'}{e.id}") }
            ) {
                ListItem(
                    leadingContent = { Text("🎉") },
                    headlineContent = { Text(e.name) },
                    supportingContent = {
                        Text("${'$'}{e.participantCount} participants • ${'$'}{dateFmt.format(Date(e.dateMillis))}")
                    },
                    trailingContent = {
                        Text(currency.format(e.totalAmountCents / 100.0))
                    }
                )
            }
        }
    }
}

