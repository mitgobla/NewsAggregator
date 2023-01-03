package com.mitgobla.newsaggregator.topics

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Topic::class], version = 4, exportSchema = false)
abstract class TopicRoomDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao

    private class TopicDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.topicDao())
                }
            }
        }

        suspend fun populateDatabase(topicDao: TopicDao) {
            topicDao.deleteAll()

            var topic = Topic(1, "Swansea", favourite = false, notify = false)
            topicDao.insert(topic)
            topic = Topic(2, "Cardiff", favourite = false, notify = false)
            topicDao.insert(topic)
        }
    }

    companion object {
        // Ensure we only have 1 instance of a Topic database at one time
        @Volatile
        private var INSTANCE: TopicRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TopicRoomDatabase {
            // If INSTANCE is not null, we return it. Otherwise we synchronously create a new instance
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TopicRoomDatabase::class.java,
                    "topic_database.db"
                )
                    .addCallback(TopicDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // Returns the new instance
                instance
            }
        }
    }
}