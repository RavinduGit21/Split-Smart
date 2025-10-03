package com.splitsmart.feature.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.splitsmart.data.EventRepository
import com.splitsmart.data.model.EventSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventListViewModel(application: Application) : AndroidViewModel(application) {
	private val repo = EventRepository.get(application)
	private val _events = MutableStateFlow<List<EventSummary>>(emptyList())
	val events: StateFlow<List<EventSummary>> = _events

	init { refresh() }

	fun refresh() {
		viewModelScope.launch {
			_events.value = repo.getEvents()
		}
	}
}


