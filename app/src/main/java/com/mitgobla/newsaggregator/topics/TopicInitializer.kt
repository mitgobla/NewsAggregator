package com.mitgobla.newsaggregator.topics

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R

/**
 * Helper class to ensure user has topics in their database
 */
class TopicInitializer {
    companion object {
        fun setupTopics(context: Context) {
            // Check if user is logged in
            val user = GoogleSignIn.getLastSignedInAccount(context)
            if (user != null) {
                val db = Firebase.firestore
                val topicsResource = context.resources.getStringArray(R.array.topics)
                for (topic in topicsResource) {
                    val topicObject = Topic(topic)
                    // Check if topic does not exist in database before adding.
                    // Prevents us overwriting user preferences when app is launched
                    db.collection("topics").document(topic).get().addOnSuccessListener { document ->
                        if (!document.exists()) {
                            db.collection("topics").document(topic).set(topicObject)
                        }
                    }
                }
            }
        }
    }
}