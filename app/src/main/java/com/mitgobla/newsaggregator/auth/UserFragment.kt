package com.mitgobla.newsaggregator.auth

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.mitgobla.newsaggregator.R

class UserFragment(private var signOutClickListener: () -> Unit) : Fragment(R.layout.fragment_user) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // get the logged in user
        val user = FirebaseAuth.getInstance().currentUser

        // Display the user's profile picture
        val profilePicture = view.findViewById<AppCompatImageView>(R.id.profileImage)
        profilePicture.load(user?.photoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_baseline_account_circle_128)
            transformations(CircleCropTransformation())
        }

        // Display the user's name
        val profileName = view.findViewById<AppCompatTextView>(R.id.profileName)
        if (user != null) {
            profileName.text = getString(R.string.profileNamePlaceholder, user.displayName)
        } else {
            profileName.text = getString(R.string.profileNamePlaceholder, getString(R.string.anonymous))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val signOutAction: MenuItem = menu.findItem(R.id.toolbarSignOut)
        signOutAction.setOnMenuItemClickListener {
            signOutClickListener.invoke()
            true
        }
    }
}