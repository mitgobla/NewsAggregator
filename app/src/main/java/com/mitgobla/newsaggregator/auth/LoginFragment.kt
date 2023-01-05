package com.mitgobla.newsaggregator.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.mitgobla.newsaggregator.R

// pass in a function to be called when the sign in button is clicked

class LoginFragment(private val signInWithGoogle: () -> Unit) : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginButton: AppCompatButton = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            signInWithGoogle.invoke()
        }
    }
}