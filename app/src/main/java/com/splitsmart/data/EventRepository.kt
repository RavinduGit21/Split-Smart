package com.splitsmart.data

import android.content.Context
import com.splitsmart.data.local.AppDatabase
import com.splitsmart.data.local.EventDao
import com.splitsmart.data.model.EventEntity
import com.splitsmart.data.model.EventSummary
import com.splitsmart.data.model.ParticipantEntity

class EventRepository private constructor(private val dao: EventDao) {

	suspend fun getEvents(): List<EventSummary> = dao.getEventSummaries()

    suspend fun addEvent(
		name: String,
		dateMillis: Long,
        location: String,
		description: String,
		totalAmountLkr: Double,
		participantNames: List<String>,
		editedSharesLkr: Map<String, Double>?
	): Long {
		val totalCents = lkrToCents(totalAmountLkr)
		val shares = computeShares(totalCents, participantNames, editedSharesLkr)
        val eventId = dao.insertEvent(
			EventEntity(
				name = name,
				dateMillis = dateMillis,
                location = location,
				description = description,
				totalAmountCents = totalCents
			)
		)
		dao.insertParticipants(
			shares.map { (participantName, amountCents) ->
				ParticipantEntity(eventId = eventId, name = participantName, shareAmountCents = amountCents)
			}
		)
		return eventId
	}

	private fun lkrToCents(amount: Double): Long = (amount * 100.0).toLong()

	private fun computeShares(
		totalCents: Long,
		participants: List<String>,
		editedSharesLkr: Map<String, Double>?
	): Map<String, Long> {
		if (participants.isEmpty()) return emptyMap()
		val overrides = editedSharesLkr?.mapValues { (_, v) -> lkrToCents(v) } ?: emptyMap()
		val remainingParticipants = participants.filterNot { overrides.containsKey(it) }
		val remainingTotal = totalCents - overrides.values.sum()
		val equalShare = if (remainingParticipants.isNotEmpty()) remainingTotal / remainingParticipants.size else 0L
		val base = remainingParticipants.associateWith { equalShare } + overrides
		// adjust rounding to exactly match total
		val diff = totalCents - base.values.sum()
		return if (diff == 0L) base else base.toMutableMap().apply {
			remainingParticipants.firstOrNull()?.let { put(it, (get(it) ?: 0L) + diff) }
		}
	}

	companion object {
		@Volatile private var instance: EventRepository? = null
		fun get(context: Context): EventRepository = instance ?: synchronized(this) {
			instance ?: EventRepository(AppDatabase.get(context).eventDao()).also { instance = it }
		}
	}
}


