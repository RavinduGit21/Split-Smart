package com.splitsmart.data

import android.content.Context
import com.splitsmart.data.local.AppDatabase
import com.splitsmart.data.local.EventDao
import com.splitsmart.data.model.EventEntity
import com.splitsmart.data.model.EventSummary
import com.splitsmart.data.model.ParticipantEntity
import com.splitsmart.data.model.ExpenseItemEntity

class EventRepository private constructor(private val dao: EventDao) {

	suspend fun getEvents(): List<EventSummary> = dao.getEventSummaries()

    suspend fun getEventDetail(eventId: Long): Triple<EventEntity, List<ParticipantEntity>, List<ExpenseItemEntity>>? {
		val event = dao.getEvent(eventId) ?: return null
		val participants = dao.getParticipants(eventId)
        val items = dao.getItems(eventId)
        return Triple(event, participants, items)
	}

    suspend fun addEvent(
		name: String,
		dateMillis: Long,
        location: String,
		description: String,
        totalAmountLkr: Double,
		participantNames: List<String>,
        editedSharesLkr: Map<String, Double>?,
        emoji: String,
        participantPaidLkr: Map<String, Double> = emptyMap(),
        items: List<Pair<String, Double>> = emptyList()
    ): Long {
        val itemsCents = items.map { (label, amount) -> label to lkrToCents(amount) }
        val itemsTotal = itemsCents.sumOf { it.second }
        val totalCents = if (totalAmountLkr > 0) lkrToCents(totalAmountLkr) else itemsTotal
        val shares = computeShares(totalCents, participantNames, editedSharesLkr)
        val eventId = dao.insertEvent(
			EventEntity(
				name = name,
				dateMillis = dateMillis,
                location = location,
				description = description,
                totalAmountCents = totalCents,
                emoji = emoji
			)
		)
        dao.insertParticipants(
            shares.map { (participantName, amountCents) ->
                val paid = participantPaidLkr[participantName]?.let { lkrToCents(it) } ?: 0L
                ParticipantEntity(eventId = eventId, name = participantName, shareAmountCents = amountCents, paidAmountCents = paid)
            }
        )
        if (itemsCents.isNotEmpty()) {
            dao.insertItems(itemsCents.map { (label, amount) ->
                ExpenseItemEntity(eventId = eventId, name = label, amountCents = amount)
            })
        }
		return eventId
	}

    suspend fun updateEvent(
		id: Long,
		name: String,
		dateMillis: Long,
		location: String,
		description: String,
		totalAmountLkr: Double,
		participantNames: List<String>,
		editedSharesLkr: Map<String, Double>?,
        emoji: String,
        participantPaidLkr: Map<String, Double> = emptyMap(),
        items: List<Pair<String, Double>> = emptyList()
	) {
        val itemsCents = items.map { (label, amount) -> label to lkrToCents(amount) }
        val itemsTotal = itemsCents.sumOf { it.second }
        val totalCents = if (totalAmountLkr > 0) lkrToCents(totalAmountLkr) else itemsTotal
		val shares = computeShares(totalCents, participantNames, editedSharesLkr)
        dao.updateEventWithParticipants(
			EventEntity(
				id = id,
				name = name,
				dateMillis = dateMillis,
				location = location,
				description = description,
				totalAmountCents = totalCents,
				emoji = emoji
			),
            shares.map { (participantName, amountCents) ->
                val paid = participantPaidLkr[participantName]?.let { lkrToCents(it) } ?: 0L
                ParticipantEntity(eventId = id, name = participantName, shareAmountCents = amountCents, paidAmountCents = paid)
            },
            itemsCents.map { (label, amount) -> ExpenseItemEntity(eventId = id, name = label, amountCents = amount) }
		)
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


