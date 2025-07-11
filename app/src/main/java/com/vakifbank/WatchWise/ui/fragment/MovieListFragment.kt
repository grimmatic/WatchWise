package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.data.repository.MoviesRepository
import com.vakifbank.WatchWise.databinding.FragmentMovieListBinding
import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.ui.adapter.MovieAdapter
import com.vakifbank.WatchWise.ui.adapter.MovieListType
import com.vakifbank.WatchWise.ui.adapter.SearchMovieAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieListFragment : BaseFragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
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
        setUpRecyclerViews()
        setUpTextColors()
        setUpSearchSection()
        setUpAuthButton()
        setupFavoriteButton(binding.favoriteFab)
        loadAllMovies()
        showScrollHint()
        navigateSeeMorePopularFragment()
        navigateSeeMoreTopRatedFragment()
        navigateSeeMoreUpcomingFragment()
    }

    override fun onResume() {
        super.onResume()
        updateAuthButton()
        setupFavoriteButton(binding.favoriteFab)
        // Eğer search mode'dayken detay sayfasından dönüyorsak search durumunu geri yükle
        if (isSearchMode && lastSearchQuery.isNotEmpty()) {
            restoreSearchState()
        }
    }

    private fun setUpAuthButton() {
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

    private fun restoreSearchState() {
        binding.searchEditText.setText(lastSearchQuery)
        binding.searchEditText.setSelection(lastSearchQuery.length)
        switchToSearchMode()
        searchMovies(lastSearchQuery)
    }

    private fun setUpRecyclerViews() {
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

    private fun setUpTextColors() {
        binding.mostPopularText.setTextColor(ContextCompat.getColor(requireContext(), R.color.popular_purple))
        binding.topRatedText.setTextColor(ContextCompat.getColor(requireContext(), R.color.top_rated_green))
        binding.upcomingText.setTextColor(ContextCompat.getColor(requireContext(), R.color.upcoming_orange))
    }

    private fun setUpSearchSection() {
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
                    searchMovies(query)
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

    private fun searchMovies(query: String) {
        val call = MoviesRepository.searchMovies(query)
        call.enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    moviesResponse?.movies?.let { movies ->
                        searchMovieAdapter.updateMovies(movies)
                    }
                } else {
                    searchMovieAdapter.clearMovies()
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                searchMovieAdapter.clearMovies()
            }
        })
    }

    private fun loadAllMovies() {
        loadPopularMovies()
        loadTopRatedMovies()
        loadUpcomingMovies()
    }

    private fun loadPopularMovies() {
        val call = MoviesRepository.getPopularMovies(1)
        call.enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    moviesResponse?.movies?.let { movies ->
                        popularMovieList.clear()
                        popularMovieList.addAll(movies)
                        popularMovieAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                Log.e("MovieListFragment", "Popular API çağrısı başarısız: ${t.message}")
            }
        })
    }

    private fun loadTopRatedMovies() {
        val call = MoviesRepository.getTopRatedMovies(1)
        call.enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    moviesResponse?.movies?.let { movies ->
                        topRatedMovieList.clear()
                        topRatedMovieList.addAll(movies)
                        topRatedMovieAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                Log.e("MovieListFragment", "Top Rated API çağrısı başarısız: ${t.message}")
            }
        })
    }

    private fun loadUpcomingMovies() {
        val call = MoviesRepository.getUpcomingMovies(1)
        call.enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    moviesResponse?.movies?.let { movies ->
                        upcomingMovieList.clear()
                        upcomingMovieList.addAll(movies)
                        upcomingMovieAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                Log.e("MovieListFragment", "Upcoming API çağrısı başarısız: ${t.message}")
            }
        })
    }

    private fun onMovieClick(movie: Movie) {
        if (movie.id == -1) {
            return
        }

        val call = MoviesRepository.getMovieDetails(movie.id)
        call.enqueue(object : Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                if (response.isSuccessful) {
                    response.body()?.let { movieDetail ->
                        val bundle = Bundle().apply {
                            putParcelable("movie_detail_data", movieDetail)
                        }
                        findNavController().navigate(R.id.action_MovieListFragment_to_movieDetailFragment, bundle)
                    }
                }
            }

            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                Log.e("MovieListFragment", "hata: ${t.message}")
            }
        })
    }

    private fun showScrollHint() {
        binding.populerRecyclerView.post {
            if (_binding == null || !isAdded) return@post

            binding.populerRecyclerView.smoothScrollBy(250, 0)

            Handler(Looper.getMainLooper()).postDelayed({
                if (_binding != null && isAdded) {
                    binding.populerRecyclerView.smoothScrollBy(-250, 0)
                }
            }, 1000)
        }
    }

    fun navigateSeeMorePopularFragment(){
        val bundle = Bundle().apply {
            putString("category_type", "popular")
            putString("category_title", "En Popüler Filmler")
        }
        binding.seeMore.setOnClickListener {
            findNavController().navigate(R.id.action_MovieListFragment_to_seeMoreFragment,bundle)
        }
    }

    fun navigateSeeMoreTopRatedFragment(){
        val bundle = Bundle().apply {
            putString("category_type", "top_rated")
            putString("category_title", "En İyi Filmler")
        }
        binding.seeMore2.setOnClickListener {
            findNavController().navigate(R.id.action_MovieListFragment_to_seeMoreFragment,bundle)
        }
    }

    fun navigateSeeMoreUpcomingFragment(){
        val bundle = Bundle().apply {
            putString("category_type", "upcoming")
            putString("category_title", "Yakında")
        }
        binding.seeMore3.setOnClickListener {
            findNavController().navigate(R.id.action_MovieListFragment_to_seeMoreFragment,bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}