package com.splitsmart.feature.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventReportCards(state: EventDetailState, currency: NumberFormat) {
	val event = state.event ?: return
	val participants = state.participants
	val items = state.items

	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
					Text("\uD83D\uDCCD ${event.location}", color = Color.White.copy(alpha = 0.9f))
				}
				if (event.description.isNotBlank()) {
					Text(event.description, color = Color.White.copy(alpha = 0.9f))
				}
			}
		}

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
				Text("${percent}% funded • Remaining: ${currency.format(remaining / 100.0)}", style = MaterialTheme.typography.bodySmall)
			}
		}

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
	}
}


