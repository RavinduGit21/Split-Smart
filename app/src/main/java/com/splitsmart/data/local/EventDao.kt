package com.splitsmart.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.splitsmart.data.model.EventEntity
import com.splitsmart.data.model.EventSummary
import com.splitsmart.data.model.ParticipantEntity

@Dao
interface EventDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertEvent(event: EventEntity): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertParticipants(participants: List<ParticipantEntity>)

	@Transaction
	suspend fun insertEventWithParticipants(event: EventEntity, participants: List<ParticipantEntity>): Long {
		val eventId = insertEvent(event)
		insertParticipants(participants.map { it.copy(eventId = eventId) })
		return eventId
	}

	@Query(
		"""
		SELECT e.id, e.name, e.dateMillis, e.totalAmountCents,
			(SELECT COUNT(*) FROM participants p WHERE p.eventId = e.id) AS participantCount
		FROM events e ORDER BY e.dateMillis DESC
		"""
	)
	suspend fun getEventSummaries(): List<EventSummary>
}


