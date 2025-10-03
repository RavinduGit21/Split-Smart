package com.splitsmart.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.splitsmart.navigation.Routes
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.splitsmart.data.EventRepository
import com.splitsmart.data.model.EventSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, vm: HomeViewModel = viewModel()) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Split Smart") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.EventEditor) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        HomeContent(padding = padding, navController = navController, vm = vm)
    }
}

@Composable
private fun HomeContent(padding: PaddingValues, navController: NavHostController, vm: HomeViewModel) {
    val state by vm.state.collectAsState()
    val currency = NumberFormat.getCurrencyInstance(Locale("si", "LK")).apply {
        currency = java.util.Currency.getInstance("LKR")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(20.dp)
            ) {
                Text("Overall Balance", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = currency.format(state.netBalanceCents / 100.0),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Gathered Amount", color = Color.White.copy(alpha = 0.9f))
                        Text(currency.format(state.totalGatheredCents / 100.0), color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Remaining Overall", color = Color.White.copy(alpha = 0.9f))
                        Text(currency.format((state.totalBudgetCents - state.totalGatheredCents).coerceAtLeast(0) / 100.0), color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Text("Recent Activity", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.events) { e ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.clickable { navController.navigate("${Routes.EventEditor}?${Routes.EventEditorArg}=${e.id}") }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        val dateFmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(e.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text(currency.format(e.totalAmountCents / 100.0), fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(dateFmt.format(Date(e.dateMillis)), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Text("Gathered: ${currency.format(e.totalPaidCents / 100.0)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)

                        // Budget completion bar
                        val total = e.totalAmountCents.coerceAtLeast(0)
                        val funded = e.totalPaidCents.coerceAtLeast(0)
                        val progress = if (total > 0) funded.toFloat() / total.toFloat() else 0f
                        androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
                        val percent = (progress * 100).toInt()
                        val remaining = (total - funded).coerceAtLeast(0)
                        Text("$percent% funded • Remaining: ${currency.format(remaining / 100.0)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Routes.EventEditor) }, modifier = Modifier.fillMaxWidth()) {
            Text("Add New Event")
        }
    }
}

data class HomeState(
    val events: List<EventSummary> = emptyList(),
    val totalBudgetCents: Long = 0,
    val totalGatheredCents: Long = 0
) {
    val netBalanceCents: Long get() = totalGatheredCents - totalBudgetCents
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = EventRepository.get(application)
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val events = repo.getEvents()
            val totalBudget = events.sumOf { it.totalAmountCents }
            val totalGathered = events.sumOf { it.totalPaidCents }
            _state.value = HomeState(
                events = events.take(10),
                totalBudgetCents = totalBudget,
                totalGatheredCents = totalGathered
            )
        }
    }
}


