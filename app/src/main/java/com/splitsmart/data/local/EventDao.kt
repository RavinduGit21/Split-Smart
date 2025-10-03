package com.splitsmart.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.splitsmart.data.model.EventEntity
import com.splitsmart.data.model.EventSummary
import com.splitsmart.data.model.ParticipantEntity
import com.splitsmart.data.model.ExpenseItemEntity

@Dao
interface EventDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertEvent(event: EventEntity): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertParticipants(participants: List<ParticipantEntity>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertItems(items: List<ExpenseItemEntity>)

	@Transaction
	suspend fun insertEventWithParticipants(event: EventEntity, participants: List<ParticipantEntity>, items: List<ExpenseItemEntity>): Long {
		val eventId = insertEvent(event)
		insertParticipants(participants.map { it.copy(eventId = eventId) })
		insertItems(items.map { it.copy(eventId = eventId) })
		return eventId
	}

	@Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
	suspend fun getEvent(eventId: Long): EventEntity?

	@Query("SELECT * FROM participants WHERE eventId = :eventId")
	suspend fun getParticipants(eventId: Long): List<ParticipantEntity>

	@Query("SELECT * FROM expense_items WHERE eventId = :eventId")
	suspend fun getItems(eventId: Long): List<ExpenseItemEntity>

	@Query("DELETE FROM participants WHERE eventId = :eventId")
	suspend fun deleteParticipantsForEvent(eventId: Long)

	@Query("DELETE FROM expense_items WHERE eventId = :eventId")
	suspend fun deleteItemsForEvent(eventId: Long)

	@Transaction
	suspend fun updateEventWithParticipants(event: EventEntity, participants: List<ParticipantEntity>, items: List<ExpenseItemEntity>) {
		// Replace event and its participants
		insertEvent(event)
		deleteParticipantsForEvent(event.id)
		insertParticipants(participants)
		deleteItemsForEvent(event.id)
		insertItems(items)
	}

	@Query(
		"""
		SELECT e.id, e.name, e.dateMillis, e.totalAmountCents,
			(SELECT COUNT(*) FROM participants p WHERE p.eventId = e.id) AS participantCount,
			(SELECT COALESCE(SUM(p.paidAmountCents),0) FROM participants p WHERE p.eventId = e.id) AS totalPaidCents
		FROM events e ORDER BY e.dateMillis DESC
		"""
	)
	suspend fun getEventSummaries(): List<EventSummary>
}


