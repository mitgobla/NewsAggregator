package com.mitgobla.newsaggregator.auth

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.topics.TopicInitializer

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    companion object {
        const val TAG = "ProfileFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private val loginFragment = LoginFragment(::signInWithGoogle)
    private val userFragment = UserFragment(::signOut)

    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), signInOptions)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // check if user is signed in
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            showUserFragment()
        } else {
            showLoginFragment()
        }
    }

    private fun showLoginFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.profileFragmentContainerView, loginFragment)
            .commit()
        // hide sign out menu item

    }


    private fun showUserFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.profileFragmentContainerView, userFragment)
            .commit()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, SIGN_IN_RESULT_CODE)
    }

    private fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
        showLoginFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleLoginResult(task)
        }
    }

    private fun handleLoginResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Potentially a new user signed in, so we need to initialize their topics
                        TopicInitializer.setupTopics(requireContext())
                        // Show the user profile fragment
                        showUserFragment()
                    } else {
                        Log.d(TAG, "handleLoginResult: ${it.exception?.message}")
                    }
                }
            }
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
        }
    }


}