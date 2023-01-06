package com.mitgobla.newsaggregator.topics

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Topic::class], version = 6, exportSchema = false)
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
            var topic = Topic(1, "Breaking", favourite = true, notify = false, 0, true)
            topicDao.insert(topic)
            topic = Topic(2, "Following", favourite = true, notify = false, 0, true)
            topicDao.insert(topic)
            topic = Topic(3, "World", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(4, "Technology", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(5, "Business", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(6, "Politics", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(7, "Sports", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(8, "Entertainment", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(9, "Science", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(10, "Health", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(11, "Arts", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(12, "Books", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(13, "Food", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(14, "Travel", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(15, "Fashion", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(16, "Movies", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(17, "Music", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(18, "Television", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(19, "Theater", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            topic = Topic(20, "Photography", favourite = false, notify = false, 0, false)
            topicDao.insert(topic)
            Log.i("TopicRoomDatabase", "Topics added")
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