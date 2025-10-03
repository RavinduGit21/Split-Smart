package com.splitsmart.feature.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorScreen(navController: NavHostController, vm: EventEditorViewModel = viewModel()) {
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
		EditorContent(padding, vm, onSaved = { navController.popBackStack() })
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorContent(padding: PaddingValues, vm: EventEditorViewModel, onSaved: () -> Unit) {
	val state = vm.state.collectAsState().value
    val scroll = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFD3E9D7), Color(0xFFF4F7F4))
                )
            )
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = vm::setName,
            label = { Text("Event name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.totalLkr,
            onValueChange = vm::setTotalLkr,
            label = { Text("Total (LKR)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        val dateState = rememberDatePickerState(initialSelectedDateMillis = state.dateMillis)
        var showDate by remember { mutableStateOf(false) }
        if (showDate) {
            DatePickerDialog(
                onDismissRequest = { showDate = false },
                confirmButton = {
                    IconButton(onClick = {
                        dateState.selectedDateMillis?.let { vm.setDate(it) }
                        showDate = false
                    }) { Text("OK") }
                },
                dismissButton = { IconButton(onClick = { showDate = false }) { Text("Cancel") } }
            ) { DatePicker(state = dateState) }
        }
        Text("Date & Time")
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Pick Date & Time", modifier = Modifier.weight(1f))
                    IconButton(onClick = { /* future: choose from contacts */ }) {
                        Icon(Icons.Filled.PersonAddAlt, contentDescription = "Add friend shortcut")
                    }
                }
                androidx.compose.material3.Divider()
                IconButton(onClick = { showDate = true }) { Text("Pick Date & Time") }
                OutlinedTextField(
                    value = state.location,
                    onValueChange = vm::setLocation,
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        OutlinedTextField(
            value = state.location,
            onValueChange = vm::setLocation,
            label = { Text("Location") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
			value = state.description,
			onValueChange = vm::setDescription,
			label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
		)
        Text("Participants")
		state.participants.forEachIndexed { index, p ->
            OutlinedTextField(
				value = p.name,
				onValueChange = { vm.updateParticipant(index, name = it) },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
			)
            OutlinedTextField(
				value = p.shareLkr,
				onValueChange = { vm.updateParticipant(index, share = it) },
				label = { Text("Share override (LKR) - optional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
			)
		}
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = vm::addParticipant) { Icon(Icons.Default.Add, contentDescription = "Add participant") }
        }
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.Button(
            onClick = { vm.save(onSaved = onSaved, onError = { }) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)))
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Save Event", color = Color.White)
            }
        }
	}
}


