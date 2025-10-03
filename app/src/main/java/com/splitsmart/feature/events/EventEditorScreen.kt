package com.splitsmart.feature.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorScreen(navController: NavHostController, initialEventId: Long? = null, vm: EventEditorViewModel = viewModel()) {
    if (initialEventId != null) {
        // load once
        val loaded = vm.state.collectAsState().value.id != initialEventId
        if (loaded) vm.load(initialEventId)
    }
	Scaffold(
		topBar = {
			val topState = vm.state.collectAsState().value
			val currencyTop = NumberFormat.getCurrencyInstance(Locale("si", "LK")).apply {
				currency = java.util.Currency.getInstance("LKR")
			}
			val totalDisplay = currencyTop.format(topState.totalLkr.replace(",", "").toDoubleOrNull() ?: 0.0)
			TopAppBar(
				title = { Text("New Event", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
					}
				},
				actions = {
					Text(totalDisplay, style = MaterialTheme.typography.titleMedium)
				}
			)
		},
		bottomBar = {
			val state = vm.state.collectAsState().value
			val isEditing = state.id != null
			val currency = NumberFormat.getCurrencyInstance(Locale("si", "LK")).apply {
				currency = java.util.Currency.getInstance("LKR")
			}
			BottomAppBar(actions = {
				Text(
					text = "Total: " + currency.format(state.totalLkr.replace(",", "").toDoubleOrNull() ?: 0.0),
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.weight(1f)
				)
				androidx.compose.material3.Button(
					onClick = { vm.save(onSaved = { navController.popBackStack() }, onError = { }) }
				) { Text(if (isEditing) "Save" else "Create") }
			})
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scroll)
    ) {
        // Basic info card
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Event name", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = state.name,
                    onValueChange = vm::setName,
                    placeholder = { Text("e.g., Beach Trip") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Total (LKR)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = state.totalLkr,
                    onValueChange = vm::setTotalLkr,
                    placeholder = { Text("e.g., 10000") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // Items list before total
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Items / Services", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                state.items.forEachIndexed { index, item ->
                    OutlinedTextField(
                        value = item.name,
                        onValueChange = { vm.updateItem(index, name = it) },
                        placeholder = { Text("e.g., Cake") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = item.amountLkr,
                        onValueChange = { vm.updateItem(index, amountLkr = it) },
                        placeholder = { Text("Amount (LKR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(onClick = { vm.removeItem(index) }) { Text("Remove") }
                }
                OutlinedButton(onClick = vm::addItem) { Text("+ Add Item") }
            }
        }

        val dateState = rememberDatePickerState(initialSelectedDateMillis = state.dateMillis)
        var showDate by remember { mutableStateOf(false) }
        if (showDate) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val dialogWidth = (screenWidth * 0.9f).coerceAtMost(400.dp).coerceAtLeast(320.dp)
            
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showDate = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    Modifier
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .width(dialogWidth)
                        .heightIn(min = 400.dp, max = 600.dp)
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select date",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        DatePicker(
                            state = dateState,
                            colors = DatePickerDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                headlineContentColor = MaterialTheme.colorScheme.onSurface,
                                weekdayContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                subheadContentColor = MaterialTheme.colorScheme.onSurface,
                                yearContentColor = MaterialTheme.colorScheme.onSurface,
                                currentYearContentColor = MaterialTheme.colorScheme.primary,
                                selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                                selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                                dayContentColor = MaterialTheme.colorScheme.onSurface,
                                disabledDayContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                                todayDateBorderColor = MaterialTheme.colorScheme.primary,
                                dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                        )
                        LaunchedEffect(dateState.selectedDateMillis) {
                            val selected = dateState.selectedDateMillis
                            if (selected != null && selected != state.dateMillis) {
                                vm.setDate(selected)
                                showDate = false
                            }
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDate = false }) { Text("Cancel") }
                            Spacer(Modifier.width(16.dp))
                            TextButton(onClick = {
                                dateState.selectedDateMillis?.let { vm.setDate(it) }
                                showDate = false
                            }) { Text("OK") }
                        }
                    }
                }
            }
        }
        // Date & location card
        Text("Date & Time", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(state.dateMillis)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    trailingIcon = { IconButton(onClick = { showDate = true }) { Text("Pick") } },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Location", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = state.location,
                    onValueChange = vm::setLocation,
                    placeholder = { Text("e.g., Colombo") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Description", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = state.description,
                    onValueChange = vm::setDescription,
                    placeholder = { Text("e.g., Birthday dinner at Galle Face") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // Participants card
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Participants", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                state.participants.forEachIndexed { index, p ->
                    Text("Name", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = p.name,
                        onValueChange = { vm.updateParticipant(index, name = it) },
                        placeholder = { Text("e.g., Jane D.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Paid amount (optional)", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = p.paidLkr,
                        onValueChange = { vm.updateParticipant(index, paid = it) },
                        placeholder = { Text("Enter paid LKR") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Enter how much each participant paid (optional).", style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = { vm.removeParticipant(index) }) { Text("Remove") }
                }
                OutlinedButton(onClick = vm::addParticipant) { Text("+ Add Participant") }
            }
        }
		Spacer(Modifier.height(8.dp))
	}
}


