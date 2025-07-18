package com.vakifbank.WatchWise.base

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import kotlinx.coroutines.launch

abstract class BaseFragment : Fragment() {

    protected fun setupFavoriteButton(favoriteButton: FloatingActionButton?) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        favoriteButton?.let { fab ->
            if (auth.currentUser != null) {
                fab.visibility = View.VISIBLE
                fab.setOnClickListener {
                    findNavController().navigate(R.id.favoriteFragment)
                }
            } else {
                fab.visibility = View.GONE
            }
        }
    }

    protected fun observeBaseState(viewModel: BaseViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.state.collect { state: UIState ->
                    when (state) {
                        is UIState.Init -> {
                        }

                        is UIState.IsLoading -> {
                            handleLoadingState(state.isLoading)
                        }

                        is UIState.ShowToast -> {
                            showToast(state.message)
                            viewModel.resetState()
                        }

                        is UIState.Error -> {
                            showErrorMessage(state.rawResponse)
                            viewModel.resetState()
                        }
                    }
                }
            }
        }
    }

    protected open fun handleLoadingState(isLoading: Boolean) {
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}