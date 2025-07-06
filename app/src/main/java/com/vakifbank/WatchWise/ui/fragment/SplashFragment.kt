package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import android.os.Handler
import android.os.Looper
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private var splashTimeout: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations()
        navigateToHome()
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

    private fun navigateToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded && !isRemoving) {
                findNavController().navigate(R.id.action_splashFragment_to_MovieListFragment)
            }
        }, splashTimeout)
    }
}