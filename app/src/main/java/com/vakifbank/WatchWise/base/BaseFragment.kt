package com.vakifbank.WatchWise.base

import android.view.View
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
                fab.visibility = View.VISIBLE
                fab.setOnClickListener {
                    findNavController().navigate(R.id.favoriteFragment)
                }
            } else
                fab.visibility = View.GONE

        }
    }
}