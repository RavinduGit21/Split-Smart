package com.splitsmart.feature.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.splitsmart.data.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ParticipantInput(val name: String, val shareLkr: String)

data class EventEditorState(
	val name: String = "",
	val dateMillis: Long = System.currentTimeMillis(),
    val location: String = "",
	val description: String = "",
	val totalLkr: String = "",
	val participants: List<ParticipantInput> = listOf(ParticipantInput("", ""))
)

class EventEditorViewModel(application: Application) : AndroidViewModel(application) {
	private val repo = EventRepository.get(application)
	private val _state = MutableStateFlow(EventEditorState())
	val state: StateFlow<EventEditorState> = _state

	fun setName(v: String) { _state.value = _state.value.copy(name = v) }
	fun setDate(millis: Long) { _state.value = _state.value.copy(dateMillis = millis) }
    fun setLocation(v: String) { _state.value = _state.value.copy(location = v) }
	fun setDescription(v: String) { _state.value = _state.value.copy(description = v) }
	fun setTotalLkr(v: String) { _state.value = _state.value.copy(totalLkr = v) }

	fun addParticipant() {
		_state.value = _state.value.copy(participants = _state.value.participants + ParticipantInput("", ""))
	}

	fun updateParticipant(index: Int, name: String? = null, share: String? = null) {
		_state.value = _state.value.copy(
			participants = _state.value.participants.mapIndexed { i, p ->
				if (i == index) p.copy(name = name ?: p.name, shareLkr = share ?: p.shareLkr) else p
			}
		)
	}

	fun save(onSaved: () -> Unit, onError: (String) -> Unit) {
		val s = _state.value
		val total = s.totalLkr.replace(",", "").toDoubleOrNull()
		if (s.name.isBlank()) { onError("Enter event name"); return }
		if (total == null || total <= 0.0) { onError("Enter total amount in LKR"); return }
		val participantNames = s.participants.mapNotNull { it.name.trim().takeIf { n -> n.isNotEmpty() } }
		if (participantNames.isEmpty()) { onError("Add at least one participant") ; return }
		val overrides = s.participants.filter { it.name.isNotBlank() && it.shareLkr.isNotBlank() }
			.associate { it.name.trim() to (it.shareLkr.replace(",", "").toDoubleOrNull() ?: 0.0) }
        viewModelScope.launch {
			repo.addEvent(
				name = s.name.trim(),
				dateMillis = s.dateMillis,
                location = s.location.trim(),
				description = s.description.trim(),
				totalAmountLkr = total,
				participantNames = participantNames,
				editedSharesLkr = overrides
			)
			onSaved()
		}
	}
}


