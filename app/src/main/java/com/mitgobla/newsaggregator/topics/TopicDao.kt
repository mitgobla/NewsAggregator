package com.mitgobla.newsaggregator.topics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Topic Data Access Object
@Dao
interface TopicDao {
    @Query("SELECT *, rowid FROM topics_table ORDER BY topic, favourite ASC")
    fun getSortedTopics(): Flow<List<Topic>>

    @Update
    suspend fun updateTopic(topic: Topic)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(topic: Topic)

    @Query("DELETE FROM topics_table")
    suspend fun deleteAll()

}