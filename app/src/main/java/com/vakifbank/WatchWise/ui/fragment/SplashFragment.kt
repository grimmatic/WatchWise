// presentation/ui/fragment/SplashFragment.kt
package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private var splashTimeout: Long = 2000

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations()
        checkAuthAndNavigate()
    }

    private fun startAnimations() {
        binding.logoImageView.alpha = 0f
        binding.logoImageView.animate()
            .alpha(1f)
            .setDuration(1500)
            .start()

        binding.appNameTextView.alpha = 0f
        binding.appNameTextView.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(500)
            .start()

        binding.taglineTextView.alpha = 0f
        binding.taglineTextView.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(1000)
            .start()
    }

    private fun checkAuthAndNavigate() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded && !isRemoving) {
                if (auth.currentUser != null) {
                    findNavController().navigate(R.id.action_splashFragment_to_MovieListFragment)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_authFragment)
                }
            }
        }, splashTimeout)
    }
}