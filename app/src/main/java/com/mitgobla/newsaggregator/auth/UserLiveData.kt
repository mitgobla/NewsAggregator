package com.mitgobla.newsaggregator.auth

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserLiveData : LiveData<FirebaseUser>() {
    private val auth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        value = auth.currentUser
    }

    override fun onActive() {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        auth.removeAuthStateListener(authStateListener)

    }
}