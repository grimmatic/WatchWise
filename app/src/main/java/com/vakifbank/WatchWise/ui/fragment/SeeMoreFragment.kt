package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.databinding.FragmentSeeMoreBinding


class SeeMoreFragment : Fragment() {

    private var _binding: FragmentSeeMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSeeMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButtonListener()




    }


    fun backButtonListener(){
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

    }
}