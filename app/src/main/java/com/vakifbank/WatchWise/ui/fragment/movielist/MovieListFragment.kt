package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentMovieListBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.usecase.GetMovieDetailsUseCase
import com.vakifbank.WatchWise.ui.fragment.movielist.MovieListViewModel
import com.vakifbank.WatchWise.ui.adapter.MovieAdapter
import com.vakifbank.WatchWise.ui.adapter.MovieListType
import com.vakifbank.WatchWise.ui.adapter.SearchMovieAdapter
import com.vakifbank.WatchWise.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class MovieListFragment : BaseFragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieListViewModel by viewModels()

    @Inject
    lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    private lateinit var popularMovieAdapter: MovieAdapter
    private lateinit var topRatedMovieAdapter: MovieAdapter
    private lateinit var upcomingMovieAdapter: MovieAdapter
    private lateinit var searchMovieAdapter: SearchMovieAdapter

    private val popularMovieList = mutableListOf<Movie>()
    private val topRatedMovieList = mutableListOf<Movie>()
    private val upcomingMovieList = mutableListOf<Movie>()
    private val searchMovieList = mutableListOf<Movie>()

    private var isSearchMode = false
    private var lastSearchQuery = ""

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupSearchSection()
        setupAuthButton()
        setupFavoriteButton(binding.favoriteFab)
        setupObservers()
        viewModel.loadAllMovies()
    }

    private fun setupObservers() {
        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            popularMovieList.clear()
            popularMovieList.addAll(movies)
            popularMovieAdapter.notifyDataSetChanged()
        }

        viewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            topRatedMovieList.clear()
            topRatedMovieList.addAll(movies)
            topRatedMovieAdapter.notifyDataSetChanged()
        }

        viewModel.upcomingMovies.observe(viewLifecycleOwner) { movies ->
            upcomingMovieList.clear()
            upcomingMovieList.addAll(movies)
            upcomingMovieAdapter.notifyDataSetChanged()
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            searchMovieAdapter.updateMovies(movies)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        popularMovieAdapter = MovieAdapter(popularMovieList, MovieListType.POPULAR) { movie ->
            onMovieClick(movie)
        }
        binding.populerRecyclerView.apply {
            adapter = popularMovieAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        topRatedMovieAdapter = MovieAdapter(topRatedMovieList, MovieListType.TOP_RATED) { movie ->
            onMovieClick(movie)
        }
        binding.enIyiRecyclerView.apply {
            adapter = topRatedMovieAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        upcomingMovieAdapter = MovieAdapter(upcomingMovieList, MovieListType.UPCOMING) { movie ->
            onMovieClick(movie)
        }
        binding.yakindaRecyclerView.apply {
            adapter = upcomingMovieAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        searchMovieAdapter = SearchMovieAdapter(searchMovieList) { movie ->
            onMovieClick(movie)
        }
        binding.searchResultsRecyclerView.apply {
            adapter = searchMovieAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSearchSection() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                lastSearchQuery = query

                if (query.isNotEmpty()) {
                    if (!isSearchMode) {
                        switchToSearchMode()
                    }
                    viewModel.searchMovies(query)
                } else if (isSearchMode) {
                    switchToNormalMode()
                }
            }
        })

        binding.backButton.setOnClickListener {
            clearSearchAndSwitchToNormal()
        }
    }

    private fun switchToSearchMode() {
        isSearchMode = true
        binding.normalContentScrollView.visibility = View.GONE
        binding.searchResultsRecyclerView.visibility = View.VISIBLE
        binding.backButton.visibility = View.VISIBLE
    }

    private fun switchToNormalMode() {
        isSearchMode = false
        lastSearchQuery = ""
        binding.normalContentScrollView.visibility = View.VISIBLE
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.backButton.visibility = View.GONE
        searchMovieAdapter.clearMovies()
    }

    private fun clearSearchAndSwitchToNormal() {
        binding.searchEditText.setText("")
        binding.searchEditText.clearFocus()
        switchToNormalMode()
    }

    private fun setupAuthButton() {
        binding.logoutButton.setOnClickListener {
            if (auth.currentUser != null) {
                showLogoutConfirmationDialog()
            } else {
                navigateToAuth()
            }
        }
        updateAuthButton()
    }

    private fun updateAuthButton() {
        if (auth.currentUser != null) {
            binding.logoutButton.setImageResource(android.R.drawable.ic_lock_power_off)
            binding.logoutButton.contentDescription = "Çıkış Yap"
        } else {
            binding.logoutButton.setImageResource(R.drawable.login_register)
            binding.logoutButton.contentDescription = "Giriş Yap"
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Çıkış Yap")
            .setMessage("Çıkış yapmak istediginizden emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                logout()
            }
            .setNegativeButton("Hayır", null)
            .show()
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(requireContext(), "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
        updateAuthButton()
        setupFavoriteButton(binding.favoriteFab)
    }

    private fun navigateToAuth() {
        findNavController().navigate(R.id.action_MovieListFragment_to_authFragment)
    }

    private fun onMovieClick(movie: Movie) {
        if (movie.id == -1) {
            return
        }

        lifecycleScope.launch {
            try {
                val movieDetail = getMovieDetailsUseCase(movie.id)
                val bundle = Bundle().apply {
                    putParcelable("movie_detail_data", movieDetail)
                }
                findNavController().navigate(R.id.action_MovieListFragment_to_movieDetailFragment, bundle)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Film detayları yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateAuthButton()
        setupFavoriteButton(binding.favoriteFab)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}