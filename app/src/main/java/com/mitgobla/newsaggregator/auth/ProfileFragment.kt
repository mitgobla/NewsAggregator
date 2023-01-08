package com.mitgobla.newsaggregator.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
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

/**
 * Fragment for the profile page.
 * It will display [LoginFragment] if the user is not logged in,
 * and [UserFragment] if the user is logged in.
 */
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
        // Check if user is signed in
        // and display the appropriate fragment
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            showUserFragment()
        } else {
            showLoginFragment()
        }
    }

    /**
     * Show the login fragment.
     */
    private fun showLoginFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.profileFragmentContainerView, loginFragment)
            .commit()
    }

    /**
     * Show the user fragment.
     */
    private fun showUserFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.profileFragmentContainerView, userFragment)
            .commit()
    }

    /**
     * Start the Google sign-in intent, which allows the user
     * to sign in with a Google account.
     */
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, SIGN_IN_RESULT_CODE)
    }

    /**
     * Sign out the user.
     * It will notify any authentication observers that the user has signed out.
     */
    private fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
        showLoginFragment()
    }

    /**
     * Handle the result of the Google sign-in intent.
     * If the sign-in was successful, it will authenticate the user with Firebase.
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleLoginResult(task)
        }
    }

    /**
     * Handle user sign-in result.
     * If the sign-in was successful, it will authenticate the user with Firebase.
     * Otherwise, it will display an error message in the form of a Toast.
     */
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
                        Toast.makeText(requireContext(),
                            context?.getString(R.string.unsuccessfulLogin, it.exception?.message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(requireContext(),
                context?.getString(R.string.unsuccessfulLogin, e.message), Toast.LENGTH_SHORT).show()
        }
    }


}