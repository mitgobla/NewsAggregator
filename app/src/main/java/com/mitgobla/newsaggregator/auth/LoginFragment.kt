package com.mitgobla.newsaggregator.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.mitgobla.newsaggregator.R

/**
 * Fragment for the login screen.
 * Takes a click listener to run the Google sign-in intent.
 */
class LoginFragment(private val signInWithGoogle: () -> Unit) : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginButton: AppCompatButton = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            signInWithGoogle.invoke()
        }
    }
}