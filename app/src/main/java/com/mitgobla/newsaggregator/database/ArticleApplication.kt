package com.mitgobla.newsaggregator.database

import android.app.Application

/**
 * Article application for the Room database.
 * Other classes can access the database and repository through this class.
 */
class ArticleApplication : Application() {
    val database by lazy { ArticleDatabase.getDatabase(this) }
    val repository by lazy { ArticleRepository(database.articleDao()) }
}