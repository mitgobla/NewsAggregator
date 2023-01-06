package com.mitgobla.newsaggregator.database

import android.app.Application

class ArticleApplication : Application() {
    val database by lazy { ArticleDatabase.getDatabase(this) }
    val repository by lazy { ArticleRepository(database.articleDao()) }
}