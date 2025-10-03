package com.splitsmart.feature.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.splitsmart.data.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ParticipantInput(val name: String, val shareLkr: String = "", val paidLkr: String = "")
data class ExpenseItemInput(val name: String, val amountLkr: String)

data class EventEditorState(
    val id: Long? = null,
	val name: String = "",
	val dateMillis: Long = System.currentTimeMillis(),
    val location: String = "",
	val description: String = "",
    val totalLkr: String = "",
    val participants: List<ParticipantInput> = listOf(ParticipantInput("", "")),
    val items: List<ExpenseItemInput> = listOf(ExpenseItemInput("", "")),
    val emoji: String = "🎉"
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
    fun setEmoji(v: String) { _state.value = _state.value.copy(emoji = v) }

    fun addParticipant() {
        _state.value = _state.value.copy(participants = _state.value.participants + ParticipantInput("", ""))
	}

    fun updateParticipant(index: Int, name: String? = null, share: String? = null, paid: String? = null) {
		_state.value = _state.value.copy(
			participants = _state.value.participants.mapIndexed { i, p ->
                if (i == index) p.copy(name = name ?: p.name, shareLkr = share ?: p.shareLkr, paidLkr = paid ?: p.paidLkr) else p
			}
		)
	}

    fun removeParticipant(index: Int) {
        _state.value = _state.value.copy(
            participants = _state.value.participants.filterIndexed { i, _ -> i != index }
        )
    }

    fun addItem() {
        val newList = _state.value.items + ExpenseItemInput("", "")
        _state.value = _state.value.copy(items = newList)
        recomputeTotalFromItems()
    }

    fun updateItem(index: Int, name: String? = null, amountLkr: String? = null) {
        _state.value = _state.value.copy(
            items = _state.value.items.mapIndexed { i, it ->
                if (i == index) it.copy(name = name ?: it.name, amountLkr = amountLkr ?: it.amountLkr) else it
            }
        )
        recomputeTotalFromItems()
    }

    fun removeItem(index: Int) {
        _state.value = _state.value.copy(items = _state.value.items.filterIndexed { i, _ -> i != index })
        recomputeTotalFromItems()
    }

    private fun recomputeTotalFromItems() {
        val total = _state.value.items.mapNotNull { it.amountLkr.replace(",", "").toDoubleOrNull() }.sum()
        if (total > 0.0) _state.value = _state.value.copy(totalLkr = total.toString())
    }

    fun load(eventId: Long) {
        viewModelScope.launch {
            val detail = repo.getEventDetail(eventId) ?: return@launch
            val (event, participants, items) = detail
            _state.value = EventEditorState(
                id = event.id,
                name = event.name,
                dateMillis = event.dateMillis,
                location = event.location,
                description = event.description,
                totalLkr = (event.totalAmountCents / 100.0).toString(),
                participants = participants.map { ParticipantInput(it.name, (it.shareAmountCents / 100.0).toString(), (it.paidAmountCents / 100.0).toString()) },
                items = items.map { ExpenseItemInput(it.name, (it.amountCents / 100.0).toString()) },
                emoji = event.emoji
            )
        }
    }

    fun save(onSaved: () -> Unit, onError: (String) -> Unit) {
		val s = _state.value
        val total = s.totalLkr.replace(",", "").toDoubleOrNull()
		if (s.name.isBlank()) { onError("Enter event name"); return }
		if (total == null || total <= 0.0) { onError("Enter total amount in LKR"); return }
        val participantNames = s.participants.mapNotNull { it.name.trim().takeIf { n -> n.isNotEmpty() } }
		if (participantNames.isEmpty()) { onError("Add at least one participant") ; return }
        val overrides = emptyMap<String, Double>()
        val paidMap = s.participants.filter { it.name.isNotBlank() && it.paidLkr.isNotBlank() }
            .associate { it.name.trim() to (it.paidLkr.replace(",", "").toDoubleOrNull() ?: 0.0) }
        val items = s.items.filter { it.name.isNotBlank() && it.amountLkr.isNotBlank() }
            .mapNotNull { it.name.trim() to (it.amountLkr.replace(",", "").toDoubleOrNull() ?: return@mapNotNull null) }
        viewModelScope.launch {
            if (s.id == null) {
                repo.addEvent(
                    name = s.name.trim(),
                    dateMillis = s.dateMillis,
                    location = s.location.trim(),
                    description = s.description.trim(),
                    totalAmountLkr = total,
                    participantNames = participantNames,
                    editedSharesLkr = overrides,
                    participantPaidLkr = paidMap,
                    emoji = s.emoji,
                    items = items
                )
            } else {
                repo.updateEvent(
                    id = s.id,
                    name = s.name.trim(),
                    dateMillis = s.dateMillis,
                    location = s.location.trim(),
                    description = s.description.trim(),
                    totalAmountLkr = total,
                    participantNames = participantNames,
                    editedSharesLkr = overrides,
                    participantPaidLkr = paidMap,
                    emoji = s.emoji,
                    items = items
                )
            }
            onSaved()
        }
	}
}


