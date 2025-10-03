package com.splitsmart.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val name: String,
	val dateMillis: Long,
	val location: String,
	val description: String,
	val totalAmountCents: Long
)

@Entity(
	tableName = "participants",
	foreignKeys = [
		ForeignKey(
			entity = EventEntity::class,
			parentColumns = ["id"],
			childColumns = ["eventId"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index("eventId")]
)
data class ParticipantEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val eventId: Long,
	val name: String,
	val shareAmountCents: Long
)

data class EventSummary(
	val id: Long,
	val name: String,
	val dateMillis: Long,
	val totalAmountCents: Long,
	val participantCount: Int
)


