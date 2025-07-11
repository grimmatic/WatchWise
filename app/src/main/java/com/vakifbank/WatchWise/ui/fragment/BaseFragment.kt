package com.vakifbank.WatchWise.ui.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R

abstract class BaseFragment : Fragment() {

    protected fun setupFavoriteButton(favoriteButton: FloatingActionButton?) {
        val auth = FirebaseAuth.getInstance()

        favoriteButton?.let { fab ->
            if (auth.currentUser != null) {
                fab.visibility = android.view.View.VISIBLE
                fab.setOnClickListener {
                    findNavController().navigate(R.id.favoriteFragment)
                }
            } else
                fab.visibility = android.view.View.GONE

        }
    }
}