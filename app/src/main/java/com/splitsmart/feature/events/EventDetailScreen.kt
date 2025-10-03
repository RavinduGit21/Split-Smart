package com.splitsmart.feature.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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
import com.splitsmart.data.model.EventEntity
import com.splitsmart.data.model.ParticipantEntity
import com.splitsmart.data.model.ExpenseItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(navController: NavHostController, eventId: Long, vm: EventDetailViewModel = viewModel()) {
    // Ensure the event is loaded when this screen is shown
    androidx.compose.runtime.LaunchedEffect(eventId) {
        vm.loadEvent(eventId)
    }
    val state by vm.state.collectAsState()
    val currency = NumberFormat.getCurrencyInstance(Locale("si", "LK")).apply {
        this.currency = java.util.Currency.getInstance("LKR")
    }
    val context = LocalContext.current
    val screenWidthPx = context.resources.displayMetrics.widthPixels
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Report", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val current = state
                        if (current.event != null) {
                            if (context is android.app.Activity) {
                                ShareUtils.renderInActivity(
                                    activity = context,
                                    widthPx = screenWidthPx,
                                    content = {
                                        com.splitsmart.ui.theme.SplitSmartTheme {
                                            val lkrFmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("si", "LK")).apply {
                                                this.currency = java.util.Currency.getInstance("LKR")
                                            }
                                            androidx.compose.foundation.layout.Column(
                                                modifier = androidx.compose.ui.Modifier
                                                    .background(androidx.compose.ui.graphics.Color.White)
                                                    .padding(20.dp)
                                            ) {
                                                InvoiceComposable(state = current, currency = lkrFmt)
                                            }
                                        }
                                    },
                                    onReady = { bmp ->
                                        ShareUtils.shareReportBitmap(context, bmp)
                                    }
                                )
                            } else {
                                val off = ShareUtils.renderComposableToBitmap(
                                    context = context,
                                    widthPx = screenWidthPx,
                                    heightPx = 0
                                ) {
                                    com.splitsmart.ui.theme.SplitSmartTheme {
                                        val lkrFmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("si", "LK")).apply {
                                            this.currency = java.util.Currency.getInstance("LKR")
                                        }
                                        androidx.compose.foundation.layout.Column(
                                            modifier = androidx.compose.ui.Modifier
                                                .background(androidx.compose.ui.graphics.Color.White)
                                                .padding(20.dp)
                                        ) {
                                            InvoiceComposable(state = current, currency = lkrFmt)
                                        }
                                    }
                                }
                                ShareUtils.shareReportBitmap(context, off)
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { navController.navigate("${Routes.EventEditor}?${Routes.EventEditorArg}=$eventId") }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        if (state.event == null) {
            Text("Loading...", modifier = Modifier
                .fillMaxSize()
                .padding(16.dp))
        } else {
            val event = state.event!!
            val participants = state.participants
            val items = state.items
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Event Header Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Text(event.name, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(event.dateMillis)), color = Color.White.copy(alpha = 0.9f))
                        if (event.location.isNotBlank()) {
                            Text("📍 ${event.location}", color = Color.White.copy(alpha = 0.9f))
                        }
                        if (event.description.isNotBlank()) {
                            Text(event.description, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }

                // Budget Summary Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Budget Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Budget:", style = MaterialTheme.typography.bodyLarge)
                            Text(currency.format(event.totalAmountCents / 100.0), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gathered Amount:", style = MaterialTheme.typography.bodyLarge)
                            Text(currency.format(state.totalPaidCents / 100.0), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                        val progress = if (event.totalAmountCents > 0) state.totalPaidCents.toFloat() / event.totalAmountCents.toFloat() else 0f
                        val percent = (progress * 100).toInt()
                        val remaining = (event.totalAmountCents - state.totalPaidCents).coerceAtLeast(0)
                        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
                        Text("$percent% funded • Remaining: ${currency.format(remaining / 100.0)}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Items & Services Card
                if (items.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Items & Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            items.forEach { item ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.name, style = MaterialTheme.typography.bodyLarge)
                                    Text(currency.format(item.amountCents / 100.0), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                // Participants Card
                if (participants.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Participants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            participants.forEach { participant ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(participant.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                        Text("Share: ${currency.format(participant.shareAmountCents / 100.0)}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("Paid: ${currency.format(participant.paidAmountCents / 100.0)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }
                }

                // Edit Button
                Button(
                    onClick = { navController.navigate("${Routes.EventEditor}?${Routes.EventEditorArg}=$eventId") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Event")
                }
            }
        }
    }
}

data class EventDetailState(
    val event: EventEntity? = null,
    val participants: List<ParticipantEntity> = emptyList(),
    val items: List<ExpenseItemEntity> = emptyList(),
    val totalPaidCents: Long = 0
)

class EventDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = EventRepository.get(application)
    private val _state = MutableStateFlow(EventDetailState())
    val state: StateFlow<EventDetailState> = _state

    fun loadEvent(eventId: Long) {
        viewModelScope.launch {
            val detail = repo.getEventDetail(eventId)
            if (detail != null) {
                val (event, participants, items) = detail
                val totalPaid = participants.sumOf { it.paidAmountCents }
                _state.value = EventDetailState(
                    event = event,
                    participants = participants,
                    items = items,
                    totalPaidCents = totalPaid
                )
            }
        }
    }
}
