package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.data.repository.MoviesRepository
import com.vakifbank.WatchWise.databinding.FragmentSeeMoreBinding
import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.ui.adapter.SearchMovieAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class MovieCategory {
    POPULAR, TOP_RATED, UPCOMING
}

class SeeMoreFragment : BaseFragment() {

    private var _binding: FragmentSeeMoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchMovieAdapter: SearchMovieAdapter
    private val movieList = mutableListOf<Movie>()

    private var categoryType: MovieCategory = MovieCategory.POPULAR
    private var categoryTitle: String = ""
    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            categoryType = when(bundle.getString("category_type")) {
                "popular" -> MovieCategory.POPULAR
                "top_rated" -> MovieCategory.TOP_RATED
                "upcoming" -> MovieCategory.UPCOMING
                else -> MovieCategory.POPULAR
            }
            categoryTitle = bundle.getString("category_title") ?: "Filmler"
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeeMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupFavoriteButton(binding.favoriteFab)
        loadMovies(currentPage)
    }

    private fun setupUI() {
        binding.categoryTitleTextView.text = categoryTitle
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        searchMovieAdapter = SearchMovieAdapter(movieList) { movie ->
            onMovieClick(movie)
        }

        binding.moviesRecyclerView.apply {
            adapter = searchMovieAdapter
            layoutManager = LinearLayoutManager(context)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                   // println("visibleItemCount: $visibleItemCount, totalItemCount: $totalItemCount, firstVisibleItemPosition: $firstVisibleItemPosition")

                    // sayfanın sonuna (20 filme) yaklaştığında yeni sayfa yükle
                    if (!isLoading && currentPage < totalPages) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            loadMovies(currentPage + 1)
                        }
                    }
                }
            })
        }
    }

    private fun loadMovies(page: Int) {
        if (isLoading) return
        isLoading = true

        val call = when(categoryType) {
            MovieCategory.POPULAR -> MoviesRepository.getPopularMovies(page)
            MovieCategory.TOP_RATED -> MoviesRepository.getTopRatedMovies(page)
            MovieCategory.UPCOMING -> MoviesRepository.getUpcomingMovies(page)
        }

        call.enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                isLoading = false

                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    moviesResponse?.let {
                        totalPages = it.totalPages ?: 1
                        it.movies?.let { movies ->
                            if (page == 1) {
                                movieList.clear()
                            }
                            movieList.addAll(movies)
                            currentPage = page
                            searchMovieAdapter.notifyDataSetChanged()

                            if (currentPage < 1 && currentPage < totalPages) {
                                loadMovies(currentPage + 1)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
            }
        })
    }

    private fun onMovieClick(movie: Movie) {
        if (movie.id == null || movie.id == -1) {
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
                        findNavController().navigate(R.id.action_seeMoreFragment_to_movieDetailFragment, bundle)
                    }
                }
            }
            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}