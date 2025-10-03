package com.splitsmart.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.splitsmart.data.model.EventEntity
import com.splitsmart.data.model.ParticipantEntity
import com.splitsmart.data.model.ExpenseItemEntity

    @Database(
    	entities = [EventEntity::class, ParticipantEntity::class, ExpenseItemEntity::class],
    	version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun eventDao(): EventDao

	companion object {
		@Volatile private var instance: AppDatabase? = null

		fun get(context: Context): AppDatabase =
			instance ?: synchronized(this) {
				instance ?: Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"splitsmart.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
			}
	}
}


