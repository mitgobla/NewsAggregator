package com.mitgobla.newsaggregator.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg articles: Article)

    @Query("SELECT topic FROM articles_table GROUP BY topic")
    fun getTopics(): Flow<List<String>>

    @Query("SELECT * FROM articles_table WHERE topic = :topic ORDER BY publishedAt DESC")
    fun getArticlesByTopic(topic: String): Flow<List<Article>>

    @Query("DELETE FROM articles_table WHERE topic = :topic")
    suspend fun deleteByTopic(topic: String)

    @Query("DELETE FROM articles_table")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM articles_table WHERE url = :url)")
    suspend fun exists(url: String): Boolean
}